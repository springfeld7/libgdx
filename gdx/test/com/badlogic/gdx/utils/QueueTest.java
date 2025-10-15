
package com.badlogic.gdx.utils;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

public class QueueTest {
    @Test
    public void addFirstAndLastTest () {
        Queue<Integer> queue = new Queue<Integer>();
        queue.addFirst(1);
        queue.addLast(2);
        queue.addFirst(3);
        queue.addLast(4);

        assertEquals(0, queue.indexOf(3, true));
        assertEquals(1, queue.indexOf(1, true));
        assertEquals(2, queue.indexOf(2, true));
        assertEquals(3, queue.indexOf(4, true));
    }

    @Test
    public void removeLastTest () {
        Queue<Integer> queue = new Queue<Integer>();
        queue.addLast(1);
        queue.addLast(2);
        queue.addLast(3);
        queue.addLast(4);

        assertEquals(4, queue.size);
        assertEquals(3, queue.indexOf(4, true));
        assertEquals(4, (Object)queue.removeLast());

        assertEquals(3, queue.size);
        assertEquals(2, queue.indexOf(3, true));
        assertEquals(3, (Object)queue.removeLast());

        assertEquals(2, queue.size);
        assertEquals(1, queue.indexOf(2, true));
        assertEquals(2, (Object)queue.removeLast());

        assertEquals(1, queue.size);
        assertEquals(0, queue.indexOf(1, true));
        assertEquals(1, (Object)queue.removeLast());

        assertEquals(0, queue.size);
    }

    @Test
    public void removeFirstTest () {
        Queue<Integer> queue = new Queue<Integer>();
        queue.addLast(1);
        queue.addLast(2);
        queue.addLast(3);
        queue.addLast(4);

        assertEquals(4, queue.size);
        assertEquals(0, queue.indexOf(1, true));
        assertEquals(1, (Object)queue.removeFirst());

        assertEquals(3, queue.size);
        assertEquals(0, queue.indexOf(2, true));
        assertEquals(2, (Object)queue.removeFirst());

        assertEquals(2, queue.size);
        assertEquals(0, queue.indexOf(3, true));
        assertEquals(3, (Object)queue.removeFirst());

        assertEquals(1, queue.size);
        assertEquals(0, queue.indexOf(4, true));
        assertEquals(4, (Object)queue.removeFirst());

        assertEquals(0, queue.size);
    }

    @Test
    public void resizableQueueTest () {
        final Queue<Integer> q = new Queue<Integer>(8);

        assertTrue("New queue is not empty!", q.size == 0);

        for (int i = 0; i < 100; i++) {

            for (int j = 0; j < i; j++) {
                try {
                    q.addLast(j);
                } catch (IllegalStateException e) {
                    fail("Failed to add element " + j + " (" + i + ")");
                }
                final Integer peeked = q.last();
                assertTrue("peekLast shows " + peeked + ", should be " + j + " (" + i + ")", peeked.equals(j));
                final int size = q.size;
                assertTrue("Size should be " + (j + 1) + " but is " + size + " (" + i + ")", size == j + 1);
            }

            if (i != 0) {
                final Integer peek = q.first();
                assertTrue("First thing is not zero but " + peek + " (" + i + ")", peek == 0);
            }

            for (int j = 0; j < i; j++) {
                final Integer pop = q.removeFirst();
                assertTrue("Popped should be " + j + " but is " + pop + " (" + i + ")", pop == j);

                final int size = q.size;
                assertTrue("Size should be " + (i - 1 - j) + " but is " + size + " (" + i + ")", size == i - 1 - j);
            }

            assertTrue("Not empty after cycle " + i, q.size == 0);
        }

        for (int i = 0; i < 56; i++) {
            q.addLast(42);
        }
        q.clear();
        assertTrue("Clear did not clear properly", q.size == 0);
    }

    /** Same as resizableQueueTest, but in reverse */
    @Test
    public void resizableDequeTest () {
        final Queue<Integer> q = new Queue<Integer>(8);

        assertTrue("New deque is not empty!", q.size == 0);

        for (int i = 0; i < 100; i++) {

            for (int j = 0; j < i; j++) {
                try {
                    q.addFirst(j);
                } catch (IllegalStateException e) {
                    fail("Failed to add element " + j + " (" + i + ")");
                }
                final Integer peeked = q.first();
                assertTrue("peek shows " + peeked + ", should be " + j + " (" + i + ")", peeked.equals(j));
                final int size = q.size;
                assertTrue("Size should be " + (j + 1) + " but is " + size + " (" + i + ")", size == j + 1);
            }

            if (i != 0) {
                final Integer peek = q.last();
                assertTrue("Last thing is not zero but " + peek + " (" + i + ")", peek == 0);
            }

            for (int j = 0; j < i; j++) {
                final Integer pop = q.removeLast();
                assertTrue("Popped should be " + j + " but is " + pop + " (" + i + ")", pop == j);

                final int size = q.size;
                assertTrue("Size should be " + (i - 1 - j) + " but is " + size + " (" + i + ")", size == i - 1 - j);
            }

            assertTrue("Not empty after cycle " + i, q.size == 0);
        }

        for (int i = 0; i < 56; i++) {
            q.addFirst(42);
        }
        q.clear();
        assertTrue("Clear did not clear properly", q.size == 0);
    }

    @Test
    public void getTest () {
        final Queue<Integer> q = new Queue<Integer>(7);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                q.addLast(j);
            }
            assertEquals("get(0) is not equal to peek (" + i + ")", q.get(0), q.first());
            assertEquals("get(size-1) is not equal to peekLast (" + i + ")", q.get(q.size - 1), q.last());
            for (int j = 0; j < 4; j++) {
                assertTrue(q.get(j) == j);
            }
            for (int j = 0; j < 4 - 1; j++) {
                q.removeFirst();
                assertEquals("get(0) is not equal to peek (" + i + ")", q.get(0), q.first());
            }
            q.removeFirst();
            assertEquals("Queue should be empty", 0, q.size);
            try {
                q.get(0);
                fail("get() on empty queue did not throw");
            } catch (IndexOutOfBoundsException ignore) {
                // Expected
            }
        }
    }

    @Test
    public void removeTest () {
        final Queue<Integer> q = new Queue<Integer>();

        // Test head < tail.
        for (int j = 0; j <= 6; j++)
            q.addLast(j);
        assertValues(q, 0, 1, 2, 3, 4, 5, 6);
        q.removeIndex(0);
        assertValues(q, 1, 2, 3, 4, 5, 6);
        q.removeIndex(1);
        assertValues(q, 1, 3, 4, 5, 6);
        q.removeIndex(4);
        assertValues(q, 1, 3, 4, 5);
        q.removeIndex(2);
        assertValues(q, 1, 3, 5);

        // Test head >= tail and index >= head.
        q.clear();
        for (int j = 2; j >= 0; j--)
            q.addFirst(j);
        for (int j = 3; j <= 6; j++)
            q.addLast(j);
        assertValues(q, 0, 1, 2, 3, 4, 5, 6);
        q.removeIndex(1);
        assertValues(q, 0, 2, 3, 4, 5, 6);
        q.removeIndex(0);
        assertValues(q, 2, 3, 4, 5, 6);

        // Test head >= tail and index < tail.
        q.clear();
        for (int j = 2; j >= 0; j--)
            q.addFirst(j);
        for (int j = 3; j <= 6; j++)
            q.addLast(j);
        assertValues(q, 0, 1, 2, 3, 4, 5, 6);
        q.removeIndex(5);
        assertValues(q, 0, 1, 2, 3, 4, 6);
        q.removeIndex(5);
        assertValues(q, 0, 1, 2, 3, 4);
    }

    @Test
    public void indexOfTest () {
        final Queue<Integer> q = new Queue<Integer>();

        // Test head < tail.
        for (int j = 0; j <= 6; j++)
            q.addLast(j);
        for (int j = 0; j <= 6; j++)
            assertEquals(q.indexOf(j, false), j);

        // Test head >= tail.
        q.clear();
        for (int j = 2; j >= 0; j--)
            q.addFirst(j);
        for (int j = 3; j <= 6; j++)
            q.addLast(j);
        for (int j = 0; j <= 6; j++)
            assertEquals(q.indexOf(j, false), j);
    }

    @Test
    public void iteratorTest () {
        final Queue<Integer> q = new Queue<Integer>();

        // Test head < tail.
        for (int j = 0; j <= 6; j++)
            q.addLast(j);
        Iterator<Integer> iter = q.iterator();
        for (int j = 0; j <= 6; j++)
            assertEquals(iter.next().intValue(), j);
        iter = q.iterator();
        iter.next();
        iter.remove();
        assertValues(q, 1, 2, 3, 4, 5, 6);
        iter.next();
        iter.remove();
        assertValues(q, 2, 3, 4, 5, 6);
        iter.next();
        iter.next();
        iter.remove();
        assertValues(q, 2, 4, 5, 6);
        iter.next();
        iter.next();
        iter.next();
        iter.remove();
        assertValues(q, 2, 4, 5);

        // Test head >= tail.
        q.clear();
        for (int j = 2; j >= 0; j--)
            q.addFirst(j);
        for (int j = 3; j <= 6; j++)
            q.addLast(j);
        iter = q.iterator();
        for (int j = 0; j <= 6; j++)
            assertEquals(iter.next().intValue(), j);
        iter = q.iterator();
        iter.next();
        iter.remove();
        assertValues(q, 1, 2, 3, 4, 5, 6);
        iter.next();
        iter.remove();
        assertValues(q, 2, 3, 4, 5, 6);
        iter.next();
        iter.next();
        iter.remove();
        assertValues(q, 2, 4, 5, 6);
        iter.next();
        iter.next();
        iter.next();
        iter.remove();
        assertValues(q, 2, 4, 5);
    }

    @Test
    public void iteratorRemoveEdgeCaseTest () {// See #4300
        Queue<Integer> queue = new Queue<Integer>();

        // Simulate normal usage
        for (int i = 0; i < 100; i++) {
            queue.addLast(i);
            if (i > 50) queue.removeFirst();
        }

        Iterator<Integer> it = queue.iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }

        queue.addLast(1337);

        Integer i = queue.first();
        assertEquals(1337, (int)i);
    }

    @Test
    public void toStringTest () {
        Queue<Integer> q = new Queue<Integer>(1);
        assertTrue(q.toString().equals("[]"));
        q.addLast(4);
        assertTrue(q.toString().equals("[4]"));
        q.addLast(5);
        q.addLast(6);
        q.addLast(7);
        assertTrue(q.toString().equals("[4, 5, 6, 7]"));
    }

    @Test
    public void hashEqualsTest () {
        Queue<Integer> q1 = new Queue<Integer>();
        Queue<Integer> q2 = new Queue<Integer>();

        assertEqualsAndHash(q1, q2);
        q1.addFirst(1);
        assertNotEquals(q1, q2);
        q2.addFirst(1);
        assertEqualsAndHash(q1, q2);

        q1.clear();
        q1.addLast(1);
        q1.addLast(2);
        q2.addLast(2);
        assertEqualsAndHash(q1, q2);

        for (int i = 0; i < 100; i++) {
            q1.addLast(i);
            q1.addLast(i);
            q1.removeFirst();

            assertNotEquals(q1, q2);

            q2.addLast(i);
            q2.addLast(i);
            q2.removeFirst();

            assertEqualsAndHash(q1, q2);
        }
    }

    private void assertEqualsAndHash (Queue<?> q1, Queue<?> q2) {
        assertEquals(q1, q2);
        assertEquals("Hash codes are not equal", q1.hashCode(), q2.hashCode());
    }

    private void assertValues (Queue<Integer> q, Integer... values) {
        for (int i = 0, n = values.length; i < n; i++) {
            Assert.assertEquals(values[i], q.get(i));
        }
    }

    /**
     * Additional tests to improve mutation coverage and edge case handling.
     * Tests cover:
     * - Constructor variations
     * - Boundary conditions and exceptions
     * - Null value handling
     * - Iterator edge cases
     * - Wrapped queue operations
     * - Hash code and equals consistency
     */

    // ========== ADDITIONAL TESTS FOR IMPROVED MUTATION COVERAGE ==========

    /**
     * Test constructors with different initial sizes and verify initial state.
     * Test that queue can grow beyond initial size.
     */
    @Test
    public void constructorTests () {
        // Test default constructor
        Queue<Integer> q1 = new Queue<Integer>();
        assertEquals(0, q1.size);
        assertTrue(q1.isEmpty());

        // Test constructor with initial size
        Queue<Integer> q2 = new Queue<Integer>(32);
        assertEquals(0, q2.size);
        assertTrue(q2.isEmpty());

        // Test that queue can grow beyond initial size
        for (int i = 0; i < 50; i++) {
            q2.addLast(i);
        }
        assertEquals(50, q2.size);
    }


    /**
     * Tests the isEmpty and notEmpty methods.
     * Verifies that:
     *   A newly created queue reports as empty
     *   A queue with elements reports as not empty
     *   A queue returns to empty state after removing all elements
     */
    @Test
    public void isEmptyAndNotEmptyTest () {
        Queue<Integer> q = new Queue<Integer>();

        // Test empty queue
        assertTrue("New queue should be empty", q.isEmpty());
        assertFalse("New queue should not be notEmpty", q.notEmpty());

        // Test after adding
        q.addLast(1);
        assertFalse("Queue with items should not be empty", q.isEmpty());
        assertTrue("Queue with items should be notEmpty", q.notEmpty());

        // Test after removing
        q.removeFirst();
        assertTrue("Queue after removing all should be empty", q.isEmpty());
        assertFalse("Queue after removing all should not be notEmpty", q.notEmpty());
    }

    /**
     * Tests the ensureCapacity method to verify proper capacity management.
     * Verifies that:
     *   The queue can be pre-allocated to avoid multiple resizes
     *   Adding elements after ensuring capacity does not trigger additional resizing
     *   All elements maintain their correct order and values after capacity increase
     *   The queue size is correctly maintained throughout capacity operations
     */
    @Test
    public void ensureCapacityTest () {
        Queue<Integer> q = new Queue<Integer>(4);

        // Add items up to initial capacity
        q.addLast(1);
        q.addLast(2);
        q.addLast(3);
        q.addLast(4);

        // Ensure capacity for 10 more items
        q.ensureCapacity(10);

        // Add 10 more items without resize
        for (int i = 5; i <= 14; i++) {
            q.addLast(i);
        }

        assertEquals(14, q.size);
        for (int i = 0; i < 14; i++) {
            assertEquals(i + 1, (int)q.get(i));
        }
    }

    /**
     * Tests the removeValue method to verify correct removal of elements by value.
     * Verifies that:
     *   Existing values are removed correctly
     *   Non-existing values do not affect the queue
     *   Identity comparison works as expected
     *   Removal works correctly in wrapped queues
     */
    @Test
    public void removeValueTest () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.addLast(2);
        q.addLast(3);
        q.addLast(2);
        q.addLast(4);

        // Test removing existing value (identity=false, uses equals)
        assertTrue("Should remove value 2", q.removeValue(2, false));
        assertValues(q, 1, 3, 2, 4);
        assertEquals(4, q.size);

        // Test removing non-existing value
        assertFalse("Should not remove non-existing value 99", q.removeValue(99, false));
        assertEquals(4, q.size);

        // Test removing with identity comparison
        Integer two = Integer.valueOf(2);
        q.removeValue(two, false);
        assertValues(q, 1, 3, 4);

        // Test removing from wrapped queue
        q.clear();
        for (int i = 0; i < 3; i++) {
            q.addFirst(i);
        }
        for (int i = 3; i < 6; i++) {
            q.addLast(i);
        }
        assertTrue(q.removeValue(4, false));
        assertEquals(5, q.size);
    }

    // ========== EXCEPTION TESTS ==========
    @Test(expected = NoSuchElementException.class)
    public void firstOnEmptyQueueThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.first(); // Should throw NoSuchElementException
    }

    @Test(expected = NoSuchElementException.class)
    public void lastOnEmptyQueueThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.last(); // Should throw NoSuchElementException
    }

    @Test(expected = NoSuchElementException.class)
    public void removeFirstOnEmptyQueueThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.removeFirst(); // Should throw NoSuchElementException
    }

    @Test(expected = NoSuchElementException.class)
    public void removeLastOnEmptyQueueThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.removeLast(); // Should throw NoSuchElementException
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWithNegativeIndexThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.get(-1); // Should throw IndexOutOfBoundsException
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getWithIndexTooLargeThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.get(5); // Should throw IndexOutOfBoundsException
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeIndexWithNegativeIndexThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.removeIndex(-1); // Should throw IndexOutOfBoundsException
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeIndexWithIndexTooLargeThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.removeIndex(5); // Should throw IndexOutOfBoundsException
    }

    /**
     * Tests the toString method with custom separator functionality.
     * Verifies that:
     * The queue correctly formats its contents as a string using the provided separator
     * Empty queues return empty strings
     * Single elements are formatted without separators
     * Multiple elements are properly joined with the specified separator
     * Different separator characters work correctly
     * Wrapped queues maintain proper order in output
     */
    @Test
    public void toStringWithSeparatorTest () {
        Queue<Integer> q = new Queue<Integer>();

        // Test empty queue
        assertEquals("Empty queue should return empty string", "", q.toString(", "));

        // Test single element
        q.addLast(1);
        assertEquals("1", q.toString(", "));

        // Test multiple elements
        q.addLast(2);
        q.addLast(3);
        assertEquals("1, 2, 3", q.toString(", "));

        // Test with different separator
        assertEquals("1-2-3", q.toString("-"));

        // Test with wrapped queue
        q.clear();
        for (int i = 2; i >= 0; i--) {
            q.addFirst(i);
        }
        for (int i = 3; i <= 5; i++) {
            q.addLast(i);
        }
        assertEquals("0|1|2|3|4|5", q.toString("|"));
    }

    /**
     * Tests the equalsIdentity method for identity-based comparison.
     * Verifies that:
     *   Empty queues are equal
     *   Same reference returns true
     *   Queues with same object instances are equal
     *   Queues with different instances (same content) are not equal
     *   Null comparison returns false
     *   Comparison with different types returns false
     *   Queues with different sizes are not equal
     */
    @Test
    public void equalsIdentityTest () {
        Queue<String> q1 = new Queue<String>();
        Queue<String> q2 = new Queue<String>();

        // Test empty queues
        assertTrue("Empty queues should be equal with identity", q1.equalsIdentity(q2));

        // Test same reference
        assertTrue("Same reference should be equal", q1.equalsIdentity(q1));

        // Test with same string instances
        String s1 = "test";
        String s2 = s1; // Same instance
        q1.addLast(s1);
        q2.addLast(s2);
        assertTrue("Queues with same instances should be equal", q1.equalsIdentity(q2));

        // Test with different string instances (same content)
        q1.clear();
        q2.clear();
        q1.addLast(new String("test"));
        q2.addLast(new String("test"));
        assertFalse("Queues with different instances should not be equal with identity", q1.equalsIdentity(q2));

        // Test with null
        assertFalse("Queue should not equal null", q1.equalsIdentity(null));

        // Test with different type
        assertFalse("Queue should not equal different type", q1.equalsIdentity("string"));

        // Test with different sizes
        q1.addLast("extra");
        assertFalse("Queues with different sizes should not be equal", q1.equalsIdentity(q2));
    }

    /**
     * Tests null value handling across all queue operations.
     * Verifies that:
     *   Null values can be added, retrieved, searched, and removed properly
     *   Queue maintains correct size with null elements
     *   indexOf works with null values using both identity and equals comparison
     *   Removal operations correctly handle null returns
     */
    @Test
    public void nullValueHandlingTest () {
        Queue<Integer> q = new Queue<Integer>();

        // Test adding null values
        q.addLast(null);
        q.addLast(1);
        q.addLast(null);
        q.addLast(2);

        assertEquals(4, q.size);
        assertNull("First element should be null", q.get(0));
        assertEquals(Integer.valueOf(1), q.get(1));
        assertNull("Third element should be null", q.get(2));
        assertEquals(Integer.valueOf(2), q.get(3));

        // Test indexOf with null (identity=true)
        assertEquals("indexOf null with identity should be 0", 0, q.indexOf(null, true));

        // Test indexOf with null (identity=false)
        assertEquals("indexOf null without identity should be 0", 0, q.indexOf(null, false));

        // Test removeValue with null
        assertTrue("Should remove null value", q.removeValue(null, false));
        assertEquals(3, q.size);

        // Test addFirst with null
        q.addFirst(null);
        assertNull("First element should be null", q.first());

        // Test removeLast returning null
        q.clear();
        q.addLast(null);
        assertNull("removeLast should return null", q.removeLast());
    }

    /**
     * Tests the QueueIterator reset method for restarting iteration.
     * Verifies that:
     *   Reset allows re-iteration from the beginning
     *   hasNext returns correct state after reset
     *   All elements are accessible again in proper order after reset
     */
    @Test
    public void iteratorResetTest () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.addLast(2);
        q.addLast(3);

        Queue.QueueIterator<Integer> iter = new Queue.QueueIterator<Integer>(q);

        // Iterate through all elements
        assertEquals(1, (int)iter.next());
        assertEquals(2, (int)iter.next());
        assertEquals(3, (int)iter.next());
        assertFalse("Should have no more elements", iter.hasNext());

        // Reset and iterate again
        iter.reset();
        assertTrue("Should have elements after reset", iter.hasNext());
        assertEquals(1, (int)iter.next());
        assertEquals(2, (int)iter.next());
        assertEquals(3, (int)iter.next());
    }

    @Test(expected = GdxRuntimeException.class)
    public void iteratorRemoveNotAllowedThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.addLast(2);

        Queue.QueueIterator<Integer> iter = new Queue.QueueIterator<Integer>(q, false); // allowRemove=false
        iter.next();
        iter.remove(); // Should throw GdxRuntimeException
    }

    @Test(expected = NoSuchElementException.class)
    public void iteratorNextBeyondSizeThrowsException () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);

        Iterator<Integer> iter = q.iterator();
        iter.next(); // OK
        iter.next(); // Should throw NoSuchElementException
    }

    /**
     * Tests the indexOf method when values are not found in the queue.
     * Verifies that:
     *   indexOf returns -1 for non-existing values with identity comparison
     *   indexOf returns -1 for non-existing values with equals comparison
     *   indexOf returns -1 when searching empty queues
     */
    @Test
    public void indexOfNotFoundTest () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.addLast(2);
        q.addLast(3);

        // Test indexOf with non-existing value
        assertEquals("indexOf non-existing value should return -1", -1, q.indexOf(99, false));
        assertEquals("indexOf non-existing value should return -1 (identity)", -1, q.indexOf(99, true));

        // Test on empty queue
        q.clear();
        assertEquals("indexOf on empty queue should return -1", -1, q.indexOf(1, false));
    }

    /**
     * Tests queue operations when the internal array is wrapped (head > tail).
     * Verifies that:
     *   get, first, and last methods work correctly in wrapped state
     *   removeIndex works correctly when elements wrap around the backing array
     *   Element order and values are maintained properly in wrapped state
     */
    @Test
    public void wrappedQueueOperationsTest () {
        Queue<Integer> q = new Queue<Integer>(8);

        // Create wrapped queue (head > tail)
        for (int i = 2; i >= 0; i--) {
            q.addFirst(i);
        }
        for (int i = 3; i <= 5; i++) {
            q.addLast(i);
        }

        // Verify values
        assertValues(q, 0, 1, 2, 3, 4, 5);

        // Test get on wrapped queue
        assertEquals(0, (int)q.get(0));
        assertEquals(3, (int)q.get(3));
        assertEquals(5, (int)q.get(5));

        // Test first and last on wrapped queue
        assertEquals(0, (int)q.first());
        assertEquals(5, (int)q.last());

        // Test removeIndex in wrapped portion
        q.removeIndex(4); // Remove 4
        assertValues(q, 0, 1, 2, 3, 5);
    }

    /**
     * Tests the clear method on empty and populated queues.
     * Verifies that:
     *   Clearing empty queues does not throw exceptions
     *   Populated queues are properly emptied
     *   Multiple consecutive clear operations are handled safely
     */
    @Test
    public void clearOnEmptyQueueTest () {
        Queue<Integer> q = new Queue<Integer>();

        // Clear empty queue (should not throw)
        q.clear();
        assertEquals(0, q.size);
        assertTrue(q.isEmpty());

        // Add items, clear, verify empty
        q.addLast(1);
        q.addLast(2);
        q.clear();
        assertEquals(0, q.size);
        assertTrue(q.isEmpty());

        // Clear again (should not throw)
        q.clear();
        assertEquals(0, q.size);
    }

    /**
     * Tests hash code consistency and equality contract for queues.
     * Verifies that:
     *   Equal queues produce identical hash codes
     *   Hash codes remain consistent across multiple calls
     *   Empty queues have matching hash codes
     *   Null values are properly handled in hash code calculation
     */
    @Test
    public void hashCodeConsistencyTest () {
        Queue<Integer> q1 = new Queue<Integer>();
        Queue<Integer> q2 = new Queue<Integer>();

        // Equal empty queues should have same hash
        int hash1 = q1.hashCode();
        int hash2 = q2.hashCode();
        assertEquals("Equal queues should have same hash code", hash1, hash2);

        // Add same elements
        q1.addLast(1);
        q1.addLast(2);
        q2.addLast(1);
        q2.addLast(2);

        hash1 = q1.hashCode();
        hash2 = q2.hashCode();
        assertEquals("Equal queues should have same hash code", hash1, hash2);

        // Hash should be consistent across multiple calls
        assertEquals("Hash code should be consistent", hash1, q1.hashCode());

        // Test with null values
        q1.addLast(null);
        q2.addLast(null);
        assertEquals("Equal queues with null should have same hash code", q1.hashCode(), q2.hashCode());
    }

    /**
     * Tests the equals method with various object types and null values.
     * Verifies that:
     *   equals returns false for null objects
     *   equals returns false for non-Queue objects
     *   equals returns true for same reference
     *   Queues containing null values are correctly compared
     *   Queues with null and non-null elements at same position are properly distinguished
     */
    @Test
    public void equalsWithDifferentTypesTest () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);

        // Test equals with null
        assertFalse("Queue should not equal null", q.equals(null));

        // Test equals with non-Queue object
        assertFalse("Queue should not equal String", q.equals("not a queue"));

        // Test equals with itself
        assertTrue("Queue should equal itself", q.equals(q));

        // Test with null values in queue
        Queue<Integer> q1 = new Queue<Integer>();
        Queue<Integer> q2 = new Queue<Integer>();
        q1.addLast(null);
        q2.addLast(null);
        assertTrue("Queues with null values should be equal", q1.equals(q2));

        // Test mismatched null and non-null
        q2.removeFirst();
        q2.addLast(1);
        assertFalse("Queue with null should not equal queue with non-null", q1.equals(q2));
    }

    /**
     * Tests the ensureCapacity method at boundary conditions.
     * Verifies that:
     *   ensureCapacity with zero additional capacity does not resize
     *   Requesting capacity for additional elements properly resizes the backing array
     *   Elements can be added after capacity increase without further resizing
     *   All values remain correct and accessible after capacity operations
     */
    @Test
    public void ensureCapacityBoundaryTest () {
        Queue<Integer> q = new Queue<Integer>(4);

        // Fill to capacity
        for (int i = 0; i < 4; i++) {
            q.addLast(i);
        }
        assertEquals(4, q.size);

        // ensureCapacity with exact current size (should not resize)
        q.ensureCapacity(0);
        assertEquals(4, q.size);

        // ensureCapacity that requires resize
        q.ensureCapacity(10);

        // Verify we can add more without resize
        for (int i = 4; i < 14; i++) {
            q.addLast(i);
        }

        assertEquals(14, q.size);

        // Verify all values are correct
        for (int i = 0; i < 14; i++) {
            assertEquals(i, (int)q.get(i));
        }
    }

    /**
     * Tests the indexOf method at various boundary positions in the queue.
     * Verifies that:
     *   indexOf correctly locates elements at head position
     *   indexOf correctly locates elements at tail-1 position
     *   indexOf correctly locates elements at middle position
     *   indexOf works correctly in wrapped queues where elements span across the array boundary
     *   Index calculation is correct for both continuous and wrapped queue configurations
     */
    @Test
    public void indexOfBoundaryPositionsTest () {
        Queue<Integer> q = new Queue<Integer>();

        // Test indexOf at different positions
        for (int i = 0; i < 10; i++) {
            q.addLast(i);
        }

        // Test head position
        assertEquals("indexOf at head should be 0", 0, q.indexOf(0, false));

        // Test tail-1 position
        assertEquals("indexOf at tail-1 should be 9", 9, q.indexOf(9, false));

        // Test middle position
        assertEquals("indexOf at middle should be 5", 5, q.indexOf(5, false));

        // Test wrapped queue indexOf
        q.clear();
        for (int i = 5; i >= 0; i--) {
            q.addFirst(i);
        }
        for (int i = 6; i < 10; i++) {
            q.addLast(i);
        }

        // Test indexOf in wrapped portion
        assertEquals("indexOf in wrapped head portion", 0, q.indexOf(0, false));
        assertEquals("indexOf in wrapped tail portion", 9, q.indexOf(9, false));
        assertEquals("indexOf in wrapped middle", 5, q.indexOf(5, false));
    }

    /**
     * Tests the removeIndex method's return value functionality.
     * Verifies that:
     *   removeIndex correctly returns the removed element
     *   Queue maintains proper size after removal
     *   Element order is preserved after removal in continuous arrays
     *   Removal works correctly in wrapped queue configurations where elements span the array boundary
     */
    @Test
    public void removeIndexReturnValueTest () {
        Queue<Integer> q = new Queue<Integer>();

        // Test removeIndex returns correct value
        q.addLast(10);
        q.addLast(20);
        q.addLast(30);
        q.addLast(40);

        assertEquals("removeIndex should return removed value", Integer.valueOf(20), q.removeIndex(1));
        assertEquals(3, q.size);
        assertValues(q, 10, 30, 40);

        assertEquals("removeIndex should return removed value", Integer.valueOf(30), q.removeIndex(1));
        assertEquals(2, q.size);
        assertValues(q, 10, 40);

        // Test removeIndex in wrapped queue
        q.clear();
        for (int i = 3; i >= 0; i--) {
            q.addFirst(i);
        }
        for (int i = 4; i < 7; i++) {
            q.addLast(i);
        }

        assertEquals("removeIndex in wrapped queue", Integer.valueOf(3), q.removeIndex(3));
        assertEquals(6, q.size);
    }

    /**
     * Tests the clear method on wrapped queues where elements span array boundaries.
     * Verifies that:
     *   Clearing a wrapped queue (head > tail) properly nullifies all elements in both array segments
     *   Head and tail indices are reset to zero after clearing
     *   Size is set to zero after clearing
     *   Queue is left in a valid empty state that can accept new elements without issues
     */
    @Test
    public void clearWrappedQueueTest () {
        Queue<Integer> q = new Queue<Integer>(8);

        // Create wrapped queue
        for (int i = 3; i >= 0; i--) {
            q.addFirst(i);
        }
        for (int i = 4; i < 7; i++) {
            q.addLast(i);
        }

        assertEquals(7, q.size);

        // Clear wrapped queue
        q.clear();

        // Verify complete clear
        assertEquals(0, q.size);
        assertEquals(0, q.head);
        assertEquals(0, q.tail);
        assertTrue(q.isEmpty());

        // Verify we can use queue after clear
        q.addLast(100);
        assertEquals(1, q.size);
        assertEquals(100, (int)q.first());
    }

    /**
     * Tests the iterator behavior in non-allocating mode where iterator instances are reused.
     * Verifies that:
     *   Collections.allocateIterators=false enables iterator pooling/reuse
     *   Iterators can be used sequentially without interference when pooling is enabled
     *   Each iterator properly iterates through all elements from the beginning
     *   Original allocateIterators setting is restored via finally block after test completion
     */
    @Test
    public void iteratorWithNonAllocatingModeTest () {
        Queue<Integer> q = new Queue<Integer>();
        q.addLast(1);
        q.addLast(2);
        q.addLast(3);

        // Save original setting
        boolean originalSetting = Collections.allocateIterators;

        try {
            // Test with allocateIterators = false (enables iterator pooling)
            Collections.allocateIterators = false;

            // Use first iterator completely
            Iterator<Integer> iter1 = q.iterator();
            assertTrue(iter1.hasNext());
            assertEquals(1, (int)iter1.next());
            assertEquals(2, (int)iter1.next());
            assertEquals(3, (int)iter1.next());
            assertFalse(iter1.hasNext());

            // Get a new iterator (should work correctly with pooling enabled)
            Iterator<Integer> iter2 = q.iterator();
            assertTrue(iter2.hasNext());
            assertEquals(1, (int)iter2.next());
            assertEquals(2, (int)iter2.next());
            assertEquals(3, (int)iter2.next());
            assertFalse(iter2.hasNext());

        } finally {
            // Restore original setting
            Collections.allocateIterators = originalSetting;
        }
    }

    /**
     * Tests the hashCode method with different values and configurations.
     * Verifies that:
     *   Queues with different content produce different hash codes
     *   Queues with null values produce different hash codes than non-null values
     *   Hash code calculation is consistent regardless of wrapped state (head > tail) or continuous state (head < tail)
     *   Logically equivalent queues with same values produce identical hash codes even when internal array layout differs
     */
    @Test
    public void hashCodeDifferentValuesTest () {
        Queue<Integer> q1 = new Queue<Integer>();
        Queue<Integer> q2 = new Queue<Integer>();

        q1.addLast(1);
        q1.addLast(2);

        q2.addLast(1);
        q2.addLast(3); // Different value

        // Different content should produce different hash (not guaranteed but very likely)
        assertNotEquals("Different queues should have different hash codes", q1.hashCode(), q2.hashCode());

        // Test with null producing different hash
        Queue<Integer> q3 = new Queue<Integer>();
        Queue<Integer> q4 = new Queue<Integer>();

        q3.addLast(1);
        q4.addLast(null);

        assertNotEquals("Queue with value vs null should have different hash", q3.hashCode(), q4.hashCode());

        // Test wrapped queue hash code
        Queue<Integer> q5 = new Queue<Integer>(4);
        for (int i = 0; i < 3; i++) {
            q5.addLast(i);
        }

        Queue<Integer> q6 = new Queue<Integer>(8);
        // Create wrapped queue with same values
        q6.addFirst(2);
        q6.addFirst(1);
        q6.addFirst(0);

        assertEquals("Same values should have same hash regardless of wrapping", q5.hashCode(), q6.hashCode());
    }

    /**
     * Tests the automatic resizing behavior of the queue at capacity boundaries.
     * Verifies that:
     *   Adding elements beyond initial capacity triggers automatic resizing
     *   All elements maintain correct values and order after resize
     *   Resizing works correctly when elements are added sequentially via addLast
     *   Resizing properly handles wrapped queues (head > tail) where elements span the array boundary
     *   Queue remains in a valid state with proper element ordering after resize
     */
    @Test
    public void resizeBoundaryTest () {
        Queue<Integer> q = new Queue<Integer>(4);

        // Fill to capacity
        for (int i = 0; i < 4; i++) {
            q.addLast(i);
        }

        // Add one more to trigger resize
        q.addLast(4);
        assertEquals(5, q.size);

        // Verify all values correct after resize
        for (int i = 0; i < 5; i++) {
            assertEquals(i, (int)q.get(i));
        }

        // Test resize with wrapped queue
        Queue<Integer> q2 = new Queue<Integer>(4);
        q2.addFirst(2);
        q2.addFirst(1);
        q2.addFirst(0);
        q2.addLast(3);

        // Now queue is wrapped, add one more to force resize
        q2.addLast(4);
        assertEquals(5, q2.size);

        // Verify all values correct after resize from wrapped state
        for (int i = 0; i < 5; i++) {
            assertEquals(i, (int)q2.get(i));
        }
    }
}
