package demo;

/**
 * Thrown when a path that's supposed to be added to the matching tree doesn't
 * conform to the format. The format is a sequence of zero or more
 * <code>[name]pattern[/]</code> blocks, where <code>name</code> is the name of
 * a context, and <code>pattern</code> is the pattern. The pattern can be an
 * empty string, the name can not.
 * @author Kim Sullivan
 * @version 1.0
 */

public class BadPathFormatException extends Exception{
  /**
   * Constructs a <code>BadPathFormatException</code> with no detail mesage.
   */
  public BadPathFormatException() {
  }

}
