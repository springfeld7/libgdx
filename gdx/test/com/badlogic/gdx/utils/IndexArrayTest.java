
package com.badlogic.gdx.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.graphics.glutils.IndexArray;

public class IndexArrayTest {
	@BeforeClass
	public static void loadNatives () {
		// Ensure libGDX native libraries are extracted and loaded for tests that call JNI methods
		com.badlogic.gdx.utils.GdxNativesLoader.load();
	}

	// Helper method to access private final fields using reflection
	private Object getPrivateFieldValue (Object instance, String fieldName) throws Exception {
		Field field = IndexArray.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(instance);
	}

	/** Test Constructor with positive max indices and getBuffer non-null contract */
	@SuppressWarnings("deprecation")
	@Test
	public void testConstructor_PositiveMaxIndices () throws Exception {
		int maxIndices = 10;
		IndexArray indexArray = new IndexArray(maxIndices);

		// Test empty field
		Boolean empty = (Boolean)getPrivateFieldValue(indexArray, "empty");
		assertFalse("The 'empty' flag should be false for maxIndices > 0", empty);

		// Test byteBuffer and buffer fields
		ByteBuffer byteBuffer = (ByteBuffer)getPrivateFieldValue(indexArray, "byteBuffer");
		assertNotNull("The byteBuffer should not be null", byteBuffer);
		assertEquals("ByteBuffer capacity should be maxIndices * 2 (" + maxIndices * 2 + ")", maxIndices * 2,
			byteBuffer.capacity());

		ShortBuffer shortBuffer = (ShortBuffer)getPrivateFieldValue(indexArray, "buffer");
		assertNotNull("The shortBuffer should not be null", shortBuffer);
		assertEquals("ShortBuffer capacity should be maxIndices (" + maxIndices + ")", maxIndices, shortBuffer.capacity());

		// Test position and limit after flip()
		assertEquals("ShortBuffer position after flip() should be 0", 0, shortBuffer.position());
		assertEquals("ShortBuffer limit after flip() should be 0", 0, shortBuffer.limit());
		assertEquals("ByteBuffer position after flip() should be 0", 0, byteBuffer.position());
		assertEquals("ByteBuffer limit after flip() should be 0", 0, byteBuffer.limit());

		// Assert getBuffer() and getBuffer(true) are non-null for valid IndexArray
		assertNotNull("getBuffer() should not return null for valid IndexArray", indexArray.getBuffer());
		assertNotNull("getBuffer(true) should not return null for valid IndexArray", indexArray.getBuffer(true));
	}

	/** Test Constructor with zero max indices */
	@Test
	public void testConstructor_ZeroMaxIndices () throws Exception {
		int maxIndices = 0;
		IndexArray indexArray = new IndexArray(maxIndices);

		// Test empty field
		Boolean empty = (Boolean)getPrivateFieldValue(indexArray, "empty");
		assertTrue("The 'empty' flag should be true for maxIndices = 0", empty);

		// Test byteBuffer and buffer fields
		int expectedCapacity = 1 * 2;

		ByteBuffer byteBuffer = (ByteBuffer)getPrivateFieldValue(indexArray, "byteBuffer");
		assertNotNull("The byteBuffer should not be null", byteBuffer);
		assertEquals("ByteBuffer capacity should be 1 * 2 (as maxIndices=0 is handled internally)", expectedCapacity,
			byteBuffer.capacity());

		ShortBuffer shortBuffer = (ShortBuffer)getPrivateFieldValue(indexArray, "buffer");
		assertNotNull("The shortBuffer should not be null", shortBuffer);
		assertEquals("ShortBuffer capacity should be 1", 1, shortBuffer.capacity());
	}

	/** Test Constructor with negative max indices */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NegativeMaxIndices () {
		int maxIndices = -5;

		new IndexArray(maxIndices);
	}

	// Test data for the short[] array tests
	private final short[] testIndices = {10, 20, 30, 40, 50, 60};

	/** Test metod to set indices using short array */
	@Test
	public void testSetIndices_ShortArray () throws Exception {
		IndexArray indexArray = new IndexArray(10);
		ShortBuffer buffer = (ShortBuffer)getPrivateFieldValue(indexArray, "buffer");
		ByteBuffer byteBuffer = (ByteBuffer)getPrivateFieldValue(indexArray, "byteBuffer");

		int offset = 1; // Start copying from 20
		int count = 4; // Copy 4 elements (20, 30, 40, 50)

		// Call set indices
		indexArray.setIndices(testIndices, offset, count);

		// Assert ShortBuffer State
		assertEquals("ShortBuffer position must be 0 after flip()", 0, buffer.position());
		assertEquals("ShortBuffer limit must be equal to count (4) after flip()", count, buffer.limit());
		assertEquals("ShortBuffer capacity must remain unchanged (10)", 10, buffer.capacity());

		// Assert ByteBuffer State
		int expectedByteLimit = count * 2;
		assertEquals("ByteBuffer position must be 0", 0, byteBuffer.position());
		assertEquals("ByteBuffer limit must be copied elements * 2 (" + expectedByteLimit + ")", expectedByteLimit,
			byteBuffer.limit());

		// Assert Data verification (check first two copied indices)
		assertEquals("First index must be the one at offset 1 (" + testIndices[offset] + ")", testIndices[offset], buffer.get(0));
		assertEquals("Second index must be the one at offset 2 (" + (offset + 1) + ")", testIndices[offset + 1], buffer.get(1));
	}

	/** Test metod to set indices using short buffer indices */
	@Test
	public void testSetIndices_ShortBuffer () throws Exception {
		IndexArray indexArray = new IndexArray(10);
		ShortBuffer buffer = (ShortBuffer)getPrivateFieldValue(indexArray, "buffer");
		ByteBuffer byteBuffer = (ByteBuffer)getPrivateFieldValue(indexArray, "byteBuffer");

		ShortBuffer inputBuffer = ShortBuffer.wrap(testIndices);

		// Set up the input buffer
		int inputOffset = 2; // Position 2 (value 30)
		int inputCount = 3; // Remaining 3 elements

		((Buffer)inputBuffer).position(inputOffset);
		((Buffer)inputBuffer).limit(inputOffset + inputCount); // limit 5

		// Current remaining count is 3
		int count = inputBuffer.remaining(); // 3

		// Call set indices
		indexArray.setIndices(inputBuffer);

		// Assert ShortBuffer State
		assertEquals("ShortBuffer position must be 0 after flip()", 0, buffer.position());
		assertEquals("ShortBuffer limit must be equal to count (" + count + ") after flip()", count, buffer.limit());

		// Assert Input Buffer State
		assertEquals("Input ShortBuffer position must be restored to its original pos (" + inputOffset + ")", inputOffset,
			inputBuffer.position());

		int expectedByteLimit = count * 2;
		assertEquals("ByteBuffer position must be 0", 0, byteBuffer.position());
		assertEquals("ByteBuffer limit must be remaining * 2 (" + count * 2 + ")", expectedByteLimit, byteBuffer.limit());

		// Assert Data verification (check first two copied indices)
		assertEquals("First index must be the one from the input buffer at pos 2 (" + inputBuffer.get(2) + ")", inputBuffer.get(2),
			buffer.get(0));
		assertEquals("Second index must be the one from the input buffer at pos 3 (" + inputBuffer.get(3) + ")", inputBuffer.get(3),
			buffer.get(1));
	}

	/** Test method to update indices */
	@Test
	public void testUpdateIndices () throws Exception {
		final int maxIndices = 10;
		final int initialSetCount = 6;
		final int initialByteBufferPos = 4; // Corresponds to 2 shorts (for position restoration test)

		IndexArray indexArray = new IndexArray(maxIndices);
		ShortBuffer buffer = (ShortBuffer)getPrivateFieldValue(indexArray, "buffer");
		ByteBuffer byteBuffer = (ByteBuffer)getPrivateFieldValue(indexArray, "byteBuffer");

		// Initialize with indices
		indexArray.setIndices(testIndices, 0, initialSetCount);

		// Set initial ByteBuffer position
		((Buffer)byteBuffer).position(initialByteBufferPos);

		// Data to update with
		short[] updateData = {80, 85, 90, 95, 100};

		int targetOffset = 2; // Start updating at index 2 (value 30)
		int sourceOffset = 2; // Start reading from 90 in updateData
		int count = 2; // Copy 2 elements (90, 95)

		// Call update indices
		indexArray.updateIndices(targetOffset, updateData, sourceOffset, count);

		// Assert State verification
		int expectedByteLimitAfterCopy = (targetOffset * 2) + (count * 2);

		assertEquals("ShortBuffer limit must remain unchanged (" + initialSetCount + ")", 6, buffer.limit());
		assertEquals("ByteBuffer must be targetOffset * 2 + count * 2 (" + expectedByteLimitAfterCopy + ")",
			expectedByteLimitAfterCopy, byteBuffer.limit());

		// Assert ByteBuffer position
		assertEquals("ByteBuffer position must be restored to its initial value (" + initialByteBufferPos + ")",
			initialByteBufferPos, byteBuffer.position());

		// Assert Data verification
		short[] expected = {10, 20, 90, 95, 50, 60};
		((Buffer)buffer).position(0);
		short[] actual = new short[buffer.limit()];
		buffer.get(actual);

		assertArrayEquals("The contents of the IndexArray buffer should match the expected updated sequence.", expected, actual);
	}

	/** Test methods getNumIndices and getNumMaxIndices */
	@Test
	public void testGetNumIndices_AfterSimulation () throws Exception {
		int maxIndices = 20;
		IndexArray indexArray = new IndexArray(maxIndices);
		ShortBuffer buffer = (ShortBuffer)getPrivateFieldValue(indexArray, "buffer");

		int simulatedIndices = 12;
		short[] inputIndices = new short[simulatedIndices];
		indexArray.setIndices(inputIndices, 0, simulatedIndices);

		// Test getNumIndices
		assertEquals("Number of indices should be " + simulatedIndices + " after simulating a write using setIndices.",
			simulatedIndices, indexArray.getNumIndices());

		// Test getNumMaxIndices
		assertEquals("Max indices (capacity) should remain " + maxIndices + ".", maxIndices, indexArray.getNumMaxIndices());

		// Verify buffer limit
		assertEquals("The internal buffer limit must be " + simulatedIndices + " after setIndices.", simulatedIndices,
			buffer.limit());
	}

	/** Test getNumMaxIndices and getNumIndices when 'empty' is false */
	@Test
	public void testNumIndicesAndMax_InitialNonEmptyCase () throws Exception {
		int maxIndices = 50;
		IndexArray indexArray = new IndexArray(maxIndices);
		ShortBuffer buffer = (ShortBuffer)getPrivateFieldValue(indexArray, "buffer");
		boolean empty = (Boolean)getPrivateFieldValue(indexArray, "empty");

		// Test getNumMaxIndices
		assertEquals("Max indices should equal the capacity of the buffer (" + buffer.capacity() + ").", maxIndices,
			indexArray.getNumMaxIndices());

		// Test getNumIndices
		assertEquals("Number of indices should be equal to the limit of the buffer (" + buffer.limit() + ").", buffer.limit(),
			indexArray.getNumIndices());

		// Assert conditional empty flag
		assertFalse("The 'empty' flag should be false for maxIndices > 0", empty);
	}

	/** Test getNumMaxIndices and getNumIndices when 'empty' is true */
	@Test
	public void testGetters_EmptyCase () throws Exception {
		int maxIndices = 0;
		IndexArray indexArray = new IndexArray(maxIndices);
		boolean empty = (Boolean)getPrivateFieldValue(indexArray, "empty");

		// Test getNumMaxIndices
		assertEquals("When 'empty' is true, max indices should be " + maxIndices + ".", 0, indexArray.getNumMaxIndices());

		// Test getNumIndices
		assertEquals("When 'empty' is true, current indices should be " + maxIndices + ".", 0, indexArray.getNumIndices());

		// Assert conditional empty flag
		assertTrue("The 'empty' flag should be true for maxIndices == 0", empty);
	}

	/** Test method dispose */
	@Test
	public void testDispose_ReleasesUnsafeByteBuffer () throws Exception {
		int before = com.badlogic.gdx.utils.BufferUtils.getAllocatedBytesUnsafe();

		IndexArray indexArray = new IndexArray(3);
		ByteBuffer byteBuffer = (ByteBuffer)getPrivateFieldValue(indexArray, "byteBuffer");

		// Assert buffer is tracked as an unsafe buffer
		assertTrue("ByteBuffer should be tracked as unsafe before dispose",
			com.badlogic.gdx.utils.BufferUtils.isUnsafeByteBuffer(byteBuffer));

		// Call dispose
		indexArray.dispose();

		// Assert buffer is NOT tracked as unsafe
		assertFalse("ByteBuffer should NOT be tracked as unsafe after dispose",
			com.badlogic.gdx.utils.BufferUtils.isUnsafeByteBuffer(byteBuffer));

		// Assert allocated bytes has decreased
		assertEquals("Allocated unsafe bytes should decrease after dispose", before,
			com.badlogic.gdx.utils.BufferUtils.getAllocatedBytesUnsafe());
	}

}
