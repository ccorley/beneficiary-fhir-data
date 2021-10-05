package gov.cms.model.rda.codegen.plugin.transformer;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;

public class IntFieldTransformer extends AbstractFieldTransformer {
  @Override
  public CodeBlock generateCodeBlock(MappingBean mapping, FieldBean field) {
    return field.isOptional()
        ? generateBlockForOptional(mapping, field)
        : generateBlockForRequired(mapping, field);
  }

  private CodeBlock generateBlockForRequired(MappingBean mapping, FieldBean field) {
    throw new IllegalArgumentException("non-optional ints are not currently supported");
  }

  private CodeBlock generateBlockForOptional(MappingBean mapping, FieldBean field) {
    return CodeBlock.builder()
        .addStatement(
            "$N.copyOptionalInt($L, $L, $L)",
            TRANSFORMER_VAR,
            sourceHasRef(field),
            sourceGetRef(field),
            destSetRef(field))
        .build();
  }
}
