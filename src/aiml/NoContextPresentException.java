package aiml;

/**
 * This exception gets thrown whenever there is an attempt to start a match
 * when there are no defined contexts.
 *
 * It's a runtime exception, because such a thing should never happen with a
 * properly initialized matching environment.
 * @author Kim Sullivan
 * @version 1.0
 */

public class NoContextPresentException
    extends RuntimeException {
  public NoContextPresentException() {
  }

}
