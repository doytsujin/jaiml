package aiml.context;

/**
 * This Exception gets thrown when information about an unknown context is requested.
 * @author Kim Sullivan
 * @version 1.0
 */

public class UnknownContextException
    extends RuntimeException {

  public UnknownContextException() {
  }

  public UnknownContextException(String p0) {
    super(p0);
  }

  public UnknownContextException(Throwable p0) {
    super(p0);
  }

  public UnknownContextException(String p0, Throwable p1) {
    super(p0, p1);
  }
}
