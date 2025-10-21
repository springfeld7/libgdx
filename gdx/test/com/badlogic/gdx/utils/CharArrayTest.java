package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CharArrayTest {

	private CharArray array;

	@Before
	public void setUp () {
		array = new CharArray();
	}

	/** Test default constructor */
	@Test
	public void defaultConstructorInitializesEmptyOrderedArray() {
		assertEquals("Size should be 0 after default construction", 0, array.size);
		assertTrue("Array should be ordered by default", array.ordered);
	}

	/** Test capacity constructor */
	@Test
	public void capacityConstructorSetsInitialCapacity() {
		CharArray array = new CharArray(100);

		assertEquals("Size should be 0 after capacity constructor", 0, array.size);
		assertEquals("Capacity should match the specified initial capacity", 100, array.capacity());
		assertTrue("Array should be ordered by default", array.ordered);
	}

	/** Test ordered + capacity constructor */
	@Test
	public void orderedCapacityConstructorSetsOrderedAndCapacity() {
		CharArray array = new CharArray(false, 50);

		assertEquals("Size should be 0 after ordered+capacity constructor", 0, array.size);
		assertEquals("Capacity should match the specified initial capacity", 50, array.capacity());
		assertFalse("Array should not be ordered", array.ordered);
	}

	/** Test copy constructor */
	@Test
	public void copyConstructorCopiesElements() {
		array.add('a', 'b');
		CharArray copy = new CharArray(array);

		assertEquals("Copied array should have same size as original", 2, copy.size);
		assertEquals("First element should be copied correctly", 'a', copy.get(0));
		assertEquals("Second element should be copied correctly", 'b', copy.get(1));
	}

	/** Test array constructor */
	@Test
	public void arrayConstructorCopiesArray() {
		char[] chars = {'x', 'y', 'z'};
		CharArray array = new CharArray(chars);

		assertEquals("Size should match input array length", 3, array.size);
		assertEquals("First element should match input array", 'x', array.get(0));
		assertEquals("Last element should match input array", 'z', array.get(2));
	}

	/** Test array constructor with offset and count */
	@Test
	public void arrayOffsetCountConstructorCopiesSubarray() {
		char[] chars = {'x', 'y', 'z'};
		CharArray array = new CharArray(true, chars, 1, 2);

		assertEquals("Size should match the subarray length", 2, array.size);
		assertEquals("First element of subarray should be copied correctly", 'y', array.get(0));
		assertEquals("Second element of subarray should be copied correctly", 'z', array.get(1));
	}

	/** Test CharSequence constructor */
	@Test
	public void charSequenceConstructorCopiesContent() {
		CharArray array = new CharArray((CharSequence)new StringBuilder("hello"));

		assertEquals("Size should match CharSequence length", 5, array.size);
		assertEquals("First element should match CharSequence content", 'h', array.get(0));
		assertEquals("Last element should match CharSequence content", 'o', array.get(4));
	}

	/** Test String constructor */
	@Test
	public void stringConstructorCopiesContent() {
		array = createCharArrayWithString("hello");

		assertEquals("Size should match String length", 5, array.size);
		assertEquals("toString() should match original String", "hello", array.toString());
	}

	/** Test StringBuilder constructor */
	@Test
	public void stringBuilderConstructorCopiesContent() {
		StringBuilder sb = new StringBuilder("world");
		CharArray array = new CharArray(sb);

		assertEquals("Size should match StringBuilder length", 5, array.size);
		assertEquals("toString() should match StringBuilder content", "world", array.toString());
	}

	/** Test adding a single element */
	@Test
	public void addSingleElement() {
		array.add('a');

		assertEquals("Size should be 1 after adding one element", 1, array.size);
		assertEquals("First element should be 'a'", 'a', array.get(0));
	}

	/** Test adding two elements */
	@Test
	public void addTwoElements() {
		array.add('b', 'c');

		assertEquals("Size should be 2 after adding two elements", 2, array.size);
		assertEquals("First element should be 'b'", 'b', array.get(0));
		assertEquals("Second element should be 'c'", 'c', array.get(1));
	}

	/** Test adding three elements */
	@Test
	public void addThreeElements() {
		array.add('d', 'e', 'f');

		assertEquals("Size should be 3 after adding three elements", 3, array.size);
		assertEquals("First element should be 'd'", 'd', array.get(0));
		assertEquals("Last element should be 'f'", 'f', array.get(2));
	}

	/** Test adding four elements */
	@Test
	public void addFourElements() {
		array.add('g', 'h', 'i', 'j');

		assertEquals("Size should be 4 after adding four elements", 4, array.size);
		assertEquals("First element should be 'g'", 'g', array.get(0));
		assertEquals("Last element should be 'j'", 'j', array.get(3));
	}

	/** Test addAll with another CharArray */
	@Test
	public void addAllWithCharArray() {
		CharArray other = new CharArray();
		other.add('k');
		other.add('l');

		array.addAll(other);
		assertEquals("Size should match number of elements added from other CharArray", 2, array.size);
		assertEquals("First element should be 'k'", 'k', array.get(0));
		assertEquals("Second element should be 'l'", 'l', array.get(1));
	}

	/** Test addAll with varargs elements */
	@Test
	public void addAllWithVarargs() {
		array.addAll('m', 'n', 'o');

		assertEquals("Size should match number of elements added via varargs", 3, array.size);
		assertEquals("First element should be 'm'", 'm', array.get(0));
		assertEquals("Last element should be 'o'", 'o', array.get(2));
	}

	/** Test addAll with char array, offset, and length */
	@Test
	public void addAllWithArrayOffsetAndLength() {
		char[] chars = {'p', 'q', 'r', 's', 't'};

		array.addAll(chars, 1, 3); // adds q, r, s

		assertEquals("Size should match number of elements added from array subset", 3, array.size);
		assertEquals("First added element should be 'q'", 'q', array.get(0));
		assertEquals("Second added element should be 'r'", 'r', array.get(1));
		assertEquals("Third added element should be 's'", 's', array.get(2));
	}

	/** Test addAll with CharArray using full range (offset + length = size) */
	@Test
	public void addAllWithOffsetAndLengthFullRange() {
		CharArray source = new CharArray();
		source.add('a', 'b', 'c', 'd');

		array.addAll(source, 0, 4);

		assertEquals("Size should match number of elements added from full range", 4, array.size);
		assertEquals("First element should be 'a'", 'a', array.get(0));
		assertEquals("Last element should be 'd'", 'd', array.get(3));
	}

	/** Test addAll(CharArray, offset, length) throws when offset + length > size */
	@Test
	public void addAllWithOffsetAndLengthThrows() {
		CharArray source = new CharArray();
		source.add('x', 'y');

		try {
			array.addAll(source, 1, 2);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException ex) {
			assertTrue("Exception message should indicate invalid offset+length",
					ex.getMessage().contains("offset + length must be <= size"));
		}
	}

	/** Test addAll(char[], int, int) triggers resizeBuffer correctly */
	@Test
	public void addAllTriggersResizeBuffer() {
		array = new CharArray(3);
		char[] data = {'a', 'b', 'c', 'd', 'e'};

		// This addAll should require resizing
		array.addAll(data, 0, data.length);

		// Verify all elements were added correctly
		assertEquals("Size should match number of elements added", 5, array.size);
		assertArrayEquals("Elements should match input array", new char[] {'a', 'b', 'c', 'd', 'e'}, array.toCharArray());
	}

	/** Test single-argument add triggers resizeBuffer */
	@Test
	public void addSingleTriggersResize() {
		CharArray array1 = new CharArray(1);
		int oldCap = array1.capacity();

		array1.add('a'); // fills capacity, no resize yet
		array1.add('b'); // triggers resize

		assertEquals("Size should be 2 after adding two elements", 2, array1.size);
		assertTrue("Capacity should increase after resize", array1.capacity() > oldCap);
	}

	/** Test two-argument add triggers resizeBuffer */
	@Test
	public void addTwoTriggersResize() {
		CharArray array2 = new CharArray(2);
		int oldCap = array2.capacity();

		array2.add('x', 'y'); // fills capacity, no resize yet
		array2.add('z', 'w'); // triggers resize

		assertEquals("Size should be 4 after adding four elements", 4, array2.size);
		assertTrue("Capacity should increase after resize", array2.capacity() > oldCap);
	}

	/** Test three-argument add triggers resizeBuffer */
	@Test
	public void addThreeTriggersResize() {
		CharArray array3 = new CharArray(3);
		int oldCap = array3.capacity();

		array3.add('a', 'b', 'c'); // fills capacity
		array3.add('d', 'e', 'f'); // triggers resize

		assertEquals("Size should be 6 after adding six elements", 6, array3.size);
		assertTrue("Capacity should increase after resize", array3.capacity() > oldCap);
	}

	/** Test four-argument add triggers resizeBuffer */
	@Test
	public void addFourTriggersResize() {
		CharArray array4 = new CharArray(4);
		int oldCap = array4.capacity();

		array4.add('1', '2', '3', '4'); // fills capacity
		array4.add('5', '6', '7', '8'); // triggers resize

		assertEquals("Size should be 8 after adding eight elements", 8, array4.size);
		assertTrue("Capacity should increase after resize", array4.capacity() > oldCap);
	}

	/** Test get method */
	@Test
	public void get() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		assertEquals("First element should be 'a'", 'a', array.get(0));
		assertEquals("Last element should be 'e'", 'e', array.get(4));
	}

	/** Test get(int) throws IndexOutOfBoundsException for invalid index */
	@Test
	public void getThrowsForInvalidIndex() {
		try {
			array.get(100);
			fail("Expected IndexOutOfBoundsException for invalid index");
		} catch (IndexOutOfBoundsException e) {
			// Expected
		}
	}

	/** Test set method */
	@Test
	public void set() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		array.set(2, 'Z');
		assertEquals("Element at index 2 should be set to 'Z'", 'Z', array.get(2));
	}

	/** Test set(int, char) throws IndexOutOfBoundsException for invalid index */
	@Test
	public void setThrowsForInvalidIndex() {
		try {
			array.set(100, 'x');
			fail("Expected IndexOutOfBoundsException for invalid index");
		} catch (IndexOutOfBoundsException e) {
			// Expected
		}
	}

	/** Test increment of a single element */
	@Test
	public void incrSingle() {
		array.addAll((char)65); // 'A'
		array.incr(0, (char)1);

		assertEquals("Element at index 0 should increment from 'A' to 'B'", (char)66, array.get(0));
	}

	/** Test incr(int, char) throws IndexOutOfBoundsException for invalid index */
	@Test
	public void incrThrowsForInvalidIndex() {
		array.addAll('a', 'b', 'c'); // size = 3

		try {
			array.incr(3, (char)1); // index == size, should throw
			fail("Expected IndexOutOfBoundsException for index >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("index can't be >= size: 3 >= 3", e.getMessage());
		}

		try {
			array.incr(10, (char)1); // index > size, should throw
			fail("Expected IndexOutOfBoundsException for index >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("index can't be >= size: 10 >= 3", e.getMessage());
		}
	}

	/** Test increment all elements */
	@Test
	public void incrAll() {
		array.addAll((char)1, (char)2, (char)3);
		array.incr((char)10);

		assertEquals("First element should increment to 11", (char)11, array.get(0));
		assertEquals("Second element should increment to 12", (char)12, array.get(1));
		assertEquals("Third element should increment to 13", (char)13, array.get(2));
	}

	/** Test multiply a single element */
	@Test
	public void mulSingle() {
		array.addAll((char)5);
		array.mul(0, (char)3);

		assertEquals("Element at index 0 should multiply to 15", (char)15, array.get(0));
	}

	/** Test multiply all elements */
	@Test
	public void mulAll() {
		array.addAll((char)2, (char)3, (char)4);
		array.mul((char)2);

		assertEquals("First element should multiply to 4", (char)4, array.get(0));
		assertEquals("Second element should multiply to 6", (char)6, array.get(1));
		assertEquals("Third element should multiply to 8", (char)8, array.get(2));
	}

	/** Test mul(int, char) throws IndexOutOfBoundsException for invalid index */
	@Test
	public void mulThrowsForInvalidIndex() {
		array.addAll('a', 'b', 'c'); // size = 3

		try {
			array.mul(3, (char)2); // index == size, should throw
			fail("Expected IndexOutOfBoundsException for index >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("index can't be >= size: 3 >= 3", e.getMessage());
		}

		try {
			array.mul(10, (char)2); // index > size, should throw
			fail("Expected IndexOutOfBoundsException for index >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("index can't be >= size: 10 >= 3", e.getMessage());
		}
	}

	/** Test swap(int, int) throws IndexOutOfBoundsException if first index is invalid */
	@Test
	public void swapThrowsForInvalidFirstIndex() {
		array.addAll('a', 'b', 'c'); // size = 3

		try {
			array.swap(3, 1); // first == size, should throw
			fail("Expected IndexOutOfBoundsException for first >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("first can't be >= size: 3 >= 3", e.getMessage());
		}

		try {
			array.swap(10, 1); // first > size, should throw
			fail("Expected IndexOutOfBoundsException for first >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("first can't be >= size: 10 >= 3", e.getMessage());
		}
	}

	/** Test swap(int, int) throws IndexOutOfBoundsException if second index is invalid */
	@Test
	public void swapThrowsForInvalidSecondIndex() {
		array.addAll('a', 'b', 'c'); // size = 3

		try {
			array.swap(1, 3); // second == size, should throw
			fail("Expected IndexOutOfBoundsException for second >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("second can't be >= size: 3 >= 3", e.getMessage());
		}

		try {
			array.swap(1, 10); // second > size, should throw
			fail("Expected IndexOutOfBoundsException for second >= size");
		} catch (IndexOutOfBoundsException e) {
			assertEquals("second can't be >= size: 10 >= 3", e.getMessage());
		}
	}

	/** Test removeValue with ordered array */
	@Test
	public void removeValueOrdered() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		assertTrue("Should remove existing element 'c'", array.removeValue('c'));
		assertEquals("Size should decrease after removal", 4, array.size);
		assertEquals("First element should remain 'a'", 'a', array.get(0));
		assertEquals("Second element should remain 'b'", 'b', array.get(1));
		assertEquals("Third element should now be 'd'", 'd', array.get(2));
		assertEquals("Fourth element should now be 'e'", 'e', array.get(3));
		assertFalse("Removing non-existing element should return false", array.removeValue('z'));
	}

	/** Test removeIndex with ordered array */
	@Test
	public void removeIndexOrdered() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		char removed = array.removeIndex(1);
		assertEquals("Removed element should be 'b'", 'b', removed);
		assertEquals("Size should decrease after removal", 4, array.size);
		assertEquals("First element should remain 'a'", 'a', array.get(0));
		assertEquals("Second element should now be 'c'", 'c', array.get(1));
		assertEquals("Third element should now be 'd'", 'd', array.get(2));
		assertEquals("Fourth element should now be 'e'", 'e', array.get(3));
	}

	/** Test removeRange with ordered array */
	@Test
	public void removeRangeOrdered() {
		array.addAll('a','b','c','d','e','f','g','h','i');

		array.removeRange(1, 4); // removes b,c,d,e

		assertEquals("Size should be 5 after removing range", 5, array.size);
		assertEquals("First element should remain 'a'", 'a', array.get(0));
		assertEquals("Second element should now be 'f'", 'f', array.get(1));
		assertEquals("Third element should now be 'g'", 'g', array.get(2));
		assertEquals("Fourth element should now be 'h'", 'h', array.get(3));
		assertEquals("Fifth element should now be 'i'", 'i', array.get(4));
	}

	/** Test removeValue with unordered array */
	@Test
	public void removeValueUnordered() {
		CharArray unordered = new CharArray(false, 10);

		unordered.addAll('a', 'b', 'c', 'd', 'e');

		assertTrue("Should remove existing element 'b'", unordered.removeValue('b'));
		assertEquals("Size should decrease after removal", 4, unordered.size);
		// last element moves to removed position
		assertEquals("First element should remain 'a'", 'a', unordered.get(0));
		assertEquals("Second element should be last element moved ('e')", 'e', unordered.get(1));
		assertEquals("Third element should remain 'c'", 'c', unordered.get(2));
		assertEquals("Fourth element should remain 'd'", 'd', unordered.get(3));
	}

	/** Test removeAll with unordered array */
	@Test
	public void removeAllUnordered() {
		CharArray unordered = new CharArray(false, 10);
		unordered.addAll('a', 'b', 'c', 'd', 'e');

		CharArray toRemove = new CharArray();
		toRemove.addAll('a', 'd');
		assertTrue("removeAll should return true if any elements removed", unordered.removeAll(toRemove));

		assertEquals("Size should decrease after removeAll", 3, unordered.size);
		assertEquals("First remaining element should be 'e'", 'e', unordered.get(0));
		assertEquals("Second remaining element should be 'b'", 'b', unordered.get(1));
		assertEquals("Third remaining element should be 'c'", 'c', unordered.get(2));
	}

	/** Test contains(char) returns correct result for present and absent elements */
	@Test
	public void containsCharReturnsCorrectResult() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertTrue("Array should contain 'l'", array.contains('l'));
		assertFalse("Array should not contain 'z'", array.contains('z'));
	}

	/** Test lastIndexOf(char) returns last occurrence or -1 if absent */
	@Test
	public void lastIndexOfCharReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals("Last occurrence of 'l' should be at index 9", 9, array.lastIndexOf('l'));
		assertEquals("Last occurrence of 'o' should be at index 7", 7, array.lastIndexOf('o'));
		assertEquals("Character 'z' is absent, should return -1", -1, array.lastIndexOf('z'));
	}

	/** Test lastIndexOf(char) works correctly with single element arrays */
	@Test
	public void lastIndexOfCharSingleElementArray() {
		array.add('q');
		assertEquals("Single element array should return 0 if it matches",
				0, array.lastIndexOf('q'));
		assertEquals("Single element array should return -1 if it does not match",
				-1, array.lastIndexOf('w'));
	}

	/** Test contains(CharSequence) returns correct result for present and absent strings */
	@Test
	public void containsStringReturnsCorrectResult() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertTrue("Array should contain 'hello'", array.contains("hello"));
		assertTrue("Array should contain 'world'", array.contains("world"));
		assertFalse("Array should not contain 'xyz'", array.contains("xyz"));
	}

	/** Test indexOf(CharSequence) returns first occurrence or -1 if absent */
	@Test
	public void indexOfStringReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals("Index of 'hello' should be 0", 0, array.indexOf("hello"));
		assertEquals("Index of 'world' should be 6", 6, array.indexOf("world"));
		assertEquals("String 'xyz' is absent, should return -1", -1, array.indexOf("xyz"));
	}

	/** Test indexOf(char) returns first occurrence or -1 if absent */
	@Test
	public void indexOfCharReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals("First occurrence of 'h' should be at index 0", 0, array.indexOf('h'));
		assertEquals("First occurrence of 'l' should be at index 2", 2, array.indexOf('l'));
		assertEquals("Character 'z' is absent, should return -1", -1, array.indexOf('z'));
	}

	/** Test lastIndexOf(CharSequence) returns last occurrence of substring */
	@Test
	public void lastIndexOfStringReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals("Last index of 'hello' should be 0", 0, array.lastIndexOf("hello"));
		assertEquals("Last index of 'll' should be 2", 2, array.lastIndexOf("ll"));
	}

	/** Test push (add) operation adds elements to the stack */
	@Test
	public void pushAddsElements() {
		array.add('a');
		array.add('b');
		array.add('c');

		assertEquals("Stack size should be 3 after pushes", 3, array.size);
		assertEquals("Top element should be 'c'", 'c', array.peek());
	}

	/** Test pop removes and returns the last element */
	@Test
	public void popRemovesAndReturnsLastElement() {
		array.add('a');
		array.add('b');
		array.add('c');

		assertEquals("Pop should return the last element 'c'", 'c', array.pop());
		assertEquals("Stack size should decrease to 2 after pop", 2, array.size);
		assertEquals("New top element should be 'b'", 'b', array.peek());
	}

	/** Test peek returns the top element without removing it */
	@Test
	public void peekReturnsTopWithoutRemoving() {
		array.add('a');
		array.add('b');

		assertEquals("Peek should return top element 'b'", 'b', array.peek());
		assertEquals("Stack size should remain unchanged after peek", 2, array.size);
	}

	/** Test first() returns the bottom element of the stack */
	@Test
	public void firstReturnsBottomElement() {
		array.add('a');
		array.add('b');
		array.add('c');

		assertEquals("First element (bottom) should be 'a'", 'a', array.first());
	}

	/** Test notEmpty() returns true when stack has elements */
	@Test
	public void notEmptyReturnsTrueWhenStackHasElements() {
		array.add('a');
		assertTrue("notEmpty should return true when array has elements", array.notEmpty());
	}

	/** Test notEmpty() returns false when stack is empty */
	@Test
	public void notEmptyReturnsFalseWhenStackIsEmpty() {
		array.clear();
		assertFalse("notEmpty should return false for empty array", array.notEmpty());
	}

	/** Test isEmpty() returns false when stack has elements */
	@Test
	public void isEmptyReturnsFalseWhenStackHasElements() {
		array.add('a');
		assertFalse("isEmpty should return false when array has elements", array.isEmpty());
	}

	/** Test isEmpty() returns true when stack is empty */
	@Test
	public void isEmptyReturnsTrueWhenStackIsEmpty() {
		array.clear();
		assertTrue("isEmpty should return true for empty array", array.isEmpty());
	}

	/** Test sort() arranges elements in ascending order */
	@Test
	public void sortOrdersElementsAscending() {
		array.addAll('d', 'b', 'e', 'a', 'c');
		array.sort();

		assertEquals("First element should be 'a' after sort", 'a', array.get(0));
		assertEquals("Second element should be 'b' after sort", 'b', array.get(1));
		assertEquals("Third element should be 'c' after sort", 'c', array.get(2));
		assertEquals("Fourth element should be 'd' after sort", 'd', array.get(3));
		assertEquals("Fifth element should be 'e' after sort", 'e', array.get(4));
	}

	/** Test swap(int, int) exchanges elements at specified indices */
	@Test
	public void swapExchangesElements() {
		array.addAll('d', 'b', 'e', 'a', 'c');
		array.swap(0, 4);

		assertEquals("Element at index 0 should now be 'c'", 'c', array.get(0));
		assertEquals("Element at index 1 should remain 'b'", 'b', array.get(1));
		assertEquals("Element at index 2 should remain 'e'", 'e', array.get(2));
		assertEquals("Element at index 3 should remain 'a'", 'a', array.get(3));
		assertEquals("Element at index 4 should now be 'd'", 'd', array.get(4));
	}

	/** Test reverse() inverts the order of elements */
	@Test
	public void reverseInvertsOrder() {
		array.addAll('1', '2', '3', '4', '5');
		array.reverse();

		assertEquals("First element should now be '5'", '5', array.get(0));
		assertEquals("Second element should now be '4'", '4', array.get(1));
		assertEquals("Third element should now be '3'", '3', array.get(2));
		assertEquals("Fourth element should now be '2'", '2', array.get(3));
		assertEquals("Fifth element should now be '1'", '1', array.get(4));
	}

	/** Test truncate(int) reduces size to specified length, keeping first elements */
	@Test
	public void truncateReducesSize() {
		array.addAll('1', '2', '3', '4', '5');
		array.truncate(3);

		assertEquals("Size should be truncated to 3", 3, array.size);
		assertEquals("First element should remain '1'", '1', array.get(0));
		assertEquals("Second element should remain '2'", '2', array.get(1));
		assertEquals("Third element should remain '3'", '3', array.get(2));
	}

	/** Test clear() removes all elements from the array */
	@Test
	public void clearEmptiesArray() {
		array.addAll('1', '2', '3', '4', '5');
		array.clear();

		assertEquals("Size should be 0 after clear", 0, array.size);
	}

	/** Test append(boolean) appends "true" or "false" */
	@Test
	public void appendBoolean() {
		array.append(true);
		assertEquals("Appending true should result in 'true'", "true", array.toString());
		array.clear();

		array.append(false);
		assertEquals("Appending false should result in 'false'", "false", array.toString());
	}

	/** Test append(char) appends a single character */
	@Test
	public void appendChar() {
		array.append('X');
		assertEquals("Appending 'X' should result in 'X'", "X", array.toString());
	}

	/** Test append(int) appends integer as string */
	@Test
	public void appendInt() {
		array.append(123);
		assertEquals("Appending integer 123 should result in '123'", "123", array.toString());
	}

	/** Test append(int, int, char) appends integer with padding */
	@Test
	public void appendIntWithPadding() {
		array.append(42, 5, '0');
		assertEquals("Appending 42 with width 5 and padding '0' should result in '00042'", "00042", array.toString());
	}

	/** Test append(long) appends long as string */
	@Test
	public void appendLong() {
		array.append(9876543210L);
		assertEquals("Appending long 9876543210 should result in '9876543210'", "9876543210", array.toString());
	}

	/** Test append(float) appends float as string */
	@Test
	public void appendFloat() {
		array.append(3.14f);
		assertEquals("Appending float 3.14 should result in '3.14'", "3.14", array.toString());
	}

	/** Test append(double) appends double as string */
	@Test
	public void appendDouble() {
		array.append(2.71828);
		assertEquals("Appending double 2.71828 should result in '2.71828'", "2.71828", array.toString());
	}

	/** Test append(String) appends a string */
	@Test
	public void appendString() {
		array.append("Hello");
		assertEquals("Appending 'Hello' should result in 'Hello'", "Hello", array.toString());

		array.append(" World");
		assertEquals("Appending ' World' should result in 'Hello World'", "Hello World", array.toString());
	}

	/** Test append(null String) appends "null" */
	@Test
	public void appendNullString() {
		array.append((String)null);
		assertEquals("Appending null string should result in 'null'", "null", array.toString());
	}

	/** Test append with separator inserts separators between elements */
	@Test
	public void appendWithSeparator() {
		array.append("one");
		array.appendSeparator(',');
		array.append("two");
		array.appendSeparator(',');
		array.append("three");

		assertEquals("Appending strings with separator ',' should result in 'one,two,three'", "one,two,three", array.toString());
	}

	/** Test append(CharArray) appends contents of another CharArray */
	@Test
	public void appendCharArray() {
		CharArray other = new CharArray("test");
		array.append(other);

		assertEquals("Appending another CharArray 'test' should result in 'test'", "test", array.toString());
	}

	/** Test append(StringBuilder) appends contents of StringBuilder */
	@Test
	public void appendStringBuilder() {
		StringBuilder sb = new StringBuilder("builder");
		array.append(sb);

		assertEquals("Appending StringBuilder 'builder' should result in 'builder'", "builder", array.toString());
	}

	/** Test append(StringBuffer) appends contents of StringBuffer */
	@Test
	public void appendStringBuffer() {
		StringBuffer sbuf = new StringBuffer("buffer");
		array.append(sbuf);

		assertEquals("Appending StringBuffer 'buffer' should result in 'buffer'", "buffer", array.toString());
	}

	/** Test appending an empty string does not change array size */
	@Test
	public void appendEmptyStringDoesNotChangeSize() {
		array.append("");
		assertEquals("Appending empty string should not change size", 0, array.size);
	}

	/** Test appending a null string appends "null" */
	@Test
	public void appendNullStringAppendsLiteralNull() {
		array.append((String) null);
		assertEquals("Appending null string should result in 'null'", "null", array.toString());
	}

	/** Test appendln(String) appends a line with newline character */
	@Test
	public void appendlnStringAddsNewline() {
		array.appendln("Line 1");
		array.appendln("Line 2");
		array.append("Line 3");

		assertEquals("Appending lines with appendln should result in correct newline placement", "Line 1\nLine 2\nLine 3", array.toString());
	}

	/** Test appendln(boolean) appends true/false and newline, returns same instance */
	@Test
	public void appendlnBooleanAppendsValueAndReturnsSameInstance() {
		CharArray returned1 = array.appendln(true);
		CharArray returned2 = array.appendln(false);

		assertEquals("Array should contain 'true\\nfalse\\n'", "true\nfalse\n", array.toString());
		assertSame("appendln(true) should return the same instance", array, returned1);
		assertSame("appendln(false) should return the same instance", array, returned2);
	}

	/** Test appendln(char) appends character and newline, returns same instance */
	@Test
	public void appendlnCharAppendsCharAndReturnsSameInstance() {
		CharArray returned1 = array.appendln('x');
		CharArray returned2 = array.appendln('y');

		assertEquals("Array should contain 'x\\ny\\n'", "x\ny\n", array.toString());
		assertSame("appendln('x') should return the same instance", array, returned1);
		assertSame("appendln('y') should return the same instance", array, returned2);
	}

	/** Test appendln(char[]) appends char array and newline, handles null, returns same instance */
	@Test
	public void appendlnCharArrayAppendsArrayAndReturnsSameInstance() {
		char[] chars = {'a', 'b', 'c'};
		CharArray returned1 = array.appendln(chars);
		CharArray returned2 = array.appendln((char[]) null); // should call appendNull()

		assertEquals("Array should contain 'abc\\nnull\\n'", "abc\nnull\n", array.toString());
		assertSame("appendln(chars) should return the same instance", array, returned1);
		assertSame("appendln(null char[]) should return the same instance", array, returned2);
	}

	/** Test appendln(int) appends int and newline, returns same instance */
	@Test
	public void appendlnIntAppendsValueAndReturnsSameInstance() {
		CharArray returned = array.appendln(42);

		assertEquals("Array should contain '42\\n'", "42\n", array.toString());
		assertSame("appendln(int) should return the same instance", array, returned);
	}

	/** Test appendln(long) appends long and newline, returns same instance */
	@Test
	public void appendlnLongAppendsValueAndReturnsSameInstance() {
		CharArray returned = array.appendln(1234567890123L);

		assertEquals("Array should contain '1234567890123\\n'", "1234567890123\n", array.toString());
		assertSame("appendln(long) should return the same instance", array, returned);
	}

	/** Test appendln(float) appends float and newline, returns same instance */
	@Test
	public void appendlnFloatAppendsValueAndReturnsSameInstance() {
		CharArray returned = array.appendln(3.14f);

		assertEquals("Array should contain '3.14\\n'", "3.14\n", array.toString());
		assertSame("appendln(float) should return the same instance", array, returned);
	}

	/** Test appendln(double) appends double and newline, returns same instance */
	@Test
	public void appendlnDoubleAppendsValueAndReturnsSameInstance() {
		CharArray returned = array.appendln(2.71828);

		assertEquals("Array should contain '2.71828\\n'", "2.71828\n", array.toString());
		assertSame("appendln(double) should return the same instance", array, returned);
	}

	/** Test appendln(Object) appends object and newline, handles null, returns same instance */
	@Test
	public void appendlnObjectAppendsValueAndReturnsSameInstance() {
		CharArray returned1 = array.appendln("hello");
		CharArray returned2 = array.appendln((Object) null);

		assertEquals("Array should contain 'hello\\nnull\\n'", "hello\nnull\n", array.toString());
		assertSame("appendln(Object) with value should return the same instance", array, returned1);
		assertSame("appendln(Object) with null should return the same instance", array, returned2);
	}

	/** Test appendln(char[], start, length) appends nothing if length is 0 */
	@Test
	public void appendlnCharArrayWithOffsetLengthZero() {
		char[] chars = {'x', 'y'};
		array.appendln(chars, 1, 0); // length = 0, should append only newline

		assertEquals("Array should contain only newline when length is 0", "\n", array.toString());
	}

	/** Test appendln(char[], start, length) throws exception for invalid start or length */
	@Test
	public void appendlnCharArrayWithOffsetThrowsForInvalidStartOrLength() {
		char[] chars = {'a', 'b'};

		try {
			array.appendln(chars, -1, 1);
			fail("Expected IndexOutOfBoundsException for negative start");
		} catch (IndexOutOfBoundsException ex) {
			assertEquals("Invalid start: -1", ex.getMessage());
		}

		try {
			array.appendln(chars, 1, -1);
			fail("Expected IndexOutOfBoundsException for negative length");
		} catch (IndexOutOfBoundsException ex) {
			assertEquals("Invalid length: -1", ex.getMessage());
		}

		try {
			array.appendln(chars, 1, 5); // start + length > array.length
			fail("Expected IndexOutOfBoundsException for start + length > array.length");
		} catch (IndexOutOfBoundsException ex) {
			assertEquals("Invalid length: 5", ex.getMessage());
		}
	}

	/** Test appendln() with no arguments appends just a newline */
	@Test
	public void appendlnNoArgsAddsNewline() {
		array.appendln();
		assertEquals("Appending appendln() with no arguments should add a single newline", "\n", array.toString());
	}

	/** Test appendLine(String) appends string and newline, handles null, returns same instance */
	@Test
	public void appendLineStringAppendsValueAndReturnsSameInstance() {
		CharArray returned1 = array.appendLine("hello");
		CharArray returned2 = array.appendLine((String) null);

		assertEquals("Array should contain 'hello\\nnull\\n'", "hello\nnull\n", array.toString());
		assertSame("appendLine(String) with value should return same instance", array, returned1);
		assertSame("appendLine(String) with null should return same instance", array, returned2);
	}

	/** Test appendln(String, start, end) appends substring and newline, handles null, returns same instance */
	@Test
	public void appendlnStringWithOffsetAppendsSubstringAndReturnsSameInstance() {
		CharArray returned1 = array.appendln("abcdef", 1, 4); // append "bcd"
		CharArray returned2 = array.appendln((String) null, 0, 0);

		assertEquals("Array should contain 'bcd\\nnull\\n'", "bcd\nnull\n", array.toString());
		assertSame("appendln(String, start, end) should return same instance", array, returned1);
		assertSame("appendln(null String, 0, 0) should return same instance", array, returned2);
	}

	/** Test appendln(StringBuffer) appends buffer and newline, handles null, returns same instance */
	@Test
	public void appendlnStringBufferAppendsValueAndReturnsSameInstance() {
		CharArray returned1 = array.appendln(new StringBuffer("buff"));
		CharArray returned2 = array.appendln((StringBuffer) null);

		assertEquals("Array should contain 'buff\\nnull\\n'", "buff\nnull\n", array.toString());
		assertSame("appendln(StringBuffer) with value should return same instance", array, returned1);
		assertSame("appendln(StringBuffer) with null should return same instance", array, returned2);
	}

	/** Test appendln(StringBuffer, start, end) appends substring and newline, handles null, returns same instance */
	@Test
	public void appendlnStringBufferWithOffsetAppendsSubstringAndReturnsSameInstance() {
		CharArray returned1 = array.appendln(new StringBuffer("abcdef"), 2, 5); // append "cde"
		CharArray returned2 = array.appendln((StringBuffer) null, 0, 0);

		assertEquals("Array should contain 'cde\\nnull\\n'", "cde\nnull\n", array.toString());
		assertSame("appendln(StringBuffer, start, end) should return same instance", array, returned1);
		assertSame("appendln(null StringBuffer, 0, 0) should return same instance", array, returned2);
	}

	/** Test appendln(StringBuilder) appends builder and newline, handles null, returns same instance */
	@Test
	public void appendlnStringBuilderAppendsValueAndReturnsSameInstance() {
		CharArray returned1 = array.appendln(new StringBuilder("build"));
		CharArray returned2 = array.appendln((StringBuilder) null);

		assertEquals("Array should contain 'build\\nnull\\n'", "build\nnull\n", array.toString());
		assertSame("appendln(StringBuilder) with value should return same instance", array, returned1);
		assertSame("appendln(StringBuilder) with null should return same instance", array, returned2);
	}

	/** Test appendln(StringBuilder, start, end) appends substring and newline, handles null, returns same instance */
	@Test
	public void appendlnStringBuilderWithOffsetAppendsSubstringAndReturnsSameInstance() {
		CharArray returned1 = array.appendln(new StringBuilder("abcdef"), 1, 4); // append "bcd"
		CharArray returned2 = array.appendln((StringBuilder) null, 0, 0);

		assertEquals("Array should contain 'bcd\\nnull\\n'", "bcd\nnull\n", array.toString());
		assertSame("appendln(StringBuilder, start, end) should return same instance", array, returned1);
		assertSame("appendln(null StringBuilder, 0, 0) should return same instance", array, returned2);
	}

	/** Test appendln(CharArray) appends another CharArray and newline, handles null, returns same instance */
	@Test
	public void appendlnCharArrayAppendsValueAndReturnsSameInstance() {
		CharArray other = createCharArrayWithString("xyz");
		CharArray returned1 = array.appendln(other);
		CharArray returned2 = array.appendln((CharArray) null);

		assertEquals("Array should contain 'xyz\\nnull\\n'", "xyz\nnull\n", array.toString());
		assertSame("appendln(CharArray) with value should return same instance", array, returned1);
		assertSame("appendln(CharArray) with null should return same instance", array, returned2);
	}

	/** Test appendln(CharArray, start, end) appends subarray and newline, handles null, returns same instance */
	@Test
	public void appendlnCharArrayWithOffsetAppendsSubarrayAndReturnsSameInstance() {
		CharArray other = createCharArrayWithString("abcdef");
		CharArray returned1 = array.appendln(other, 1, 4); // append "bcd"
		CharArray returned2 = array.appendln((CharArray) null, 0, 0);

		assertEquals("Array should contain 'bcd\\nnull\\n'", "bcd\nnull\n", array.toString());
		assertSame("appendln(CharArray, start, end) should return same instance", array, returned1);
		assertSame("appendln(null CharArray, 0, 0) should return same instance", array, returned2);
	}

	/** Test appendln() appends only newline */
	@Test
	public void appendlnNoArgsAppendsOnlyNewline() {
		array.appendln().appendln();

		assertEquals("Array should contain two newlines", "\n\n", array.toString());
	}

	/** Test appendPadding(int, char) appends padding characters after existing content */
	@Test
	public void appendPaddingAddsCorrectCharacters() {
		array.append("Hello");
		array.appendPadding(5, '*');
		assertEquals("Appending 5 '*' characters after 'Hello' should result in 'Hello*****'", "Hello*****", array.toString());
	}

	/** Test appendFixedWidthPadLeft(String, int, char) pads left correctly */
	@Test
	public void appendFixedWidthPadLeftAddsPadding() {
		array.appendFixedWidthPadLeft("42", 5, '0');
		assertEquals("Pad left with '0' to width 5 should result in '00042'", "00042", array.toString());
	}

	/** Test appendFixedWidthPadLeft(String, int, char) truncates when string too long */
	@Test
	public void appendFixedWidthPadLeftTruncatesWhenTooLong() {
		array.appendFixedWidthPadLeft("12345", 3, '0');
		assertEquals("Truncate string to width 3 should result in '345'", "345", array.toString());
	}

	/** Test appendFixedWidthPadRight(String, int, char) pads right correctly */
	@Test
	public void appendFixedWidthPadRightAddsPadding() {
		array.appendFixedWidthPadRight("Hi", 5, ' ');
		assertEquals("Pad right with spaces to width 5 should result in 'Hi   '", "Hi   ", array.toString());
	}

	/** Test delete(int, int) removes a range of characters */
	@Test
	public void deleteRangeRemovesCharacters() {
		array = createCharArrayWithString("Hello World!");
		array.delete(5, 11);
		assertEquals("Deleting characters from index 5 to 11 should result in 'Hello!'", "Hello!", array.toString());
	}

	/** Test deleteCharAt(int) removes a character at the specified index */
	@Test
	public void deleteCharAtRemovesCharacter() {
		array = createCharArrayWithString("Hello!");
		array.deleteCharAt(5);
		assertEquals("Deleting character at index 5 should result in 'Hello'", "Hello", array.toString());
	}

	/** Test deleteAll(char) removes all occurrences of the given character */
	@Test
	public void deleteAllCharRemovesAllOccurrences() {
		array = createCharArrayWithString("Hello World!");
		array.deleteAll('l');
		assertEquals("Deleting all occurrences of 'l' should result in 'Heo Word!'", "Heo Word!", array.toString());
	}

	/** Test deleteFirst(char) removes only the first occurrence of the given character */
	@Test
	public void deleteFirstCharRemovesOnlyFirst() {
		array = createCharArrayWithString("Hello World!");
		array.deleteFirst('l');

		assertEquals("Deleting first 'l' should result in 'Helo World!'", "Helo World!", array.toString());
	}

	/** Test deleteAll(String) removes all occurrences of the given string */
	@Test
	public void deleteAllStringRemovesAllOccurrences() {
		array = createCharArrayWithString("Hello World! Hello!");
		array.deleteAll("Hello");

		assertEquals("Deleting all 'Hello' should result in ' World! !'", " World! !", array.toString());
	}

	/** Test deleteFirst(String) removes only the first occurrence of the given string */
	@Test
	public void deleteFirstStringRemovesOnlyFirst() {
		array = createCharArrayWithString("Hello World! Hello!");
		array.deleteFirst("Hello");

		assertEquals("Deleting first 'Hello' should result in ' World! Hello!'", " World! Hello!", array.toString());
	}

	/** Test replaceFirst(char, char) replaces the first occurrence of a character */
	@Test
	public void replaceFirstCharReplacesOnlyFirstOccurrence() {
		array = createCharArrayWithString("Hello World!");

		assertTrue("Replacing first 'l' with 'L' should return true", array.replaceFirst('l', 'L'));
		assertEquals("After replacement, array should be 'HeLlo World!'", "HeLlo World!", array.toString());
		assertFalse("Replacing 'z' with 'Z' should return false", array.replaceFirst('z', 'Z')); // returns false if char not found
	}

	/** Test replaceAll(char, char) replaces all occurrences of a character */
	@Test
	public void replaceAllCharReplacesAllOccurrences() {
		array = createCharArrayWithString("Hello World!");

		int count = array.replaceAll('l', 'L');
		assertEquals("Number of replaced 'l' should be 3", 3, count);
		assertEquals("After replacement, array should be 'HeLLo WorLd!'", "HeLLo WorLd!", array.toString());
	}

	/** Test replace(int, int, String) replaces a range of characters with a string */
	@Test
	public void replaceRangeWithString() {
		array = createCharArrayWithString("Hello World!");

		array.replace(0, 5, "Hi");

		assertEquals("Replacing range 0-5 with 'Hi' should result in 'Hi World!'", "Hi World!", array.toString());
	}

	/** Test replaceAll(String, String) replaces all occurrences of a string */
	@Test
	public void replaceAllStringReplacesAllOccurrences() {
		array = createCharArrayWithString("Hello World! Hello!");

		array.replaceAll("Hello", "Hi");

		assertEquals("Replacing all 'Hello' with 'Hi' should result in 'Hi World! Hi!'", "Hi World! Hi!", array.toString());
	}

	/** Test replaceFirst(String, String) replaces only the first occurrence of a string */
	@Test
	public void replaceFirstStringReplacesOnlyFirstOccurrence() {
		array = createCharArrayWithString("Hello World! Hello!");

		array.replaceFirst("Hello", "Hi");

		assertEquals("Replacing first 'Hello' with 'Hi' should result in 'Hi World! Hello!'", "Hi World! Hello!", array.toString());
	}

	/** Test replaceFirst when value == replacement */
	@Test
	public void replaceFirstWithSameValueReturnsFalse() {
		array.addAll('a', 'b', 'c');

		assertFalse("Replacing 'a' with same 'a' should return false", array.replaceFirst('a', 'a')); // value == replacement
	}

	/** Test replace(char, String) replaces a character with a string */
	@Test
	public void replaceCharWithString() {
		array = createCharArrayWithString("a-b-c");

		array.replace('-', " to ");

		assertEquals("Replacing '-' with ' to ' should result in 'a to b to c'", "a to b to c", array.toString());
	}

	/** Test insert(int, char) inserts a character at the specified index */
	@Test
	public void insertCharInsertsAtIndex() {
		array = createCharArrayWithString("Hello!");

		array.insert(5, ' ');

		assertEquals("Inserting ' ' at index 5 should result in 'Hello !'", "Hello !", array.toString());
	}

	/** Test insert(int, String) inserts a string at the specified index */
	@Test
	public void insertStringInsertsAtIndex() {
		array = createCharArrayWithString("Hello !");

		array.insert(6, "World");

		assertEquals("Inserting 'World' at index 6 should result in 'Hello World!'", "Hello World!", array.toString());
	}

	/** Test insert(int, String) inserts at the beginning of the array */
	@Test
	public void insertStringAtBeginning() {
		array = createCharArrayWithString("Hello World!");

		array.insert(0, "Say ");

		assertEquals("Inserting 'Say ' at index 0 should result in 'Say Hello World!'", "Say Hello World!", array.toString());
	}

	/** Test insert(int, boolean) inserts boolean as string at specified index */
	@Test
	public void insertBooleanInsertsAsString() {
		array = createCharArrayWithString("Value: ");

		array.insert(7, true);

		assertEquals("Inserting boolean true at index 7 should result in 'Value: true'", "Value: true", array.toString());
	}

	/** Test insert(int, int) inserts integer as string at specified index */
	@Test
	public void insertIntInsertsAsString() {
		array = createCharArrayWithString("Number: ");

		array.insert(8, 42);

		assertEquals("Inserting integer 42 at index 8 should result in 'Number: 42'", "Number: 42", array.toString());
	}

	/** Test insert(int, char[]) inserts an array of characters at specified index */
	@Test
	public void insertCharArrayInsertsCharacters() {
		array = createCharArrayWithString("AB");
		char[] chars = {'C', 'D', 'E'};

		array.insert(1, chars);

		assertEquals("Inserting char array {'C','D','E'} at index 1 should result in 'ACDEB'", "ACDEB", array.toString());
	}

	/** Test insertRange(int, int) inserts a range and allows setting values */
	@Test
	public void insertRangeAllowsSettingValues() {
		array = createCharArrayWithString("AC");

		array.insertRange(1, 2);
		array.set(1, 'B');
		array.set(2, 'B');

		assertEquals("Inserting range at index 1 and setting values should result in 'ABBC'", "ABBC", array.toString());
	}

	/** Test substring(int, int) and substring(int) returns correct substrings */
	@Test
	public void substringReturnsCorrectSubstrings() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("substring(0,5) should return 'Hello'", "Hello", array.substring(0, 5));
		assertEquals("substring(6) should return 'World!'", "World!", array.substring(6));
		assertEquals("substring(6,11) should return 'World'", "World", array.substring(6, 11));
	}

	/** Test leftString(int) returns the leftmost characters correctly */
	@Test
	public void leftStringReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("leftString(5) should return 'Hello'", "Hello", array.leftString(5));
		assertEquals("leftString(0) should return empty string", "", array.leftString(0));
		assertEquals("leftString(20) should return full string when count > length", "Hello World!", array.leftString(20));
	}

	/** Test rightString(int) returns the rightmost characters correctly */
	@Test
	public void rightStringReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("rightString(6) should return 'World!'", "World!", array.rightString(6));
		assertEquals("rightString(0) should return empty string", "", array.rightString(0));
		assertEquals("rightString(20) should return full string when count > length", "Hello World!", array.rightString(20));
	}

	/** Test midString(int, int) returns substring from start index with given length */
	@Test
	public void midStringReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("midString(6,5) should return 'World'", "World", array.midString(6, 5));
		assertEquals("midString(6,0) should return empty string", "", array.midString(6, 0));
		assertEquals("midString(6,10) should return 'World!' when length > available", "World!", array.midString(6, 10));
	}

	/** Test startsWith(String) returns true if array starts with given string */
	@Test
	public void startsWithReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue("startsWith('Hello') should return true", array.startsWith("Hello"));
		assertFalse("startsWith('World') should return false", array.startsWith("World"));
		assertTrue("startsWith('') should always return true", array.startsWith(""));
	}

	/** Test startsWith returns false if string length exceeds array size */
	@Test
	public void startsWithReturnsFalseForLongString() {
		array = new CharArray(true, 5);
		array.addAll('a', 'b', 'c'); // size = 3

		String tooLong = "abcd"; // length 4 > size 3
		assertFalse("startsWith should return false when string length > array size",
				array.startsWith(tooLong));

		String exactSize = "abc"; // length == size
		assertTrue("startsWith should return true when string matches entire array",
				array.startsWith(exactSize));
	}

	/** Test endsWith(String) returns true if array ends with given string */
	@Test
	public void endsWithReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue("endsWith('World') should return true", array.endsWith("World"));
		assertFalse("endsWith('Hello') should return false", array.endsWith("Hello"));
		assertTrue("endsWith('') should always return true", array.endsWith(""));
	}

	/** Test contains(String) returns true if substring exists in array */
	@Test
	public void containsReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue("contains('Hello') should return true", array.contains("Hello"));
		assertTrue("contains('World') should return true", array.contains("World"));
		assertTrue("contains(' ') should return true", array.contains(" "));
		assertFalse("contains('xyz') should return false", array.contains("xyz"));
	}

	/** Test containsIgnoreCase(String) returns true ignoring case */
	@Test
	public void containsIgnoreCaseReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue("containsIgnoreCase('hello') should return true", array.containsIgnoreCase("hello"));
		assertTrue("containsIgnoreCase('WORLD') should return true", array.containsIgnoreCase("WORLD"));
		assertFalse("containsIgnoreCase('xyz') should return false", array.containsIgnoreCase("xyz"));
	}

	/** Test startsWith returns false if the string length is greater than the array size */
	@Test
	public void startsWithReturnsFalseIfLengthGreaterThanSize() {
		// Create an array with a small known size
		array = new CharArray(true, 5);
		array.addAll('a', 'b', 'c');

		String longPrefix = "abcdef"; // length 6 > array size 3
		assertFalse("startsWith should return false if string length > array size",
				array.startsWith(longPrefix));
	}


	/** Test equals(CharArray) and equalsString(String) return true for exact matches */
	@Test
	public void equalsReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");
		CharArray other = new CharArray("Hello World");

		assertTrue("equals(CharArray) should return true for identical arrays", array.equals(other));
		assertTrue("equalsString(String) should return true for identical strings", array.equalsString("Hello World"));

		other.append("!");
		assertFalse("equals(CharArray) should return false if arrays differ", array.equals(other));
	}

	/** Test equalsIgnoreCase(CharArray/String) returns true ignoring case */
	@Test
	public void equalsIgnoreCaseReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");
		CharArray upper = new CharArray("HELLO WORLD");

		assertTrue("equalsIgnoreCase(CharArray) should return true ignoring case", array.equalsIgnoreCase(upper));
		assertTrue("equalsIgnoreCase(String) should return true ignoring case", array.equalsIgnoreCase("hello world"));
	}

	/** Test length() returns the correct number of characters */
	@Test
	public void lengthReturnsCorrectValue() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("length() should return 12", 12, array.length());
	}

	/** Test charAt(int) returns the character at the specified index */
	@Test
	public void charAtReturnsCorrectCharacter() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("charAt(0) should return 'H'", 'H', array.charAt(0));
		assertEquals("charAt(11) should return '!'", '!', array.charAt(11));
	}

	/** Test subSequence(int, int) returns the correct subsequence */
	@Test
	public void subSequenceReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		CharSequence sub = array.subSequence(0, 5);

		assertEquals("subSequence(0,5) should return 'Hello'", "Hello", sub.toString());
	}

	/** Test capacity() returns initial capacity and size reflects appended elements */
	@Test
	public void capacityReturnsInitialCapacity() {
		array = new CharArray(100);

		array.append("Hello");

		assertEquals("capacity() should return initial capacity of 100", 100, array.capacity());
		assertEquals("size should reflect number of appended elements (5)", 5, array.size);
	}

	/** Test trim() reduces internal buffer but preserves content and size */
	@Test
	public void trimReducesBufferPreservingContent() {
		array = new CharArray(100);

		array.append("Hello");
		array.trim();

		assertEquals("trim() should preserve content", "Hello", array.toString());
		assertEquals("trim() should preserve size", 5, array.size);
	}

	/** Test trim removes leading spaces */
	@Test
	public void trimRemovesLeadingSpaces() {
		CharArray array = new CharArray("   abc".toCharArray());
		array = array.trim();
		assertEquals("abc", array.toString());
	}

	/** Test trim removes trailing spaces */
	@Test
	public void trimRemovesTrailingSpaces() {
		CharArray array = new CharArray("abc   ".toCharArray());
		array = array.trim();
		assertEquals("abc", array.toString());
	}

	/** Test trim removes both leading and trailing spaces */
	@Test
	public void trimRemovesLeadingAndTrailingSpaces() {
		CharArray array = new CharArray("  abc  ".toCharArray());
		array = array.trim();
		assertEquals("abc", array.toString());
	}

	/** Test trimToSize() adjusts capacity to match current size */
	@Test
	public void trimToSizeAdjustsCapacity() {
		array = new CharArray(100);

		array.append("Test");
		array.trimToSize();

		assertEquals("trimToSize() should adjust capacity to match current size", 4, array.capacity());
	}

	/** Test trim returns same instance when array is empty */
	@Test
	public void trimReturnsThisForEmptyArray() {
		array = new CharArray(true, 10); // empty array
		CharArray returned = array.trim();
		assertSame("trim should return the same instance for an empty array", array, returned);
	}

	/** Test appendSeparator appends separator only if separator is not null and loopIndex > 0 */
	@Test
	public void appendSeparatorAppendsWhenSeparatorNotNullAndLoopIndexPositive() {
		array = createCharArrayWithString("a");
		array.appendSeparator(", ", 1);

		assertEquals("Separator should be appended when loopIndex > 0 and separator is not null", "a, ", array.toString());
	}

	/** Test appendSeparator does not append when loopIndex is 0 */
	@Test
	public void appendSeparatorDoesNotAppendWhenLoopIndexZero() {
		array = createCharArrayWithString("a");
		array.appendSeparator(", ", 0);

		assertEquals("Separator should not be appended when loopIndex is 0", "a", array.toString());
	}

	/** Test appendSeparator does not append when separator is null */
	@Test
	public void appendSeparatorDoesNotAppendWhenSeparatorIsNull() {
		array = createCharArrayWithString("a");
		array.appendSeparator(null, 1);

		assertEquals("Separator should not be appended when separator is null", "a", array.toString());
	}

	/** Test appendSeparator does not append when separator is null and loopIndex is 0 */
	@Test
	public void appendSeparatorDoesNotAppendWhenSeparatorNullAndLoopIndexZero() {
		array = createCharArrayWithString("a");
		array.appendSeparator(null, 0);

		assertEquals("Separator should not be appended when separator is null and loopIndex is 0", "a", array.toString());
	}

	/** Test codePointBefore returns correct code point for given index */
	@Test
	public void codePointBeforeReturnsCorrectCodePoint() {
		array = createCharArrayWithString("abc");

		int cp = array.codePointBefore(1); // before 'a' → index 0
		assertEquals("codePointBefore(1) should return code point of 'a'", 'a', cp);

		cp = array.codePointBefore(3); // before 'c' → index 2
		assertEquals("codePointBefore(3) should return code point of 'c'", 'c', cp);
	}


	/** Test offsetByCodePoints with positive offsets */
	@Test
	public void offsetByCodePointsPositiveOffset() {
		array = createCharArrayWithString("abc");
		int index = 0;
		int offset = 2; // move forward 2 code points
		int result = array.offsetByCodePoints(index, offset);

		assertEquals("Offset by 2 code points from index 0 should be 2", 2, result);
	}

	/** Test offsetByCodePoints with negative offsets */
	@Test
	public void offsetByCodePointsNegativeOffset() {
		array = createCharArrayWithString("abc");
		int index = 2;
		int offset = -1; // move back 1 code point
		int result = array.offsetByCodePoints(index, offset);

		assertEquals("Offset by -1 code point from index 2 should be 1", 1, result);
	}

	/** Test offsetByCodePoints with zero offset returns same index */
	@Test
	public void offsetByCodePointsZeroOffset() {
		array = createCharArrayWithString("abc");
		int index = 1;
		int result = array.offsetByCodePoints(index, 0);

		assertEquals("Offset by 0 code points should return the same index", 1, result);
	}

	/** Test offsetByCodePoints handles surrogate pairs correctly */
	@Test
	public void offsetByCodePointsWithSurrogatePairs() {
		// U+1F600 😀 = surrogate pair
		array = new CharArray("a\uD83D\uDE00b".toCharArray()); // "a😀b"
		int index = 0;
		int result = array.offsetByCodePoints(index, 2); // move past 'a' + 😀
		assertEquals("Offset by 2 code points from index 0 should land after 😀", 3, result);

		result = array.offsetByCodePoints(3, -2); // move back 2 code points
		assertEquals("Offset back by 2 code points should return to index 0", 0, result);
	}

	/** Test codePointBefore throws exception for index < 1 with correct message */
	@Test
	public void codePointBeforeThrowsForIndexLessThanOneWithMessage() {
		array = createCharArrayWithString("abc");
		try {
			array.codePointBefore(0);
			fail("Expected IndexOutOfBoundsException for index < 1");
		} catch (IndexOutOfBoundsException ex) {
			assertEquals("Exception message should indicate index and size",
					"index: 0, size: 3", ex.getMessage());
		}
	}

	/** Test codePointBefore throws exception for index > size with correct message */
	@Test
	public void codePointBeforeThrowsForIndexGreaterThanSizeWithMessage() {
		array = createCharArrayWithString("abc");
		try {
			array.codePointBefore(4); // size = 3
			fail("Expected IndexOutOfBoundsException for index > size");
		} catch (IndexOutOfBoundsException ex) {
			assertEquals("Exception message should indicate index and size",
					"index: 4, size: 3", ex.getMessage());
		}
	}

	/** Test that setLength throws for negative length */
	@Test
	public void setLengthThrowsForNegative() {
		array = createCharArrayWithString("Hello");
		try {
			array.setLength(-1);
			fail("Expected IndexOutOfBoundsException for negative length");
		} catch (IndexOutOfBoundsException ex) {
			assertEquals("length: -1", ex.getMessage());
		}
	}

	/** Test that setLength truncates the array when length < size */
	@Test
	public void setLengthTruncatesArray() {
		array = createCharArrayWithString("Hello");
		CharArray returned = array.setLength(3);
		assertEquals("Array should truncate to 'Hel'", "Hel", array.toString());
		assertSame("setLength should return the same instance", array, returned);
	}

	/** Test that setLength does nothing when length == size */
	@Test
	public void setLengthNoChangeWhenLengthEqualsSize() {
		array = createCharArrayWithString("Hello");
		CharArray returned = array.setLength(5); // size == 5
		assertEquals("Array size should remain 5", 5, array.size);
		assertSame(array, returned);
	}

	/** Test that setLength extends the array and fills new portion with '\0' */
	@Test
	public void setLengthExtendsArrayAndFills() {
		array = createCharArrayWithString("Hello"); // size = 5
		CharArray returned = array.setLength(7); // extend to 7
		char[] expected = {'H','e','l','l','o','\0','\0'};
		for (int i = 0; i < 7; i++) {
			assertEquals("Extended portion should be correct", expected[i], array.items[i]);
		}
		assertSame(array, returned);
	}

	/** Test that setLength truncates completely to zero */
	@Test
	public void setLengthTruncateToZero() {
		array = createCharArrayWithString("Hello");
		CharArray returned = array.setLength(0);
		assertEquals("Array should truncate to length 0", 0, array.size);
		assertSame(array, returned);
	}

	/** Test set(CharSequence) replaces contents with a new string */
	@Test
	public void setReplacesContents() {
		array = createCharArrayWithString("Hello");

		array.set("World");
		assertEquals("Array size should match new string length", 5, array.size);
		assertEquals("Array contents should match 'World'", "World", array.toString());
	}

	/** Test set(CharSequence) with empty string clears the array */
	@Test
	public void setWithEmptyStringClearsArray() {
		array = createCharArrayWithString("Hello");

		array.set("");
		assertEquals("Array size should be 0 after setting empty string", 0, array.size);
		assertEquals("Array should be empty", "", array.toString());
	}

	/** Test set(CharSequence) returns this for chaining */
	@Test
	public void setReturnsThisForChaining() {
		array = createCharArrayWithString("Hello");

		CharArray returned = array.set("Test");
		assertSame("set should return the same instance for chaining", array, returned);
	}

	/** Test toCharArray returns correct subarray for valid ranges */
	@Test
	public void toCharArrayReturnsSubarray() {
		array = createCharArrayWithString("abcdef");

		char[] result1 = array.toCharArray(0, 3);
		assertArrayEquals("Subarray 0..3 should be ['a','b','c']", new char[] {'a','b','c'}, result1);

		char[] result2 = array.toCharArray(2, 6);
		assertArrayEquals("Subarray 2..6 should be ['c','d','e','f']", new char[] {'c','d','e','f'}, result2);

		char[] result3 = array.toCharArray(3, 3);
		assertArrayEquals("Subarray 3..3 should be empty", new char[] {}, result3);
	}

	/** Test toCharArray throws exception for invalid ranges */
	@Test(expected = IndexOutOfBoundsException.class)
	public void toCharArrayThrowsForNegativeStart() {
		array = createCharArrayWithString("abcdef");
		array.toCharArray(-1, 3);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void toCharArrayThrowsForEndBeforeStart() {
		array = createCharArrayWithString("abcdef");
		array.toCharArray(3, 2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void toCharArrayThrowsForEndBeyondSize() {
		array = createCharArrayWithString("abcdef");
		array.toCharArray(2, 100); // end > size is invalid
	}

	/** Test equals returns true for the same instance */
	@Test
	public void equalsReturnsTrueForSameInstance() {
		array = createCharArrayWithString("Hello");
		assertTrue("equals should return true for same instance", array.equals(array));
	}

	/** Test equals returns false when this array is unordered */
	@Test
	public void equalsReturnsFalseIfThisIsUnordered() {
		array = createCharArrayWithString("Hello");
		array.ordered = false; // make this unordered
		CharArray other = createCharArrayWithString("Hello");
		assertFalse("equals should return false if this array is unordered", array.equals((Object) other));
	}

	/** Test equals returns false for null and different types */
	@Test
	public void equalsReturnsFalseForNullOrDifferentType() {
		array = createCharArrayWithString("Hello");
		assertFalse("equals should return false for null", array.equals(null));
		assertFalse("equals should return false for different type", array.equals("Hello"));
	}

	/** Test equals returns false if the other array is unordered */
	@Test
	public void equalsReturnsFalseIfOtherIsUnordered() {
		array = new CharArray(true, 16);
		array.addAll('H','e','l','l','o');

		CharArray other = new CharArray(false, 16);
		other.addAll('H','e','l','l','o');

		assertFalse("equals should return false if other array is unordered", array.equals( (Object) other));
	}

	/** Test equals returns false for arrays of different sizes */
	@Test
	public void equalsReturnsFalseForDifferentSizes() {
		array = createCharArrayWithString("Hello");
		CharArray other = createCharArrayWithString("Hell");
		assertFalse("equals should return false for arrays of different sizes", array.equals(other));
	}

	/** Test equals returns false for arrays with different content */
	@Test
	public void equalsReturnsFalseForDifferentContent() {
		array = createCharArrayWithString("Hello");
		CharArray other = createCharArrayWithString("World");
		assertFalse("equals should return false for arrays with different content", array.equals(other));
	}

	/** Test equals returns true for arrays with same content and ordered */
	@Test
	public void equalsReturnsTrueForSameContent() {
		array = createCharArrayWithString("Hello");
		CharArray other = createCharArrayWithString("Hello");
		assertTrue("equals should return true for arrays with same content and ordered", array.equals(other));
	}

	/** Test equals returns false if the other array has a different size */
	@Test
	public void equalsReturnsFalseIfOtherHasDifferentSize() {
		array = new CharArray(true, 5);
		array.addAll('H','e','l','l','o');
		CharArray other = new CharArray(true, 6);
		other.addAll('H','e','l','l','o','!');

		assertFalse(array.equals((Object) other));
	}

	/** Test equals returns false if the other array has the same size but different content */
	@Test
	public void equalsReturnsFalseIfOtherHasDifferentContent() {
		array = new CharArray(true, 5);
		array.addAll('H','e','l','l','o');
		CharArray other = new CharArray(true, 5);
		other.addAll('H','x','l','l','o'); // differs at index 1

		assertFalse(array.equals((Object) other));
	}

	/** Test equals returns true if the other array has the same size and identical content */
	@Test
	public void equalsReturnsTrueIfOtherHasSameContent() {
		array = new CharArray(true, 5);
		array.addAll('H','e','l','l','o');
		CharArray other = new CharArray(true, 5);
		other.addAll('H','e','l','l','o');

		assertTrue(array.equals((Object) other));
	}

	/** Test hashCode() returns same value for equal content and different for different content */
	@Test
	public void hashCodeReturnsConsistentValues() {
		array = createCharArrayWithString("Hello");
		CharArray array2 = createCharArrayWithString("Hello");
		CharArray array3 = createCharArrayWithString("World");

		assertEquals("hashCode should be equal for equal content", array.hashCode(), array2.hashCode());
		assertNotEquals("hashCode should differ for different content", array.hashCode(), array3.hashCode());
	}

	/** Test Reader functionality from CharArray */
	@Test
	public void readerReturnsCorrectCharacters() throws IOException {
		array = createCharArrayWithString("Hello World!");

		try (Reader reader = array.reader()) {
			char[] buffer = new char[5];
			int read = reader.read(buffer);
			assertEquals("Reader should read 5 characters", 5, read);
			assertArrayEquals("Reader should read 'Hello'", new char[] {'H', 'e', 'l', 'l', 'o'}, buffer);

			// Test single char read
			assertEquals("Reader should read space character", ' ', reader.read());
			assertEquals("Reader should read 'W'", 'W', reader.read());

			// Test skip
			reader.skip(2);
			assertEquals("Reader should read 'l' after skip", 'l', reader.read());
		}
	}

	/** Test Writer functionality to CharArray */
	@Test
	public void writerWritesCharactersCorrectly() throws IOException {

		try (Writer writer = array.writer()) {
			writer.write("Test");
			assertEquals("Writer should write 'Test'", "Test", array.toString());

			writer.write(' ');
			writer.write(new char[] {'1', '2', '3'});
			assertEquals("Writer should write 'Test 123'", "Test 123", array.toString());
		}
	}

	/** Test appendCodePoint(int) correctly appends a Unicode code point */
	@Test
	public void appendCodePointAddsSurrogatePair() {
		int smiley = 0x1F600; // 😀

		array.appendCodePoint(smiley);

		assertEquals("appendCodePoint should add 2 characters (surrogate pair)", 2, array.size);
	}

	/** Test codePointAt(int) returns the correct code point */
	@Test
	public void codePointAtReturnsCorrectValue() {
		int smiley = 0x1F600; // 😀

		array.appendCodePoint(smiley);
		int cp = array.codePointAt(0);

		assertEquals("codePointAt should return the correct code point", smiley, cp);
	}

	/** Test codePointCount(int, int) counts code points correctly */
	@Test
	public void codePointCountReturnsCorrectNumber() {
		int smiley = 0x1F600; // 😀

		array.appendCodePoint(smiley);
		array.append("Hello");
		int count = array.codePointCount(0, array.size);

		assertEquals("codePointCount should count 1 emoji + 5 chars = 6", 6, count);
	}

	/** Test reverseCodePoints() correctly reverses Unicode code points */
	@Test
	public void reverseCodePointsReversesUnicode() {
		array.appendCodePoint(0x1F600); // 😀

		array.append("Hi");
		array.reverseCodePoints();

		assertEquals("reverseCodePoints should reverse code points", "iH", array.substring(0, 2));
	}

	/** Test appendAll(Iterable) appends all elements from a collection */
	@Test
	public void appendAllIterableAppendsElements() {
		ArrayList<String> list = new ArrayList<>();
		list.add("One");
		list.add("Two");
		list.add("Three");

		array.clear();
		array.appendAll(list);

		assertEquals("appendAll(Iterable) should append all elements", "OneTwoThree", array.toString());
	}

	/** Test appendWithSeparators(Iterable, String) joins elements with separator */
	@Test
	public void appendWithSeparatorsIterableJoinsElements() {
		ArrayList<String> list = new ArrayList<>();
		list.add("One");
		list.add("Two");
		list.add("Three");

		array.clear();
		array.appendWithSeparators(list, ", ");

		assertEquals("appendWithSeparators should join elements with ', '", "One, Two, Three", array.toString());
	}

	/** Test appendAll(Iterator) appends all elements from an iterator */
	@Test
	public void appendAllIteratorAppendsElements() {
		ArrayList<String> list = new ArrayList<>();
		list.add("One");
		list.add("Two");
		list.add("Three");

		array.clear();
		Iterator<String> iter = list.iterator();
		array.appendAll(iter);

		assertEquals("appendAll(Iterator) should append all elements", "OneTwoThree", array.toString());
	}

	/** Tests that {@link CharArray#appendAll(Object...)} correctly handles a null argument */
	@Test
	public void appendAllNullArrayTest() {
		array.append('a');
		array.appendAll((Object[]) null);

		assertEquals("appendAll(null) should not change array size", 1, array.size);
		assertEquals("First element should remain 'a'", 'a', array.items[0]);
	}

	/** Tests that {@link CharArray#appendAll(Object...)} correctly handles an empty varargs array */
	@Test
	public void appendAllEmptyArrayTest() {
		array.append('a');
		array.appendAll();

		assertEquals("appendAll() with empty array should not change array size", 1, array.size);
		assertEquals("First element should remain 'a'", 'a', array.items[0]);
	}

	/** Tests that {@link CharArray#appendAll(Object...)} appends all provided elements */
	@Test
	public void appendAllNormalUsageTest() {
		array.appendAll('a', 'b', 'c');

		assertEquals("appendAll should append all elements", 3, array.size);
		assertArrayEquals("Array contents should match appended elements", new char[]{'a', 'b', 'c'}, Arrays.copyOf(array.items, array.size));
	}

	/** Test appendWithSeparators(String[], String) joins array elements with separator */
	@Test
	public void appendWithSeparatorsArrayJoinsElements() {
		String[] strArray = {"A", "B", "C"};

		array.clear();
		array.appendWithSeparators(strArray, "-");

		assertEquals("appendWithSeparators should join array elements with '-'", "A-B-C", array.toString());
	}

	/** Test toCharArray() returns a new char array with all elements */
	@Test
	public void toCharArrayReturnsCorrectArray() {
		array.clear();
		array.addAll('a', 'b', 'c', 'd', 'e');

		char[] chars = array.toCharArray();
		assertArrayEquals("toCharArray should return all elements", new char[] {'a', 'b', 'c', 'd', 'e'}, chars);
	}

	/** Test getChars(int, int, char[], int) copies elements to target array correctly */
	@Test
	public void getCharsCopiesElementsCorrectly() {
		array.clear();
		array.addAll('a', 'b', 'c', 'd', 'e');

		char[] target = new char[10];
		array.getChars(1, 4, target, 2);
		assertEquals("getChars should copy 'b' to target[2]", 'b', target[2]);
		assertEquals("getChars should copy 'c' to target[3]", 'c', target[3]);
		assertEquals("getChars should copy 'd' to target[4]", 'd', target[4]);
	}

	/** Test drainChar(int) removes a single character and returns it */
	@Test
	public void drainCharRemovesSingleCharacter() {
		array = createCharArrayWithString("Hello World!");

		char drained = array.drainChar(6);

		assertEquals("drainChar should remove character at index 6 ('W')", 'W', drained);
		assertEquals("drainChar should update array contents", "Hello orld!", array.toString());
	}

	/** Test drainChars(int, int, char[], int) removes a range of characters correctly */
	@Test
	public void drainCharsRemovesRangeCorrectly() {
		array = createCharArrayWithString("Hello World!");

		char[] target = new char[5];
		int count = array.drainChars(0, 5, target, 0);

		assertEquals("drainChars should return count of removed characters", 5, count);
		assertArrayEquals("drainChars should copy removed characters to target", new char[] {'H', 'e', 'l', 'l', 'o'}, target);
		assertEquals("drainChars should update array contents", " World!", array.toString());
	}

	/** Test appendSeparator(char) adds separator only after first append */
	@Test
	public void appendSeparatorCharAddsOnlyAfterFirst() {
		array.appendSeparator(',');
		array.append("first");
		assertEquals("appendSeparator should not add separator before first append", "first", array.toString());

		array.appendSeparator(',');
		array.append("second");
		assertEquals("appendSeparator should add separator before subsequent appends", "first,second", array.toString());
	}

	/** Test appendSeparator(char, char) adds default for empty array */
	@Test
	public void appendSeparatorWithDefaultAddsForEmptyArray() {
		array.appendSeparator(',', ';');

		assertEquals("appendSeparator with default should add default to empty array", ";", array.toString());
	}

	/** Test appendSeparator(char, int) with loop index adds separators correctly */
	@Test
	public void appendSeparatorWithIndexAddsSeparators() {
		for (int i = 0; i < 3; i++) {
			array.appendSeparator(',', i);
			array.append("item" + i);
		}
		assertEquals("appendSeparator with index should add separators correctly", "item0,item1,item2", array.toString());
	}

	/** Test that setSize returns the internal items array. */
	@Test
	public void setSizeReturnsItemsArray() {
		array = new CharArray(5);

		char[] returned = array.setSize(3);

		assertSame("setSize should return internal items array", array.items, returned);
	}

	/** Test that setSize throws IllegalArgumentException when newSize is negative. */
	@Test
	public void setSizeThrowsWhenNegative() {

		try {
			array.setSize(-1);
			fail("Expected IllegalArgumentException for negative size");
		} catch (IllegalArgumentException ex) {
			assertTrue("Exception message should indicate newSize must be >= 0", ex.getMessage().contains("newSize must be >= 0"));
		}
	}

	/** Test that setSize allows zero and updates size correctly. */
	@Test
	public void setSizeAllowsZero() {
		array = new CharArray(5); // ensure capacity is enough

		char[] returned = array.setSize(0);

		assertEquals("Size should be set to 0 when newSize is 0", 0, array.size);
		assertSame("setSize should return internal items array", array.items, returned);
	}

	/** Test that setSize resizes only when newSize is greater than current capacity. */
	@Test
	public void setSizeResizesWhenLarger() {
		array = new CharArray(2);
		char[] oldItems = array.items;

		array.setSize(2); // equal to capacity → should not resize
		assertSame("Items array should not change when newSize equals current capacity",
				oldItems, array.items);

		array.setSize(3); // greater than capacity → should resize
		assertNotSame("Items array should change when newSize exceeds current capacity",
				oldItems, array.items);
		assertEquals("Size should be updated to newSize after resizing",
				3, array.size);
	}

	/**
	 * Tests that setSize does not resize when newSize fits within capacity.
	 */
	@Test
	public void setSizeNoResizeWhenWithinCapacity() {
		array = new CharArray(5);
		char[] oldItems = array.items;

		array.setSize(3);

		assertSame("setSize should not allocate new array if within capacity", oldItems, array.items);
		assertEquals("setSize should update size correctly", 3, array.size);
	}

	/** Test toArray() returns a copy with correct contents */
	@Test
	public void toArrayReturnsCopyWithCorrectContents() {
		array.add('a');
		array.add('b');
		array.add('c');
		char[] result = array.toArray();

		assertArrayEquals("toArray should return a copy with correct elements", new char[] {'a', 'b', 'c'}, result);
		assertEquals("toArray should return array of correct length", 3, result.length);
		assertNotSame("toArray should return a new array, not the internal items array", array.items, result);
	}

	/** Test appendSeparator(String) adds string separators correctly */
	@Test
	public void appendSeparatorStringAddsSeparators() {
		array.append("A");
		array.appendSeparator(" | ");
		array.append("B");
		array.appendSeparator(" | ");
		array.append("C");

		assertEquals("appendSeparator(String) should add separators correctly", "A | B | C", array.toString());
	}

	/** Test insert(int, char[], int, int) inserts NULL placeholder when array is null */
	@Test
	public void insertCharArrayInsertsNullPlaceholderWhenArrayIsNull() {
		array.add('a');
		array.insert(0, null, 0, 0);

		assertEquals("insert should add 'null' placeholder when array is null, total size", 5, array.size); // "null" = 4 chars + 'a'
		assertEquals("insert: first char should be 'n'", 'n', array.items[0]);
		assertEquals("insert: second char should be 'u'", 'u', array.items[1]);
		assertEquals("insert: third char should be 'l'", 'l', array.items[2]);
		assertEquals("insert: fourth char should be 'l'", 'l', array.items[3]);
		assertEquals("insert: fifth char should be original 'a'", 'a', array.items[4]);
	}

	/** Test insert(int, char[], int, int) throws IndexOutOfBoundsException for invalid offset */
	@Test(expected = IndexOutOfBoundsException.class)
	public void insertCharArrayThrowsForInvalidOffset() {
		char[] src = {'x','y'};
		array.insert(0, src, -1, 1);
	}

	/** Test insert(int, char[], int, int) throws IndexOutOfBoundsException for invalid length */
	@Test(expected = IndexOutOfBoundsException.class)
	public void insertCharArrayThrowsForInvalidLength() {
		char[] src = {'x','y'};
		array.insert(0, src, 0, 5);
	}

	/** Test insert(int, char[], int, int) with length 0 does not change size */
	@Test
	public void insertCharArrayWithZeroLengthDoesNothing() {
		array.add('a');
		char[] src = {'x','y'};
		array.insert(0, src, 0, 0);

		assertEquals("insert with length 0 should not change size", 1, array.size);
		assertEquals("insert with length 0 should not modify original element", 'a', array.items[0]);
	}

	/** Test insert(int, char[], int, int) inserts characters correctly */
	@Test
	public void insertCharArrayInsertsCharactersCorrectly() {
		array.add('a');
		char[] src = {'x','y','z'};
		array.insert(0, src, 0, 3);

		assertEquals("insert should increase size correctly", 4, array.size); // 3 inserted + 1 original
		assertEquals("first inserted char should be 'x'", 'x', array.items[0]);
		assertEquals("second inserted char should be 'y'", 'y', array.items[1]);
		assertEquals("third inserted char should be 'z'", 'z', array.items[2]);
		assertEquals("original char should be shifted to index 3", 'a', array.items[3]);
	}

	/** Test insert(int, long) inserts the string representation of the long value */
	@Test
	public void insertInsertsStringOfLong() {
		array.add('a');
		array.insert(0, 123L);

		assertEquals("insert should increase size for string representation of long", 4, array.size); // "123" is 3 chars + original 'a'
		assertEquals("first char should be '1'", '1', array.items[0]);
		assertEquals("second char should be '2'", '2', array.items[1]);
		assertEquals("third char should be '3'", '3', array.items[2]);
		assertEquals("original char should be at index 3", 'a', array.items[3]);
	}

	/** Test insert(int, float) inserts the string representation of the float value */
	@Test
	public void insertInsertsStringOfFloat() {
		array.add('x');
		array.insert(0, 1.5f);

		String inserted = new String(array.items, 0, array.size);
		assertTrue("inserted string should start with float representation '1.5'", inserted.startsWith("1.5"));
		assertTrue("inserted string should end with original char 'x'", inserted.endsWith("x"));
	}

	/** Test insert(int, double) inserts the string representation of the double value */
	@Test
	public void insertInsertsStringOfDouble() {
		array.add('y');
		array.insert(0, 2.75);

		String inserted = new String(array.items, 0, array.size);
		assertTrue("inserted string should start with double representation '2.75'", inserted.startsWith("2.75"));
		assertTrue("inserted string should end with original char 'y'", inserted.endsWith("y"));
	}

	/** Test insert(int, Object) inserts object string when object is not null */
	@Test
	public void insertInsertsObjectStringWhenObjectIsNotNull() {
		array.add('a');
		array.insert(0, "b");

		assertEquals("insert should increase size when inserting non-null object", 2, array.size);
		assertEquals("first char should be 'b'", 'b', array.items[0]);
	}

	/** Test insert(int, Object) inserts NULL placeholder when object is null */
	@Test
	public void insertInsertsNullPlaceholderWhenObjectIsNull() {
		array.add('a');
		array.insert(0, (Object) null);

		assertEquals("insert should add 'null' placeholder when object is null, total size", 5, array.size); // 4 chars "null" + 'a'
		assertEquals("first char should be 'n'", 'n', array.items[0]);
		assertEquals("second char should be 'u'", 'u', array.items[1]);
		assertEquals("third char should be 'l'", 'l', array.items[2]);
		assertEquals("fourth char should be 'l'", 'l', array.items[3]);
		assertEquals("original char should be at index 4", 'a', array.items[4]);
	}

	/** Test lastIndexOf(char, int) returns correct index when character is present */
	@Test
	public void lastIndexOfReturnsCorrectIndexWhenPresent() {
		array.addAll('a', 'b', 'c', 'b');

		assertEquals("Should find last 'b' at index 3 when starting from 3", 3, array.lastIndexOf('b', 3));
		assertEquals("Should find previous 'b' at index 1 when starting from 2", 1, array.lastIndexOf('b', 2));
		assertEquals("Should find 'a' at index 0 when starting from 3", 0, array.lastIndexOf('a', 3));
	}

	/** Test lastIndexOf(char, int) returns -1 when character is not present */
	@Test
	public void lastIndexOfReturnsMinusOneWhenNotPresent() {
		array.addAll('a', 'b', 'c');

		assertEquals("Should return -1 if char not present", -1, array.lastIndexOf('x', 2));
	}

	/** Test lastIndexOf(char, int) handles start index greater than size */
	@Test
	public void lastIndexOfHandlesStartGreaterThanSize() {
		array.addAll('a', 'b', 'c');

		assertEquals("When start >= size, should clamp to last index", 2, array.lastIndexOf('c', 5));
		assertEquals("When start == size, should also clamp to last index", 2, array.lastIndexOf('c', 3));
	}

	/** Test lastIndexOf(char, int) returns -1 when start is negative */
	@Test
	public void lastIndexOfHandlesNegativeStart() {
		array.addAll('a', 'b', 'c');

		assertEquals("Negative start index should return -1", -1, array.lastIndexOf('a', -1));
	}

	/** Test lastIndexOf(char, int) finds match at exact boundary index 0 */
	@Test
	public void lastIndexOfFindsAtIndexZero() {
		array.addAll('a', 'b', 'c');
		assertEquals("Should find match at index 0 when start is 0", 0, array.lastIndexOf('a', 0));
	}

	/** Test lastIndexOf(String, int) returns -1 when substring is not present */
	@Test
	public void lastIndexOfReturnsMinusOneIfNotFoundInArray() {
		array = createCharArrayWithString("abcdef");

		assertEquals(-1, array.lastIndexOf("xyz", 5));
	}

	/** Test lastIndexOf(String, int) throws IllegalArgumentException when string is null */
	@Test
	public void lastIndexOfThrowsForNullString() {
		array = createCharArrayWithString("abcdef");

		try {
			array.lastIndexOf(null, 3);
			fail("Expected IllegalArgumentException for null string");
		} catch (IllegalArgumentException ex) {
			assertEquals("str cannot be null.", ex.getMessage());
		}
	}

	/** Test readFrom(CharBuffer) reads all remaining characters and returns correct delta */
	@Test
	public void readFromCharBufferReadsAllRemainingCharacters() {
		array = new CharArray(5); // small initial capacity
		array.add('x');            // oldSize > 0
		char[] data = new char[10];
		Arrays.fill(data, 'y');
		CharBuffer buffer = CharBuffer.wrap(data);

		int result = array.readFrom(buffer);

		assertEquals("readFrom should return number of characters read", 10, result);                  // newly read chars
		assertEquals("readFrom should update array size correctly", 11, array.size);              // 1 old + 10 new
		char[] expected = new char[11];
		expected[0] = 'x';
		Arrays.fill(expected, 1, expected.length, 'y');
		assertArrayEquals("readFrom should append buffer contents correctly", expected, Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Readable) delegates to Reader and returns correct delta */
	@Test
	public void readFromReadableDelegatesToReader() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "abcdefghij"; // 10 chars > initial capacity
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader);

		assertEquals("readFrom(Reader) should return number of chars read", 10, result);
		assertEquals("readFrom(Reader) should update size correctly", 11, array.size);
		assertArrayEquals("readFrom(Reader) should append input after existing content",
				("x" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Readable) delegates to CharBuffer and returns correct delta */
	@Test
	public void readFromReadableDelegatesToCharBuffer() {
		array.add('x'); // oldSize > 0
		CharBuffer buffer = CharBuffer.wrap(new char[] {'y', 'z'});

		int result = array.readFrom(buffer);

		assertEquals("readFrom(CharBuffer) should return number of chars read", 2, result);
		assertEquals("readFrom(CharBuffer) should update size correctly", 3, array.size);
		assertArrayEquals("readFrom(CharBuffer) should append buffer after existing content",
				new char[] {'x', 'y', 'z'}, Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Readable) reads from generic Readable and returns correct delta */
	@Test
	public void readFromReadableReadsFromGenericReadable() throws IOException {
		array = new CharArray(5);
		array.add('x');
		char[] data = new char[10];
		Arrays.fill(data, 'p');

		Readable readable = new Readable() {
			int pos = 0;
			@Override
			public int read(CharBuffer cb) {
				if (pos >= data.length) return -1;
				int n = Math.min(cb.remaining(), data.length - pos);
				cb.put(data, pos, n);
				pos += n;
				return n;
			}
		};

		int result = array.readFrom(readable);

		assertEquals("readFrom(Readable) should return number of chars read", 10, result);
		assertEquals("readFrom(Readable) should update size correctly", 11, array.size);
		char[] expected = new char[11];
		expected[0] = 'x';
		Arrays.fill(expected, 1, expected.length, 'p');
		assertArrayEquals("readFrom(Readable) should append all read chars correctly",
				expected, Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader) triggers require(), resizes buffer, and returns correct delta */
	@Test
	public void readFromReaderTriggersRequire() throws IOException {
		array.add('x'); // oldSize > 0
		String input = "abcdef"; // longer than initial capacity
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader);

		assertEquals("readFrom(Reader) should return number of chars read", input.length(), result);       // newly read chars
		assertEquals("readFrom(Reader) should update size correctly", input.length() + 1, array.size); // old + new
		assertArrayEquals("readFrom(Reader) should append input correctly after existing content",
				("x" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader, int) triggers require(), resizes buffer, and returns correct delta */
	@Test
	public void readFromReaderWithCountTriggersRequire() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "abcdefghij"; // 10 chars
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader, input.length());

		assertEquals("readFrom(Reader, count) should return number of chars read", 10, result);
		assertEquals("readFrom(Reader, count) should update size correctly", 11, array.size);
		assertArrayEquals("readFrom(Reader, count) should append input correctly",
				("x" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Readable) triggers require(), resizes buffer, and returns correct delta */
	@Test
	public void readFromReadableTriggersRequire() throws IOException {
		array.add('z'); // oldSize > 0
		String input = "abcdef";

		Readable readable = new Readable() {
			final char[] data = input.toCharArray();
			int pos = 0;

			@Override
			public int read(CharBuffer cb) {
				if (pos >= data.length) return -1;
				int n = Math.min(cb.remaining(), data.length - pos);
				cb.put(data, pos, n);
				pos += n;
				return n;
			}
		};

		int result = array.readFrom(readable);

		assertEquals("readFrom(Readable) should return number of chars read", input.length(), result);
		assertEquals("readFrom(Readable) should update size correctly", input.length() + 1, array.size);
		assertArrayEquals("readFrom(Readable) should append all read chars correctly",
				("z" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader) returns -1 when reader is empty */
	@Test
	public void readFromReturnsMinusOneWhenReaderEmpty() throws IOException {
		Reader reader = new StringReader(""); // immediate EOS

		int result = array.readFrom(reader);

		assertEquals("readFrom(Reader) should return -1 when empty", -1, result);
		assertEquals("readFrom(Reader) should not change size when empty", 0, array.size);
	}

	/** Test readFrom(Reader) reads all characters until end of stream */
	@Test
	public void readFromReadsAllCharactersUntilEndOfStream() throws IOException {
		Reader reader = new StringReader("abc");

		int result = array.readFrom(reader);

		assertEquals("readFrom(Reader) should return total chars read", 3, result);
		assertEquals("readFrom(Reader) should update size correctly", 3, array.size);
		assertArrayEquals("readFrom(Reader) should store correct chars",
				new char[] {'a', 'b', 'c'}, Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader, int) returns -1 when reader is empty */
	@Test
	public void readFromWithCountReturnsMinusOneWhenReaderEmpty() throws IOException {
		Reader reader = new StringReader(""); // immediate EOS

		int result = array.readFrom(reader, 3);

		assertEquals("readFrom(Reader, count) should return -1 when empty", -1, result);
		assertEquals("readFrom(Reader, count) should not change size when empty", 0, array.size);
	}

	/** Test readFrom(Reader, int) returns 0 when count is zero or negative */
	@Test
	public void readFromWithCountReturnsZeroForNonPositiveCount() throws IOException {
		Reader reader = new StringReader("abc");

		assertEquals("readFrom(Reader, 0) should return 0", 0, array.readFrom(reader, 0));
		assertEquals("readFrom(Reader, negative) should return 0", 0, array.readFrom(reader, -5));
	}

	/** Test readFrom(Reader, int) reads exactly count characters */
	@Test
	public void readFromWithCountReadsExactNumberOfCharacters() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "abcdef";
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader, 3); // count < input.length()

		assertEquals("readFrom(Reader, count) should return count chars read", 3, result);
		assertEquals("readFrom(Reader, count) should update size correctly", 4, array.size); // 1 old + 3 new
		assertArrayEquals("readFrom(Reader, count) should store correct chars",
				new char[] {'x', 'a', 'b', 'c'}, Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader, int) reads until EOS if reader has fewer chars than count */
	@Test
	public void readFromWithCountReadsUntilEos() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "ab"; // fewer than count
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader, 5); // count > input.length()

		assertEquals("readFrom(Reader, count) should return number of chars actually read", 2, result);
		assertEquals("readFrom(Reader, count) should update size correctly", 3, array.size); // 1 old + 2 new
		assertArrayEquals("readFrom(Reader, count) should store correct chars",
				new char[] {'x', 'a', 'b'}, Arrays.copyOf(array.items, array.size));
	}

	/** Test random() returns default for empty array */
	@Test
	public void randomReturnsDefaultForEmptyArray() {
		char emptyRandom = array.random();

		assertEquals("random() on empty array should return '\\u0000'", '\u0000', emptyRandom);
	}

	/** Test random() returns an element from the array */
	@Test
	public void randomReturnsElementFromArray() {
		array.addAll('a', 'b', 'c', 'd', 'e');
		char random = array.random();

		assertTrue("random() should return a character that exists in the array", array.contains(random));
	}

	/** Test shuffle() maintains size and all elements */
	@Test
	public void shuffleMaintainsSizeAndElements() {
		array.addAll('a', 'b', 'c', 'd', 'e');
		array.shuffle();

		assertEquals("shuffle() should maintain array size", 5, array.size);
		assertTrue(array.contains('a'));
		assertTrue(array.contains('b'));
		assertTrue(array.contains('c'));
		assertTrue(array.contains('d'));
		assertTrue(array.contains('e'));
	}

	/** Test appendTo(StringBuilder) appends the CharArray content correctly */
	@Test
	public void appendToStringBuilderAppendsContent() throws IOException {
		array = createCharArrayWithString("Hello World");

		StringBuilder sb = new StringBuilder("Start: ");
		array.appendTo(sb);
		assertEquals("appendTo(StringBuilder) should append CharArray content correctly",
				"Start: Hello World", sb.toString());
	}

	/** Test appendTo(StringBuffer) appends the CharArray content correctly */
	@Test
	public void appendToStringBufferAppendsContent() throws IOException {
		array = createCharArrayWithString("Hello World");

		StringBuffer sbuf = new StringBuffer("Start: ");
		array.appendTo(sbuf);
		assertEquals("appendTo(StringBuffer) should append CharArray content correctly",
				"Start: Hello World", sbuf.toString());
	}

	/** Test setCharAt(int, char) sets a single character correctly */
	@Test
	public void setCharAtSetsSingleCharacter() {
		array = createCharArrayWithString("Hello");

		array.setCharAt(1, 'a');

		assertEquals("setCharAt() should update character at specified index", "Hallo", array.toString());
	}

	/** Test setCharAt(int, char) supports chaining for multiple updates */
	@Test
	public void setCharAtSupportsChaining() {
		array = createCharArrayWithString("Hello");

		array.setCharAt(1, 'a').setCharAt(2, 'p').setCharAt(3, 'p').setCharAt(4, 'y');

		assertEquals("setCharAt() chaining should update all specified indices", "Happy", array.toString());
	}

	/** Test CharArray.wrap(char[]) creates array with correct size and content */
	@Test
	public void wrapArrayCreatesCorrectCharArray() {
		char[] chars = {'a', 'b', 'c'};
		array = CharArray.wrap(chars);

		assertEquals("Wrapped array should have size equal to initial buffer length", chars.length, array.size);
		for (int i = 0; i < chars.length; i++) {
			assertEquals("Wrapped array element at index " + i + " should match original", chars[i], array.get(i));
		}

		// Test that changes to original array are reflected
		chars[0] = 'z';
		assertEquals("Wrapped array should reflect changes to original array", 'z', array.get(0));
	}

	/** Test CharArray.wrap(char[], int) creates array with correct size and content */
	@Test
	public void wrapArrayWithLengthCreatesCorrectCharArray() {
		char[] chars = {'x', 'y', 'z'};
		array = CharArray.wrap(chars, 2);

		assertEquals("Wrapped array should have size equal to specified length", 2, array.size);
		assertEquals("Element at index 0 should match original", 'x', array.get(0));
		assertEquals("Element at index 1 should match original", 'y', array.get(1));
	}

	/** Test CharArray.with(...) creates array with correct size and content */
	@Test
	public void withArrayCreatesCorrectCharArray() {
		array = CharArray.with('m', 'n', 'o');

		assertEquals("Array created with 'with' should have correct size", 3, array.size);
		assertEquals("Element at index 0 should be 'm'", 'm', array.get(0));
		assertEquals("Element at index 1 should be 'n'", 'n', array.get(1));
		assertEquals("Element at index 2 should be 'o'", 'o', array.get(2));
	}

	/** Test numChars with zero and small positive values */
	@Test
	public void numCharsHandlesZeroAndPositive() {
		assertEquals("numChars(0,10) should return 1 for zero", 1, CharArray.numChars(0L, 10));
		assertEquals("numChars(5,10) should return 1 for single digit positive", 1, CharArray.numChars(5L, 10));
		assertEquals("numChars(123,10) should return 3 for multiple digit positive", 3, CharArray.numChars(123L, 10));
	}

	/** Test numChars with negative values */
	@Test
	public void numCharsHandlesNegative() {
		assertEquals("numChars(-1,10) should return 2 for single digit negative", 2, CharArray.numChars(-1L, 10));
		assertEquals("numChars(-1234,10) should return 5 for multiple digit negative", 5, CharArray.numChars(-1234L, 10));
		assertEquals("numChars(Long.MIN_VALUE,10) should return 20 for extreme negative", 20, CharArray.numChars(Long.MIN_VALUE, 10));
	}

	/** Test toStringAndClear */
	@Test
	public void toStringAndClearTest () {
		CharArray array = createCharArrayWithString("Test String");
		String result = array.toStringAndClear();

		assertEquals("toStringAndClear() should return previous content", "Test String", result);
		assertEquals("toStringAndClear() should reset size to 0", 0, array.size);
		assertTrue("toStringAndClear() should make array empty", array.isEmpty());
	}

	/** Test toString(String) joins multiple elements with a separator */
	@Test
	public void toStringWithSeparatorReturnsJoinedElements() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		String result = array.toString(",");
		assertEquals("toString(separator) should join elements with separator", "a,b,c,d,e", result);
	}

	/** Test toString(String) returns empty string for empty array */
	@Test
	public void toStringWithSeparatorReturnsEmptyForEmptyArray() {
		assertEquals("toString(separator) on empty array should return empty string", "", array.toString(","));
	}

	/** Test toString(String) returns element itself for single-element array */
	@Test
	public void toStringWithSeparatorReturnsSingleElement() {
		array.add('x');

		assertEquals("toString(separator) on single-element array should return the element itself", "x", array.toString(","));
	}

	/** Test operations on an empty array return expected results */
	@Test
	public void emptyArrayOperationsReturnExpected() {
		assertEquals("indexOf() on empty array should return -1", -1, array.indexOf('a'));
		assertEquals("lastIndexOf() on empty array should return -1", -1, array.lastIndexOf('a'));
		assertFalse("contains() on empty array should return false", array.contains('a'));
		assertFalse("removeValue() on empty array should return false", array.removeValue('a'));
	}

	/** Test CharArray can handle very large capacity */
	@Test
	public void largeCapacityArrayHandlesManyElements() {
		CharArray large = new CharArray(10000);

		for (int i = 0; i < 10000; i++) {
			large.add((char)('A' + (i % 26)));
		}
		assertEquals("Large CharArray should store all elements correctly", 10000, large.size);
	}

	/** Helper method for specific initialization */
	private CharArray createCharArrayWithString (String value) {
		return new CharArray(value);
	}
}
