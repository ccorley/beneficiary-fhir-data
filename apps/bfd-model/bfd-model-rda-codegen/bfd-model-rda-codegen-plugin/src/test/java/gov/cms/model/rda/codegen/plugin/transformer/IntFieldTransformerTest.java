package gov.cms.model.rda.codegen.plugin.transformer;

import static org.junit.Assert.assertEquals;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;
import org.junit.Test;

public class IntFieldTransformerTest {
  @Test(expected = IllegalArgumentException.class)
  public void requiredField() {
    ColumnBean column =
        ColumnBean.builder().name("idrDtlCnt").nullable(true).sqlType("int").build();
    FieldBean field = FieldBean.builder().optional(false).column(column).from("idrDtlCnt").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    IntFieldTransformer generator = new IntFieldTransformer();
    generator.generateCodeBlock(mapping, field);
  }

  @Test
  public void optionalField() {
    ColumnBean column =
        ColumnBean.builder().name("idrDtlCnt").nullable(true).sqlType("int").build();
    FieldBean field = FieldBean.builder().column(column).from("idrDtlCnt").build();
    MappingBean mapping =
        MappingBean.builder().entity("gov.cms.bfd.model.rda.PreAdjFissClaim").field(field).build();
    RootBean model = RootBean.builder().mapping(mapping).build();

    IntFieldTransformer generator = new IntFieldTransformer();
    CodeBlock block = generator.generateCodeBlock(mapping, field);
    assertEquals(
        "transformer.copyOptionalInt(from::hasIdrDtlCnt, from::getIdrDtlCnt, to::setIdrDtlCnt);\n",
        block.toString());
  }
}
