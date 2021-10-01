package gov.cms.model.rda.codegen.library;

import com.google.protobuf.ProtocolMessageEnum;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * The RDA API uses a combination of oneof and custom options for their enum values. Specifically
 * the oneof will contain either a valid enum value or a string. The string is used when they have
 * no match on their side for one of the defined enum cases.
 *
 * <p>When the string does match one of the standard cases the API provides access to the underlying
 * string in the form of a protobuf "custom option".
 *
 * <p>In our database we simply store the original string value. Getting to that value requires a
 * series of tests and calls to pull the value from either the custom option data or the
 * unrecognized value.
 *
 * <p>This class encapsulates the logic needed to extract the string value of an enum. It is
 * immutable and uses lambdas to access the values it needs for a specific record at runtime.
 * Lambdas have to be used because protobuf generates the record classes and does not generate any
 * kind of base class we can use.
 *
 * @param <TRecord> the protobuf object class
 * @param <TEnum> the protobuf field's enum class
 */
public interface EnumStringExtractor<TRecord, TEnum extends ProtocolMessageEnum> {
  /** Single Result instance for any missing value. */
  Result NO_VALUE_RESULT = new Result(Status.NoValue);
  /** Single Result instance for any invalid value. */
  Result INVALID_VALUE_RESULT = new Result(Status.InvalidValue);

  /**
   * Check the record to see if a value exists and return a Result object containing either the
   * string value or a Status indicating why a value could not be returned.
   *
   * @param record object containing the enum field
   * @return the Result object containing Status and (possibly) a value
   */
  Result getEnumString(TRecord record);

  /** Used to indicate the outcome of the value lookup in a Result object. */
  public enum Status {
    /** Neither the enum nor the unrecognized value string were set in the protobuf field. */
    NoValue,
    /**
     * The server set an invalid ordinal value in the enum field and protobuf mapped this to the
     * unrecognizedValue.
     */
    InvalidValue,
    /** Either the enum was set to a valid value or the unrecognized string value was set. */
    HasValue,
    /** A value was present but was rejected as unsupported. */
    UnsupportedValue
  }

  @Getter
  @EqualsAndHashCode
  @ToString
  public static class Result {
    private final Status status;
    @Nullable private final String value;

    public Result(Status status) {
      this.status = status;
      value = null;
    }

    public Result(@Nullable String value) {
      status = Status.HasValue;
      this.value = value;
    }

    public Result(Status status, @Nullable String value) {
      this.status = status;
      this.value = value;
    }
  }

  /**
   * Additional options that can be used to alter default behavior. Currently, there is only one
   * option available but using an enum instead of a boolean to enable the option improves code
   * clarity.
   */
  public enum Options {
    /** Report an unsupported value result if the field has its unrecognized value. */
    RejectUnrecognized
  }

  interface Factory<TRecord, TEnum extends ProtocolMessageEnum> {
    /*
     * Constructs a value using the provided functions and value.
     *
     * @param hasEnumValue lambda used to determine if the enun has a value
     * @param getEnumValue lambda used to get the enum's value
     * @param hasUnrecognizedValue lambda used to determine if the field has an unrecognized value
     *     string
     * @param getUnrecognizedValue lambda used to get the value string
     * @param invalidValue enum value (usually TEnum.UNRECOGNIZED) for protobuf's bad enum value
     * @param unsupportedEnumValues set of enum values that should generate an UnsupportedValue Result
     * @param options the (usually empty) set of options to be used while processing
     */ EnumStringExtractor createEnumStringExtractor(
        Predicate<TRecord> hasEnumValue,
        Function<TRecord, ProtocolMessageEnum> getEnumValue,
        Predicate<TRecord> hasUnrecognizedValue,
        Function<TRecord, String> getUnrecognizedValue,
        TEnum invalidValue,
        Set<ProtocolMessageEnum> unsupportedEnumValues,
        Set<Options> options);
  }
}