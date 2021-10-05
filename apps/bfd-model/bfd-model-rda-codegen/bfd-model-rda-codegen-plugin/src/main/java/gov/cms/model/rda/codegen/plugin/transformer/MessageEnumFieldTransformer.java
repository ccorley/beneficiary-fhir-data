package gov.cms.model.rda.codegen.plugin.transformer;

import static gov.cms.model.rda.codegen.plugin.transformer.TransformerUtil.capitalize;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import gov.cms.model.rda.codegen.library.EnumStringExtractor;
import gov.cms.model.rda.codegen.plugin.PoetUtil;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import java.util.List;
import javax.lang.model.element.Modifier;

public class MessageEnumFieldTransformer extends AbstractFieldTransformer {
  @Override
  public CodeBlock generateCodeBlock(MappingBean mapping, FieldBean field) {
    CodeBlock.Builder builder = CodeBlock.builder();
    if (field.getColumn().isChar()) {
      builder.addStatement(
          "$L.copyEnumAsCharacter($L, $L.getEnumString($L), $L)",
          TRANSFORMER_VAR,
          fieldNameReference(mapping, field),
          extractorName(field),
          SOURCE_VAR,
          destSetRef(field));
    } else {
      builder.addStatement(
          "$L.copyEnumAsString($L,$L,1,$L,$L.getEnumString($L),$L)",
          TRANSFORMER_VAR,
          fieldNameReference(mapping, field),
          field.getColumn().isNullable(),
          field.getColumn().computeLength(),
          extractorName(field),
          SOURCE_VAR,
          destSetRef(field));
    }
    return builder.build();
  }

  @Override
  public List<FieldSpec> generateFieldSpecs(MappingBean mapping, FieldBean field) {
    final ClassName messageClass = PoetUtil.toClassName(mapping.getMessage());
    final ClassName enumClass = PoetUtil.toClassName(field.transformerOption("enumClass").get());
    FieldSpec.Builder builder =
        FieldSpec.builder(
            ParameterizedTypeName.get(
                ClassName.get(EnumStringExtractor.class), messageClass, enumClass),
            extractorName(field),
            Modifier.PRIVATE,
            Modifier.FINAL);
    return ImmutableList.of(builder.build());
  }

  @Override
  public List<CodeBlock> generateFieldInitializers(MappingBean mapping, FieldBean field) {
    final ClassName messageClass = PoetUtil.toClassName(mapping.getMessage());
    final ClassName enumClass = PoetUtil.toClassName(field.transformerOption("enumClass").get());
    CodeBlock initializer =
        CodeBlock.builder()
            .addStatement(
                "$L = $L.createEnumStringExtractor($T::$L,$T::$L,$T::$L,$T::$L,$T.UNRECOGNIZED,$T.of(),$T.of())",
                extractorName(field),
                ENUM_FACTORY_VAR,
                messageClass,
                sourceEnumHashValueMethod(field),
                messageClass,
                sourceEnumGetValueMethod(field),
                messageClass,
                sourceHasUnrecognizedMethod(field),
                messageClass,
                sourceGetUnrecognizedMethod(field),
                enumClass,
                ImmutableSet.class,
                ImmutableSet.class)
            .build();
    return ImmutableList.of(initializer);
  }

  private String sourceEnumHashValueMethod(FieldBean field) {
    return String.format("has%sEnum", capitalize(field.getFrom()));
  }

  private String sourceEnumGetValueMethod(FieldBean field) {
    return String.format("get%sEnum", capitalize(field.getFrom()));
  }

  private String sourceHasUnrecognizedMethod(FieldBean field) {
    return String.format("has%sUnrecognized", capitalize(field.getFrom()));
  }

  private String sourceGetUnrecognizedMethod(FieldBean field) {
    return String.format("get%sUnrecognized", capitalize(field.getFrom()));
  }

  private static String extractorName(FieldBean field) {
    return String.format("%sExtractor", field.getTo());
  }
}
