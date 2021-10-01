package gov.cms.model.rda.codegen.plugin.transformer;

import static org.junit.Assert.assertEquals;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import org.junit.Test;

public class CharFieldGeneratorTest {
  @Test
  public void requiredField() {
    ColumnBean column =
        ColumnBean.builder().name("curr1Status").nullable(false).sqlType("char(1)").build();
    FieldBean field =
        FieldBean.builder().optional(false).column(column).from("curr1Status").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    CharFieldGenerator generator = new CharFieldGenerator();
    CodeBlock block = generator.generateCodeBlock(model, mapping, field);
    assertEquals(
        "transformer.copyCharacter(gov.cms.bfd.model.rda.PreAdjFissClaim.Fields.curr1Status, from.getCurr1Status(), to::setCurr1Status);\n",
        block.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void optionalField() {
    ColumnBean column =
        ColumnBean.builder().name("idrDtlCnt").nullable(true).sqlType("char(1)").build();
    FieldBean field = FieldBean.builder().column(column).from("idrDtlCnt").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    CharFieldGenerator generator = new CharFieldGenerator();
    generator.generateCodeBlock(model, mapping, field);
  }
}
