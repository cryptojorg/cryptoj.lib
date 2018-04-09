package org.cryptoj.common;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class BaseTest {
	
	private boolean silent = false;

	protected String join(String [] parts) {
		return String.join(" ", Arrays.asList(parts));
	}
	
	protected File createTempFile() throws IOException {
		File file = File.createTempFile("cryptoj-unittest-", ".tmp");
		file.deleteOnExit();
		return file;
	}
	
	protected void log(String format, Object... args) {
		log(String.format(format, args));
	}
	
	protected void log(String message) {
		if(!silent) {
			System.out.println(message);
		}
	}
	
	protected static void logStatic(String message, boolean silent) {
		if(!silent) {
			System.out.println(message);
		}
	}
}
