package demo;

/**
 * Thrown when there is an irrecoverable error during
 * pattern loading.
 * @author Kim Sullivan
 * @version 1.0
 */

public class LoadErrorException extends Exception {
  /**
   * Constructs a <code>LoadErrorException</code> with no detail mesage.
   */
  public LoadErrorException() {
  }
  /**
   * Constructs a <code>LoadErrorException</code> with the specified detail
   * mesage.
   */
  public LoadErrorException(String message) {
    super(message);
  }
  /**
   * Constructs a <code>LoadErrorException</code> with a detail message and a
   * cause.
   */
  public LoadErrorException(String message, Throwable cause) {
    super(message,cause);
  }
  /**
   * Constructs a <code>LoadErrorException</code> with a cause and the causes'
   * detail message.
   */
  public LoadErrorException(Throwable cause) {
    super(cause);
  }

}
