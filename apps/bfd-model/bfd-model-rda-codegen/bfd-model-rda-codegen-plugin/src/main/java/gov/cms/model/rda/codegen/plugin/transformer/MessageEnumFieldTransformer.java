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
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import java.util.List;
import javax.lang.model.element.Modifier;

public class MessageEnumFieldTransformer extends AbstractFieldTransformer {
  public static final String ENUM_CLASS_OPT = "enumClass";
  public static final String HAS_UNRECOGNIZED_OPT = "hasUnrecognized";
  public static final String UNSUPPORTED_ENUM_VALUES_OPT = "unsupportedEnumValues";
  public static final String EXTRACTOR_OPTIONS_OPT = "extractorOptions";

  @Override
  public CodeBlock generateCodeBlock(MappingBean mapping, FieldBean field) {
    CodeBlock.Builder builder = CodeBlock.builder();
    if (field.getColumn().isChar()) {
      builder.addStatement(
          "$L.copyEnumAsCharacter($L, $L.getEnumString($L), $L)",
          TRANSFORMER_VAR,
          fieldNameReference(mapping, field),
          extractorName(mapping, field),
          SOURCE_VAR,
          destSetRef(field));
    } else {
      builder.addStatement(
          "$L.copyEnumAsString($L,$L,1,$L,$L.getEnumString($L),$L)",
          TRANSFORMER_VAR,
          fieldNameReference(mapping, field),
          field.getColumn().isNullable(),
          field.getColumn().computeLength(),
          extractorName(mapping, field),
          SOURCE_VAR,
          destSetRef(field));
    }
    return builder.build();
  }

  @Override
  public List<FieldSpec> generateFieldSpecs(MappingBean mapping, FieldBean field) {
    final ClassName messageClass = PoetUtil.toClassName(mapping.getMessage());
    final ClassName enumClass = PoetUtil.toClassName(field.transformerOption(ENUM_CLASS_OPT).get());
    FieldSpec.Builder builder =
        FieldSpec.builder(
            ParameterizedTypeName.get(
                ClassName.get(EnumStringExtractor.class), messageClass, enumClass),
            extractorName(mapping, field),
            Modifier.PRIVATE,
            Modifier.FINAL);
    return ImmutableList.of(builder.build());
  }

  @Override
  public List<CodeBlock> generateFieldInitializers(MappingBean mapping, FieldBean field) {
    final ClassName messageClass = PoetUtil.toClassName(mapping.getMessage());
    final ClassName enumClass = PoetUtil.toClassName(field.transformerOption(ENUM_CLASS_OPT).get());
    final boolean hasUnrecognized =
        field.transformerOption(HAS_UNRECOGNIZED_OPT).map(Boolean::parseBoolean).orElse(true);
    CodeBlock initializer =
        CodeBlock.builder()
            .addStatement(
                "$L = $L.createEnumStringExtractor($L,$L,$L,$L,$T.UNRECOGNIZED,$L,$L)",
                extractorName(mapping, field),
                ENUM_FACTORY_VAR,
                sourceEnumHashValueMethod(messageClass, field),
                sourceEnumGetValueMethod(messageClass, field),
                sourceHasUnrecognizedMethod(hasUnrecognized, messageClass, field),
                sourceGetUnrecognizedMethod(hasUnrecognized, messageClass, field),
                enumClass,
                unsupportedEnumValues(enumClass, field),
                extractorOptions(field))
            .build();
    return ImmutableList.of(initializer);
  }

  private CodeBlock sourceEnumHashValueMethod(ClassName sourceClass, FieldBean field) {
    return CodeBlock.of("$T::has$LEnum", sourceClass, capitalize(field.getFrom()));
  }

  private CodeBlock sourceEnumGetValueMethod(ClassName sourceClass, FieldBean field) {
    return CodeBlock.of("$T::get$LEnum", sourceClass, capitalize(field.getFrom()));
  }

  private CodeBlock sourceHasUnrecognizedMethod(
      boolean hasMethod, ClassName sourceClass, FieldBean field) {
    if (hasMethod) {
      return CodeBlock.of("$T::has$LUnrecognized", sourceClass, capitalize(field.getFrom()));
    } else {
      return CodeBlock.of("ignored -> false");
    }
  }

  private CodeBlock sourceGetUnrecognizedMethod(
      boolean hasMethod, ClassName sourceClass, FieldBean field) {
    if (hasMethod) {
      return CodeBlock.of("$T::get$LUnrecognized", sourceClass, capitalize(field.getFrom()));
    } else {
      return CodeBlock.of("ignored -> null");
    }
  }

  private CodeBlock unsupportedEnumValues(ClassName enumClass, FieldBean field) {
    return createOptionsSet(enumClass, field, UNSUPPORTED_ENUM_VALUES_OPT);
  }

  private CodeBlock extractorOptions(FieldBean field) {
    return createOptionsSet(
        ClassName.get(EnumStringExtractor.Options.class), field, EXTRACTOR_OPTIONS_OPT);
  }

  private CodeBlock createOptionsSet(TypeName enumClass, FieldBean field, String enumOptionName) {
    final List<String> enumValues =
        field.transformerListOption(enumOptionName).orElse(ImmutableList.of());
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

  private static String extractorName(MappingBean mapping, FieldBean field) {
    return String.format("%s_%s_Extractor", mapping.entityClassName(), field.getTo());
  }
}
