package gov.cms.model.rda.codegen.plugin.transformer;

import static org.junit.Assert.assertEquals;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import org.junit.Test;

public class StringFieldGeneratorTest {
  @Test
  public void requiredField() {
    ColumnBean column =
        ColumnBean.builder().name("hicNo").nullable(true).sqlType("varchar(12)").build();
    FieldBean field = FieldBean.builder().optional(false).column(column).from("hicNo").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    StringFieldGenerator generator = new StringFieldGenerator();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals(
        "transformer.copyString(gov.cms.bfd.model.rda.PreAdjFissClaim.Fields.hicNo, true, 1, 12, from.getHicNo(), to::setHicNo);\n",
        block.toString());
  }

  @Test
  public void optionalField() {
    ColumnBean column =
        ColumnBean.builder().name("hicNo").nullable(true).sqlType("varchar(12)").build();
    FieldBean field = FieldBean.builder().column(column).from("hicNo").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    StringFieldGenerator generator = new StringFieldGenerator();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals(
        "transformer.copyOptionalString(gov.cms.bfd.model.rda.PreAdjFissClaim.Fields.hicNo, 1, 12, from::hasHicNo, from::getHicNo, to::setHicNo);\n",
        block.toString());
  }
}
