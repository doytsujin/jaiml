/*
    jaiml - java AIML library
    Copyright (C) 2004-2005  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

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
