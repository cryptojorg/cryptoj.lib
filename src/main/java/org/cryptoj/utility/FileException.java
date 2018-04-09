package org.cryptoj.utility;

public class FileException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public FileException(String message) {
		super(message);
	}

	public FileException(String message, Exception e) {
		super(message, e);
	}
}
