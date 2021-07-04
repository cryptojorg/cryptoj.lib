package org.cryptoj.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cryptoj.common.BaseTest;
import org.junit.jupiter.api.Test;

public class HexUtitlityTest extends BaseTest {
	
	byte [] BYTE_ARRAY = { (byte)133, (byte)53, (byte)234, (byte)241 };
	String HEX_STRING_NO_PREFIX = "8535eaf1";
	String HEX_STRING_WITH_PREFIX = "0x8535eaf1";

	@Test
	public void testNullBytes() {
		assertThrows(IllegalArgumentException.class, () -> {
			HexUtility.bytesToHex(null);
		});
	}

	@Test
	public void testEncoding() {
		String hex = HexUtility.bytesToHex(BYTE_ARRAY);
		assertEquals(HEX_STRING_WITH_PREFIX, hex);
	}

	@Test
	public void testNullString() {
		assertThrows(IllegalArgumentException.class, () -> {
			HexUtility.hexToBytes(null);
		});
	}

	@Test
	public void testMissingPrefix() {
		assertThrows(IllegalArgumentException.class, () -> {
			HexUtility.hexToBytes(HEX_STRING_NO_PREFIX);
		});
	}

	@Test
	public void testInvalidLenth() {
		assertThrows(IllegalArgumentException.class, () -> {
			HexUtility.hexToBytes("");
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			HexUtility.hexToBytes("0x");
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			HexUtility.hexToBytes("0x853");
		});
	}

	@Test
	public void testDecoding() {
		byte [] bytes = HexUtility.hexToBytes(HEX_STRING_WITH_PREFIX);
		assertArrayEquals(BYTE_ARRAY, bytes);
	}

}
