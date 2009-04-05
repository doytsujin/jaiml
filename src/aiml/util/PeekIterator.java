/*
    jaiml - java AIML library
    Copyright (C) 2009  Kim Sullivan
    
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
    
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package aiml.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A special iterator that allows peeking at the next value without advancing
 * the position in the list.
 * 
 * @author Kim Sullivan
 */
public class PeekIterator<T> implements Iterator<T> {
  private Iterator<T> iterator;
  private T next;

  /**
   * Constructs a new peek iterator using the given iterable.
   * 
   * @param iterator
   */
  public PeekIterator(Iterable<T> iterable) {
    this.iterator = iterable.iterator();
    prepareNext();
  }

  /**
   * Internally advances the iterator and caches the next value.
   */
  private void prepareNext() {
    if (iterator.hasNext()) {
      next = iterator.next();
    } else {
      next = null;
    }
  }

  /**
   * Returns tha value that <code>next</code> will return, without advancing the
   * iterator to the next position.
   * 
   * @return the next element in the iteration
   * @throws NoSuchElementException
   *           iteration has no more elements.
   */
  public T peek() {
    if (next == null) {
      throw new NoSuchElementException();
    }
    return next;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

  @Override
  public T next() {
    T result = peek();
    prepareNext();
    return result;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}