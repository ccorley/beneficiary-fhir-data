package gov.cms.model.rda.codegen.plugin.transformer;

import static org.junit.Assert.assertEquals;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import org.junit.Test;

public class DateFieldGeneratorTest {
  @Test
  public void requiredField() {
    ColumnBean column = ColumnBean.builder().name("beneDob").nullable(true).sqlType("date").build();
    FieldBean field = FieldBean.builder().optional(false).column(column).from("beneDob").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    DateFieldGenerator generator = new DateFieldGenerator();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals(
        "transformer.copyDate(gov.cms.bfd.model.rda.PreAdjFissClaim.Fields.beneDob, true, from.getBeneDob(), to::setBeneDob);\n",
        block.toString());
  }

  @Test
  public void optionalField() {
    ColumnBean column = ColumnBean.builder().name("beneDob").nullable(true).sqlType("date").build();
    FieldBean field = FieldBean.builder().column(column).from("beneDob").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    DateFieldGenerator generator = new DateFieldGenerator();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals(
        "transformer.copyOptionalDate(gov.cms.bfd.model.rda.PreAdjFissClaim.Fields.beneDob, from::hasBeneDob, from::getBeneDob, to::setBeneDob);\n",
        block.toString());
  }
}
