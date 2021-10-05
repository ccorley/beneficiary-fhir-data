package gov.cms.model.rda.codegen.plugin.transformer;

import static org.junit.Assert.assertEquals;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import org.junit.Test;

public class AmountFieldTransformerTest {
  @Test
  public void requiredField() {
    ColumnBean column =
        ColumnBean.builder().name("estAmtDue").nullable(true).sqlType("decimal(11,2)").build();
    FieldBean field = FieldBean.builder().optional(false).column(column).from("estAmtDue").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    AmountFieldTransformer generator = new AmountFieldTransformer();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals(
        "transformer.copyAmount(gov.cms.bfd.model.rda.PreAdjFissClaim.Fields.estAmtDue, true, from.getEstAmtDue(), to::setEstAmtDue);\n",
        block.toString());
  }

  @Test
  public void optionalField() {
    ColumnBean column =
        ColumnBean.builder().name("estAmtDue").nullable(true).sqlType("decimal(11,2)").build();
    FieldBean field = FieldBean.builder().column(column).from("estAmtDue").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    AmountFieldTransformer generator = new AmountFieldTransformer();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals(
        "transformer.copyOptionalAmount(gov.cms.bfd.model.rda.PreAdjFissClaim.Fields.estAmtDue, from::hasEstAmtDue, from::getEstAmtDue, to::setEstAmtDue);\n",
        block.toString());
  }
}
