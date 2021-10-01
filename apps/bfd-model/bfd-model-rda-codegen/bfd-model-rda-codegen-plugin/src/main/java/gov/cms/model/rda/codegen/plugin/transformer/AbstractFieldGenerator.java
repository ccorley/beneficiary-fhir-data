package gov.cms.model.rda.codegen.plugin.transformer;

import static org.apache.commons.lang3.StringUtils.capitalize;

import com.squareup.javapoet.CodeBlock;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.RootBean;

/**
 * A FieldTransformGenerator is an object that generates java code for a specific type of field
 * transformation. Note the plugin does not actually do any data movement so these objects don't
 * actually interact with data. Instead they interact with objects from the DSL model that define
 * classes and entity types, etc and use these to generate methods to actually accomplish data
 * movement when that method is called by the program that uses the code we're generating.
 *
 * <p>The plugin will use a mapping of names to instances of this interface to select the proper
 * generator to use for each field in the model.
 */
public abstract class AbstractFieldGenerator {
  static final String SOURCE_VAR = "from";
  static final String DEST_VAR = "to";
  static final String TRANSFORMER_VAR = "transformer";

  /**
   * Generate a code block that would copy the field from the source object to the dest object using
   * the transformer object. The CodeBlock will be inserted into a method definition by the caller.
   * That method will have variables for the source, destination, and transformer objects. The
   * generated code can use the {@code SOURCE_VAR} to reference the RDA * API source object, {@code
   * DEST_VAR} to reference the destination entity object, and {@code * TRANSFORMER_VAR} to
   * reference the {@link gov.cms.model.rda.codegen.library.DataTransformer}.
   *
   * @param model The root of the DSL model file.
   * @param mapping The mapping that contains the field.
   * @param field The specific field to be copied.
   * @return CodeBlock for the generated block of code
   */
  public abstract CodeBlock generateCodeBlock(RootBean model, MappingBean mapping, FieldBean field);

  protected String fieldNameReference(MappingBean mapping, FieldBean field) {
    return String.format("%s.Fields.%s", mapping.getEntity(), field.getTo());
  }

  protected String sourceHasRef(FieldBean field) {
    return String.format("%s::has%s", SOURCE_VAR, capitalize(field.getFrom()));
  }

  protected String sourceGetRef(FieldBean field) {
    return String.format("%s::get%s", SOURCE_VAR, capitalize(field.getFrom()));
  }

  protected String sourceValue(FieldBean field) {
    return String.format("%s.get%s()", SOURCE_VAR, capitalize(field.getFrom()));
  }

  protected String destSetRef(FieldBean field) {
    return String.format("%s::set%s", DEST_VAR, capitalize(field.getTo()));
  }
}
