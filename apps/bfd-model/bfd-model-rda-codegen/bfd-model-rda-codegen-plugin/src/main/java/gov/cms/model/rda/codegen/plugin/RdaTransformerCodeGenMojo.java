package gov.cms.model.rda.codegen.plugin;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import gov.cms.model.rda.codegen.library.DataTransformer;
import gov.cms.model.rda.codegen.library.EnumStringExtractor;
import gov.cms.model.rda.codegen.plugin.model.ArrayElement;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.ModelUtil;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import gov.cms.model.rda.codegen.plugin.transformer.AbstractFieldTransformer;
import gov.cms.model.rda.codegen.plugin.transformer.TransformerUtil;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.lang.model.element.Modifier;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * A Maven Mojo that generates code to copy and transform data from RDA API message objects into JPA
 * entity objects.
 */
@Mojo(name = "transformers", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RdaTransformerCodeGenMojo extends AbstractMojo {
  private static final String TRANSFORM_METHOD_NAME = "transformMessage";

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
      if (mapping.hasTransformer()) {
        TypeSpec rootEntity = createTransformerClassForMapping(mapping, root::findMappingWithId);
        JavaFile javaFile = JavaFile.builder(mapping.transformerPackageName(), rootEntity).build();
        javaFile.writeTo(outputDir);
      }
    }
    project.addCompileSourceRoot(outputDirectory);
  }

  private TypeSpec createTransformerClassForMapping(
      MappingBean mapping, Function<String, Optional<MappingBean>> mappingFinder)
      throws MojoExecutionException {
    final List<MappingBean> allMappings = findAllMappingsForRootMapping(mapping, mappingFinder);
    TypeSpec.Builder classBuilder =
        TypeSpec.classBuilder(mapping.transformerClassName())
            .addModifiers(Modifier.PUBLIC)
            .addField(
                Clock.class, AbstractFieldTransformer.CLOCK_VAR, Modifier.PRIVATE, Modifier.FINAL)
            .addField(
                ParameterizedTypeName.get(Function.class, String.class, String.class),
                AbstractFieldTransformer.HASHER_VAR,
                Modifier.PRIVATE,
                Modifier.FINAL);
    for (MappingBean aMapping : allMappings) {
      for (FieldSpec field : createFieldsForMapping(aMapping)) {
        classBuilder.addField(field);
      }
    }
    MethodSpec.Builder constructor =
        MethodSpec.constructorBuilder()
            .addParameter(Clock.class, AbstractFieldTransformer.CLOCK_VAR)
            .addParameter(
                ParameterizedTypeName.get(Function.class, String.class, String.class),
                AbstractFieldTransformer.HASHER_VAR)
            .addParameter(
                EnumStringExtractor.Factory.class, AbstractFieldTransformer.ENUM_FACTORY_VAR)
            .addStatement(
                "this.$L = $L",
                AbstractFieldTransformer.CLOCK_VAR,
                AbstractFieldTransformer.CLOCK_VAR)
            .addStatement(
                "this.$L = $L",
                AbstractFieldTransformer.HASHER_VAR,
                AbstractFieldTransformer.HASHER_VAR);
    for (MappingBean aMapping : allMappings) {
      for (CodeBlock initializer : createFieldInitializersForMapping(aMapping)) {
        constructor.addCode(initializer);
      }
    }
    classBuilder
        .addMethod(constructor.build())
        .addMethod(createPublicTransformMessageMethodForParentMapping(mapping));
    for (MappingBean aMapping : allMappings) {
      classBuilder.addMethod(
          createPrivateTransformMessageMethodForMapping(aMapping, mappingFinder));
    }
    return classBuilder.build();
  }

  /**
   * Finds all MappingBeans reachable from the specified root mapping by recursively following all
   * of its array mappings. The result is all of the mappings that this transformer class will need
   * to generate code for.
   *
   * @param root Base mapping that we are creating transformer for.
   * @param mappingFinder Lookup method to find mappings by their id.
   * @return List of all mappings reachable from root (including root).
   */
  private List<MappingBean> findAllMappingsForRootMapping(
      MappingBean root, Function<String, Optional<MappingBean>> mappingFinder) {
    final ImmutableList.Builder<MappingBean> answer = ImmutableList.builder();
    final Set<String> visited = new HashSet<>();
    final List<MappingBean> queue = new ArrayList<>();

    queue.add(root);
    while (queue.size() > 0) {
      MappingBean mapping = queue.remove(0);
      visited.add(mapping.getId());
      answer.add(mapping);
      for (ArrayElement element : mapping.getArrays()) {
        Optional<MappingBean> elementMapping = mappingFinder.apply(element.getMapping());
        if (elementMapping.isPresent() && !visited.contains(element.getMapping())) {
          queue.add(elementMapping.get());
        }
      }
    }
    return answer.build();
  }

  private List<FieldSpec> createFieldsForMapping(MappingBean mapping) {
    return mapping.getFields().stream()
        .flatMap(
            field ->
                TransformerUtil.selectTransformerForField(field)
                    .map(transformer -> transformer.generateFieldSpecs(mapping, field))
                    .orElse(Collections.emptyList()).stream())
        .collect(ImmutableList.toImmutableList());
  }

  private List<CodeBlock> createFieldInitializersForMapping(MappingBean mapping) {
    return mapping.getFields().stream()
        .flatMap(
            field ->
                TransformerUtil.selectTransformerForField(field)
                    .map(transformer -> transformer.generateFieldInitializers(mapping, field))
                    .orElse(Collections.emptyList()).stream())
        .collect(ImmutableList.toImmutableList());
  }

  private MethodSpec createPublicTransformMessageMethodForParentMapping(MappingBean mapping)
      throws MojoExecutionException {
    final TypeName messageClassType = ModelUtil.classType(mapping.getMessage());
    final TypeName entityClassType = ModelUtil.classType(mapping.getEntity());
    final MethodSpec.Builder builder =
        MethodSpec.methodBuilder(TRANSFORM_METHOD_NAME)
            .returns(entityClassType)
            .addModifiers(Modifier.PUBLIC)
            .addParameter(messageClassType, AbstractFieldTransformer.SOURCE_VAR)
            .addStatement(
                "final $T $L = new $T()",
                DataTransformer.class,
                AbstractFieldTransformer.TRANSFORMER_VAR,
                DataTransformer.class)
            .addStatement(
                "final $T $L = $L($L,$L)",
                entityClassType,
                AbstractFieldTransformer.DEST_VAR,
                TRANSFORM_METHOD_NAME,
                AbstractFieldTransformer.SOURCE_VAR,
                AbstractFieldTransformer.TRANSFORMER_VAR);
    builder.addStatement(
        "final $T errors = $L.getErrors();",
        ParameterizedTypeName.get(List.class, DataTransformer.ErrorMessage.class),
        AbstractFieldTransformer.TRANSFORMER_VAR);
    builder
        .beginControlFlow("if (errors.size() > 0)")
        .addStatement(
            "String message = String.format($S, errors.size(), from.get$L(), errors)",
            "failed with %d errors: key=%s errors=%s",
            TransformerUtil.capitalize(mapping.firstPrimaryKeyField().get().getTo()))
        .addStatement("throw new DataTransformer.TransformationException(message, errors)")
        .endControlFlow();
    builder.addStatement("return $L", AbstractFieldTransformer.DEST_VAR);
    return builder.build();
  }

  private MethodSpec createPrivateTransformMessageMethodForMapping(
      MappingBean mapping, Function<String, Optional<MappingBean>> mappingFinder)
      throws MojoExecutionException {
    final TypeName messageClassType = ModelUtil.classType(mapping.getMessage());
    final TypeName entityClassType = ModelUtil.classType(mapping.getEntity());
    final MethodSpec.Builder builder =
        MethodSpec.methodBuilder(TRANSFORM_METHOD_NAME)
            .returns(entityClassType)
            .addModifiers(Modifier.PRIVATE)
            .addParameter(messageClassType, AbstractFieldTransformer.SOURCE_VAR)
            .addParameter(DataTransformer.class, AbstractFieldTransformer.TRANSFORMER_VAR)
            .addStatement(
                "final $T $L = new $T()",
                entityClassType,
                AbstractFieldTransformer.DEST_VAR,
                entityClassType);
    for (FieldBean field : mapping.getFields()) {
      TransformerUtil.selectTransformerForField(field)
          .map(generator -> generator.generateCodeBlock(mapping, field))
          .ifPresent(builder::addCode);
    }
    for (ArrayElement arrayElement : mapping.getArrays()) {
      final MappingBean elementMapping =
          mappingFinder
              .apply(arrayElement.getMapping())
              .orElseThrow(
                  () ->
                      failure(
                          "array element of %s references undefined mapping %s",
                          mapping.getId(), arrayElement.getMapping()));
      CodeBlock.Builder loop =
          CodeBlock.builder()
              .beginControlFlow(
                  "for (short index = 0; index < $L.get$LCount(); ++index)",
                  AbstractFieldTransformer.SOURCE_VAR,
                  TransformerUtil.capitalize(arrayElement.getFrom()));
      loop.addStatement(
              "final $T itemFrom = $L.get$L(index)",
              PoetUtil.toClassName(elementMapping.getMessage()),
              AbstractFieldTransformer.SOURCE_VAR,
              TransformerUtil.capitalize(arrayElement.getFrom()))
          .addStatement(
              "final $T itemTo = $L(itemFrom,$L)",
              PoetUtil.toClassName(elementMapping.getEntity()),
              TRANSFORM_METHOD_NAME,
              AbstractFieldTransformer.TRANSFORMER_VAR);
      for (FieldBean elementField : elementMapping.getFields()) {
        final String elementFrom = elementField.getFrom();
        if (elementFrom.equals(TransformerUtil.IndexFromName)) {
          // set the field to the current array element's index within the array
          loop.addStatement(
              "itemTo.set$L(index)", TransformerUtil.capitalize(elementField.getTo()));
        } else if (elementFrom.equals(TransformerUtil.ParentFromName)) {
          // copy the same field from parent into the array element field
          loop.addStatement(
              "itemTo.set$L($L.get$L())",
              TransformerUtil.capitalize(elementField.getTo()),
              AbstractFieldTransformer.SOURCE_VAR,
              TransformerUtil.capitalize(elementField.getTo()));
        }
      }
      loop.addStatement(
              "$L.get$L().add(itemTo)",
              AbstractFieldTransformer.DEST_VAR,
              TransformerUtil.capitalize(arrayElement.getTo()))
          .endControlFlow();
      builder.addCode(loop.build());
    }
    builder.addStatement("return $L", AbstractFieldTransformer.DEST_VAR);
    return builder.build();
  }

  private MojoExecutionException failure(String formatString, Object... args) {
    String message = String.format(formatString, args);
    return new MojoExecutionException(message);
  }

  private void fail(String formatString, Object... args) throws MojoExecutionException {
    throw failure(formatString, args);
  }
}
