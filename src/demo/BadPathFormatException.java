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
