package aiml;

/**
 * Multiple contexts of the same type aren't supported in a single path, the
 * result should be multiple paths, this has to be ensured by the loader
 * @author Kim Sullivan
 * @version 1.0
 */

public class MultipleContextsException
    extends RuntimeException {
  public MultipleContextsException() {
  }

}
