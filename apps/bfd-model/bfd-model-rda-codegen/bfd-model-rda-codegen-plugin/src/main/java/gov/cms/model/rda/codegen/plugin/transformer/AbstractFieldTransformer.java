package gov.cms.model.rda.codegen.plugin.transformer;

import static gov.cms.model.rda.codegen.plugin.transformer.TransformerUtil.capitalize;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import gov.cms.model.rda.codegen.plugin.PoetUtil;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.MappingBean;
import gov.cms.model.rda.codegen.plugin.model.TransformationBean;
import java.util.List;

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
public abstract class AbstractFieldTransformer {
  public static final String SOURCE_VAR = "from";
  public static final String DEST_VAR = "to";
  public static final String TRANSFORMER_VAR = "transformer";
  public static final String NOW_VAR = "now";
  public static final String HASHER_VAR = "idHasher";
  public static final String CLOCK_VAR = "clock";
  public static final CodeBlock NOW_VALUE = CodeBlock.of("$L", NOW_VAR);
  public static final String ENUM_FACTORY_VAR = "enumExtractorFactory";

  /**
   * Generate a code block that would copy the field from the source object to the dest object using
   * the transformer object. The CodeBlock will be inserted into a method definition by the caller.
   * That method will have variables for the source, destination, and transformer objects. The
   * generated code can use the {@code SOURCE_VAR} to reference the RDA * API source object, {@code
   * DEST_VAR} to reference the destination entity object, and {@code * TRANSFORMER_VAR} to
   * reference the {@link gov.cms.model.rda.codegen.library.DataTransformer}.
   *
   * @param mapping The mapping that contains the field.
   * @param transformation The specific field to be copied.
   * @return CodeBlock for the generated block of code
   */
  public abstract CodeBlock generateCodeBlock(
      MappingBean mapping, ColumnBean column, TransformationBean transformation);

  public List<FieldSpec> generateFieldSpecs(
      MappingBean mapping, ColumnBean column, TransformationBean transformation) {
    return ImmutableList.of();
  }

  public List<CodeBlock> generateFieldInitializers(
      MappingBean mapping, ColumnBean column, TransformationBean transformation) {
    return ImmutableList.of();
  }

  protected CodeBlock fieldNameReference(MappingBean mapping, ColumnBean column) {
    return CodeBlock.of(
        "$T.Fields.$L", PoetUtil.toClassName(mapping.getEntityClassName()), column.getName());
  }

  protected CodeBlock sourceHasRef(TransformationBean transformation) {
    return CodeBlock.of("$L::has$L", SOURCE_VAR, capitalize(transformation.getFrom()));
  }

  protected CodeBlock sourceGetRef(TransformationBean transformation) {
    return CodeBlock.of("$L::get$L", SOURCE_VAR, capitalize(transformation.getFrom()));
  }

  protected CodeBlock sourceValue(TransformationBean transformation) {
    return CodeBlock.of("$L.get$L()", SOURCE_VAR, capitalize(transformation.getFrom()));
  }

  protected CodeBlock destSetter(ColumnBean column, CodeBlock value) {
    return CodeBlock.builder()
        .addStatement("$L.set$L($L)", DEST_VAR, capitalize(column.getName()), value)
        .build();
  }

  protected CodeBlock destSetRef(ColumnBean column) {
    return CodeBlock.of("$L::set$L", DEST_VAR, capitalize(column.getName()));
  }
}
