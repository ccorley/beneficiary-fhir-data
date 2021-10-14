package gov.cms.bfd.sharedutils.generators.exceptions;

/** Thrown when a generator specific error was encountered. */
public class GeneratorException extends RuntimeException {

  public GeneratorException(String message) {
    super(message);
  }

  public GeneratorException(String message, Throwable cause) {
    super(message, cause);
  }
}