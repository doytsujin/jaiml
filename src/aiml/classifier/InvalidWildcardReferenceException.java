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

import aiml.context.ContextInfo;

/**
 * This exception gets raised whenever a template script requests a value for a
 * wildcard that hasn't been bound.
 * 
 * @author Kim Sullivan
 * 
 */
public class InvalidWildcardReferenceException extends Exception {

  public InvalidWildcardReferenceException(int context, int wildcadIndex) {
    super("Invalid wildcard reference: context \"" +
        ContextInfo.getInstance().getContext(context).getName() +
        "\" does not have a wildcard with the index " + wildcadIndex +
        " bound for the current match");
  }
}
