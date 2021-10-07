package gov.cms.model.rda.codegen.plugin;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import gov.cms.model.rda.codegen.plugin.model.ArrayElement;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.EnumTypeBean;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.FieldNameConstants;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/** A Maven Mojo that generates code for RDA API JPA entities. */
@Mojo(name = "entities", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RdaEntityCodeGenMojo extends AbstractMojo {
  // region Fields
  private static final String PRIMARY_KEY_CLASS_NAME = "PK";

  @Parameter(property = "mappingFile")
  private String mappingFile;

  @Parameter(
      property = "outputDirectory",
      defaultValue = "${project.build.directory}/generated-sources/rda-entities")
  private String outputDirectory;

  @Parameter(property = "project", readonly = true)
  private MavenProject project;
  // endregion

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
      JavaFile javaFile = JavaFile.builder(mapping.entityPackageName(), rootEntity).build();
      javaFile.writeTo(outputDir);
    }
    project.addCompileSourceRoot(outputDirectory);
  }

  // region Implementation Details
  private TypeSpec createEntityFromMapping(
      MappingBean mapping, Function<String, Optional<MappingBean>> mappingFinder)
      throws MojoExecutionException {
    TypeSpec.Builder classBuilder =
        TypeSpec.classBuilder(mapping.entityClassName())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Entity.class)
            .addAnnotation(Getter.class)
            .addAnnotation(Setter.class)
            .addAnnotation(Builder.class)
            .addAnnotation(AllArgsConstructor.class)
            .addAnnotation(NoArgsConstructor.class)
            .addAnnotation(createEqualsAndHashCodeAnnotation())
            .addAnnotation(FieldNameConstants.class)
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
    TypeName fieldType;
    for (ColumnBean column : mapping.getTable().getColumns()) {
      if (column.isEnum()) {
        fieldType =
            ClassName.get(
                mapping.entityPackageName(), mapping.entityClassName(), column.getEnumType());
      } else {
        fieldType = column.computeJavaType();
      }
      FieldSpec.Builder builder =
          FieldSpec.builder(fieldType, column.getName())
              .addModifiers(Modifier.PRIVATE)
              .addAnnotation(createColumnAnnotation(column));
      if (mapping.getTable().isPrimaryKey(column.getName())) {
        builder.addAnnotation(Id.class).addAnnotation(EqualsAndHashCode.Include.class);
      }
      if (column.isEnum()) {
        builder.addAnnotation(createEnumeratedAnnotation(mapping, column));
      }
      FieldSpec fieldSpec = builder.build();
      classBuilder.addField(fieldSpec);
      if (mapping.getTable().isPrimaryKey(column.getName())) {
        primaryKeySpecs.add(fieldSpec);
      }
    }
  }

  private AnnotationSpec createEnumeratedAnnotation(MappingBean mapping, ColumnBean column)
      throws MojoExecutionException {
    if (!column.isString()) {
      fail(
          "enum columns must have String type but this one does not: mapping=%s column=%s",
          mapping.getId(), column.getName());
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
          "classes with arrays must have a single primary key column but this one has %d: mapping=%s",
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
          mapping.getTable().getPrimaryKeyColumns().get(0),
          arrayElement,
          arrayMapping.get());
    }
  }

  private ClassName computePrimaryKeyClassName(MappingBean mapping) {
    return ClassName.get(
        mapping.entityPackageName(), mapping.entityClassName(), PRIMARY_KEY_CLASS_NAME);
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
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .addAnnotation(Data.class)
            .addAnnotation(NoArgsConstructor.class)
            .addAnnotation(AllArgsConstructor.class);
    for (FieldSpec fieldSpec : parentKeySpecs) {
      FieldSpec.Builder keyFieldBuilder =
          FieldSpec.builder(fieldSpec.type, fieldSpec.name).addModifiers(Modifier.PRIVATE);
      FieldSpec keyFieldSpec = keyFieldBuilder.build();
      pkClassBuilder.addField(keyFieldSpec);
    }
    return pkClassBuilder.build();
  }

  private void addArrayField(
      TypeSpec.Builder classBuilder,
      String primaryKeyFieldName,
      ArrayElement arrayElement,
      MappingBean elementMapping) {
    ClassName entityClass =
        ClassName.get(elementMapping.entityPackageName(), elementMapping.entityClassName());
    ParameterizedTypeName setType =
        ParameterizedTypeName.get(ClassName.get(Set.class), entityClass);
    FieldSpec.Builder fieldBuilder =
        FieldSpec.builder(setType, arrayElement.getTo())
            .addModifiers(Modifier.PRIVATE)
            .initializer("new $T<>()", HashSet.class)
            .addAnnotation(createOneToManyAnnotation(primaryKeyFieldName))
            .addAnnotation(Builder.Default.class);
    FieldSpec fieldSpec = fieldBuilder.build();
    classBuilder.addField(fieldSpec);
  }

  private AnnotationSpec createOneToManyAnnotation(String mappedBy) {
    return AnnotationSpec.builder(OneToMany.class)
        .addMember("mappedBy", "$S", mappedBy)
        .addMember("fetch", "$T.$L", FetchType.class, FetchType.EAGER)
        .addMember("orphanRemoval", "$L", true)
        .addMember("cascade", "$T.$L", CascadeType.class, CascadeType.ALL)
        .build();
  }

  private AnnotationSpec createEqualsAndHashCodeAnnotation() {
    return AnnotationSpec.builder(EqualsAndHashCode.class)
        .addMember("onlyExplicitlyIncluded", "$L", true)
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

  private AnnotationSpec createColumnAnnotation(ColumnBean column) {
    AnnotationSpec.Builder builder =
        AnnotationSpec.builder(Column.class)
            .addMember("name", "$S", quoteName(column.getColumnName(column.getName())));
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
  // endregion

  private void fail(String formatString, Object... args) throws MojoExecutionException {
    String message = String.format(formatString, args);
    throw new MojoExecutionException(message);
  }
}
