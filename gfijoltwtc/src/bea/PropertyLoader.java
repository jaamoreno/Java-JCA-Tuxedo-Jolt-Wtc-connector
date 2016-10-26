package bea;

import java.io.InputStream;
import java.util.Properties;

public abstract class PropertyLoader {

	public static Properties loadProperties(String name, ClassLoader loader)
			throws IllegalArgumentException {
		Properties result;

		if (name == null) {
			throw new IllegalArgumentException("null input: name");
		}

		if (name.startsWith("/")) {
			name = name.substring(1);
		}

		if (name.endsWith(SUFFIX)) {
			name = name.substring(0, name.length() - SUFFIX.length());
		}

		result = null;
		InputStream inputStream = null;

		try {
			if (loader == null) {
				loader = ClassLoader.getSystemClassLoader();
			}

			name = name.replace('.', '/');

			if (!name.endsWith(SUFFIX)) {
				name = name.concat(SUFFIX);
			}

			inputStream = loader.getResourceAsStream(name);

			if (inputStream != null) {
				result = new Properties();
				result.load(inputStream);
			}
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable ignore) {
					ignore.printStackTrace();
				}
			}
		}

		if (result == null) {
			throw new IllegalArgumentException("could not load [" + name + "]"
					+ " as " + "a classloader resource");
		} else {
			return result;
		}
	}

	public static Properties loadProperties(String name)
			throws IllegalArgumentException {
		return loadProperties(name, Thread.currentThread()
				.getContextClassLoader());
	}

	private PropertyLoader() {
	}

	private static final String SUFFIX = ".properties";
}
