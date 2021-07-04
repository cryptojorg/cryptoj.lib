package org.cryptoj.utility;

public class HexUtility {
	
	public static String bytesToHex(byte [] bytes) {
		if(bytes == null) {
	        throw new IllegalArgumentException("byte array is null");
		}
		
		StringBuffer buf = new StringBuffer("0x");
		
	    for(byte val: bytes) {
	        buf.append(byteToHex(val));
	    }
	    
	    return buf.toString();
	}
	
	public static byte [] hexToBytes(String hex) {
		if(hex == null) {
	        throw new IllegalArgumentException("Hex string is null");
		}
		
		if(!hex.startsWith("0x")) {
	        throw new IllegalArgumentException("Hex string not starting with '0x'");
	    }
		
		hex = hex.substring(2);
		
		if(hex.length() % 2 == 1 || hex.length() < 2) {
	        throw new IllegalArgumentException("Invalid length for hex string");
	    }
	    
	    byte[] bytes = new byte[hex.length() / 2];
	    
	    for(int i = 0; i < hex.length(); i += 2) {
	        bytes[i / 2] = hexToByte(hex.substring(i, i + 2));
	    }
	    
	    return bytes;
	}

	private static String byteToHex(byte val) {
		char first = Character.forDigit((val >> 4) & 0xF, 16);
		char second = Character.forDigit((val & 0xF), 16);
	    return new String(new char [] {first, second});		
	}

	private static byte hexToByte(String hexString) {
	    int first = toDigit(hexString.charAt(0));
	    int second = toDigit(hexString.charAt(1));
	    return (byte)((first << 4) + second);
	}
	
	private static int toDigit(char hexChar) {
	    int digit = Character.digit(hexChar, 16);
	    
	    if(digit == -1) {
	        throw new IllegalArgumentException(
	          "hex char not valid: "+ hexChar);
	    }
	    
	    return digit;
	}	
}
