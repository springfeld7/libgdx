
package com.badlogic.gdx.graphics.glutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.ETC1.ETC1Data;
import com.badlogic.gdx.utils.BufferUtils;

public class ETC1Test {
	@BeforeClass
	public static void loadNatives () {
		// Ensure libGDX native libraries are extracted and loaded for tests that call JNI methods
		com.badlogic.gdx.utils.GdxNativesLoader.load();
	}

	/** Test getCompressedDataSize with valid dimensions */
	@Test
	public void testGetCompressedDataSize_ValidDimensions () {
		int width = 16;
		int height = 16;

		int compressedSize = ETC1.getCompressedDataSize(width, height);

		// ETC1 uses 4x4 blocks, each compressed to 8 bytes
		int expectedBlocks = (width / 4) * (height / 4);
		int expectedSize = expectedBlocks * 8;

		assertEquals("Compressed data size for 16x16 should be " + expectedSize + " bytes", expectedSize, compressedSize);
	}

	/** Test getCompressedDataSize with different dimensions */
	@Test
	public void testGetCompressedDataSize_DifferentDimensions () {
		int width = 64;
		int height = 32;

		int compressedSize = ETC1.getCompressedDataSize(width, height);

		// ETC1 uses 4x4 blocks, each compressed to 8 bytes
		int expectedBlocks = (width / 4) * (height / 4);
		int expectedSize = expectedBlocks * 8;

		assertEquals("Compressed data size for 64x32 should be " + expectedSize + " bytes", expectedSize, compressedSize);
	}

	/** Test formatHeader writes PKM header correctly */
	@Test
	public void testFormatHeader_WritesHeader () {
		int width = 32;
		int height = 32;

		ByteBuffer header = BufferUtils.newByteBuffer(ETC1.PKM_HEADER_SIZE);

		// Call format header
		ETC1.formatHeader(header, 0, width, height);

		// Verify header was written (not all zeros)
		boolean hasNonZeroBytes = false;
		for (int i = 0; i < ETC1.PKM_HEADER_SIZE; i++) {
			if (header.get(i) != 0) {
				hasNonZeroBytes = true;
				break;
			}
		}

		assertTrue("PKM header should contain non-zero bytes after formatting", hasNonZeroBytes);
	}

	/** Test getWidthPKM retrieves correct width from header */
	@Test
	public void testGetWidthPKM_RetrievesCorrectWidth () {
		int expectedWidth = 64;
		int height = 32;

		ByteBuffer header = BufferUtils.newByteBuffer(ETC1.PKM_HEADER_SIZE);
		ETC1.formatHeader(header, 0, expectedWidth, height);

		int actualWidth = ETC1.getWidthPKM(header, 0);

		assertEquals("Width retrieved from PKM header should match the width used to format it", expectedWidth, actualWidth);
	}

	/** Test getHeightPKM retrieves correct height from header */
	@Test
	public void testGetHeightPKM_RetrievesCorrectHeight () {
		int width = 64;
		int expectedHeight = 128;

		ByteBuffer header = BufferUtils.newByteBuffer(ETC1.PKM_HEADER_SIZE);
		ETC1.formatHeader(header, 0, width, expectedHeight);

		int actualHeight = ETC1.getHeightPKM(header, 0);

		assertEquals("Height retrieved from PKM header should match the height used to format it", expectedHeight, actualHeight);
	}

	/** Test isValidPKM returns true for valid header */
	@Test
	public void testIsValidPKM_ValidHeader () {
		int width = 32;
		int height = 32;

		ByteBuffer header = BufferUtils.newByteBuffer(ETC1.PKM_HEADER_SIZE);
		ETC1.formatHeader(header, 0, width, height);

		boolean isValid = ETC1.isValidPKM(header, 0);

		assertTrue("PKM header should be valid after formatting", isValid);
	}

	/** Test isValidPKM returns false for invalid header */
	@Test
	public void testIsValidPKM_InvalidHeader () {
		ByteBuffer header = BufferUtils.newByteBuffer(ETC1.PKM_HEADER_SIZE);
		// Leave header uninitialized (all zeros)

		boolean isValid = ETC1.isValidPKM(header, 0);

		assertFalse("PKM header should be invalid when not formatted", isValid);
	}

	/** Test formatHeader with offset */
	@Test
	public void testFormatHeader_WithOffset () {
		int width = 16;
		int height = 16;
		int offset = 8;

		ByteBuffer header = BufferUtils.newByteBuffer(ETC1.PKM_HEADER_SIZE + offset);
		ETC1.formatHeader(header, offset, width, height);

		int actualWidth = ETC1.getWidthPKM(header, offset);
		int actualHeight = ETC1.getHeightPKM(header, offset);

		assertEquals("Width should be correct when using offset", width, actualWidth);
		assertEquals("Height should be correct when using offset", height, actualHeight);
	}

	/** Test ETC1Data constructor with valid parameters */
	@Test
	public void testETC1Data_Constructor_ValidParameters () {
		int width = 32;
		int height = 32;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize);
		int dataOffset = 0;

		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, dataOffset);

		assertNotNull("ETC1Data instance should not be null", etc1Data);
		assertEquals("Width should match constructor parameter", width, etc1Data.width);
		assertEquals("Height should match constructor parameter", height, etc1Data.height);
		assertEquals("Data offset should match constructor parameter", dataOffset, etc1Data.dataOffset);
		assertNotNull("Compressed data should not be null", etc1Data.compressedData);

		etc1Data.dispose();
	}

	/** Test ETC1Data constructor with valid PKM header */
	@Test
	public void testETC1Data_Constructor_WithValidPKMHeader () {
		int width = 16;
		int height = 16;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize + ETC1.PKM_HEADER_SIZE);
		ETC1.formatHeader(compressedData, 0, width, height);
		int dataOffset = ETC1.PKM_HEADER_SIZE;

		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, dataOffset);

		assertTrue("ETC1Data should indicate it has a PKM header", etc1Data.hasPKMHeader());
		assertTrue("toString() should start with 'valid' when PKM is valid", etc1Data.toString().startsWith("valid"));
		assertEquals("Data offset should be PKM_HEADER_SIZE (16)", ETC1.PKM_HEADER_SIZE, etc1Data.dataOffset);

		etc1Data.dispose();
	}

	/** Test ETC1Data constructor with invalid PKM header */
	@Test
	public void testETC1Data_Constructor_WithInvalidPKMHeader () {
		int width = 16;
		int height = 16;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		// Create buffer with PKM_HEADER_SIZE offset but DON'T format it (leave it invalid)
		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize + ETC1.PKM_HEADER_SIZE);
		// Leave header uninitialized (all zeros) - this makes it invalid
		int dataOffset = ETC1.PKM_HEADER_SIZE;

		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, dataOffset);

		assertTrue("ETC1Data should indicate it has a PKM header", etc1Data.hasPKMHeader());
		assertFalse("PKM header should be invalid", ETC1.isValidPKM(compressedData, 0));
		assertTrue("toString() should start with 'invalid' when PKM is invalid", etc1Data.toString().startsWith("invalid"));
		assertEquals("Data offset should be PKM_HEADER_SIZE (16)", ETC1.PKM_HEADER_SIZE, etc1Data.dataOffset);

		etc1Data.dispose();
	}

	/** Test ETC1Data hasPKMHeader returns false without header */
	@Test
	public void testETC1Data_HasPKMHeader_WithoutHeader () {
		int width = 32;
		int height = 32;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize);
		int dataOffset = 0;

		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, dataOffset);

		assertFalse("ETC1Data should indicate it does not have a PKM header", etc1Data.hasPKMHeader());
		assertTrue("toString() should start with 'raw' when PKM is invalid", etc1Data.toString().startsWith("raw"));
	}

	/** Test ETC1Data hasPKMHeader returns true with correct offset */
	@Test
	public void testETC1Data_HasPKMHeader_WithCorrectOffset () {
		int width = 16;
		int height = 16;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize + ETC1.PKM_HEADER_SIZE);
		ETC1.formatHeader(compressedData, 0, width, height);

		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, 16);

		assertTrue("ETC1Data should indicate it has a PKM header when dataOffset is 16", etc1Data.hasPKMHeader());

		etc1Data.dispose();
	}

	/** Test ETC1Data hasPKMHeader returns false with non-16 offset */
	@Test
	public void testETC1Data_HasPKMHeader_WithNon16Offset () {
		int width = 16;
		int height = 16;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize + 8);

		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, 8);

		assertFalse("ETC1Data should indicate it does not have a PKM header when dataOffset is not 16", etc1Data.hasPKMHeader());

		etc1Data.dispose();
	}

	/** Test ETC1Data dispose releases unsafe byte buffer */
	@Test
	public void testETC1Data_Dispose_ReleasesUnsafeByteBuffer () {
		int width = 16;
		int height = 16;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		int before = BufferUtils.getAllocatedBytesUnsafe();

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize);
		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, 0);

		// Assert buffer is tracked as unsafe before dispose
		assertTrue("ByteBuffer should be tracked as unsafe before dispose",
			BufferUtils.isUnsafeByteBuffer(etc1Data.compressedData));

		// Call dispose
		etc1Data.dispose();

		// Assert buffer is NOT tracked as unsafe after dispose
		assertFalse("ByteBuffer should NOT be tracked as unsafe after dispose",
			BufferUtils.isUnsafeByteBuffer(etc1Data.compressedData));

		// Assert allocated bytes has decreased
		assertEquals("Allocated unsafe bytes should decrease after dispose", before, BufferUtils.getAllocatedBytesUnsafe());
	}

	/** Test ETC1Data toString with PKM header */
	@Test
	public void testETC1Data_ToString_WithPKMHeader () {
		int width = 32;
		int height = 32;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize + ETC1.PKM_HEADER_SIZE);
		ETC1.formatHeader(compressedData, 0, width, height);

		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, ETC1.PKM_HEADER_SIZE);

		String toString = etc1Data.toString();

		assertNotNull("toString should not return null", toString);
		assertTrue("toString should contain 'pkm'", toString.contains("pkm"));
		assertTrue("toString should contain width", toString.contains(String.valueOf(width)));
		assertTrue("toString should contain height", toString.contains(String.valueOf(height)));
		assertTrue("toString should contain compressed size", toString.contains(String.valueOf(compressedSize)));

		etc1Data.dispose();
	}

	/** Test ETC1Data toString without PKM header */
	@Test
	public void testETC1Data_ToString_WithoutPKMHeader () {
		int width = 16;
		int height = 16;
		int compressedSize = ETC1.getCompressedDataSize(width, height);

		ByteBuffer compressedData = BufferUtils.newUnsafeByteBuffer(compressedSize);
		ETC1Data etc1Data = new ETC1Data(width, height, compressedData, 0);

		String toString = etc1Data.toString();

		assertNotNull("toString should not return null", toString);
		assertTrue("toString should contain 'raw'", toString.contains("raw"));
		assertTrue("toString should contain width", toString.contains(String.valueOf(width)));
		assertTrue("toString should contain height", toString.contains(String.valueOf(height)));
		assertTrue("toString should contain compressed size",
			toString.contains(String.valueOf(compressedSize - ETC1.PKM_HEADER_SIZE)));

		etc1Data.dispose();
	}

	/** Test encodeImage with RGB565 format */
	@Test
	public void testEncodeImage_RGB565 () {
		int width = 16;
		int height = 16;

		Pixmap pixmap = new Pixmap(width, height, Format.RGB565);
		// Fill with some test data
		pixmap.setColor(1, 0, 0, 1);
		pixmap.fill();

		ETC1Data etc1Data = ETC1.encodeImage(pixmap);

		assertNotNull("ETC1Data should not be null", etc1Data);
		assertEquals("Width should match pixmap width", width, etc1Data.width);
		assertEquals("Height should match pixmap height", height, etc1Data.height);
		assertNotNull("Compressed data should not be null", etc1Data.compressedData);
		assertFalse("Should not have PKM header", etc1Data.hasPKMHeader());
		assertEquals("Data offset should be 0", 0, etc1Data.dataOffset);

		// Verify compressed data size is correct
		int expectedSize = ETC1.getCompressedDataSize(width, height);
		assertEquals("Compressed data buffer should have correct capacity", expectedSize, etc1Data.compressedData.capacity());

		// Clean up
		etc1Data.dispose();
		pixmap.dispose();
	}

	/** Test encodeImage with RGB888 format */
	@Test
	public void testEncodeImage_RGB888 () {
		int width = 16;
		int height = 16;

		Pixmap pixmap = new Pixmap(width, height, Format.RGB888);
		// Fill with some test data
		pixmap.setColor(0, 1, 0, 1);
		pixmap.fill();

		ETC1Data etc1Data = ETC1.encodeImage(pixmap);

		assertNotNull("ETC1Data should not be null", etc1Data);
		assertEquals("Width should match pixmap width", width, etc1Data.width);
		assertEquals("Height should match pixmap height", height, etc1Data.height);
		assertNotNull("Compressed data should not be null", etc1Data.compressedData);
		assertFalse("Should not have PKM header", etc1Data.hasPKMHeader());

		// Verify compressed data size is correct
		int expectedSize = ETC1.getCompressedDataSize(width, height);
		assertEquals("Compressed data buffer should have correct capacity", expectedSize, etc1Data.compressedData.capacity());

		// Clean up
		etc1Data.dispose();
		pixmap.dispose();
	}

	/** Test encodeImage with unsupported format throws exception */
	@Test(expected = com.badlogic.gdx.utils.GdxRuntimeException.class)
	public void testEncodeImage_UnsupportedFormat () {
		int width = 16;
		int height = 16;

		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);

		try {
			ETC1.encodeImage(pixmap);
		} finally {
			pixmap.dispose();
		}
	}

	/** Test encodeImagePKM with RGB565 format */
	@Test
	public void testEncodeImagePKM_RGB565 () throws Exception {
		int width = 16;
		int height = 16;
		Format format = Format.RGB565;

		Pixmap pixmap = new Pixmap(width, height, format);
		// Fill with some test data
		pixmap.setColor(0, 0, 1, 1);
		pixmap.fill();

		ETC1Data etc1Data = ETC1.encodeImagePKM(pixmap);

		assertNotNull("ETC1Data should not be null", etc1Data);
		assertEquals("Width should match pixmap width", width, etc1Data.width);
		assertEquals("Height should match pixmap height", height, etc1Data.height);
		assertNotNull("Compressed data should not be null", etc1Data.compressedData);
		assertTrue("Should have PKM header", etc1Data.hasPKMHeader());
		assertEquals("Data offset should be PKM_HEADER_SIZE (16)", ETC1.PKM_HEADER_SIZE, etc1Data.dataOffset);

		java.lang.reflect.Method m = ETC1.class.getDeclaredMethod("getPixelSize", Format.class);
		m.setAccessible(true);
		assertTrue("getPixelSize(Format.RGB565) should be 2", (Integer)m.invoke(null, format) == 2);

		// Clean up
		etc1Data.dispose();
		pixmap.dispose();
	}

	/** Test encodeImagePKM with RGB888 format */
	@Test
	public void testEncodeImagePKM_RGB888 () throws Exception {
		int width = 32;
		int height = 32;
		Format format = Format.RGB888;

		Pixmap pixmap = new Pixmap(width, height, format);
		// Fill with some test data
		pixmap.setColor(1, 1, 0, 1);
		pixmap.fill();

		ETC1Data etc1Data = ETC1.encodeImagePKM(pixmap);

		assertNotNull("ETC1Data should not be null", etc1Data);
		assertEquals("Width should match pixmap width", width, etc1Data.width);
		assertEquals("Height should match pixmap height", height, etc1Data.height);
		assertTrue("Should have PKM header", etc1Data.hasPKMHeader());

		java.lang.reflect.Method m = ETC1.class.getDeclaredMethod("getPixelSize", Format.class);
		m.setAccessible(true);
		assertTrue("getPixelSize(Format.RGB888) should be 3", (Integer)m.invoke(null, format) == 3);

		// Clean up
		etc1Data.dispose();
		pixmap.dispose();
	}

	/** Test decodeImage with RGB565 format */
	@Test
	public void testDecodeImage_RGB565 () {
		int width = 16;
		int height = 16;

		// Create and encode a test image
		Pixmap original = new Pixmap(width, height, Format.RGB565);
		original.setColor(1, 0, 0, 1);
		original.fill();

		ETC1Data etc1Data = ETC1.encodeImagePKM(original);

		// Decode the image
		Pixmap decoded = ETC1.decodeImage(etc1Data, Format.RGB565);

		assertNotNull("Decoded pixmap should not be null", decoded);
		assertEquals("Decoded width should match original width", width, decoded.getWidth());
		assertEquals("Decoded height should match original height", height, decoded.getHeight());
		assertEquals("Decoded format should be RGB565", Format.RGB565, decoded.getFormat());
		assertTrue("Decoded pixel should not be zero (data should be decoded)", decoded.getPixel(0, 0) != 0);
		assertTrue("Decoded pixel at center should not be zero", decoded.getPixel(width / 2, height / 2) != 0);

		// Clean up
		decoded.dispose();
		etc1Data.dispose();
		original.dispose();
	}

	/** Test decodeImage with RGB888 format */
	@Test
	public void testDecodeImage_RGB888 () {
		int width = 16;
		int height = 16;

		// Create and encode a test image
		Pixmap original = new Pixmap(width, height, Format.RGB888);
		original.setColor(0, 1, 0, 1);
		original.fill();

		ETC1Data etc1Data = ETC1.encodeImagePKM(original);

		// Decode the image
		Pixmap decoded = ETC1.decodeImage(etc1Data, Format.RGB888);

		assertNotNull("Decoded pixmap should not be null", decoded);
		assertEquals("Decoded width should match original width", width, decoded.getWidth());
		assertEquals("Decoded height should match original height", height, decoded.getHeight());
		assertEquals("Decoded format should be RGB888", Format.RGB888, decoded.getFormat());
		assertTrue("Decoded pixel should not be zero (data should be decoded)", decoded.getPixel(0, 0) != 0);
		assertTrue("Decoded pixel at center should not be zero", decoded.getPixel(width / 2, height / 2) != 0);

		// Clean up
		decoded.dispose();
		etc1Data.dispose();
		original.dispose();
	}

	/** Test decodeImage without PKM header */
	@Test
	public void testDecodeImage_WithoutPKMHeader () {
		int width = 16;
		int height = 16;

		// Create and encode a test image without PKM header
		Pixmap original = new Pixmap(width, height, Format.RGB565);
		original.setColor(0, 0, 1, 1);
		original.fill();

		ETC1Data etc1Data = ETC1.encodeImage(original);

		// Decode the image
		Pixmap decoded = ETC1.decodeImage(etc1Data, Format.RGB565);

		assertNotNull("Decoded pixmap should not be null", decoded);
		assertEquals("Decoded width should match original width", width, decoded.getWidth());
		assertEquals("Decoded height should match original height", height, decoded.getHeight());
		assertTrue("Decoded pixel should not be zero", decoded.getPixel(0, 0) != 0);

		// Clean up
		decoded.dispose();
		etc1Data.dispose();
		original.dispose();
	}

	/** Test encode and decode round trip preserves dimensions */
	@Test
	public void testEncodeDecodeRoundTrip_PreservesDimensions () {
		int width = 32;
		int height = 32;

		// Create original image
		Pixmap original = new Pixmap(width, height, Format.RGB888);
		original.setColor(0.5f, 0.5f, 0.5f, 1);
		original.fill();

		// Encode
		ETC1Data etc1Data = ETC1.encodeImagePKM(original);

		// Decode
		Pixmap decoded = ETC1.decodeImage(etc1Data, Format.RGB888);

		// Verify dimensions are preserved
		assertEquals("Width should be preserved through encode/decode", width, decoded.getWidth());
		assertEquals("Height should be preserved through encode/decode", height, decoded.getHeight());

		// Clean up
		decoded.dispose();
		etc1Data.dispose();
		original.dispose();
	}

	/** Test ETC1Data write and read from file */
	@Test
	public void testETC1Data_WriteAndRead () throws Exception {
		int width = 16;
		int height = 16;

		// Create and encode a test image
		Pixmap original = new Pixmap(width, height, Format.RGB565);
		original.setColor(1, 0, 0, 1);
		original.fill();

		ETC1Data etc1Data = ETC1.encodeImagePKM(original);

		// Write to a temporary file
		com.badlogic.gdx.files.FileHandle tempFile = new com.badlogic.gdx.files.FileHandle(
			java.io.File.createTempFile("etc1test", ".pkm"));

		try {
			etc1Data.write(tempFile);

			// Read it back
			ETC1Data loadedData = new ETC1Data(tempFile);

			// Verify dimensions
			assertEquals("Width should match after write/read", width, loadedData.width);
			assertEquals("Height should match after write/read", height, loadedData.height);
			assertTrue("Loaded data should have PKM header", loadedData.hasPKMHeader());

			// Verify we can decode it
			Pixmap decoded = ETC1.decodeImage(loadedData, Format.RGB565);
			assertNotNull("Decoded pixmap should not be null", decoded);
			assertEquals("Decoded width should match", width, decoded.getWidth());
			assertEquals("Decoded height should match", height, decoded.getHeight());

			// Clean up
			decoded.dispose();
			loadedData.dispose();
		} finally {
			tempFile.delete();
			etc1Data.dispose();
			original.dispose();
		}
	}

	/** Test ETC1Data write handles data correctly */
	@Test
	public void testETC1Data_Write_HandlesDataCorrectly () throws Exception {
		int width = 32;
		int height = 32;

		// Create test data
		Pixmap pixmap = new Pixmap(width, height, Format.RGB888);
		pixmap.setColor(0, 1, 0, 1);
		pixmap.fill();

		ETC1Data etc1Data = ETC1.encodeImagePKM(pixmap);

		// Write to file
		com.badlogic.gdx.files.FileHandle tempFile = new com.badlogic.gdx.files.FileHandle(
			java.io.File.createTempFile("etc1test", ".pkm"));

		try {
			etc1Data.write(tempFile);

			// Verify file exists and has data
			assertTrue("File should exist after write", tempFile.exists());
			assertTrue("File should have data", tempFile.length() > 0);

			// Read back and verify
			ETC1Data loaded = new ETC1Data(tempFile);
			assertEquals("Width should match", width, loaded.width);
			assertEquals("Height should match", height, loaded.height);

			loaded.dispose();
		} finally {
			tempFile.delete();
			etc1Data.dispose();
			pixmap.dispose();
		}
	}

	/** Test PKM header constant value */
	@Test
	public void testPKMHeaderSize_Constant () {
		assertEquals("PKM_HEADER_SIZE should be 16 bytes", 16, ETC1.PKM_HEADER_SIZE);
	}

	/** Test ETC1_RGB8_OES constant value */
	@Test
	public void testETC1RGB8OES_Constant () {
		assertEquals("ETC1_RGB8_OES should be 0x00008d64", 0x00008d64, ETC1.ETC1_RGB8_OES);
	}

	/** Test checkNPOT and println for non-power-of-two */
	@Test
	public void testCheckNPOT_WarningPrintedForNonPowerOfTwo () {
		PrintStream originalOut = System.out;
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));

		int width = 30; // Not a power of two
		int height = 50; // Not a power of two

		ByteBuffer buffer = BufferUtils.newUnsafeByteBuffer(ETC1.getCompressedDataSize(32, 32));
		new ETC1Data(width, height, buffer, 0);

		String output = outContent.toString();
		assertTrue("Warning should be printed for non-power-of-two ETC1 textures",
			output.contains("warning: non-power-of-two ETC1 textures"));

		System.setOut(originalOut);
	}

	/** Test checkNPOT for power-of-two (no warning) */
	@Test
	public void testCheckNPOT_NoWarningForPowerOfTwo () {
		PrintStream originalOut = System.out;
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));

		int width = 32;
		int height = 32;

		ByteBuffer buffer = BufferUtils.newUnsafeByteBuffer(ETC1.getCompressedDataSize(width, height));
		new ETC1Data(width, height, buffer, 0);

		String output = outContent.toString();
		assertFalse("Warning should NOT be printed for power-of-two ETC1 textures",
			output.contains("warning: non-power-of-two ETC1 textures"));

		System.setOut(originalOut);
	}
}
