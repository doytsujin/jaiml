package aiml;

/**
 * <p>This exception gets thrown when there is an attempt to add an already
 * present path to the matching tree.</p>
 * <p><i>Note to self:</i> When I tried to include the path that caused the
 * exception, I found out that I actually don't have a reference to it at the
 * place where this exception occurs. Somthing has to be done about this, using
 * a generic iterator is probably not the way to go.</p>
 * @author Kim Sullivan
 * @version 1.0
 */

public class DuplicatePathException
    extends Exception {
  public DuplicatePathException() {
  }
}
