package gov.cms.model.rda.codegen.plugin;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.ModelUtil;
import gov.cms.model.rda.codegen.plugin.model.TableBean;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/** A Maven Mojo that generates code for RDA API JPA entities. */
@Mojo(name = "entities", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RdaEntityCodeGenMojo extends AbstractMojo {
  @Parameter(property = "mappingFile")
  private String mappingFile;

  @Parameter(
      property = "outputDirectory",
      defaultValue = "${project.build.directory}/generated-sources/rda-entities")
  private String outputDirectory;

  @Parameter(property = "project", readonly = true)
  private MavenProject project;

  @SneakyThrows(IOException.class)
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (mappingFile == null || !new File(mappingFile).isFile()) {
      throw new MojoFailureException("mappingFile not defined or does not exist");
    }

    MappingBean rootMapping = ModelUtil.loadMappingFromYamlFile(mappingFile);
    TypeSpec rootEntity = createEntityFromMapping(rootMapping);
    JavaFile javaFile = JavaFile.builder(getPackageName(rootMapping), rootEntity).build();
    File outputDir = new File(outputDirectory);
    outputDir.mkdirs();
    javaFile.writeTo(outputDir);
    project.addCompileSourceRoot(outputDirectory);
  }

  private TypeSpec createEntityFromMapping(MappingBean mapping) throws MojoFailureException {
    TypeSpec.Builder classBuilder =
        TypeSpec.classBuilder(getClassName(mapping))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Entity.class)
            .addAnnotation(createTableAnnotation(mapping.getTable()));
    final Set<String> primaryKeys = mapping.getTable().computePrimaryKeys();
    if (primaryKeys.size() == 0) {
      throw new MojoFailureException("at least one primary key field is required");
    }
    List<FieldSpec> primaryKeySpecs = new ArrayList<>();
    for (FieldBean field : mapping.getFields()) {
      FieldSpec.Builder builder =
          FieldSpec.builder(field.getColumn().computeJavaType(), field.getTo());
      builder.addModifiers(Modifier.PRIVATE);
      builder.addAnnotation(createColumnAnnotation(field));
      if (primaryKeys.contains(field.getTo())) {
        builder.addAnnotation(Id.class);
      }
      FieldSpec fieldSpec = builder.build();
      classBuilder.addField(fieldSpec);
      PoetUtil.addGetter(fieldSpec, classBuilder);
      PoetUtil.addSetter(fieldSpec, classBuilder);
      if (primaryKeys.contains(field.getTo())) {
        primaryKeySpecs.add(fieldSpec);
      }
    }
    PoetUtil.addHashCode(primaryKeySpecs, classBuilder);
    PoetUtil.addEquals(
        ClassName.get(getPackageName(mapping), getClassName(mapping)),
        primaryKeySpecs,
        classBuilder);
    return classBuilder.build();
  }

  private String quoteName(String name) {
    return "`" + name + "`";
  }

  private String getPackageName(MappingBean mapping) {
    String className = mapping.getEntity();
    return className.substring(0, className.lastIndexOf("."));
  }

  private String getClassName(MappingBean mapping) {
    String className = mapping.getEntity();
    return className.substring(1 + className.lastIndexOf("."));
  }

  private AnnotationSpec createTableAnnotation(TableBean table) {
    AnnotationSpec.Builder builder = AnnotationSpec.builder(Table.class);
    builder.addMember("name", "$S", quoteName(table.getName()));
    if (table.hasSchema()) {
      builder.addMember("schema", "$S", quoteName(table.getSchema()));
    }
    return builder.build();
  }

  private AnnotationSpec createColumnAnnotation(FieldBean field) {
    ColumnBean column = field.getColumn();
    AnnotationSpec.Builder builder = AnnotationSpec.builder(Column.class);
    builder.addMember("name", "$S", quoteName(column.getColumnName(field.getTo())));
    if (!column.isNullable()) {
      builder.addMember("nullable", "$L", false);
    }
    builder.addMember("columnDefinition", "$S", column.getSqlType());
    int length = column.computeLength();
    if (length > 0) {
      builder.addMember("length", "$L", length);
    }
    return builder.build();
  }
}
