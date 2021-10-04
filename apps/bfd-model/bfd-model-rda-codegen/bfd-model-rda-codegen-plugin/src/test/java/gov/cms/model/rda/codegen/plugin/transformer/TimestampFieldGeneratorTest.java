package gov.cms.model.rda.codegen.plugin.transformer;

import static org.junit.Assert.assertEquals;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import org.junit.Test;

public class TimestampFieldGeneratorTest {
  @Test
  public void test() {
    FieldBean field = FieldBean.builder().to("lastUpdated").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    TimestampFieldGenerator generator = new TimestampFieldGenerator();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals("to.setLastUpdated(clock.instant());\n", block.toString());
  }
}
