package aiml.context;

/**
 * This exception gets thrown when someone tries to register an already existing
 * context with the same name.
 * @author Kim Sullivan
 * @version 1.0
 */
public class DuplicateContextException
    extends RuntimeException {
  public DuplicateContextException() {
  }

}
