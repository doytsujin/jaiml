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
