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

package aiml.context;

/**
 * This Exception gets thrown when information about an unknown context is
 * requested.
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class UnknownContextException extends RuntimeException {

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
