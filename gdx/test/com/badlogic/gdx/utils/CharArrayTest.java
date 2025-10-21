package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.jetbrains.annotations.NotNull;
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
		assertEquals(0, array.size);
		assertTrue(array.ordered);
	}

	/** Test capacity constructor */
	@Test
	public void capacityConstructorSetsInitialCapacity() {
		CharArray array = new CharArray(100);

		assertEquals(0, array.size);
		assertEquals(100, array.capacity());
		assertTrue(array.ordered);
	}

	/** Test ordered + capacity constructor */
	@Test
	public void orderedCapacityConstructorSetsOrderedAndCapacity() {
		CharArray array = new CharArray(false, 50);

		assertEquals(0, array.size);
		assertEquals(50, array.capacity());
		assertFalse(array.ordered);
	}

	/** Test copy constructor */
	@Test
	public void copyConstructorCopiesElements() {
		array.add('a', 'b');
		CharArray copy = new CharArray(array);

		assertEquals(2, copy.size);
		assertEquals('a', copy.get(0));
		assertEquals('b', copy.get(1));
	}

	/** Test array constructor */
	@Test
	public void arrayConstructorCopiesArray() {
		char[] chars = {'x', 'y', 'z'};
		CharArray array = new CharArray(chars);

		assertEquals(3, array.size);
		assertEquals('x', array.get(0));
		assertEquals('z', array.get(2));
	}

	/** Test array constructor with offset and count */
	@Test
	public void arrayOffsetCountConstructorCopiesSubarray() {
		char[] chars = {'x', 'y', 'z'};
		CharArray array = new CharArray(true, chars, 1, 2);

		assertEquals(2, array.size);
		assertEquals('y', array.get(0));
		assertEquals('z', array.get(1));
	}

	/** Test CharSequence constructor */
	@Test
	public void charSequenceConstructorCopiesContent() {
		CharArray array = new CharArray((CharSequence)new StringBuilder("hello"));

		assertEquals(5, array.size);
		assertEquals('h', array.get(0));
		assertEquals('o', array.get(4));
	}

	/** Test String constructor */
	@Test
	public void stringConstructorCopiesContent() {
		array = createCharArrayWithString("hello");

		assertEquals(5, array.size);
		assertEquals("hello", array.toString());
	}

	/** Test StringBuilder constructor */
	@Test
	public void stringBuilderConstructorCopiesContent() {
		StringBuilder sb = new StringBuilder("world");
		CharArray array = new CharArray(sb);

		assertEquals(5, array.size);
		assertEquals("world", array.toString());
	}

	/** Test adding a single element */
	@Test
	public void addSingleElement() {
		array.add('a');

		assertEquals(1, array.size);
		assertEquals('a', array.get(0));
	}

	/** Test adding two elements */
	@Test
	public void addTwoElements() {
		array.add('b', 'c');

		assertEquals(2, array.size);
		assertEquals('b', array.get(0));
		assertEquals('c', array.get(1));
	}

	/** Test adding three elements */
	@Test
	public void addThreeElements() {
		array.add('d', 'e', 'f');

		assertEquals(3, array.size);
		assertEquals('d', array.get(0));
		assertEquals('f', array.get(2));
	}

	/** Test adding four elements */
	@Test
	public void addFourElements() {
		array.add('g', 'h', 'i', 'j');

		assertEquals(4, array.size);
		assertEquals('g', array.get(0));
		assertEquals('j', array.get(3));
	}

	/** Test addAll with another CharArray */
	@Test
	public void addAllWithCharArray() {
		CharArray other = new CharArray();
		other.add('k');
		other.add('l');

		array.addAll(other);
		assertEquals(2, array.size);
		assertEquals('k', array.get(0));
		assertEquals('l', array.get(1));
	}

	/** Test addAll with varargs elements */
	@Test
	public void addAllWithVarargs() {
		array.addAll('m', 'n', 'o');

		assertEquals(3, array.size);
		assertEquals('m', array.get(0));
		assertEquals('o', array.get(2));
	}

	/** Test addAll with char array, offset, and length */
	@Test
	public void addAllWithArrayOffsetAndLength() {
		char[] chars = {'p', 'q', 'r', 's', 't'};

		array.addAll(chars, 1, 3); // adds q, r, s

		assertEquals(3, array.size);
		assertEquals('q', array.get(0));
		assertEquals('r', array.get(1));
		assertEquals('s', array.get(2));
	}


	/** Test addAll with CharArray using full range (offset + length = size) */
	@Test
	public void addAllWithOffsetAndLengthFullRange() {
		CharArray source = new CharArray();
		source.add('a', 'b', 'c', 'd');

		array.addAll(source, 0, 4);

		assertEquals(4, array.size);
		assertEquals('a', array.get(0));
		assertEquals('d', array.get(3));
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
			assertTrue(ex.getMessage().contains("offset + length must be <= size"));
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
		assertEquals(5, array.size);
		assertArrayEquals(new char[] {'a', 'b', 'c', 'd', 'e'}, array.toCharArray());
	}

	/** Test single-argument add triggers resizeBuffer */
	@Test
	public void addSingleTriggersResize() {
		CharArray array1 = new CharArray(1);
		int oldCap = array1.capacity();

		array1.add('a'); // fills capacity, no resize yet
		array1.add('b'); // triggers resize

		assertEquals(2, array1.size);
		assertTrue(array1.capacity() > oldCap);
	}

	/** Test two-argument add triggers resizeBuffer */
	@Test
	public void addTwoTriggersResize() {
		CharArray array2 = new CharArray(2);
		int oldCap = array2.capacity();

		array2.add('x', 'y'); // fills capacity, no resize yet
		array2.add('z', 'w'); // triggers resize

		assertEquals(4, array2.size);
		assertTrue(array2.capacity() > oldCap);
	}

	/** Test three-argument add triggers resizeBuffer */
	@Test
	public void addThreeTriggersResize() {
		CharArray array3 = new CharArray(3);
		int oldCap = array3.capacity();

		array3.add('a', 'b', 'c'); // fills capacity
		array3.add('d', 'e', 'f'); // triggers resize

		assertEquals(6, array3.size);
		assertTrue(array3.capacity() > oldCap);
	}

	/** Test four-argument add triggers resizeBuffer */
	@Test
	public void addFourTriggersResize() {
		CharArray array4 = new CharArray(4);
		int oldCap = array4.capacity();

		array4.add('1', '2', '3', '4'); // fills capacity
		array4.add('5', '6', '7', '8'); // triggers resize

		assertEquals(8, array4.size);
		assertTrue(array4.capacity() > oldCap);
	}

	/** Test get method */
	@Test
	public void get() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		assertEquals('a', array.get(0));
		assertEquals('e', array.get(4));
	}

	/** Test get(int) throws IndexOutOfBoundsException for invalid index */
	@Test
	public void getThrowsForInvalidIndex() {
		try {
			array.get(100);
			fail("Should throw exception");
		} catch (IndexOutOfBoundsException e) {
			// Expected
		}
	}

	/** Test set method */
	@Test
	public void set() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		array.set(2, 'Z');
		assertEquals('Z', array.get(2));
	}

	/** Test set(int, char) throws IndexOutOfBoundsException for invalid index */
	@Test
	public void setThrowsForInvalidIndex() {
		try {
			array.set(100, 'x');
			fail("Should throw exception");
		} catch (IndexOutOfBoundsException e) {
			// Expected
		}
	}

	/** Test increment of a single element */
	@Test
	public void incrSingle() {
		array.addAll((char)65); // 'A'
		array.incr(0, (char)1);

		assertEquals((char)66, array.get(0)); // 'B'
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

		assertEquals((char)11, array.get(0));
		assertEquals((char)12, array.get(1));
		assertEquals((char)13, array.get(2));
	}

	/** Test multiply a single element */
	@Test
	public void mulSingle() {
		array.addAll((char)5);
		array.mul(0, (char)3);

		assertEquals((char)15, array.get(0));
	}

	/** Test multiply all elements */
	@Test
	public void mulAll() {
		array.addAll((char)2, (char)3, (char)4);
		array.mul((char)2);

		assertEquals((char)4, array.get(0));
		assertEquals((char)6, array.get(1));
		assertEquals((char)8, array.get(2));
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

		assertTrue(array.removeValue('c'));
		assertEquals(4, array.size);
		assertEquals('a', array.get(0));
		assertEquals('b', array.get(1));
		assertEquals('d', array.get(2));
		assertEquals('e', array.get(3));
		assertFalse(array.removeValue('z'));
	}

	/** Test removeIndex with ordered array */
	@Test
	public void removeIndexOrdered() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		char removed = array.removeIndex(1);
		assertEquals('b', removed);
		assertEquals(4, array.size);
		assertEquals('a', array.get(0));
		assertEquals('c', array.get(1));
		assertEquals('d', array.get(2));
		assertEquals('e', array.get(3));
	}

	/** Test removeRange with ordered array */
	@Test
	public void removeRangeOrdered() {
		array.addAll('a','b','c','d','e','f','g','h','i');

		array.removeRange(1, 4); // removes b,c,d,e

		assertEquals(5, array.size);
		assertEquals('a', array.get(0));
		assertEquals('f', array.get(1));
		assertEquals('g', array.get(2));
		assertEquals('h', array.get(3));
		assertEquals('i', array.get(4));
	}

	/** Test removeValue with unordered array */
	@Test
	public void removeValueUnordered() {
		CharArray unordered = new CharArray(false, 10);

		unordered.addAll('a', 'b', 'c', 'd', 'e');

		assertTrue(unordered.removeValue('b'));
		assertEquals(4, unordered.size);
		// last element moves to removed position
		assertEquals('a', unordered.get(0));
		assertEquals('e', unordered.get(1));
		assertEquals('c', unordered.get(2));
		assertEquals('d', unordered.get(3));
	}

	/** Test removeAll with unordered array */
	@Test
	public void removeAllUnordered() {
		CharArray unordered = new CharArray(false, 10);
		unordered.addAll('a', 'b', 'c', 'd', 'e');

		CharArray toRemove = new CharArray();
		toRemove.addAll('a', 'd');
		assertTrue(unordered.removeAll(toRemove));

		assertEquals(3, unordered.size);
		assertEquals('e', unordered.get(0));
		assertEquals('b', unordered.get(1));
		assertEquals('c', unordered.get(2));
	}

	/** Test contains(char) returns correct result for present and absent elements */
	@Test
	public void containsCharReturnsCorrectResult() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertTrue(array.contains('l'));
		assertFalse(array.contains('z'));
	}

	/** Test indexOf(char) returns first occurrence or -1 if absent */
	@Test
	public void indexOfCharReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals(0, array.indexOf('h'));
		assertEquals(2, array.indexOf('l'));
		assertEquals(-1, array.indexOf('z'));
	}

	/** Test lastIndexOf(char) returns last occurrence or -1 if absent */
	@Test
	public void lastIndexOfCharReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals(9, array.lastIndexOf('l'));
		assertEquals(7, array.lastIndexOf('o'));
		assertEquals(-1, array.lastIndexOf('z'));
	}

	/** Test contains(CharSequence) returns correct result for present and absent strings */
	@Test
	public void containsStringReturnsCorrectResult() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertTrue(array.contains("hello"));
		assertTrue(array.contains("world"));
		assertFalse(array.contains("xyz"));
	}

	/** Test indexOf(CharSequence) returns first occurrence or -1 if absent */
	@Test
	public void indexOfStringReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals(0, array.indexOf("hello"));
		assertEquals(6, array.indexOf("world"));
		assertEquals(-1, array.indexOf("xyz"));
	}

	/** Test lastIndexOf(CharSequence) returns last occurrence of substring */
	@Test
	public void lastIndexOfStringReturnsCorrectIndex() {
		array.addAll('h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd');

		assertEquals(0, array.lastIndexOf("hello"));
		assertEquals(2, array.lastIndexOf("ll"));
	}

	/** Test push (add) operation adds elements to the stack */
	@Test
	public void pushAddsElements() {
		array.add('a');
		array.add('b');
		array.add('c');

		assertEquals(3, array.size);
		assertEquals('c', array.peek()); // Top of stack should be last pushed
	}

	/** Test pop removes and returns the last element */
	@Test
	public void popRemovesAndReturnsLastElement() {
		array.add('a');
		array.add('b');
		array.add('c');

		assertEquals('c', array.pop());
		assertEquals(2, array.size);
		assertEquals('b', array.peek()); // New top after pop
	}

	/** Test peek returns the top element without removing it */
	@Test
	public void peekReturnsTopWithoutRemoving() {
		array.add('a');
		array.add('b');

		assertEquals('b', array.peek());
		assertEquals(2, array.size); // Size shouldn't change
	}

	/** Test first() returns the bottom element of the stack */
	@Test
	public void firstReturnsBottomElement() {
		array.add('a');
		array.add('b');
		array.add('c');

		assertEquals('a', array.first());
	}

	/** Test notEmpty() returns true when stack has elements */
	@Test
	public void notEmptyReturnsTrueWhenStackHasElements() {
		array.add('a');
		assertTrue(array.notEmpty());
	}

	/** Test notEmpty() returns false when stack is empty */
	@Test
	public void notEmptyReturnsFalseWhenStackIsEmpty() {
		array.clear();
		assertFalse(array.notEmpty());
	}

	/** Test isEmpty() returns false when stack has elements */
	@Test
	public void isEmptyReturnsFalseWhenStackHasElements() {
		array.add('a');
		assertFalse(array.isEmpty());
	}

	/** Test isEmpty() returns true when stack is empty */
	@Test
	public void isEmptyReturnsTrueWhenStackIsEmpty() {
		array.clear();
		assertTrue(array.isEmpty());
	}

	/** Test sort() arranges elements in ascending order */
	@Test
	public void sortOrdersElementsAscending() {
		array.addAll('d', 'b', 'e', 'a', 'c');
		array.sort();

		assertEquals('a', array.get(0));
		assertEquals('b', array.get(1));
		assertEquals('c', array.get(2));
		assertEquals('d', array.get(3));
		assertEquals('e', array.get(4));
	}

	/** Test swap(int, int) exchanges elements at specified indices */
	@Test
	public void swapExchangesElements() {
		array.addAll('d', 'b', 'e', 'a', 'c');
		array.swap(0, 4);

		assertEquals('c', array.get(0));
		assertEquals('b', array.get(1));
		assertEquals('e', array.get(2));
		assertEquals('a', array.get(3));
		assertEquals('d', array.get(4));
	}

	/** Test reverse() inverts the order of elements */
	@Test
	public void reverseInvertsOrder() {
		array.addAll('1', '2', '3', '4', '5');
		array.reverse();

		assertEquals('5', array.get(0));
		assertEquals('4', array.get(1));
		assertEquals('3', array.get(2));
		assertEquals('2', array.get(3));
		assertEquals('1', array.get(4));
	}

	/** Test truncate(int) reduces size to specified length, keeping first elements */
	@Test
	public void truncateReducesSize() {
		array.addAll('1', '2', '3', '4', '5');
		array.truncate(3);

		assertEquals(3, array.size);
		assertEquals('1', array.get(0));
		assertEquals('2', array.get(1));
		assertEquals('3', array.get(2));
	}

	/** Test clear() removes all elements from the array */
	@Test
	public void clearEmptiesArray() {
		array.addAll('1', '2', '3', '4', '5');
		array.clear();

		assertEquals(0, array.size);
	}

	/** Test append(boolean) appends "true" or "false" */
	@Test
	public void appendBoolean() {
		array.append(true);
		assertEquals("true", array.toString());
		array.clear();

		array.append(false);
		assertEquals("false", array.toString());
	}

	/** Test append(char) appends a single character */
	@Test
	public void appendChar() {
		array.append('X');
		assertEquals("X", array.toString());
	}

	/** Test append(int) appends integer as string */
	@Test
	public void appendInt() {
		array.append(123);

		assertEquals("123", array.toString());
	}

	/** Test append(int, int, char) appends integer with padding */
	@Test
	public void appendIntWithPadding() {
		array.append(42, 5, '0');

		assertEquals("00042", array.toString());
	}

	/** Test append(long) appends long as string */
	@Test
	public void appendLong() {
		array.append(9876543210L);

		assertEquals("9876543210", array.toString());
	}

	/** Test append(float) appends float as string */
	@Test
	public void appendFloat() {
		array.append(3.14f);

		assertEquals("3.14", array.toString());
	}

	/** Test append(double) appends double as string */
	@Test
	public void appendDouble() {
		array.append(2.71828);

		assertEquals("2.71828", array.toString());
	}

	/** Test append(String) appends a string */
	@Test
	public void appendString() {
		array.append("Hello");
		assertEquals("Hello", array.toString());

		array.append(" World");
		assertEquals("Hello World", array.toString());
	}

	/** Test append(null String) appends "null" */
	@Test
	public void appendNullString() {
		array.append((String)null);

		assertEquals("null", array.toString());
	}

	/** Test append with separator inserts separators between elements */
	@Test
	public void appendWithSeparator() {
		array.append("one");
		array.appendSeparator(',');
		array.append("two");
		array.appendSeparator(',');
		array.append("three");

		assertEquals("one,two,three", array.toString());
	}

	/** Test append(CharArray) appends contents of another CharArray */
	@Test
	public void appendCharArray() {
		CharArray other = new CharArray("test");

		array.append(other);

		assertEquals("test", array.toString());
	}

	/** Test append(StringBuilder) appends contents of StringBuilder */
	@Test
	public void appendStringBuilder() {
		StringBuilder sb = new StringBuilder("builder");

		array.append(sb);

		assertEquals("builder", array.toString());
	}

	/** Test append(StringBuffer) appends contents of StringBuffer */
	@Test
	public void appendStringBuffer() {
		StringBuffer sbuf = new StringBuffer("buffer");

		array.append(sbuf);

		assertEquals("buffer", array.toString());
	}

	/** Test appending an empty string does not change array size */
	@Test
	public void appendEmptyStringDoesNotChangeSize() {
		array.append("");

		assertEquals(0, array.size);
	}

	/** Test appending a null string appends "null" */
	@Test
	public void appendNullStringAppendsLiteralNull() {
		array.append((String) null);

		assertEquals("null", array.toString());
	}


	/** Test appendln(String) appends a line with newline character */
	@Test
	public void appendlnStringAddsNewline() {
		array.appendln("Line 1");
		array.appendln("Line 2");
		array.append("Line 3");

		assertEquals("Line 1\nLine 2\nLine 3", array.toString());
	}

	/** Test appendln() with no arguments appends just a newline */
	@Test
	public void appendlnNoArgsAddsNewline() {
		array.appendln();

		assertEquals("\n", array.toString());
	}

	/** Test appendPadding(int, char) appends padding characters after existing content */
	@Test
	public void appendPaddingAddsCorrectCharacters() {
		array.append("Hello");
		array.appendPadding(5, '*');

		assertEquals("Hello*****", array.toString());
	}

	/** Test appendFixedWidthPadLeft(String, int, char) pads left correctly */
	@Test
	public void appendFixedWidthPadLeftAddsPadding() {
		array.appendFixedWidthPadLeft("42", 5, '0');

		assertEquals("00042", array.toString());
	}

	/** Test appendFixedWidthPadLeft(String, int, char) truncates when string too long */
	@Test
	public void appendFixedWidthPadLeftTruncatesWhenTooLong() {
		array.appendFixedWidthPadLeft("12345", 3, '0');

		assertEquals("345", array.toString()); // Keeps rightmost chars
	}

	/** Test appendFixedWidthPadRight(String, int, char) pads right correctly */
	@Test
	public void appendFixedWidthPadRightAddsPadding() {
		array.appendFixedWidthPadRight("Hi", 5, ' ');

		assertEquals("Hi   ", array.toString());
	}

	/** Test delete(int, int) removes a range of characters */
	@Test
	public void deleteRangeRemovesCharacters() {
		array = createCharArrayWithString("Hello World!");
		array.delete(5, 11);

		assertEquals("Hello!", array.toString());
	}

	/** Test deleteCharAt(int) removes a character at the specified index */
	@Test
	public void deleteCharAtRemovesCharacter() {
		array = createCharArrayWithString("Hello!");
		array.deleteCharAt(5);

		assertEquals("Hello", array.toString());
	}

	/** Test deleteAll(char) removes all occurrences of the given character */
	@Test
	public void deleteAllCharRemovesAllOccurrences() {
		array = createCharArrayWithString("Hello World!");
		array.deleteAll('l');

		assertEquals("Heo Word!", array.toString());
	}

	/** Test deleteFirst(char) removes only the first occurrence of the given character */
	@Test
	public void deleteFirstCharRemovesOnlyFirst() {
		array = createCharArrayWithString("Hello World!");
		array.deleteFirst('l');

		assertEquals("Helo World!", array.toString());
	}

	/** Test deleteAll(String) removes all occurrences of the given string */
	@Test
	public void deleteAllStringRemovesAllOccurrences() {
		array = createCharArrayWithString("Hello World! Hello!");
		array.deleteAll("Hello");

		assertEquals(" World! !", array.toString());
	}

	/** Test deleteFirst(String) removes only the first occurrence of the given string */
	@Test
	public void deleteFirstStringRemovesOnlyFirst() {
		array = createCharArrayWithString("Hello World! Hello!");
		array.deleteFirst("Hello");

		assertEquals(" World! Hello!", array.toString());
	}

	/** Test replaceFirst(char, char) replaces the first occurrence of a character */
	@Test
	public void replaceFirstCharReplacesOnlyFirstOccurrence() {
		array = createCharArrayWithString("Hello World!");

		assertTrue(array.replaceFirst('l', 'L'));
		assertEquals("HeLlo World!", array.toString());
		assertFalse(array.replaceFirst('z', 'Z')); // returns false if char not found
	}

	/** Test replaceAll(char, char) replaces all occurrences of a character */
	@Test
	public void replaceAllCharReplacesAllOccurrences() {
		array = createCharArrayWithString("Hello World!");

		int count = array.replaceAll('l', 'L');
		assertEquals(3, count);
		assertEquals("HeLLo WorLd!", array.toString());
	}

	/** Test replace(int, int, String) replaces a range of characters with a string */
	@Test
	public void replaceRangeWithString() {
		array = createCharArrayWithString("Hello World!");

		array.replace(0, 5, "Hi");

		assertEquals("Hi World!", array.toString());
	}

	/** Test replaceAll(String, String) replaces all occurrences of a string */
	@Test
	public void replaceAllStringReplacesAllOccurrences() {
		array = createCharArrayWithString("Hello World! Hello!");

		array.replaceAll("Hello", "Hi");

		assertEquals("Hi World! Hi!", array.toString());
	}

	/** Test replaceFirst(String, String) replaces only the first occurrence of a string */
	@Test
	public void replaceFirstStringReplacesOnlyFirstOccurrence() {
		array = createCharArrayWithString("Hello World! Hello!");

		array.replaceFirst("Hello", "Hi");

		assertEquals("Hi World! Hello!", array.toString());
	}

	/** Test replaceFirst when value == replacement */
	@Test
	public void replaceFirstWithSameValueReturnsFalse() {
		array.addAll('a', 'b', 'c');

		assertFalse(array.replaceFirst('a', 'a')); // value == replacement
	}


	/** Test replace(char, String) replaces a character with a string */
	@Test
	public void replaceCharWithString() {
		array = createCharArrayWithString("a-b-c");

		array.replace('-', " to ");

		assertEquals("a to b to c", array.toString());
	}

	/** Test insert(int, char) inserts a character at the specified index */
	@Test
	public void insertCharInsertsAtIndex() {
		array = createCharArrayWithString("Hello!");

		array.insert(5, ' ');

		assertEquals("Hello !", array.toString());
	}

	/** Test insert(int, String) inserts a string at the specified index */
	@Test
	public void insertStringInsertsAtIndex() {
		array = createCharArrayWithString("Hello !");

		array.insert(6, "World");

		assertEquals("Hello World!", array.toString());
	}

	/** Test insert(int, String) inserts at the beginning of the array */
	@Test
	public void insertStringAtBeginning() {
		array = createCharArrayWithString("Hello World!");

		array.insert(0, "Say ");

		assertEquals("Say Hello World!", array.toString());
	}

	/** Test insert(int, boolean) inserts boolean as string at specified index */
	@Test
	public void insertBooleanInsertsAsString() {
		array = createCharArrayWithString("Value: ");

		array.insert(7, true);

		assertEquals("Value: true", array.toString());
	}

	/** Test insert(int, int) inserts integer as string at specified index */
	@Test
	public void insertIntInsertsAsString() {
		array = createCharArrayWithString("Number: ");

		array.insert(8, 42);

		assertEquals("Number: 42", array.toString());
	}

	/** Test insert(int, char[]) inserts an array of characters at specified index */
	@Test
	public void insertCharArrayInsertsCharacters() {
		array = createCharArrayWithString("AB");
		char[] chars = {'C', 'D', 'E'};

		array.insert(1, chars);

		assertEquals("ACDEB", array.toString());
	}

	/** Test insertRange(int, int) inserts a range and allows setting values */
	@Test
	public void insertRangeAllowsSettingValues() {
		array = createCharArrayWithString("AC");

		array.insertRange(1, 2);
		array.set(1, 'B');
		array.set(2, 'B');

		assertEquals("ABBC", array.toString());
	}

	/** Test substring(int, int) and substring(int) returns correct substrings */
	@Test
	public void substringReturnsCorrectSubstrings() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("Hello", array.substring(0, 5));
		assertEquals("World!", array.substring(6));
		assertEquals("World", array.substring(6, 11));
	}

	/** Test leftString(int) returns the leftmost characters correctly */
	@Test
	public void leftStringReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("Hello", array.leftString(5));
		assertEquals("", array.leftString(0));
		assertEquals("Hello World!", array.leftString(20)); // More than length
	}

	/** Test rightString(int) returns the rightmost characters correctly */
	@Test
	public void rightStringReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("World!", array.rightString(6));
		assertEquals("", array.rightString(0));
		assertEquals("Hello World!", array.rightString(20)); // More than length
	}

	/** Test midString(int, int) returns substring from start index with given length */
	@Test
	public void midStringReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		assertEquals("World", array.midString(6, 5));
		assertEquals("", array.midString(6, 0));
		assertEquals("World!", array.midString(6, 10)); // More than available
	}

	/** Test startsWith(String) returns true if array starts with given string */
	@Test
	public void startsWithReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue(array.startsWith("Hello"));
		assertFalse(array.startsWith("World"));
		assertTrue(array.startsWith(""));
	}

	/** Test endsWith(String) returns true if array ends with given string */
	@Test
	public void endsWithReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue(array.endsWith("World"));
		assertFalse(array.endsWith("Hello"));
		assertTrue(array.endsWith(""));
	}

	/** Test contains(String) returns true if substring exists in array */
	@Test
	public void containsReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue(array.contains("Hello"));
		assertTrue(array.contains("World"));
		assertTrue(array.contains(" "));
		assertFalse(array.contains("xyz"));
	}

	/** Test containsIgnoreCase(String) returns true ignoring case */
	@Test
	public void containsIgnoreCaseReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");

		assertTrue(array.containsIgnoreCase("hello"));
		assertTrue(array.containsIgnoreCase("WORLD"));
		assertFalse(array.containsIgnoreCase("xyz"));
	}

	/** Test equals(CharArray) and equalsString(String) return true for exact matches */
	@Test
	public void equalsReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");
		CharArray other = new CharArray("Hello World");

		assertTrue(array.equals(other));
		assertTrue(array.equalsString("Hello World"));

		other.append("!");
		assertFalse(array.equals(other));
	}

	/** Test equalsIgnoreCase(CharArray/String) returns true ignoring case */
	@Test
	public void equalsIgnoreCaseReturnsCorrectResult() {
		array = createCharArrayWithString("Hello World");
		CharArray upper = new CharArray("HELLO WORLD");

		assertTrue(array.equalsIgnoreCase(upper));
		assertTrue(array.equalsIgnoreCase("hello world"));
	}

	/** Test length() returns the correct number of characters */
	@Test
	public void lengthReturnsCorrectValue() {
		array = createCharArrayWithString("Hello World!");

		assertEquals(12, array.length());
	}

	/** Test charAt(int) returns the character at the specified index */
	@Test
	public void charAtReturnsCorrectCharacter() {
		array = createCharArrayWithString("Hello World!");

		assertEquals('H', array.charAt(0));
		assertEquals('!', array.charAt(11));
	}

	/** Test subSequence(int, int) returns the correct subsequence */
	@Test
	public void subSequenceReturnsCorrectSubstring() {
		array = createCharArrayWithString("Hello World!");

		CharSequence sub = array.subSequence(0, 5);

		assertEquals("Hello", sub.toString());
	}

	/** Test capacity() returns initial capacity and size reflects appended elements */
	@Test
	public void capacityReturnsInitialCapacity() {
		array = new CharArray(100);

		array.append("Hello");

		assertEquals(100, array.capacity());
		assertEquals(5, array.size);
	}

	/** Test trim() reduces internal buffer but preserves content and size */
	@Test
	public void trimReducesBufferPreservingContent() {
		array = new CharArray(100);

		array.append("Hello");
		array.trim();

		assertEquals("Hello", array.toString());
		assertEquals(5, array.size);
	}

	/** Test trimToSize() adjusts capacity to match current size */
	@Test
	public void trimToSizeAdjustsCapacity() {
		array = new CharArray(100);

		array.append("Test");
		array.trimToSize();

		assertEquals(4, array.capacity());
	}

	/** Test setLength(int) truncates or extends the array correctly */
	@Test
	public void setLengthTruncatesOrExtendsArray() {
		array = createCharArrayWithString("Hello");

		// Truncate
		array.setLength(3);
		assertEquals("Hel", array.toString());

		// Extend (fills with '\0')
		array.setLength(5);
		assertEquals(5, array.length());
	}

	/** Test equals(Object) returns true for same instance or content and false otherwise */
	@Test
	public void equalsObjectHandlesVariousCases() {
		array = createCharArrayWithString("Hello");
		CharArray array2 = createCharArrayWithString("Hello");
		CharArray array3 = createCharArrayWithString("World");

		assertTrue(array.equals(array));          // same instance
		assertTrue(array.equals(array2));          // same content
		assertFalse(array.equals(array3));         // different content
		assertFalse(array.equals(null));           // null
		assertFalse(array.equals("Hello"));        // different type
	}

	/** Test hashCode() returns same value for equal content and different for different content */
	@Test
	public void hashCodeReturnsConsistentValues() {
		array = createCharArrayWithString("Hello");
		CharArray array2 = createCharArrayWithString("Hello");
		CharArray array3 = createCharArrayWithString("World");

		assertEquals(array.hashCode(), array2.hashCode());
		assertNotEquals(array.hashCode(), array3.hashCode());
	}

	/** Test Reader functionality from CharArray */
	@Test
	public void readerReturnsCorrectCharacters() throws IOException {
		array = createCharArrayWithString("Hello World!");

		try (Reader reader = array.reader()) {
			char[] buffer = new char[5];
			int read = reader.read(buffer);
			assertEquals(5, read);
			assertArrayEquals(new char[] {'H', 'e', 'l', 'l', 'o'}, buffer);

			// Test single char read
			assertEquals(' ', reader.read());
			assertEquals('W', reader.read());

			// Test skip
			reader.skip(2);
			assertEquals('l', reader.read());
		}
	}

	/** Test Writer functionality to CharArray */
	@Test
	public void writerWritesCharactersCorrectly() throws IOException {

		try (Writer writer = array.writer()) {
			writer.write("Test");
			assertEquals("Test", array.toString());

			writer.write(' ');
			writer.write(new char[] {'1', '2', '3'});
			assertEquals("Test 123", array.toString());
		}
	}
	/** Test appendCodePoint(int) correctly appends a Unicode code point */
	@Test
	public void appendCodePointAddsSurrogatePair() {
		int smiley = 0x1F600; // 😀

		array.appendCodePoint(smiley);

		assertEquals(2, array.size); // Surrogate pair
	}

	/** Test codePointAt(int) returns the correct code point */
	@Test
	public void codePointAtReturnsCorrectValue() {
		int smiley = 0x1F600; // 😀

		array.appendCodePoint(smiley);
		int cp = array.codePointAt(0);

		assertEquals(smiley, cp);
	}

	/** Test codePointCount(int, int) counts code points correctly */
	@Test
	public void codePointCountReturnsCorrectNumber() {
		int smiley = 0x1F600; // 😀

		array.appendCodePoint(smiley);
		array.append("Hello");
		int count = array.codePointCount(0, array.size);

		assertEquals(6, count); // 1 emoji + 5 chars
	}

	/** Test reverseCodePoints() correctly reverses Unicode code points */
	@Test
	public void reverseCodePointsReversesUnicode() {
		array.appendCodePoint(0x1F600); // 😀

		array.append("Hi");
		array.reverseCodePoints();

		assertEquals("iH", array.substring(0, 2));
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

		assertEquals("OneTwoThree", array.toString());
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

		assertEquals("One, Two, Three", array.toString());
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

		assertEquals("OneTwoThree", array.toString());
	}

	/** Tests that {@link CharArray#appendAll(Object...)} correctly handles a null argument */
	@Test
	public void appendAllNullArrayTest() {
		array.append('a');
		array.appendAll((Object[]) null);

		assertEquals(1, array.size);
		assertEquals('a', array.items[0]);
	}

	/** Tests that {@link CharArray#appendAll(Object...)} correctly handles an empty varargs array */
	@Test
	public void appendAllEmptyArrayTest() {
		array.append('a');
		array.appendAll();

		assertEquals(1, array.size);
		assertEquals('a', array.items[0]);
	}

	/** Tests that {@link CharArray#appendAll(Object...)} appends all provided elements */
	@Test
	public void appendAllNormalUsageTest() {
		array.appendAll('a', 'b', 'c');

		assertEquals(3, array.size);
		assertArrayEquals(new char[]{'a', 'b', 'c'}, Arrays.copyOf(array.items, array.size));
	}

	/** Test appendWithSeparators(String[], String) joins array elements with separator */
	@Test
	public void appendWithSeparatorsArrayJoinsElements() {
		String[] strArray = {"A", "B", "C"};

		array.clear();
		array.appendWithSeparators(strArray, "-");

		assertEquals("A-B-C", array.toString());
	}

	/** Test edge cases and error conditions */
	@Test
	public void edgeCasesTest () {

		// Empty array operations
		assertEquals(-1, array.indexOf('a'));
		assertEquals(-1, array.lastIndexOf('a'));
		assertFalse(array.contains('a'));
		assertFalse(array.removeValue('a'));

		// Append empty/null strings
		array.append("");
		assertEquals(0, array.size);

		array.append((String)null);
		assertEquals("null", array.toString());

		// Large capacity
		CharArray large = new CharArray(10000);
		for (int i = 0; i < 10000; i++) {
			large.add((char)('A' + (i % 26)));
		}
		assertEquals(10000, large.size);

		// Test boundaries
		try {
			array.get(100);
			fail("Should throw exception");
		} catch (IndexOutOfBoundsException e) {
			// Expected
		}

		try {
			array.set(100, 'x');
			fail("Should throw exception");
		} catch (IndexOutOfBoundsException e) {
			// Expected
		}
	}
	/** Test toCharArray() returns a new char array with all elements */
	@Test
	public void toCharArrayReturnsCorrectArray() {
		array.clear();
		array.addAll('a', 'b', 'c', 'd', 'e');

		char[] chars = array.toCharArray();
		assertArrayEquals(new char[] {'a', 'b', 'c', 'd', 'e'}, chars);
	}

	/** Test getChars(int, int, char[], int) copies elements to target array correctly */
	@Test
	public void getCharsCopiesElementsCorrectly() {
		array.clear();
		array.addAll('a', 'b', 'c', 'd', 'e');

		char[] target = new char[10];
		array.getChars(1, 4, target, 2);
		assertEquals('b', target[2]);
		assertEquals('c', target[3]);
		assertEquals('d', target[4]);
	}

	/** Test drainChar(int) removes a single character and returns it */
	@Test
	public void drainCharRemovesSingleCharacter() {
		array = createCharArrayWithString("Hello World!");

		char drained = array.drainChar(6);

		assertEquals('W', drained);
		assertEquals("Hello orld!", array.toString());
	}

	/** Test drainChars(int, int, char[], int) removes a range of characters correctly */
	@Test
	public void drainCharsRemovesRangeCorrectly() {
		array = createCharArrayWithString("Hello World!");

		char[] target = new char[5];
		int count = array.drainChars(0, 5, target, 0);

		assertEquals(5, count);
		assertArrayEquals(new char[] {'H', 'e', 'l', 'l', 'o'}, target);
		assertEquals(" World!", array.toString());
	}

	/** Test appendSeparator(char) adds separator only after first append */
	@Test
	public void appendSeparatorCharAddsOnlyAfterFirst() {
		array.appendSeparator(',');
		array.append("first");
		assertEquals("first", array.toString());

		array.appendSeparator(',');
		array.append("second");
		assertEquals("first,second", array.toString());
	}

	/** Test appendSeparator(char, char) adds default for empty array */
	@Test
	public void appendSeparatorWithDefaultAddsForEmptyArray() {
		array.appendSeparator(',', ';');

		assertEquals(";", array.toString());
	}

	/** Test appendSeparator(char, int) with loop index adds separators correctly */
	@Test
	public void appendSeparatorWithIndexAddsSeparators() {
		for (int i = 0; i < 3; i++) {
			array.appendSeparator(',', i);
			array.append("item" + i);
		}
		assertEquals("item0,item1,item2", array.toString());
	}

	/** Test that setSize throws IllegalArgumentException when newSize is negative. */
	@Test
	public void setSizeThrowsWhenNegative() {

		try {
			array.setSize(-1);
			fail("Expected IllegalArgumentException for negative size");
		} catch (IllegalArgumentException ex) {
			assertTrue(ex.getMessage().contains("newSize must be >= 0"));
		}
	}

	/** Test that setSize resizes when newSize is greater than current capacity. */
	@Test
	public void setSizeResizesWhenLarger() {
		array = new CharArray(2);
		char[] oldItems = array.items;

		array.setSize(10);

		assertNotSame(oldItems, array.items);
		assertEquals(10, array.size);
	}

	/**
	 * Tests that setSize does not resize when newSize fits within capacity.
	 */
	@Test
	public void setSizeNoResizeWhenWithinCapacity() {
		array = new CharArray(5);
		char[] oldItems = array.items;

		array.setSize(3);

		assertSame(oldItems, array.items);
		assertEquals(3, array.size);
	}

	/** Test toArray() returns a copy with correct contents */
	@Test
	public void toArrayReturnsCopyWithCorrectContents() {
		array.add('a');
		array.add('b');
		array.add('c');
		char[] result = array.toArray();

		assertArrayEquals(new char[] {'a', 'b', 'c'}, result);
		assertEquals(3, result.length);
		assertNotSame(array.items, result);
	}

	/** Test appendSeparator(String) adds string separators correctly */
	@Test
	public void appendSeparatorStringAddsSeparators() {
		array.append("A");
		array.appendSeparator(" | ");
		array.append("B");
		array.appendSeparator(" | ");
		array.append("C");

		assertEquals("A | B | C", array.toString());
	}

	/** Test insert(int, char[], int, int) inserts NULL placeholder when array is null */
	@Test
	public void insertCharArrayInsertsNullPlaceholderWhenArrayIsNull() {
		array.add('a');
		array.insert(0, null, 0, 0);

		assertEquals(5, array.size); // "null" = 4 chars + 'a'
		assertEquals('n', array.items[0]);
		assertEquals('u', array.items[1]);
		assertEquals('l', array.items[2]);
		assertEquals('l', array.items[3]);
		assertEquals('a', array.items[4]);
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

		assertEquals(1, array.size);
		assertEquals('a', array.items[0]);
	}

	/** Test insert(int, char[], int, int) inserts characters correctly */
	@Test
	public void insertCharArrayInsertsCharactersCorrectly() {
		array.add('a');
		char[] src = {'x','y','z'};
		array.insert(0, src, 0, 3);

		assertEquals(4, array.size); // 3 inserted + 1 original
		assertEquals('x', array.items[0]);
		assertEquals('y', array.items[1]);
		assertEquals('z', array.items[2]);
		assertEquals('a', array.items[3]);
	}

	/** Test insert(int, long) inserts the string representation of the long value */
	@Test
	public void insertInsertsStringOfLong() {
		array.add('a');
		array.insert(0, 123L);

		assertEquals(4, array.size); // "123" is 3 chars + original 'a'
		assertEquals('1', array.items[0]);
		assertEquals('2', array.items[1]);
		assertEquals('3', array.items[2]);
		assertEquals('a', array.items[3]);
	}

	/** Test insert(int, float) inserts the string representation of the float value */
	@Test
	public void insertInsertsStringOfFloat() {
		array.add('x');
		array.insert(0, 1.5f);

		String inserted = new String(array.items, 0, array.size);
		assertTrue(inserted.startsWith("1.5"));
		assertTrue(inserted.endsWith("x"));
	}

	/** Test insert(int, double) inserts the string representation of the double value */
	@Test
	public void insertInsertsStringOfDouble() {
		array.add('y');
		array.insert(0, 2.75);

		String inserted = new String(array.items, 0, array.size);
		assertTrue(inserted.startsWith("2.75"));
		assertTrue(inserted.endsWith("y"));
	}

	/** Test insert(int, Object) inserts object string when object is not null */
	@Test
	public void insertInsertsObjectStringWhenObjectIsNotNull() {
		array.add('a');
		array.insert(0, "b");

		assertEquals(2, array.size);
		assertEquals('b', array.items[0]);
	}

	/** Test insert(int, Object) inserts NULL placeholder when object is null */
	@Test
	public void insertInsertsNullPlaceholderWhenObjectIsNull() {
		array.add('a');
		array.insert(0, (Object) null);

		assertEquals(5, array.size); // 3 chars "null" + 'a'
		assertEquals('n', array.items[0]);
		assertEquals('u', array.items[1]);
		assertEquals('l', array.items[2]);
		assertEquals('l', array.items[3]);
		assertEquals('a', array.items[4]);
	}

	/** Test lastIndexOf(char, int) returns correct index when character is present */
	@Test
	public void lastIndexOfReturnsCorrectIndexWhenPresent() {
		array.add('a');
		array.add('b');
		array.add('c');
		array.add('b');

		assertEquals(3, array.lastIndexOf('b', 3));
		assertEquals(1, array.lastIndexOf('b', 2));
		assertEquals(0, array.lastIndexOf('a', 3));
	}

	/** Test lastIndexOf(char, int) returns -1 when character is not present */
	@Test
	public void lastIndexOfReturnsMinusOneWhenNotPresent() {
		array.add('a');
		array.add('b');
		array.add('c');

		assertEquals(-1, array.lastIndexOf('x', 2));
	}

	/** Test lastIndexOf(char, int) handles start index out of bounds */
	@Test
	public void lastIndexOfHandlesStartIndexOutOfBounds() {
		array.add('a');
		array.add('b');

		assertEquals(1, array.lastIndexOf('b', 10));  // start > size
		assertEquals(-1, array.lastIndexOf('a', -5)); // start < 0
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

		assertEquals(10, result);                  // newly read chars
		assertEquals(11, array.size);              // 1 old + 10 new
		char[] expected = new char[11];
		expected[0] = 'x';
		Arrays.fill(expected, 1, expected.length, 'y');
		assertArrayEquals(expected, Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Readable) delegates to Reader and returns correct delta */
	@Test
	public void readFromReadableDelegatesToReader() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "abcdefghij"; // 10 chars > initial capacity
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader);

		assertEquals(10, result);
		assertEquals(11, array.size);
		assertArrayEquals(("x" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Readable) delegates to CharBuffer and returns correct delta */
	@Test
	public void readFromReadableDelegatesToCharBuffer() {
		array.add('x'); // oldSize > 0
		CharBuffer buffer = CharBuffer.wrap(new char[] {'y', 'z'});

		int result = array.readFrom(buffer);

		assertEquals(2, result);
		assertEquals(3, array.size);
		assertArrayEquals(new char[] {'x', 'y', 'z'}, Arrays.copyOf(array.items, array.size));
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
			public int read(@NotNull CharBuffer cb) {
				if (pos >= data.length) return -1;
				int n = Math.min(cb.remaining(), data.length - pos);
				cb.put(data, pos, n);
				pos += n;
				return n;
			}
		};

		int result = array.readFrom(readable);

		assertEquals(10, result);
		assertEquals(11, array.size);
		char[] expected = new char[11];
		expected[0] = 'x';
		Arrays.fill(expected, 1, expected.length, 'p');
		assertArrayEquals(expected, Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader) triggers require(), resizes buffer, and returns correct delta */
	@Test
	public void readFromReaderTriggersRequire() throws IOException {
		array.add('x'); // oldSize > 0
		String input = "abcdef"; // longer than initial capacity
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader);

		assertEquals(input.length(), result);       // newly read chars
		assertEquals(input.length() + 1, array.size); // old + new
		assertArrayEquals(("x" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader, int) triggers require(), resizes buffer, and returns correct delta */
	@Test
	public void readFromReaderWithCountTriggersRequire() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "abcdefghij"; // 10 chars
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader, input.length());

		assertEquals(10, result);
		assertEquals(11, array.size);
		assertArrayEquals(("x" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
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
			public int read(@NotNull CharBuffer cb) {
				if (pos >= data.length) return -1;
				int n = Math.min(cb.remaining(), data.length - pos);
				cb.put(data, pos, n);
				pos += n;
				return n;
			}
		};

		int result = array.readFrom(readable);

		assertEquals(input.length(), result);
		assertEquals(input.length() + 1, array.size);
		assertArrayEquals(("z" + input).toCharArray(), Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader) returns -1 when reader is empty */
	@Test
	public void readFromReturnsMinusOneWhenReaderEmpty() throws IOException {
		Reader reader = new StringReader(""); // immediate EOS

		int result = array.readFrom(reader);

		assertEquals(-1, result);
		assertEquals(0, array.size);
	}

	/** Test readFrom(Reader) reads all characters until end of stream */
	@Test
	public void readFromReadsAllCharactersUntilEndOfStream() throws IOException {
		Reader reader = new StringReader("abc");

		int result = array.readFrom(reader);

		assertEquals(3, result);
		assertEquals(3, array.size);
		assertArrayEquals(new char[] {'a', 'b', 'c'},
				Arrays.copyOf(array.items, array.size));
	}

	/** Test readFrom(Reader, int) returns -1 when reader is empty */
	@Test
	public void readFromWithCountReturnsMinusOneWhenReaderEmpty() throws IOException {
		Reader reader = new StringReader(""); // immediate EOS

		int result = array.readFrom(reader, 3);

		assertEquals(-1, result);
		assertEquals(0, array.size);
	}

	/** Test readFrom(Reader, int) returns 0 when count is zero or negative */
	@Test
	public void readFromWithCountReturnsZeroForNonPositiveCount() throws IOException {
		Reader reader = new StringReader("abc");

		assertEquals(0, array.readFrom(reader, 0));
		assertEquals(0, array.readFrom(reader, -5));
	}

	/** Test readFrom(Reader, int) reads exactly count characters */
	@Test
	public void readFromWithCountReadsExactNumberOfCharacters() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "abcdef";
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader, 3); // count < input.length()

		assertEquals(3, result);
		assertEquals(4, array.size); // 1 old + 3 new
		assertArrayEquals(new char[] {'x', 'a', 'b', 'c'}, Arrays.copyOf(array.items, array.size));
	}


	/** Test readFrom(Reader, int) reads until EOS if reader has fewer chars than count */
	@Test
	public void readFromWithCountReadsUntilEos() throws IOException {
		array = new CharArray(5);
		array.add('x');
		String input = "ab"; // fewer than count
		Reader reader = new StringReader(input);

		int result = array.readFrom(reader, 5); // count > input.length()

		assertEquals(2, result);
		assertEquals(3, array.size); // 1 old + 2 new
		assertArrayEquals(new char[] {'x', 'a', 'b'}, Arrays.copyOf(array.items, array.size));
	}

	/** Test random() returns default for empty array */
	@Test
	public void randomReturnsDefaultForEmptyArray() {
		char emptyRandom = array.random();

		assertEquals('\u0000', emptyRandom);
	}

	/** Test random() returns an element from the array */
	@Test
	public void randomReturnsElementFromArray() {
		array.addAll('a', 'b', 'c', 'd', 'e');
		char random = array.random();

		assertTrue(array.contains(random));
	}

	/** Test shuffle() maintains size and all elements */
	@Test
	public void shuffleMaintainsSizeAndElements() {
		array.addAll('a', 'b', 'c', 'd', 'e');
		array.shuffle();

		assertEquals(5, array.size);
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
		assertEquals("Start: Hello World", sb.toString());
	}

	/** Test appendTo(StringBuffer) appends the CharArray content correctly */
	@Test
	public void appendToStringBufferAppendsContent() throws IOException {
		array = createCharArrayWithString("Hello World");

		StringBuffer sbuf = new StringBuffer("Start: ");
		array.appendTo(sbuf);
		assertEquals("Start: Hello World", sbuf.toString());
	}

	/** Test setCharAt(int, char) sets a single character correctly */
	@Test
	public void setCharAtSetsSingleCharacter() {
		array = createCharArrayWithString("Hello");

		array.setCharAt(1, 'a');

		assertEquals("Hallo", array.toString());
	}

	/** Test setCharAt(int, char) supports chaining for multiple updates */
	@Test
	public void setCharAtSupportsChaining() {
		array = createCharArrayWithString("Hello");

		array.setCharAt(1, 'a').setCharAt(2, 'p').setCharAt(3, 'p').setCharAt(4, 'y');

		assertEquals("Happy", array.toString());
	}

	/** Test toStringAndClear */
	@Test
	public void toStringAndClearTest () {
		CharArray array = createCharArrayWithString("Test String");
		String result = array.toStringAndClear();

		assertEquals("Test String", result);
		assertEquals(0, array.size);
		assertTrue(array.isEmpty());
	}

	/** Test toString(String) joins multiple elements with a separator */
	@Test
	public void toStringWithSeparatorReturnsJoinedElements() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		String result = array.toString(",");
		assertEquals("a,b,c,d,e", result);
	}

	/** Test toString(String) returns empty string for empty array */
	@Test
	public void toStringWithSeparatorReturnsEmptyForEmptyArray() {
		assertEquals("", array.toString(","));
	}

	/** Test toString(String) returns element itself for single-element array */
	@Test
	public void toStringWithSeparatorReturnsSingleElement() {
		array.add('x');

		assertEquals("x", array.toString(","));
	}

	/** Test operations on an empty array return expected results */
	@Test
	public void emptyArrayOperationsReturnExpected() {
		assertEquals(-1, array.indexOf('a'));
		assertEquals(-1, array.lastIndexOf('a'));
		assertFalse(array.contains('a'));
		assertFalse(array.removeValue('a'));
	}

	/** Test CharArray can handle very large capacity */
	@Test
	public void largeCapacityArrayHandlesManyElements() {
		CharArray large = new CharArray(10000);

		for (int i = 0; i < 10000; i++) {
			large.add((char)('A' + (i % 26)));
		}
		assertEquals(10000, large.size);
	}



	/** Helper method for specific initialization */
	private CharArray createCharArrayWithString (String value) {
		return new CharArray(value);
	}
}
