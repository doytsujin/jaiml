/*
    jaiml - java AIML library
    Copyright (C) 2004, 2009  Kim Sullivan

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
 * <p>
 * This exception gets thrown when there is an attempt to add an already present
 * path to the matching tree.
 * </p>
 * <p>
 * <i>Note to self:</i> When I tried to include the path that caused the
 * exception, I found out that I actually don't have a reference to it at the
 * place where this exception occurs. Somthing has to be done about this, using
 * a generic iterator is probably not the way to go.
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class DuplicatePathException extends Exception {
  public DuplicatePathException() {
  }
}
