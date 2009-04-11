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

package aiml.context;

import aiml.environment.Environment;

/**
 * A special "fake" context that's associated with terminal ContextNodes in the
 * classifier tree. This context has the has the highest possible order, and
 * always comes last.
 * 
 * @author Kim Sullivan
 * 
 */
public class LeafContext extends Context<Object> {

  public static LeafContext LEAF_CONTEXT = new LeafContext();

  private LeafContext() {
    super(null, null, null);
  }

  @Override
  public int getOrder() {
    return Integer.MAX_VALUE;
  }

  @Override
  public String getValue(Environment e) {
    return null;
  }

}
