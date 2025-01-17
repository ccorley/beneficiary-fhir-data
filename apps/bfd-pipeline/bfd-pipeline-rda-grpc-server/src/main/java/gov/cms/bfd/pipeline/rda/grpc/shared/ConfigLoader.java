package gov.cms.bfd.pipeline.rda.grpc.shared;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Abstracts loading configuration values from one or more sources of key/value pairs. Provides
 * commonly needed methods to pull in configuration data and parse it various ways. A lambda
 * function or method reference can be used as the source of data (e.g. System::getenv or
 * myMap::get) or the Builder object can be used to construct chained sources that can use a basic
 * set of defaults but allow something else to override them selectively. For example using a Map of
 * default values but allow environment variables to override anything in the Map.
 */
public class ConfigLoader {
  private final Function<String, String> source;

  /**
   * Constructs a ConfigLoader that uses the provided Function as the source of key/value
   * configuration data. The function will be called whenever a specific configuration value is
   * needed. It can return null to indicate that no value is available for the requested key.
   *
   * @param source function used to obtain key/value configuration data
   */
  public ConfigLoader(Function<String, String> source) {
    this.source = source;
  }

  /**
   * Creates a Builder that can be used to combine multiple sources of configuration data into a
   * prioritized chain.
   *
   * @return a Builder object for creating ConfigLoader objects.
   */
  public static ConfigLoader.Builder builder() {
    return new Builder();
  }

  /**
   * Gets a required configuration value.
   *
   * @param name name of configuration value
   * @return non-empty string value
   * @throws ConfigException if there is no non-empty value
   */
  public String stringValue(String name) {
    final String value = source.apply(name);
    if (Strings.isNullOrEmpty(value)) {
      throw new ConfigException(name, "required option not provided");
    } else {
      return value;
    }
  }

  /**
   * Gets an optional configuration value or a defaultValue if there is no non-empty value.
   *
   * @param name name of configuration value
   * @return either the non-empty string value or defaultValue
   */
  public String stringValue(String name, String defaultValue) {
    final String value = source.apply(name);
    return Strings.isNullOrEmpty(value) ? defaultValue : value;
  }

  /**
   * Gets an Optional for the specified configuration value.
   *
   * @param name name of configuration value
   * @return empty Option if there is no non-empty value, otherwise Option holding the value
   */
  public Optional<String> stringOption(String name) {
    final String value = source.apply(name);
    return Strings.isNullOrEmpty(value) ? Optional.empty() : Optional.of(value);
  }

  /**
   * Gets a required integer configuration value.
   *
   * @param name name of configuration value
   * @return integer value
   * @throws ConfigException if there is no valid integer value
   */
  public int intValue(String name) {
    final String value = source.apply(name);
    if (Strings.isNullOrEmpty(value)) {
      throw new ConfigException(name, "required option not provided");
    }
    try {
      return Integer.parseInt(value);
    } catch (Exception ex) {
      throw new ConfigException(name, "not a valid integer", ex);
    }
  }

  /**
   * Gets an optional integer configuration value or a defaultValue if there is no value.
   *
   * @param name name of configuration value
   * @return either the integer value or defaultValue
   * @throws ConfigException if a value existed but was not a valid integer
   */
  public int intValue(String name, int defaultValue) {
    final String value = source.apply(name);
    if (Strings.isNullOrEmpty(value)) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value);
    } catch (Exception ex) {
      throw new ConfigException(name, "not a valid integer", ex);
    }
  }

  /**
   * Gets an Optional for the specified integer configuration value.
   *
   * @param name name of configuration value
   * @return empty Option if there is no value, otherwise Option holding the value
   * @throws ConfigException if a value existed but was not a valid integer
   */
  public Optional<Integer> intOption(String name) {
    final String value = source.apply(name);
    if (Strings.isNullOrEmpty(value)) {
      return Optional.empty();
    }
    try {
      return Optional.of(Integer.parseInt(value));
    } catch (Exception ex) {
      throw new ConfigException(name, "not a valid integer", ex);
    }
  }

  /**
   * Gets an Optional for the specified long configuration value.
   *
   * @param name name of configuration value
   * @return empty Option if there is no value, otherwise Option holding the value
   * @throws ConfigException if a value existed but was not a valid long
   */
  public Optional<Long> longOption(String name) {
    final String value = source.apply(name);
    if (Strings.isNullOrEmpty(value)) {
      return Optional.empty();
    }
    try {
      return Optional.of(Long.parseLong(value));
    } catch (Exception ex) {
      throw new ConfigException(name, "not a valid long", ex);
    }
  }

  /**
   * Gets a required enum configuration value.
   *
   * @param name name of configuration value
   * @return enum value
   * @throws ConfigException if there is no valid enum value
   */
  public <T extends Enum<T>> T enumValue(String name, Function<String, T> parser) {
    return enumOption(name, parser)
        .orElseThrow(() -> new ConfigException(name, "required enum not provided"));
  }

  /**
   * Gets an optional enum configuration value.
   *
   * @param name name of configuration value
   * @return Optional enum value
   * @throws ConfigException if there is no valid enum value
   */
  public <T extends Enum<T>> Optional<T> enumOption(String name, Function<String, T> parser) {
    try {
      return stringOption(name).map(parser);
    } catch (Exception ex) {
      throw new ConfigException(name, "not a valid enum value: " + ex.getMessage(), ex);
    }
  }

  /**
   * Gets a required File configuration value.
   *
   * @param name name of configuration value
   * @return File value
   * @throws ConfigException if there is no value or the file is not readable
   */
  public File readableFile(String name) {
    return readableFileOption(name)
        .orElseThrow(() -> new ConfigException(name, "required option not provided"));
  }

  /**
   * Gets an Optional for the specified File configuration value.
   *
   * @param name name of configuration value
   * @return Optional containing File value or empty if no file specified
   * @throws ConfigException if there is no value or the file is not readable
   */
  public Optional<File> readableFileOption(String name) {
    Optional<File> file = stringOption(name).map(File::new);
    file.ifPresent(f -> validateReadableFile(name, f));
    return file;
  }

  /**
   * Gets a required File configuration value. Either the file must exist and be writeable or else
   * its parent directory must exist and be writeable.
   *
   * @param name name of configuration value
   * @return File value
   * @throws ConfigException if there is no value or the file/parent is not writeable
   */
  public File writeableFile(String name) {
    String value = stringValue(name);
    File file = new File(value);
    return validateWriteableFile(name, file);
  }

  /**
   * Gets an optional boolean configuration value or a defaultValue if there is no value.
   *
   * @param name name of configuration value
   * @return either the boolean value or defaultValue
   * @throws ConfigException if a value existed but it wasn't a valid boolean
   */
  public boolean booleanValue(String name, boolean defaultValue) {
    String value = source.apply(name);
    if (Strings.isNullOrEmpty(value)) {
      return defaultValue;
    }
    switch (value.toLowerCase()) {
      case "true":
        return true;
      case "false":
        return false;
      default:
        throw new ConfigException(name, "invalid boolean value: " + value);
    }
  }

  /**
   * Verifies that the specified file is readable. Returns the file if the file is readable.
   * Otherwise, throws a ConfigException.
   *
   * @param name name of configuration value that created this file
   * @param file the file object to be validated
   * @return the original File object
   * @throws ConfigException if the file is not readable
   */
  @VisibleForTesting
  File validateReadableFile(String name, File file) throws ConfigException {
    try {
      if (!file.isFile()) {
        throw new ConfigException(name, "object referenced by path is not a file");
      }
      if (!file.canRead()) {
        throw new ConfigException(name, "file is not readable");
      }
    } catch (ConfigException ex) {
      // pass our own exception through
      throw ex;
    } catch (Exception ex) {
      // wrap any other exception
      throw new ConfigException(name, "attempt to validate the file failed with an exception", ex);
    }
    return file;
  }

  /**
   * Verifies that the specified file is writeable. Either the file must exist and be writeable or
   * else its parent directory must exist and be writeable. Returns the file if the file is
   * writeable. Otherwise, throws a ConfigException.
   *
   * @param name name of configuration value that created this file
   * @param file the file object to be validated
   * @return the original File object
   * @throws ConfigException if the file/parent is not writeable
   */
  @VisibleForTesting
  File validateWriteableFile(String name, File file) throws ConfigException {
    try {
      if (file.exists()) {
        if (!file.isFile()) {
          throw new ConfigException(name, "object referenced by path is not a file");
        } else if (!file.canWrite()) {
          throw new ConfigException(name, "file is not writeable");
        }
      } else if (!file.getAbsoluteFile().getParentFile().canWrite()) {
        throw new ConfigException(name, "file does not exist and parent is not writeable");
      }
    } catch (ConfigException ex) {
      // pass our own exception through
      throw ex;
    } catch (Exception ex) {
      throw new ConfigException(name, "attempt to validate the file failed with an exception", ex);
    }
    return file;
  }

  /**
   * Builder to construct ConfigLoader instances. Each call to add a source inserts the specified
   * source as the primary source and any old source becomes a fallback if the new one has no value.
   * Multiple calls can chain any number of sources in this way. All methods return the Builder so
   * that calls can be chained.
   */
  public static class Builder {
    private Function<String, String> source = ignored -> null;

    public ConfigLoader build() {
      return new ConfigLoader(source);
    }

    public Builder add(Function<String, String> newSource) {
      Function<String, String> oldSource = this.source;
      this.source =
          name -> {
            String value = newSource.apply(name);
            return Strings.isNullOrEmpty(value) ? oldSource.apply(name) : value;
          };
      return this;
    }

    /** Adds a source that pulls values from environment variables. */
    public Builder addEnvironmentVariables() {
      return add(System::getenv);
    }

    /** Adds a source that pulls values from system properties. */
    public Builder addSystemProperties() {
      return add(System::getProperty);
    }

    /**
     * Adds a source that pulls values from the specified properties object.
     *
     * @param properties source of properties
     */
    public Builder addProperties(Properties properties) {
      return add(properties::getProperty);
    }

    /**
     * Reads properties from the specified file and adds the resulting Properties object as a
     * source. The file must exist and must be a valid Properties file.
     *
     * @param propertiesFile normal java Properties file
     * @throws IOException if reading the file failed
     */
    public Builder addPropertiesFile(File propertiesFile) throws IOException {
      Properties props = new Properties();
      try (Reader in = new BufferedReader(new FileReader(propertiesFile))) {
        props.load(in);
      }
      return addProperties(props);
    }

    /**
     * Adds key value pairs from an array of strings. Used with command line arguments array to pull
     * in options like key:value. Puts them into a Map and adds the Map's get method as a source.
     *
     * @param args command line arguments of the form key:value
     * @return the builder with the arguments mapped
     */
    public Builder addKeyValueCommandLineArguments(String[] args) {
      Map<String, String> map = new HashMap<>();
      for (String arg : args) {
        int prefixEnd = arg.indexOf(":");
        if (prefixEnd > 0) {
          map.put(arg.substring(0, prefixEnd), arg.substring(prefixEnd + 1));
        }
      }
      return add(map::get);
    }
  }
}
