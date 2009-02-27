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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * The ContextInfo class maintains information about all the different contexts
 * and provides methods to query the value of context variables. ContextInfo is
 * meant to serve as an interface between the different types of contextual data
 * sources, for example a database, system scripts and the matching algorithm.
 * </p>
 * 
 * <p>
 * <i>Note to self: </i> I'm thinking about how to ensure the general
 * availability of this class and it's methods, I'll probably use a singleton,
 * static methods or something like that...
 * </p>
 * 
 * @author Kim Sullivan
 * @version 1.0
 */

public class ContextInfo {

  /**
   * An array of known contexts, the context order is defined by the order of
   * insertion.
   */
  private List<Context> contexts = new ArrayList<Context>();

  /**
   * An array of known contexts but this tame keyed by their name, and not
   * order.
   */
  private Map<String, Context> contextNames = new HashMap<String, Context>();

  /**
   * The default constructor is private, people aren't generally supposed to
   * create ContextInfo objects. It is possible that future implementations will
   * use a more robust mechanism.
   */
  private ContextInfo() {
  }

  private static class Holder {
    private static final ContextInfo contextInfo = new ContextInfo();
  }

  public static ContextInfo getInstance() {
    return Holder.contextInfo;
  }

  /**
   * <p>
   * Get information about a context
   * </p>
   * 
   * <p>
   * If an invalid context ID has been specified, an
   * {@link UnknownContextException} is thrown (this should never happen in a
   * properly initialized matching environment)
   * </p>
   * 
   * @param name
   *          the name of the context
   * @return information about a context
   */

  public Context getContext(String name) {
    Context c = contextNames.get(name);
    if (c == null) {
      throw new UnknownContextException("The context \"" + name +
          "\" is unknown");
    } else {
      return c;
    }
  }

  /**
   * <p>
   * Get information about a context
   * </p>
   * 
   * <p>
   * If an invalid context ID has been specified, an
   * {@link UnknownContextException} is thrown (this should never happen in a
   * properly initialized matching environment)
   * </p>
   * 
   * @param context
   *          the order of the context
   * @return information about a context
   */
  public Context getContext(int context) {
    try {
      return contexts.get(context);
    } catch (IndexOutOfBoundsException e) {
      throw new UnknownContextException("The context with ID " + context +
          " is unknown", e);
    }
  }

  /**
   * Register a new context. This method sets the new contexts order and adds it
   * to te list of known contexts.
   * 
   * @param c
   *          the context to be added
   */
  public void registerContext(Context c) {
    //Ensure that duplicate contexts can't be added...
    if (contextNames.get(c.getName()) != null) {
      throw new DuplicateContextException();
    }
    if (c.getName().equals("input") && !(c instanceof InputContext)) {
      throw new BadContextClassException();
    }
    c.setOrder(contexts.size());
    contexts.add(c);
    contextNames.put(c.getName(), c);
  }

  /**
   * Return the number of contexts. All contexts (or at least some) must already
   * be present before matching can occur, otherwise a
   * NoContextsPresentException is thrown.
   * 
   * @return the number of registered contexts
   */
  public int getCount() {
    return contexts.size();
  }

  /**
   * Resets the entire ContextInfo structure. This can be used for example if a
   * different matching order is desired. Unfortunately this means the whole
   * matching tree is invalid, and must be regenerated (this is <i>not</i>
   * ensured by a call to this method).
   */
  public void reset() {
    contexts.clear();
    contextNames.clear();
  }
}
