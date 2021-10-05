package gov.cms.model.rda.codegen.plugin.transformer;

import com.google.common.collect.ImmutableMap;
import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import java.util.Map;
import java.util.Optional;

public class TransformerUtil {
  private static final String TimestampFromName = "NOW";
  private static final String NoMappingFromName = "NONE";
  private static final CharFieldTransformer CharInstance = new CharFieldTransformer();
  private static final IntFieldTransformer IntInstance = new IntFieldTransformer();
  private static final DateFieldTransformer DateInstance = new DateFieldTransformer();
  private static final AmountFieldTransformer AmountInstance = new AmountFieldTransformer();
  private static final StringFieldTransformer StringInstance = new StringFieldTransformer();
  private static final IdHashFieldTransformer IdHashInstance = new IdHashFieldTransformer();
  private static final MessageEnumFieldTransformer MessageEnumInstance =
      new MessageEnumFieldTransformer();
  private static final TimestampFieldTransformer TimestampInstance =
      new TimestampFieldTransformer();
  private static final Map<String, AbstractFieldTransformer> namedTransformers =
      ImmutableMap.of(
          "IdHash", IdHashInstance,
          "Now", TimestampInstance,
          "MessageEnum", MessageEnumInstance);

  public static String capitalize(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  public static Optional<AbstractFieldTransformer> selectTransformerForField(FieldBean field) {
    if (field.hasTransformer()) {
      return Optional.ofNullable(namedTransformers.get(field.getTransformer()));
    }

    final ColumnBean column = field.getColumn();
    if (column.isEnum()) {
      // TODO add support for message enums
      return Optional.empty();
    }

    if (column.isChar()) {
      return Optional.of(CharInstance);
    }

    if (column.isString()) {
      return Optional.of(StringInstance);
    }

    if (column.isInt()) {
      return Optional.of(IntInstance);
    }

    if (column.isDecimal()) {
      return Optional.of(AmountInstance);
    }

    if (column.isDate()) {
      return Optional.of(DateInstance);
    }

    if (TimestampFromName.equals(field.getFrom())) {
      return Optional.of(TimestampInstance);
    }

    if (NoMappingFromName.equals(field.getFrom())) {
      // Sequence numbers are set by the hand written caller from the value in the RdaChange object.
      // As a consequence we want to do nothing when the NoMappingFromName is used in the DSL.
      return Optional.empty();
    }

    return Optional.empty();
  }
}
