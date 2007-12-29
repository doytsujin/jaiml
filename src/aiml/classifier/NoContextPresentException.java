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

package aiml.classifier;

/**
 * This exception gets thrown whenever there is an attempt to start a match when
 * there are no defined contexts.
 * 
 * It's a runtime exception, because such a thing should never happen with a
 * properly initialized matching environment.
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class NoContextPresentException extends RuntimeException {
  public NoContextPresentException() {
  }

}
