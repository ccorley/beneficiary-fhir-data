package gov.cms.model.rda.codegen.plugin.transformer;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;

public class DateFieldTransformer extends AbstractFieldTransformer {
  @Override
  public CodeBlock generateCodeBlock(MappingBean mapping, FieldBean field) {
    return field.isOptional()
        ? generateBlockForOptional(mapping, field)
        : generateBlockForRequired(mapping, field);
  }

  private CodeBlock generateBlockForRequired(MappingBean mapping, FieldBean field) {
    return CodeBlock.builder()
        .addStatement(
            "$L.copyDate($L, $L, $L, $L)",
            TRANSFORMER_VAR,
            fieldNameReference(mapping, field),
            field.getColumn().isNullable(),
            sourceValue(field),
            destSetRef(field))
        .build();
  }

  private CodeBlock generateBlockForOptional(MappingBean mapping, FieldBean field) {
    return CodeBlock.builder()
        .addStatement(
            "$L.copyOptionalDate($L, $L, $L, $L)",
            TRANSFORMER_VAR,
            fieldNameReference(mapping, field),
            sourceHasRef(field),
            sourceGetRef(field),
            destSetRef(field))
        .build();
  }
}
