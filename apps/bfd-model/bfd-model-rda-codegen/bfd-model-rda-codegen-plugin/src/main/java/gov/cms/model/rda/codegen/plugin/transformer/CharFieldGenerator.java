package gov.cms.model.rda.codegen.plugin.transformer;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;

public class CharFieldGenerator extends AbstractFieldTransform {
  @Override
  public CodeBlock generateCodeBlock(RootBean model, MappingBean mapping, FieldBean field) {
    return field.isOptional()
        ? generateBlockForOptional(mapping, field)
        : generateBlockForRequired(mapping, field);
  }

  private CodeBlock generateBlockForRequired(MappingBean mapping, FieldBean field) {
    return CodeBlock.builder()
        .addStatement(
            "$N.copyCharacter($N, $L, $L)",
            TRANSFORMER_VAR,
            fieldNameReference(mapping, field),
            sourceValue(field),
            destSetRef(field))
        .build();
  }

  private CodeBlock generateBlockForOptional(MappingBean mapping, FieldBean field) {
    throw new IllegalArgumentException("optional chars are not currently supported");
  }
}