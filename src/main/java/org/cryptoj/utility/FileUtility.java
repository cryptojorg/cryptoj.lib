package org.cryptoj.utility;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class FileUtility {

	public static JSONObject readJsonFile(File file) {
		String jsonString = readTextFile(file);

		if(jsonString.isEmpty()) {
			throw new JsonFileException("Empty json file");
		}

		try {
			return new JSONObject(jsonString);
		}
		catch (JSONException e) {
			throw new JsonFileException("Conversion to json object failed", e);
		}
	}

	public static String readTextFile(String fileName) {
		try {
			return readTextFile(new File(fileName));
		}
		catch (Exception e) {
			throw new TextFileException("Failed to read content from text file " + fileName, e);
		}
	}

	public static String readTextFile(File file) {

		if(!file.exists()) {
			throw new TextFileException("File to read does not exist " + file.getAbsolutePath());
		}

		if(file.isDirectory()) {
			throw new TextFileException("Provided path is directory, not file " + file.getAbsolutePath());
		}

		StringBuilder sb = new StringBuilder();

		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF8");
			BufferedReader in = new BufferedReader(isr);
			String str = null;

			while((str = in.readLine()) != null) {
				sb.append(str);
			}

			in.close();
		}
		catch (Exception e) {
			throw new TextFileException("Failed to read content from file " + file.getAbsolutePath(), e);
		}

		return sb.toString();
	}

	public static File saveToFile(String buf, String fileName) {
		boolean overwrite = false;
		return saveToFile(buf, fileName, overwrite);
	}

	public static File saveToFile(byte [] buf, String fileName) {
		boolean overwrite = false;
		return saveToFile(buf, fileName, overwrite);
	}

	public static File saveToFile(String buf, String fileName, boolean overwrite) {
		File file = new File(fileName);
		verifyFileExists(file, overwrite);

		try(PrintWriter out = new PrintWriter(file)) {
			if(buf != null) {
				out.println(buf);
			}
		}
		catch(Exception e) {
			throw new TextFileException("Failed to write content to file " + fileName, e);
		}

		return file;
	}

	public static File saveToFile(byte [] buf, String fileName, boolean overwrite) {
		File file = new File(fileName);
		verifyFileExists(file, overwrite);

		try (FileOutputStream fos = new FileOutputStream(file)) {
			fos.write(buf);
		}
		catch(Exception e) {
			throw new FileException("Failed to write content to file " + file, e);
		}

		return file;
	}

	private static void verifyFileExists(File file, boolean overwrite) {
		if(overwrite) {
			return;
		}

		if(file.exists()) {
			throw new FileException(String.format("File %s already exits. Not writing anything", file));
		}
	}

	public static List<String> getResourceAsStrings(String fileName) {
		try {
			InputStream is = FileUtility.class.getResourceAsStream(fileName);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			List<String> lines = new ArrayList<>();
			String line = br.readLine();

			while(line != null) {
				lines.add(line);
				line = br.readLine();
			}

			return lines;
		}
		catch (Exception e) {
			throw new TextFileException("Failed to read text from resource " + fileName, e);
		}
	}

	public static byte [] getResourceAsBytes(String fileName) {
		try {
			InputStream is = FileUtility.class.getResourceAsStream(fileName);
			byte [] buf = getBytesFromInputStream(is);

			return buf;
		}
		catch (Exception e) {
			throw new FileException("Failed to read content from resource " + fileName, e);
		}
	}

	private static byte[] getBytesFromInputStream(InputStream is) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
			byte[] buffer = new byte[0xFFFF];

			for (int len; (len = is.read(buffer)) != -1;)
				os.write(buffer, 0, len);

			os.flush();

			return os.toByteArray();
		}
	}
}
