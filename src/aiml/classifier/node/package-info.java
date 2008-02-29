/*
    jaiml - java AIML library
    Copyright (C) 2004-2008  Kim Sullivan

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * <p>Provides classes of pattern tree nodes. Except for explicitly registering
 * node types so they can be used in the matching process, the end user should
 * never create instances of nodes, all node creation is handled by the adding
 * mechanism.</p>
 * <p>Many nodes implement a static <code>register()</code> method that registers
 * them in the <code>PatternNodeFactory</code> factory class. You may note that
 * this method is not part of the base class, neither that it belongs to any kind
 * of interface. This is done in purpose, because many nodes are conflicting in
 * nature, and should theybe used together the matching system may behave in
 * unpredictable ways. This way, the programmer is forced to hand-pick and
 * register the basic nodes he wants to use.</p>
 */
package aiml.classifier.node;