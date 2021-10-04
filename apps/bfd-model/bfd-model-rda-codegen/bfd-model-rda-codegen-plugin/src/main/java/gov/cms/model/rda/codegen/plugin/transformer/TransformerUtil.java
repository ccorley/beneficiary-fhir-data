package gov.cms.model.rda.codegen.plugin.transformer;

import gov.cms.model.rda.codegen.plugin.model.ColumnBean;
import gov.cms.model.rda.codegen.plugin.model.FieldBean;
import java.util.Optional;

public class TransformerUtil {
  private static final String TimestampFromName = "NOW";
  private static final String NoMappingFromName = "NONE";
  private static final CharFieldGenerator CharInstance = new CharFieldGenerator();
  private static final IntFieldGenerator IntInstance = new IntFieldGenerator();
  private static final DateFieldGenerator DateInstance = new DateFieldGenerator();
  private static final AmountFieldGenerator AmountInstance = new AmountFieldGenerator();
  private static final StringFieldGenerator StringInstance = new StringFieldGenerator();
  private static final TimestampFieldGenerator TimestampInstance = new TimestampFieldGenerator();

  public static String capitalize(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  public static Optional<AbstractFieldGenerator> selectTransformerForField(FieldBean field) {
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
