package gov.cms.model.rda.codegen.plugin.transformer;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;

public class TimestampFieldGenerator extends AbstractFieldGenerator {
  @Override
  public CodeBlock generateCodeBlock(MappingBean mapping, FieldBean field) {
    return CodeBlock.builder().addStatement("$N($L)", destSetter(field), NOW_VAR).build();
  }
}
