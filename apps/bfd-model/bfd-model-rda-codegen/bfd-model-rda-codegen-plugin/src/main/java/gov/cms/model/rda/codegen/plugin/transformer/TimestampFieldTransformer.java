package gov.cms.model.rda.codegen.plugin.transformer;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;

public class TimestampFieldTransformer extends AbstractFieldTransformer {
  @Override
  public CodeBlock generateCodeBlock(MappingBean mapping, FieldBean field) {
    return destSetter(field, NOW_VALUE);
  }
}
