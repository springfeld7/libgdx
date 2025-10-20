package com.badlogic.gdx.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
		CharArray array = new CharArray("hello");

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

	/** Test set method */
	@Test
	public void set() {
		array.addAll('a', 'b', 'c', 'd', 'e');

		array.set(2, 'Z');
		assertEquals('Z', array.get(2));
	}

	/** Test increment of a single element */
	@Test
	public void incrSingle() {
		array.addAll((char)65); // 'A'
		array.incr(0, (char)1);

		assertEquals((char)66, array.get(0)); // 'B'
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


	/** Test replace methods */
	@Test
	public void replaceTest () {
		CharArray array = createCharArrayWithString("Hello World!");

		// Replace first char
		assertTrue(array.replaceFirst('l', 'L'));
		assertEquals("HeLlo World!", array.toString());
		assertFalse(array.replaceFirst('z', 'Z'));

		// Replace all chars
		array = new CharArray("Hello World!");
		int count = array.replaceAll('l', 'L');
		assertEquals(3, count);
		assertEquals("HeLLo WorLd!", array.toString());

		// Replace string range
		array = new CharArray("Hello World!");
		array.replace(0, 5, "Hi");
		assertEquals("Hi World!", array.toString());

		// Replace all strings
		array = new CharArray("Hello World! Hello!");
		array.replaceAll("Hello", "Hi");
		assertEquals("Hi World! Hi!", array.toString());

		// Replace first string
		array = new CharArray("Hello World! Hello!");
		array.replaceFirst("Hello", "Hi");
		assertEquals("Hi World! Hello!", array.toString());

		// Replace char with string
		array = new CharArray("a-b-c");
		array.replace('-', " to ");
		assertEquals("a to b to c", array.toString());
	}

	/** Test insert methods */
	@Test
	public void insertTest () {
		CharArray array = createCharArrayWithString("Hello!");

		// Insert char
		array.insert(5, ' ');
		assertEquals("Hello !", array.toString());

		// Insert string
		array.insert(6, "World");
		assertEquals("Hello World!", array.toString());

		// Insert at beginning
		array.insert(0, "Say ");
		assertEquals("Say Hello World!", array.toString());

		// Insert boolean
		array = new CharArray("Value: ");
		array.insert(7, true);
		assertEquals("Value: true", array.toString());

		// Insert numbers
		array = new CharArray("Number: ");
		array.insert(8, 42);
		assertEquals("Number: 42", array.toString());

		// Insert char array
		array = new CharArray("AB");
		char[] chars = {'C', 'D', 'E'};
		array.insert(1, chars);
		assertEquals("ACDEB", array.toString());

		// Insert range
		array = new CharArray("AC");
		array.insertRange(1, 2);
		array.set(1, 'B');
		array.set(2, 'B');
		assertEquals("ABBC", array.toString());
	}

	/** Test substring methods */
	@Test
	public void substringTest () {
		CharArray array = createCharArrayWithString("Hello World!");

		// Substring
		assertEquals("Hello", array.substring(0, 5));
		assertEquals("World!", array.substring(6));
		assertEquals("World", array.substring(6, 11));

		// LeftString
		assertEquals("Hello", array.leftString(5));
		assertEquals("", array.leftString(0));
		assertEquals("Hello World!", array.leftString(20)); // More than length

		// RightString
		assertEquals("World!", array.rightString(6));
		assertEquals("", array.rightString(0));
		assertEquals("Hello World!", array.rightString(20)); // More than length

		// MidString
		assertEquals("World", array.midString(6, 5));
		assertEquals("", array.midString(6, 0));
		assertEquals("World!", array.midString(6, 10)); // More than available
	}

	/** Test string comparison methods */
	@Test
	public void stringComparisonTest () {
		CharArray array = createCharArrayWithString("Hello World");

		// StartsWith
		assertTrue(array.startsWith("Hello"));
		assertFalse(array.startsWith("World"));
		assertTrue(array.startsWith(""));

		// EndsWith
		assertTrue(array.endsWith("World"));
		assertFalse(array.endsWith("Hello"));
		assertTrue(array.endsWith(""));

		// Contains
		assertTrue(array.contains("Hello"));
		assertTrue(array.contains("World"));
		assertTrue(array.contains(" "));
		assertFalse(array.contains("xyz"));

		// ContainsIgnoreCase
		assertTrue(array.containsIgnoreCase("hello"));
		assertTrue(array.containsIgnoreCase("WORLD"));
		assertFalse(array.containsIgnoreCase("xyz"));

		// Equals
		CharArray other = new CharArray("Hello World");
		assertTrue(array.equals(other));
		assertTrue(array.equalsString("Hello World"));

		other.append("!");
		assertFalse(array.equals(other));

		// EqualsIgnoreCase
		CharArray upper = new CharArray("HELLO WORLD");
		assertTrue(array.equalsIgnoreCase(upper));
		assertTrue(array.equalsIgnoreCase("hello world"));
	}

	/** Test CharSequence methods */
	@Test
	public void charSequenceTest () {
		CharArray array = createCharArrayWithString("Hello World!");

		// Length
		assertEquals(12, array.length());

		// CharAt
		assertEquals('H', array.charAt(0));
		assertEquals('!', array.charAt(11));

		// SubSequence
		CharSequence sub = array.subSequence(0, 5);
		assertEquals("Hello", sub.toString());
	}

	/** Test trim and capacity methods */
	@Test
	public void trimCapacityTest () {
		CharArray array = new CharArray(100);
		array.append("Hello");

		assertEquals(100, array.capacity());
		assertEquals(5, array.size);

		// Trim
		array.trim();
		assertEquals("Hello", array.toString());
		assertEquals(5, array.size);

		// TrimToSize
		array = new CharArray(100);
		array.append("Test");
		array.trimToSize();
		assertEquals(4, array.capacity());

		// SetLength
		array = new CharArray("Hello");
		array.setLength(3);
		assertEquals("Hel", array.toString());

		array.setLength(5);
		assertEquals(5, array.length());
		// Extended with null chars
	}

	/** Test hashCode and equals */
	@Test
	public void hashCodeEqualsTest () {
		CharArray array1 = new CharArray("Hello");
		CharArray array2 = new CharArray("Hello");
		CharArray array3 = new CharArray("World");

		// Equals
		assertTrue(array1.equals(array1));
		assertTrue(array1.equals(array2));
		assertFalse(array1.equals(array3));
		assertFalse(array1.equals(null));
		assertFalse(array1.equals("Hello")); // Different type

		// HashCode
		assertEquals(array1.hashCode(), array2.hashCode());
		assertNotEquals(array1.hashCode(), array3.hashCode());
	}

	/** Test Reader and Writer */
	@Test
	public void readerWriterTest () throws IOException {
		CharArray array = createCharArrayWithString("Hello World!");

		// Test Reader with try-with-resources
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

		// Test Writer with try-with-resources
		CharArray array2 = new CharArray();
		try (Writer writer = array2.writer()) {
			writer.write("Test");
			assertEquals("Test", array2.toString());

			writer.write(' ');
			writer.write(new char[] {'1', '2', '3'});
			assertEquals("Test 123", array2.toString());
		}
	}

	/** Test Unicode/code point methods */
	@Test
	public void unicodeTest () {

		// Append code point (emoji)
		int smiley = 0x1F600; // 😀
		array.appendCodePoint(smiley);
		assertEquals(2, array.size); // Surrogate pair

		// Code point at
		int cp = array.codePointAt(0);
		assertEquals(smiley, cp);

		// Code point count
		array.append("Hello");
		int count = array.codePointCount(0, array.size);
		assertEquals(6, count); // 1 emoji + 5 chars

		// Reverse with code points
		CharArray array2 = new CharArray();
		array2.appendCodePoint(0x1F600); // 😀
		array2.append("Hi");
		array2.reverseCodePoints();
		assertEquals("iH", array2.substring(0, 2));
	}

	/** Test iterator methods */
	@Test
	public void iteratorTest () {

		// AppendAll with Iterable
		ArrayList<String> list = new ArrayList<>();
		list.add("One");
		list.add("Two");
		list.add("Three");
		array.appendAll(list);
		assertEquals("OneTwoThree", array.toString());

		// AppendWithSeparators
		array.clear();
		array.appendWithSeparators(list, ", ");
		assertEquals("One, Two, Three", array.toString());

		// With Iterator
		array.clear();
		Iterator<String> iter = list.iterator();
		array.appendAll(iter);
		assertEquals("OneTwoThree", array.toString());

		// AppendWithSeparators with array
		array.clear();
		String[] strArray = {"A", "B", "C"};
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

	/** Test toArray conversions */
	@Test
	public void toArrayTest () {
		array.addAll('a', 'b', 'c', 'd', 'e');

		// ToCharArray
		char[] chars = array.toCharArray();
		assertArrayEquals(new char[] {'a', 'b', 'c', 'd', 'e'}, chars);

		// GetChars
		char[] target = new char[10];
		array.getChars(1, 4, target, 2);
		assertEquals('b', target[2]);
		assertEquals('c', target[3]);
		assertEquals('d', target[4]);
	}

	/** Test drain methods */
	@Test
	public void drainTest () {
		CharArray array = createCharArrayWithString("Hello World!");

		// DrainChar
		char drained = array.drainChar(6);
		assertEquals('W', drained);
		assertEquals("Hello orld!", array.toString());

		// DrainChars
		char[] target = new char[5];
		int count = array.drainChars(0, 5, target, 0);
		assertEquals(5, count);
		assertArrayEquals(new char[] {'H', 'e', 'l', 'l', 'o'}, target);
		assertEquals(" orld!", array.toString());
	}

	/** Test appendSeparator variations */
	@Test
	public void appendSeparatorTest () {

		// First append - no separator
		array.appendSeparator(',');
		array.append("first");
		assertEquals("first", array.toString());

		// Second append - separator added
		array.appendSeparator(',');
		array.append("second");
		assertEquals("first,second", array.toString());

		// With default for empty
		CharArray array2 = new CharArray();
		array2.appendSeparator(',', ';');
		assertEquals(";", array2.toString());

		// With loop index
		CharArray array3 = new CharArray();
		for (int i = 0; i < 3; i++) {
			array3.appendSeparator(',', i);
			array3.append("item" + i);
		}
		assertEquals("item0,item1,item2", array3.toString());

		// String separators
		CharArray array4 = new CharArray();
		array4.append("A");
		array4.appendSeparator(" | ");
		array4.append("B");
		array4.appendSeparator(" | ");
		array4.append("C");
		assertEquals("A | B | C", array4.toString());
	}

	/** Test random access */
	@Test
	public void randomTest () {

		// Test random() when the array is empty
		char emptyRandom = array.random();
		assertEquals('\u0000', emptyRandom);

		array.addAll('a', 'b', 'c', 'd', 'e');

		// Random should return one of the elements
		char random = array.random();
		assertTrue(array.contains(random));

		// Shuffle - just verify size is maintained
		array.shuffle();
		assertEquals(5, array.size);
		// Elements should still be there, just in different order
		assertTrue(array.contains('a'));
		assertTrue(array.contains('b'));
		assertTrue(array.contains('c'));
		assertTrue(array.contains('d'));
		assertTrue(array.contains('e'));
	}

	/** Test appendTo method */
	@Test
	public void appendToTest () throws IOException {
		CharArray array = createCharArrayWithString("Hello World");

		// Append to StringBuilder
		StringBuilder sb = new StringBuilder("Start: ");
		array.appendTo(sb);
		assertEquals("Start: Hello World", sb.toString());

		// Append to StringBuffer
		StringBuffer sbuf = new StringBuffer("Start: ");
		array.appendTo(sbuf);
		assertEquals("Start: Hello World", sbuf.toString());
	}

	/** Test setCharAt */
	@Test
	public void setCharAtTest () {
		CharArray array = createCharArrayWithString("Hello");
		array.setCharAt(1, 'a');
		assertEquals("Hallo", array.toString());

		// Chain calls
		array.setCharAt(2, 'p').setCharAt(3, 'p').setCharAt(4, 'y');
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

	/** Test toString with separator */
	@Test
	public void toStringWithSeparatorTest () {

		array.addAll('a', 'b', 'c', 'd', 'e');

		String result = array.toString(",");
		assertEquals("a,b,c,d,e", result);

		// Empty array
		CharArray empty = new CharArray();
		assertEquals("", empty.toString(","));

		// Single element
		CharArray single = new CharArray();
		single.add('x');
		assertEquals("x", single.toString(","));
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

	/** Helper method for specific initialization */
	private CharArray createCharArrayWithString (String value) {
		return new CharArray(value);
	}
}
