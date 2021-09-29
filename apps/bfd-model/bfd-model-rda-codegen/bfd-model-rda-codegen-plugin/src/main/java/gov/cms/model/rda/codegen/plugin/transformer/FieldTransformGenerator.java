package gov.cms.model.rda.codegen.plugin.transformer;

import com.squareup.javapoet.MethodSpec;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
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
public interface FieldTransformGenerator {
  /**
   * Generate a new method with the provided name in the provided classBuilder that, when called
   * with data, performs the transformation and copying of data from the source to the destination
   * object. The caller defines the method name so that it can be assured to be unique and
   * meaningful.
   *
   * <p>The generated method must have a signature conforming to the {@link
   * gov.cms.model.rda.codegen.library.FieldTransform} interface since that is how it will be called
   * at runtime.
   *
   * @param model The root of the DSL model file.
   * @param field The specific field to be copied.
   * @param methodName The name to give the generated method.
   * @return MethodSpec for the generated method
   */
  MethodSpec generateFieldTransformMethod(RootBean model, FieldBean field, String methodName);
}
