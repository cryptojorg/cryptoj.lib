package org.cryptoj.utility;

public class JsonFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public JsonFileException(String message) {
		super(message);
	}

	public JsonFileException(String message, Exception e) {
		super(message, e);
	}
}
