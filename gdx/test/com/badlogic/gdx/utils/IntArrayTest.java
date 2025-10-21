package com.badlogic.gdx.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Comprehensive test suite for IntArray class.
 * Tests cover constructors, add/remove operations, searching, sorting,
 * capacity management, and edge cases for both ordered and unordered arrays.
 */
public class IntArrayTest {

	/**
	 * Tests default constructor creates an empty ordered array.
	 */
	@Test
	public void testDefaultConstructor () {
		IntArray array = new IntArray();
		assertEquals(0, array.size);
		assertTrue(array.ordered);
		assertNotNull(array.items);
	}

	/**
	 * Tests constructor with capacity creates array with specified initial capacity.
	 */
	@Test
	public void testConstructorWithCapacity () {
		IntArray array = new IntArray(32);
		assertEquals(0, array.size);
		assertTrue(array.ordered);
		assertTrue(array.items.length >= 32);
	}

	/**
	 * Tests constructor with ordered flag and capacity.
	 */
	@Test
	public void testConstructorWithOrderedAndCapacity () {
		IntArray orderedArray = new IntArray(true, 10);
		assertTrue(orderedArray.ordered);
		assertEquals(0, orderedArray.size);

		IntArray unorderedArray = new IntArray(false, 10);
		assertFalse(unorderedArray.ordered);
		assertEquals(0, unorderedArray.size);
	}

	/**
	 * Tests copy constructor creates identical but separate array.
	 */
	@Test
	public void testCopyConstructor () {
		IntArray original = new IntArray();
		original.add(1);
		original.add(2);
		original.add(3);

		IntArray copy = new IntArray(original);
		assertEquals(original.size, copy.size);
		assertEquals(original.ordered, copy.ordered);

		// Verify values are copied
		for (int i = 0; i < original.size; i++) {
			assertEquals(original.get(i), copy.get(i));
		}

		// Verify they are separate arrays
		copy.add(4);
		assertEquals(3, original.size);
		assertEquals(4, copy.size);
	}

	/**
	 * Tests constructor from int array.
	 */
	@Test
	public void testConstructorFromArray () {
		int[] values = {5, 10, 15, 20};
		IntArray array = new IntArray(values);

		assertEquals(4, array.size);
		assertTrue(array.ordered);
		assertEquals(5, array.get(0));
		assertEquals(10, array.get(1));
		assertEquals(15, array.get(2));
		assertEquals(20, array.get(3));
	}

	/**
	 * Tests constructor from array with ordered flag, start index and count.
	 */
	@Test
	public void testConstructorFromArrayWithRange () {
		int[] values = {1, 2, 3, 4, 5};
		IntArray array = new IntArray(false, values, 1, 3);

		assertEquals(3, array.size);
		assertFalse(array.ordered);
		assertEquals(2, array.get(0));
		assertEquals(3, array.get(1));
		assertEquals(4, array.get(2));
	}

	/**
	 * Tests static factory method with().
	 */
	@Test
	public void testWithFactoryMethod () {
		IntArray array = IntArray.with(7, 8, 9);
		assertEquals(3, array.size);
		assertEquals(7, array.get(0));
		assertEquals(8, array.get(1));
		assertEquals(9, array.get(2));
	}

	/**
	 * Tests add single value and automatic resizing.
	 */
	@Test
	public void testAddSingleValue () {
		IntArray array = new IntArray(2);
		array.add(1);
		array.add(2);
		array.add(3); // Should trigger resize

		assertEquals(3, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
	}

	/**
	 * Tests add two values at once.
	 */
	@Test
	public void testAddTwoValues () {
		IntArray array = new IntArray();
		array.add(10, 20);

		assertEquals(2, array.size);
		assertEquals(10, array.get(0));
		assertEquals(20, array.get(1));
	}

	/**
	 * Tests add three values at once.
	 */
	@Test
	public void testAddThreeValues () {
		IntArray array = new IntArray();
		array.add(5, 10, 15);

		assertEquals(3, array.size);
		assertEquals(5, array.get(0));
		assertEquals(10, array.get(1));
		assertEquals(15, array.get(2));
	}

	/**
	 * Tests add four values at once.
	 */
	@Test
	public void testAddFourValues () {
		IntArray array = new IntArray();
		array.add(1, 2, 3, 4);

		assertEquals(4, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
		assertEquals(4, array.get(3));
	}

	/**
	 * Tests addAll with another IntArray.
	 */
	@Test
	public void testAddAllIntArray () {
		IntArray array1 = new IntArray();
		array1.add(1);
		array1.add(2);

		IntArray array2 = new IntArray();
		array2.add(3);
		array2.add(4);

		array1.addAll(array2);

		assertEquals(4, array1.size);
		assertEquals(1, array1.get(0));
		assertEquals(2, array1.get(1));
		assertEquals(3, array1.get(2));
		assertEquals(4, array1.get(3));
	}

	/**
	 * Tests addAll with IntArray, offset and length.
	 */
	@Test
	public void testAddAllIntArrayWithOffsetAndLength () {
		IntArray array1 = new IntArray();
		array1.add(1);

		IntArray array2 = new IntArray();
		array2.add(10);
		array2.add(20);
		array2.add(30);
		array2.add(40);

		array1.addAll(array2, 1, 2);

		assertEquals(3, array1.size);
		assertEquals(1, array1.get(0));
		assertEquals(20, array1.get(1));
		assertEquals(30, array1.get(2));
	}

	/**
	 * Tests addAll with int varargs.
	 */
	@Test
	public void testAddAllVarargs () {
		IntArray array = new IntArray();
		array.add(1);
		array.addAll(2, 3, 4);

		assertEquals(4, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
		assertEquals(4, array.get(3));
	}

	/**
	 * Tests addAll with int array, offset and length.
	 */
	@Test
	public void testAddAllArrayWithOffsetAndLength () {
		IntArray array = new IntArray();
		array.add(1);

		int[] values = {10, 20, 30, 40, 50};
		array.addAll(values, 1, 3);

		assertEquals(4, array.size);
		assertEquals(1, array.get(0));
		assertEquals(20, array.get(1));
		assertEquals(30, array.get(2));
		assertEquals(40, array.get(3));
	}

	/**
	 * Tests get and set operations.
	 */
	@Test
	public void testGetAndSet () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);
		array.add(15);

		assertEquals(5, array.get(0));
		assertEquals(10, array.get(1));
		assertEquals(15, array.get(2));

		array.set(1, 100);
		assertEquals(100, array.get(1));
	}

	/**
	 * Tests increment at specific index.
	 */
	@Test
	public void testIncrementAtIndex () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);

		array.incr(0, 3);
		assertEquals(8, array.get(0));

		array.incr(1, -5);
		assertEquals(5, array.get(1));
	}

	/**
	 * Tests increment all values.
	 */
	@Test
	public void testIncrementAll () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);

		array.incr(10);

		assertEquals(11, array.get(0));
		assertEquals(12, array.get(1));
		assertEquals(13, array.get(2));
	}

	/**
	 * Tests multiply at specific index.
	 */
	@Test
	public void testMultiplyAtIndex () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);

		array.mul(0, 3);
		assertEquals(15, array.get(0));

		array.mul(1, 2);
		assertEquals(20, array.get(1));
	}

	/**
	 * Tests multiply all values.
	 */
	@Test
	public void testMultiplyAll () {
		IntArray array = new IntArray();
		array.add(2);
		array.add(3);
		array.add(4);

		array.mul(10);

		assertEquals(20, array.get(0));
		assertEquals(30, array.get(1));
		assertEquals(40, array.get(2));
	}

	/**
	 * Tests insert in ordered array maintains order.
	 */
	@Test
	public void testInsertOrdered () {
		IntArray array = new IntArray(true, 4);
		array.add(1);
		array.add(2);
		array.add(4);

		array.insert(2, 3);

		assertEquals(4, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
		assertEquals(4, array.get(3));
	}

	/**
	 * Tests insert in unordered array.
	 */
	@Test
	public void testInsertUnordered () {
		IntArray array = new IntArray(false, 4);
		array.add(1);
		array.add(2);
		array.add(3);

		array.insert(1, 99);

		assertEquals(4, array.size);
		assertEquals(99, array.get(1));
	}

	/**
	 * Tests insertRange creates space for new elements.
	 */
	@Test
	public void testInsertRange () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(5);

		array.insertRange(2, 2);

		assertEquals(5, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		// Element from index 2 was shifted to index 4
		assertEquals(5, array.get(4));
	}

	/**
	 * Tests swap exchanges two elements.
	 */
	@Test
	public void testSwap () {
		IntArray array = new IntArray();
		array.add(10);
		array.add(20);
		array.add(30);

		array.swap(0, 2);

		assertEquals(30, array.get(0));
		assertEquals(20, array.get(1));
		assertEquals(10, array.get(2));
	}

	/**
	 * Tests replaceFirst replaces first occurrence only.
	 */
	@Test
	public void testReplaceFirst () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);
		array.add(5);
		array.add(15);

		assertTrue(array.replaceFirst(5, 99));

		assertEquals(99, array.get(0));
		assertEquals(5, array.get(2)); // Second 5 unchanged

		assertFalse(array.replaceFirst(100, 200)); // Not found
	}

	/**
	 * Tests replaceAll replaces all occurrences.
	 */
	@Test
	public void testReplaceAll () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);
		array.add(5);
		array.add(5);

		int replacements = array.replaceAll(5, 99);

		assertEquals(3, replacements);
		assertEquals(99, array.get(0));
		assertEquals(10, array.get(1));
		assertEquals(99, array.get(2));
		assertEquals(99, array.get(3));

		assertEquals(0, array.replaceAll(100, 200)); // Not found
	}

	/**
	 * Tests contains finds existing values.
	 */
	@Test
	public void testContains () {
		IntArray array = new IntArray();
		array.add(10);
		array.add(20);
		array.add(30);

		assertTrue(array.contains(10));
		assertTrue(array.contains(20));
		assertTrue(array.contains(30));
		assertFalse(array.contains(40));
	}

	/**
	 * Tests indexOf returns first occurrence index.
	 */
	@Test
	public void testIndexOf () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);
		array.add(5);
		array.add(15);

		assertEquals(0, array.indexOf(5));
		assertEquals(1, array.indexOf(10));
		assertEquals(3, array.indexOf(15));
		assertEquals(-1, array.indexOf(99));
	}

	/**
	 * Tests lastIndexOf returns last occurrence index.
	 */
	@Test
	public void testLastIndexOf () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);
		array.add(5);
		array.add(15);

		assertEquals(2, array.lastIndexOf(5));
		assertEquals(1, array.lastIndexOf(10));
		assertEquals(3, array.lastIndexOf(15));
		assertEquals(-1, array.lastIndexOf(99));
	}

	/**
	 * Tests removeValue removes first occurrence.
	 */
	@Test
	public void testRemoveValue () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(10);
		array.add(5);

		assertTrue(array.removeValue(5));
		assertEquals(2, array.size);
		assertEquals(10, array.get(0));
		assertEquals(5, array.get(1));

		assertFalse(array.removeValue(99));
	}

	/**
	 * Tests removeIndex in ordered array maintains order.
	 */
	@Test
	public void testRemoveIndexOrdered () {
		IntArray array = new IntArray(true, 4);
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);

		int removed = array.removeIndex(1);

		assertEquals(2, removed);
		assertEquals(3, array.size);
		assertEquals(1, array.get(0));
		assertEquals(3, array.get(1));
		assertEquals(4, array.get(2));
	}

	/**
	 * Tests removeIndex in unordered array may change order.
	 */
	@Test
	public void testRemoveIndexUnordered () {
		IntArray array = new IntArray(false, 4);
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);

		int removed = array.removeIndex(1);

		assertEquals(2, removed);
		assertEquals(3, array.size);
		// In unordered, last element moves to removed position
		assertEquals(4, array.get(1));
	}

	/**
	 * Tests removeRange removes inclusive range.
	 */
	@Test
	public void testRemoveRange () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		array.add(5);

		array.removeRange(1, 3);

		assertEquals(2, array.size);
		assertEquals(1, array.get(0));
		assertEquals(5, array.get(1));
	}

	/**
	 * Tests removeAll removes first instance of each value.
	 */
	@Test
	public void testRemoveAll () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		array.add(2);

		IntArray toRemove = new IntArray();
		toRemove.add(2);
		toRemove.add(4);

		assertTrue(array.removeAll(toRemove));

		assertEquals(3, array.size);
		assertEquals(1, array.get(0));
		assertEquals(3, array.get(1));
		assertEquals(2, array.get(2)); // Second 2 remains
	}

	/**
	 * Tests pop removes and returns last element.
	 */
	@Test
	public void testPop () {
		IntArray array = new IntArray();
		array.add(10);
		array.add(20);
		array.add(30);

		assertEquals(30, array.pop());
		assertEquals(2, array.size);
		assertEquals(20, array.pop());
		assertEquals(1, array.size);
	}

	/**
	 * Tests peek returns last element without removing.
	 */
	@Test
	public void testPeek () {
		IntArray array = new IntArray();
		array.add(10);
		array.add(20);

		assertEquals(20, array.peek());
		assertEquals(2, array.size); // Size unchanged
	}

	/**
	 * Tests first returns first element or throws exception.
	 */
	@Test
	public void testFirst () {
		IntArray array = new IntArray();
		array.add(10);
		array.add(20);

		assertEquals(10, array.first());
	}

	/**
	 * Tests isEmpty and notEmpty.
	 */
	@Test
	public void testIsEmptyAndNotEmpty () {
		IntArray array = new IntArray();
		assertTrue(array.isEmpty());
		assertFalse(array.notEmpty());

		array.add(1);
		assertFalse(array.isEmpty());
		assertTrue(array.notEmpty());

		array.clear();
		assertTrue(array.isEmpty());
		assertFalse(array.notEmpty());
	}

	/**
	 * Tests clear removes all elements.
	 */
	@Test
	public void testClear () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);

		array.clear();

		assertEquals(0, array.size);
		assertTrue(array.isEmpty());
	}

	/**
	 * Tests shrink reduces backing array to exact size.
	 */
	@Test
	public void testShrink () {
		IntArray array = new IntArray(100);
		array.add(1);
		array.add(2);

		array.shrink();

		assertEquals(2, array.size);
		assertEquals(2, array.items.length);
	}

	/**
	 * Tests ensureCapacity allocates space for additional elements.
	 */
	@Test
	public void testEnsureCapacity () {
		IntArray array = new IntArray(4);
		array.add(1);
		array.add(2);

		array.ensureCapacity(10);

		// Should have space for at least size + additionalCapacity
		assertTrue(array.items.length >= 12); // 2 (size) + 10 (additional capacity)
		assertEquals(2, array.size); // Size unchanged
	}

	/**
	 * Tests setSize expands or contracts array.
	 */
	@Test
	public void testSetSize () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);

		array.setSize(5);
		assertEquals(5, array.size);

		array.setSize(1);
		assertEquals(1, array.size);
		assertEquals(1, array.get(0));
	}

	/**
	 * Tests sort arranges elements in ascending order.
	 */
	@Test
	public void testSort () {
		IntArray array = new IntArray();
		array.add(5);
		array.add(2);
		array.add(8);
		array.add(1);

		array.sort();

		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(5, array.get(2));
		assertEquals(8, array.get(3));
	}

	/**
	 * Tests reverse reverses element order.
	 */
	@Test
	public void testReverse () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);

		array.reverse();

		assertEquals(4, array.get(0));
		assertEquals(3, array.get(1));
		assertEquals(2, array.get(2));
		assertEquals(1, array.get(3));
	}

	/**
	 * Tests shuffle randomizes element order.
	 */
	@Test
	public void testShuffle () {
		IntArray array = new IntArray();
		for (int i = 0; i < 20; i++) {
			array.add(i);
		}

		IntArray copy = new IntArray(array);
		array.shuffle();

		// Should have same elements
		assertEquals(copy.size, array.size);
		for (int i = 0; i < copy.size; i++) {
			assertTrue(array.contains(copy.get(i)));
		}

		// Order should likely be different (not guaranteed but extremely likely)
		// We won't assert this as shuffle is random
	}

	/**
	 * Tests truncate reduces size to specified value.
	 */
	@Test
	public void testTruncate () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);

		array.truncate(2);
		assertEquals(2, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));

		// Truncate to larger size has no effect
		array.truncate(10);
		assertEquals(2, array.size);
	}

	/**
	 * Tests random returns value from array or zero if empty.
	 */
	@Test
	public void testRandom () {
		IntArray array = new IntArray();
		assertEquals(0, array.random()); // Empty returns 0

		array.add(5);
		array.add(10);
		array.add(15);

		int value = array.random();
		assertTrue(array.contains(value));
	}

	/**
	 * Tests toArray creates new array with elements.
	 */
	@Test
	public void testToArray () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);

		int[] result = array.toArray();

		assertEquals(3, result.length);
		assertEquals(1, result[0]);
		assertEquals(2, result[1]);
		assertEquals(3, result[2]);

		// Verify it's a copy
		result[0] = 99;
		assertEquals(1, array.get(0));
	}

	/**
	 * Tests hashCode for ordered arrays.
	 */
	@Test
	public void testHashCodeOrdered () {
		IntArray array1 = new IntArray();
		array1.add(1);
		array1.add(2);
		array1.add(3);

		IntArray array2 = new IntArray();
		array2.add(1);
		array2.add(2);
		array2.add(3);

		assertEquals(array1.hashCode(), array2.hashCode());
	}

	/**
	 * Tests equals for ordered arrays.
	 */
	@Test
	public void testEqualsOrdered () {
		IntArray array1 = new IntArray();
		array1.add(1);
		array1.add(2);

		IntArray array2 = new IntArray();
		array2.add(1);
		array2.add(2);

		assertTrue(array1.equals(array2));
		assertTrue(array1.equals(array1)); // Same reference

		array2.add(3);
		assertFalse(array1.equals(array2)); // Different size

		assertFalse(array1.equals(null));
		assertFalse(array1.equals("string"));
	}

	/**
	 * Tests unordered arrays are not equal even with same values.
	 */
	@Test
	public void testEqualsUnordered () {
		IntArray array1 = new IntArray(false, 4);
		array1.add(1);
		array1.add(2);

		IntArray array2 = new IntArray(false, 4);
		array2.add(1);
		array2.add(2);

		assertFalse(array1.equals(array2)); // Unordered arrays don't equal
	}

	/**
	 * Tests toString with default format.
	 */
	@Test
	public void testToString () {
		IntArray array = new IntArray();
		assertEquals("[]", array.toString());

		array.add(1);
		assertEquals("[1]", array.toString());

		array.add(2);
		array.add(3);
		assertEquals("[1, 2, 3]", array.toString());
	}

	/**
	 * Tests toString with custom separator.
	 */
	@Test
	public void testToStringWithSeparator () {
		IntArray array = new IntArray();
		assertEquals("", array.toString(", "));

		array.add(1);
		assertEquals("1", array.toString(", "));

		array.add(2);
		array.add(3);
		assertEquals("1, 2, 3", array.toString(", "));
		assertEquals("1-2-3", array.toString("-"));
	}

	// ========== EXCEPTION TESTS ==========

	/**
	 * Tests get throws exception for index out of bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetIndexOutOfBounds () {
		IntArray array = new IntArray();
		array.add(1);
		array.get(5);
	}

	/**
	 * Tests set throws exception for index out of bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetIndexOutOfBounds () {
		IntArray array = new IntArray();
		array.add(1);
		array.set(5, 99);
	}

	/**
	 * Tests incr at index throws exception for index out of bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testIncrIndexOutOfBounds () {
		IntArray array = new IntArray();
		array.add(1);
		array.incr(5, 10);
	}

	/**
	 * Tests mul at index throws exception for index out of bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testMulIndexOutOfBounds () {
		IntArray array = new IntArray();
		array.add(1);
		array.mul(5, 10);
	}

	/**
	 * Tests insert throws exception when index exceeds size.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertIndexTooLarge () {
		IntArray array = new IntArray();
		array.add(1);
		array.insert(5, 99);
	}

	/**
	 * Tests insertRange throws exception when index exceeds size.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testInsertRangeIndexTooLarge () {
		IntArray array = new IntArray();
		array.add(1);
		array.insertRange(5, 2);
	}

	/**
	 * Tests swap throws exception for first index out of bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSwapFirstIndexOutOfBounds () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.swap(5, 1);
	}

	/**
	 * Tests swap throws exception for second index out of bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSwapSecondIndexOutOfBounds () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.swap(0, 5);
	}

	/**
	 * Tests removeIndex throws exception for index out of bounds.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveIndexOutOfBounds () {
		IntArray array = new IntArray();
		array.add(1);
		array.removeIndex(5);
	}

	/**
	 * Tests removeRange throws exception when end exceeds size.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveRangeEndTooLarge () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.removeRange(0, 5);
	}

	/**
	 * Tests removeRange throws exception when start exceeds end.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveRangeStartGreaterThanEnd () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);
		array.removeRange(2, 1);
	}

	/**
	 * Tests first throws exception when array is empty.
	 */
	@Test(expected = IllegalStateException.class)
	public void testFirstOnEmptyArray () {
		IntArray array = new IntArray();
		array.first();
	}

	/**
	 * Tests ensureCapacity throws exception for negative capacity.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEnsureCapacityNegative () {
		IntArray array = new IntArray();
		array.ensureCapacity(-1);
	}

	/**
	 * Tests setSize throws exception for negative size.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetSizeNegative () {
		IntArray array = new IntArray();
		array.setSize(-1);
	}

	/**
	 * Tests truncate throws exception for negative size.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testTruncateNegative () {
		IntArray array = new IntArray();
		array.truncate(-1);
	}

	/**
	 * Tests addAll with offset and length throws exception when out of bounds.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddAllIntArrayOutOfBounds () {
		IntArray array1 = new IntArray();
		IntArray array2 = new IntArray();
		array2.add(1);
		array2.add(2);

		array1.addAll(array2, 1, 5); // offset + length exceeds size
	}

	// ========== ADDITIONAL MUTATION-KILLING TESTS ==========

	/**
	 * Tests insert at exact size boundary (should work like addLast).
	 */
	@Test
	public void testInsertAtSize () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);

		// Insert at index == size should work
		array.insert(2, 3);

		assertEquals(3, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
	}

	/**
	 * Tests reverse on single element and empty arrays.
	 */
	@Test
	public void testReverseSingleAndEmpty () {
		// Empty array
		IntArray empty = new IntArray();
		empty.reverse();
		assertEquals(0, empty.size);

		// Single element
		IntArray single = new IntArray();
		single.add(42);
		single.reverse();
		assertEquals(1, single.size);
		assertEquals(42, single.get(0));

		// Two elements
		IntArray two = new IntArray();
		two.add(1);
		two.add(2);
		two.reverse();
		assertEquals(2, two.get(0));
		assertEquals(1, two.get(1));
	}

	/**
	 * Tests sort on empty and single element arrays.
	 */
	@Test
	public void testSortEmptyAndSingle () {
		IntArray empty = new IntArray();
		empty.sort();
		assertEquals(0, empty.size);

		IntArray single = new IntArray();
		single.add(42);
		single.sort();
		assertEquals(1, single.size);
		assertEquals(42, single.get(0));
	}

	/**
	 * Tests equals with arrays that differ at various positions.
	 */
	@Test
	public void testEqualsWithDifferentValues () {
		IntArray array1 = new IntArray();
		array1.add(1);
		array1.add(2);
		array1.add(3);

		IntArray array2 = new IntArray();
		array2.add(1);
		array2.add(2);
		array2.add(4); // Different at last position

		assertFalse(array1.equals(array2));

		IntArray array3 = new IntArray();
		array3.add(1);
		array3.add(99);
		array3.add(3); // Different at middle position

		assertFalse(array1.equals(array3));

		IntArray array4 = new IntArray();
		array4.add(99);
		array4.add(2);
		array4.add(3); // Different at first position

		assertFalse(array1.equals(array4));
	}

	/**
	 * Tests shrink returns the items array.
	 */
	@Test
	public void testShrinkReturnValue () {
		IntArray array = new IntArray(100);
		array.add(1);
		array.add(2);

		int[] result = array.shrink();
		assertNotNull(result);
		assertSame(array.items, result);
		assertEquals(2, result.length);
	}

	/**
	 * Tests ensureCapacity returns the items array.
	 */
	@Test
	public void testEnsureCapacityReturnValue () {
		IntArray array = new IntArray(4);
		array.add(1);

		int[] result = array.ensureCapacity(10);
		assertNotNull(result);
		assertSame(array.items, result);
	}

	/**
	 * Tests setSize returns the items array.
	 */
	@Test
	public void testSetSizeReturnValue () {
		IntArray array = new IntArray();
		array.add(1);

		int[] result = array.setSize(5);
		assertNotNull(result);
		assertSame(array.items, result);
	}

	/**
	 * Tests removeAll when nothing is removed.
	 */
	@Test
	public void testRemoveAllNothingRemoved () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);

		IntArray toRemove = new IntArray();
		toRemove.add(99);
		toRemove.add(100);

		assertFalse(array.removeAll(toRemove)); // Nothing removed
		assertEquals(3, array.size);
	}

	/**
	 * Tests removeRange with single element range.
	 */
	@Test
	public void testRemoveRangeSingleElement () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);

		array.removeRange(1, 1); // Remove only index 1

		assertEquals(3, array.size);
		assertEquals(1, array.get(0));
		assertEquals(3, array.get(1));
		assertEquals(4, array.get(2));
	}

	/**
	 * Tests removeRange removing from end.
	 */
	@Test
	public void testRemoveRangeAtEnd () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);
		array.add(4);
		array.add(5);

		array.removeRange(3, 4); // Remove last two elements

		assertEquals(3, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
	}

	/**
	 * Tests contains on empty array.
	 */
	@Test
	public void testContainsEmpty () {
		IntArray array = new IntArray();
		assertFalse(array.contains(1));
		assertFalse(array.contains(0));
	}

	/**
	 * Tests indexOf on empty array.
	 */
	@Test
	public void testIndexOfEmpty () {
		IntArray array = new IntArray();
		assertEquals(-1, array.indexOf(1));
	}

	/**
	 * Tests lastIndexOf on empty array.
	 */
	@Test
	public void testLastIndexOfEmpty () {
		IntArray array = new IntArray();
		assertEquals(-1, array.lastIndexOf(1));
	}

	/**
	 * Tests hashCode for unordered arrays uses Object.hashCode().
	 */
	@Test
	public void testHashCodeUnordered () {
		IntArray array1 = new IntArray(false, 4);
		IntArray array2 = new IntArray(false, 4);

		// Unordered arrays should use Object.hashCode(), so they won't be equal
		assertNotEquals(array1.hashCode(), array2.hashCode());
	}

	/**
	 * Tests truncate with exact size (no truncation).
	 */
	@Test
	public void testTruncateExactSize () {
		IntArray array = new IntArray();
		array.add(1);
		array.add(2);
		array.add(3);

		array.truncate(3); // Truncate to exact current size
		assertEquals(3, array.size);
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
	}
}