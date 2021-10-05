package gov.cms.model.rda.codegen.plugin.transformer;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;

public class StringFieldTransformer extends AbstractFieldTransformer {
  @Override
  public CodeBlock generateCodeBlock(MappingBean mapping, FieldBean field) {
    return field.isOptional()
        ? generateBlockForOptional(mapping, field)
        : generateBlockForRequired(mapping, field);
  }

  private CodeBlock generateBlockForRequired(MappingBean mapping, FieldBean field) {
    return CodeBlock.builder()
        .addStatement(
            "$N.copyString($N, $L, 1, $L, $L, $L)",
            TRANSFORMER_VAR,
            fieldNameReference(mapping, field),
            field.getColumn().isNullable(),
            field.getColumn().computeLength(),
            sourceValue(field),
            destSetRef(field))
        .build();
  }

  private CodeBlock generateBlockForOptional(MappingBean mapping, FieldBean field) {
    return CodeBlock.builder()
        .addStatement(
            "$N.copyOptionalString($N, 1, $L, $L, $L, $L)",
            TRANSFORMER_VAR,
            fieldNameReference(mapping, field),
            field.getColumn().computeLength(),
            sourceHasRef(field),
            sourceGetRef(field),
            destSetRef(field))
        .build();
  }
}
