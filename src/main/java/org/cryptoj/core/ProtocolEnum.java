package org.cryptoj.core;

public enum ProtocolEnum {
	Bitcoin, 
	Ethereum;
	
	public static ProtocolEnum get(String name) {
		if(name == null || name.length() == 0) {
			throw new IllegalArgumentException("Provided name must not be null or empty");
		}
		
		String query = name.toLowerCase();
		
		if(Bitcoin.name().toLowerCase().equals(query)) {
			return Bitcoin;
		}
		else if(Ethereum.name().toLowerCase().equals(query)) {
			return Ethereum;
		}
		else {
			throw new IllegalArgumentException("Unknown protocol:" + name);
		}
	}
}
