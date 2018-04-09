package org.cryptoj.utility;

public class TextFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TextFileException(String message) {
		super(message);
	}

	public TextFileException(String message, Exception e) {
		super(message, e);
	}
}
