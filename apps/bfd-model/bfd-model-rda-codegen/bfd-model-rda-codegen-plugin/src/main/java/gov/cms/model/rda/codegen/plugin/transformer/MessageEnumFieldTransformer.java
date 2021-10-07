package gov.cms.model.rda.codegen.plugin.transformer;

import static gov.cms.model.rda.codegen.plugin.transformer.TransformerUtil.capitalize;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import gov.cms.model.rda.codegen.library.EnumStringExtractor;
import gov.cms.model.rda.codegen.plugin.PoetUtil;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.TransformationBean;
import java.util.List;
import javax.lang.model.element.Modifier;

public class MessageEnumFieldTransformer extends AbstractFieldTransformer {
  public static final String ENUM_CLASS_OPT = "enumClass";
  public static final String HAS_UNRECOGNIZED_OPT = "hasUnrecognized";
  public static final String UNSUPPORTED_ENUM_VALUES_OPT = "unsupportedEnumValues";
  public static final String EXTRACTOR_OPTIONS_OPT = "extractorOptions";

  @Override
  public CodeBlock generateCodeBlock(
      MappingBean mapping, ColumnBean column, TransformationBean transformation) {
    CodeBlock.Builder builder = CodeBlock.builder();
    if (column.isChar()) {
      builder.addStatement(
          "$L.copyEnumAsCharacter($L, $L.getEnumString($L), $L)",
          TRANSFORMER_VAR,
          fieldNameReference(mapping, column),
          extractorName(mapping, transformation),
          SOURCE_VAR,
          destSetRef(column));
    } else {
      builder.addStatement(
          "$L.copyEnumAsString($L,$L,1,$L,$L.getEnumString($L),$L)",
          TRANSFORMER_VAR,
          fieldNameReference(mapping, column),
          column.isNullable(),
          column.computeLength(),
          extractorName(mapping, transformation),
          SOURCE_VAR,
          destSetRef(column));
    }
    return builder.build();
  }

  @Override
  public List<FieldSpec> generateFieldSpecs(
      MappingBean mapping, ColumnBean column, TransformationBean transformation) {
    final ClassName messageClass = PoetUtil.toClassName(mapping.getMessageClassName());
    final ClassName enumClass =
        PoetUtil.toClassName(transformation.transformerOption(ENUM_CLASS_OPT).get());
    FieldSpec.Builder builder =
        FieldSpec.builder(
            ParameterizedTypeName.get(
                ClassName.get(EnumStringExtractor.class), messageClass, enumClass),
            extractorName(mapping, transformation),
            Modifier.PRIVATE,
            Modifier.FINAL);
    return ImmutableList.of(builder.build());
  }

  @Override
  public List<CodeBlock> generateFieldInitializers(
      MappingBean mapping, ColumnBean column, TransformationBean transformation) {
    final ClassName messageClass = PoetUtil.toClassName(mapping.getMessageClassName());
    final ClassName enumClass =
        PoetUtil.toClassName(transformation.transformerOption(ENUM_CLASS_OPT).get());
    final boolean hasUnrecognized =
        transformation
            .transformerOption(HAS_UNRECOGNIZED_OPT)
            .map(Boolean::parseBoolean)
            .orElse(true);
    CodeBlock initializer =
        CodeBlock.builder()
            .addStatement(
                "$L = $L.createEnumStringExtractor($L,$L,$L,$L,$T.UNRECOGNIZED,$L,$L)",
                extractorName(mapping, transformation),
                ENUM_FACTORY_VAR,
                sourceEnumHashValueMethod(messageClass, transformation),
                sourceEnumGetValueMethod(messageClass, transformation),
                sourceHasUnrecognizedMethod(hasUnrecognized, messageClass, transformation),
                sourceGetUnrecognizedMethod(hasUnrecognized, messageClass, transformation),
                enumClass,
                unsupportedEnumValues(enumClass, transformation),
                extractorOptions(transformation))
            .build();
    return ImmutableList.of(initializer);
  }

  private CodeBlock sourceEnumHashValueMethod(
      ClassName sourceClass, TransformationBean transformation) {
    return CodeBlock.of("$T::has$LEnum", sourceClass, capitalize(transformation.getFrom()));
  }

  private CodeBlock sourceEnumGetValueMethod(ClassName sourceClass, TransformationBean field) {
    return CodeBlock.of("$T::get$LEnum", sourceClass, capitalize(field.getFrom()));
  }

  private CodeBlock sourceHasUnrecognizedMethod(
      boolean hasMethod, ClassName sourceClass, TransformationBean transformation) {
    if (hasMethod) {
      return CodeBlock.of(
          "$T::has$LUnrecognized", sourceClass, capitalize(transformation.getFrom()));
    } else {
      return CodeBlock.of("ignored -> false");
    }
  }

  private CodeBlock sourceGetUnrecognizedMethod(
      boolean hasMethod, ClassName sourceClass, TransformationBean transformation) {
    if (hasMethod) {
      return CodeBlock.of(
          "$T::get$LUnrecognized", sourceClass, capitalize(transformation.getFrom()));
    } else {
      return CodeBlock.of("ignored -> null");
    }
  }

  private CodeBlock unsupportedEnumValues(ClassName enumClass, TransformationBean transformation) {
    return createOptionsSet(enumClass, transformation, UNSUPPORTED_ENUM_VALUES_OPT);
  }

  private CodeBlock extractorOptions(TransformationBean field) {
    return createOptionsSet(
        ClassName.get(EnumStringExtractor.Options.class), field, EXTRACTOR_OPTIONS_OPT);
  }

  private CodeBlock createOptionsSet(
      TypeName enumClass, TransformationBean transformation, String enumOptionName) {
    final List<String> enumValues =
        transformation.transformerListOption(enumOptionName).orElse(ImmutableList.of());
    boolean first = true;
    CodeBlock.Builder builder = CodeBlock.builder();
    builder.add("$T.of(", ImmutableSet.class);
    for (String enumValue : enumValues) {
      builder.add("$L$T.$L", first ? "" : ",", enumClass, enumValue);
      first = false;
    }
    builder.add(")");
    return builder.build();
  }

  private static String extractorName(MappingBean mapping, TransformationBean transformation) {
    return String.format("%s_%s_Extractor", mapping.entityClassName(), transformation.getTo());
  }
}
