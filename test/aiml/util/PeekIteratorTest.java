package aiml.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import junit.framework.TestCase;

public class PeekIteratorTest extends TestCase {

  public void testPeek() {
    LinkedList<Integer> aList = new LinkedList<Integer>(Arrays.asList(0, 1, 2,
        3));
    LinkedList<Integer> bList = new LinkedList<Integer>(aList);
    Iterator<Integer> aIterator = aList.iterator();
    PeekIterator<Integer> bIterator = new PeekIterator<Integer>(bList);

    Integer peek;
    peek = bIterator.peek();
    assertEquals(aIterator.hasNext(), bIterator.hasNext());
    peek = bIterator.peek();
    assertEquals(aIterator.hasNext(), bIterator.hasNext());
    peek = bIterator.peek();
    assertEquals(aIterator.hasNext(), bIterator.hasNext());

    peek = bIterator.peek();
    assertEquals(Integer.valueOf(0), peek);
    assertEquals(peek, aIterator.next());
    assertEquals(peek, bIterator.next());

    assertEquals(aIterator.hasNext(), bIterator.hasNext());

    peek = bIterator.peek();
    assertEquals(Integer.valueOf(1), peek);
    assertEquals(peek, aIterator.next());
    assertEquals(peek, bIterator.next());

    assertEquals(aIterator.hasNext(), bIterator.hasNext());

    peek = bIterator.peek();
    assertEquals(Integer.valueOf(2), peek);
    peek = bIterator.peek();
    assertEquals(Integer.valueOf(2), peek);
    peek = bIterator.peek();
    assertEquals(Integer.valueOf(2), peek);

    assertEquals(peek, aIterator.next());
    assertEquals(peek, bIterator.next());

    assertEquals(aIterator.hasNext(), bIterator.hasNext());

    peek = bIterator.peek();
    assertEquals(Integer.valueOf(3), peek);
    assertEquals(aIterator.hasNext(), bIterator.hasNext());
    assertEquals(peek, aIterator.next());
    assertEquals(peek, bIterator.next());

    assertEquals(false, aIterator.hasNext());
    assertEquals(false, bIterator.hasNext());

  }

}
