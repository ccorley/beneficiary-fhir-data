package gov.cms.model.rda.codegen.plugin;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import gov.cms.model.rda.codegen.plugin.model.ArrayElement;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.EnumTypeBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.ModelUtil;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import gov.cms.model.rda.codegen.plugin.model.TableBean;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.lang.model.element.Modifier;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/** A Maven Mojo that generates code for RDA API JPA entities. */
@Mojo(name = "entities", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RdaEntityCodeGenMojo extends AbstractMojo {
  private static final String PRIMARY_KEY_CLASS_NAME = "PK";

  @Parameter(property = "mappingFile")
  private String mappingFile;

  @Parameter(
      property = "outputDirectory",
      defaultValue = "${project.build.directory}/generated-sources/rda-entities")
  private String outputDirectory;

  @Parameter(property = "project", readonly = true)
  private MavenProject project;

  @SneakyThrows(IOException.class)
  public void execute() throws MojoExecutionException {
    if (mappingFile == null || !new File(mappingFile).isFile()) {
      fail("mappingFile not defined or does not exist");
    }

    File outputDir = new File(outputDirectory);
    outputDir.mkdirs();
    RootBean root = ModelUtil.loadMappingsFromYamlFile(mappingFile);
    List<MappingBean> rootMappings = root.getMappings();
    for (MappingBean mapping : rootMappings) {
      TypeSpec rootEntity = createEntityFromMapping(mapping, root::findMappingWithId);
      JavaFile javaFile = JavaFile.builder(mapping.computePackageName(), rootEntity).build();
      javaFile.writeTo(outputDir);
    }
    project.addCompileSourceRoot(outputDirectory);
  }

  private TypeSpec createEntityFromMapping(
      MappingBean mapping, Function<String, Optional<MappingBean>> mappingFinder)
      throws MojoExecutionException {
    TypeSpec.Builder classBuilder =
        TypeSpec.classBuilder(mapping.computeClassName())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Entity.class)
            .addAnnotation(createTableAnnotation(mapping.getTable()));
    addEnums(mapping.getEnumTypes(), classBuilder);
    if (!mapping.getTable().hasPrimaryKey()) {
      fail("mapping has no primary key fields: mapping=%s", mapping.getId());
    }
    List<FieldSpec> primaryKeySpecs = new ArrayList<>();
    addColumnFields(mapping, classBuilder, primaryKeySpecs);
    if (primaryKeySpecs.size() > 1) {
      classBuilder
          .addAnnotation(createIdClassAnnotation(mapping))
          .addType(createPrimaryKeyClass(mapping, primaryKeySpecs));
    }
    if (mapping.getArrays().size() > 0) {
      addArrayFields(mapping, mappingFinder, classBuilder, primaryKeySpecs);
    }
    PoetUtil.addHashCode(primaryKeySpecs, classBuilder);
    PoetUtil.addEquals(
        ClassName.get(mapping.computePackageName(), mapping.computeClassName()),
        primaryKeySpecs,
        classBuilder);
    return classBuilder.build();
  }

  private void addEnums(List<EnumTypeBean> enumMappings, TypeSpec.Builder classBuilder) {
    for (EnumTypeBean enumMapping : enumMappings) {
      classBuilder.addType(createEnum(enumMapping));
    }
  }

  private TypeSpec createEnum(EnumTypeBean mapping) {
    TypeSpec.Builder builder =
        TypeSpec.enumBuilder(mapping.getName()).addModifiers(Modifier.PUBLIC);
    for (String value : mapping.getValues()) {
      builder.addEnumConstant(value);
    }
    return builder.build();
  }

  private void addColumnFields(
      MappingBean mapping, TypeSpec.Builder classBuilder, List<FieldSpec> primaryKeySpecs)
      throws MojoExecutionException {
    for (FieldBean field : mapping.getFields()) {
      FieldSpec.Builder builder =
          FieldSpec.builder(field.getColumn().computeJavaType(), field.getTo())
              .addModifiers(Modifier.PRIVATE)
              .addAnnotation(createColumnAnnotation(field));
      if (mapping.getTable().isPrimaryKey(field.getTo())) {
        builder.addAnnotation(Id.class);
      }
      if (field.getColumn().isEnum()) {
        builder.addAnnotation(createEnumeratedAnnotation(mapping, field));
      }
      FieldSpec fieldSpec = builder.build();
      classBuilder.addField(fieldSpec);
      PoetUtil.addGetter(fieldSpec, classBuilder);
      PoetUtil.addSetter(fieldSpec, classBuilder);
      if (mapping.getTable().isPrimaryKey(field.getTo())) {
        primaryKeySpecs.add(fieldSpec);
      }
    }
  }

  private AnnotationSpec createEnumeratedAnnotation(MappingBean mapping, FieldBean fieldMapping)
      throws MojoExecutionException {
    if (!fieldMapping.getColumn().isString()) {
      fail(
          "enum columns must have String type but this one does not: mapping=%s field=%s",
          mapping.getId(), fieldMapping.getTo());
    }
    return AnnotationSpec.builder(Enumerated.class)
        .addMember("value", "$T.$L", EnumType.class, EnumType.STRING)
        .build();
  }

  private void addArrayFields(
      MappingBean mapping,
      Function<String, Optional<MappingBean>> mappingFinder,
      TypeSpec.Builder classBuilder,
      List<FieldSpec> primaryKeySpecs)
      throws MojoExecutionException {
    if (primaryKeySpecs.size() != 1) {
      fail(
          "classes with arrays must have a single primary key field but this one has %d: mapping=%s",
          primaryKeySpecs.size(), mapping.getId());
    }
    for (ArrayElement arrayElement : mapping.getArrays()) {
      Optional<MappingBean> arrayMapping = mappingFinder.apply(arrayElement.getMapping());
      if (!arrayMapping.isPresent()) {
        fail(
            "array references unknown mapping: mapping=%s array=%s missing=%s",
            mapping.getId(), arrayElement.getTo(), arrayElement.getMapping());
      }
      addArrayField(
          classBuilder,
          mapping.getTable().getPrimaryKeyFields().get(0),
          arrayElement,
          arrayMapping.get());
    }
  }

  private ClassName computePrimaryKeyClassName(MappingBean mapping) {
    return ClassName.get(
        mapping.computePackageName(), mapping.computeClassName(), PRIMARY_KEY_CLASS_NAME);
  }

  private AnnotationSpec createIdClassAnnotation(MappingBean mapping) {
    return AnnotationSpec.builder(IdClass.class)
        .addMember("value", "$T.class", computePrimaryKeyClassName(mapping))
        .build();
  }

  private TypeSpec createPrimaryKeyClass(MappingBean mapping, List<FieldSpec> parentKeySpecs) {
    TypeSpec.Builder pkClassBuilder =
        TypeSpec.classBuilder(PRIMARY_KEY_CLASS_NAME)
            .addSuperinterface(Serializable.class)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
    List<FieldSpec> keyFieldSpecs = new ArrayList<>();
    for (FieldSpec fieldSpec : parentKeySpecs) {
      FieldSpec.Builder keyFieldBuilder =
          FieldSpec.builder(fieldSpec.type, fieldSpec.name).addModifiers(Modifier.PRIVATE);
      FieldSpec keyFieldSpec = keyFieldBuilder.build();
      keyFieldSpecs.add(keyFieldSpec);
      pkClassBuilder.addField(keyFieldSpec);
      PoetUtil.addGetter(keyFieldSpec, pkClassBuilder);
      PoetUtil.addSetter(keyFieldSpec, pkClassBuilder);
    }
    PoetUtil.addHashCode(parentKeySpecs, pkClassBuilder);
    PoetUtil.addEquals(computePrimaryKeyClassName(mapping), keyFieldSpecs, pkClassBuilder);
    return pkClassBuilder.build();
  }

  private void addArrayField(
      TypeSpec.Builder classBuilder,
      String primaryKeyFieldName,
      ArrayElement arrayElement,
      MappingBean elementMapping) {
    ClassName entityClass =
        ClassName.get(elementMapping.computePackageName(), elementMapping.computeClassName());
    ParameterizedTypeName setType =
        ParameterizedTypeName.get(ClassName.get(Set.class), entityClass);
    FieldSpec.Builder fieldBuilder =
        FieldSpec.builder(setType, arrayElement.getTo())
            .addModifiers(Modifier.PRIVATE)
            .initializer("new $T<>()", HashSet.class)
            .addAnnotation(createOneToManyAnnotation(primaryKeyFieldName));
    FieldSpec fieldSpec = fieldBuilder.build();
    classBuilder.addField(fieldSpec);
    PoetUtil.addGetter(fieldSpec, classBuilder);
    PoetUtil.addSetter(fieldSpec, classBuilder);
  }

  private AnnotationSpec createOneToManyAnnotation(String mappedBy) {
    return AnnotationSpec.builder(OneToMany.class)
        .addMember("mappedBy", "$S", mappedBy)
        .addMember("fetch", "$T.$L", FetchType.class, FetchType.EAGER)
        .addMember("orphanRemoval", "$L", true)
        .addMember("cascade", "$T.$L", CascadeType.class, CascadeType.ALL)
        .build();
  }

  private String quoteName(String name) {
    return "`" + name + "`";
  }

  private AnnotationSpec createTableAnnotation(TableBean table) {
    AnnotationSpec.Builder builder =
        AnnotationSpec.builder(Table.class).addMember("name", "$S", quoteName(table.getName()));
    if (table.hasSchema()) {
      builder.addMember("schema", "$S", quoteName(table.getSchema()));
    }
    return builder.build();
  }

  private AnnotationSpec createColumnAnnotation(FieldBean field) {
    ColumnBean column = field.getColumn();
    AnnotationSpec.Builder builder =
        AnnotationSpec.builder(Column.class)
            .addMember("name", "$S", quoteName(column.getColumnName(field.getTo())));
    if (!column.isNullable()) {
      builder.addMember("nullable", "$L", false);
    }
    if (column.isColumnDefRequired()) {
      builder.addMember("columnDefinition", "$S", column.getSqlType());
    }
    int length = column.computeLength();
    if (length > 0) {
      builder.addMember("length", "$L", length);
    }
    return builder.build();
  }

  private void fail(String formatString, Object... args) throws MojoExecutionException {
    String message = String.format(formatString, args);
    throw new MojoExecutionException(message);
  }
}
