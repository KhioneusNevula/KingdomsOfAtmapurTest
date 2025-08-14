package _utilities;

import java.util.stream.Stream;

public class StringUtils {
	private StringUtils() {
	}

	/**
	 * Removes the suffix of the string , where prefix is of the given length
	 * 
	 * @param fora
	 * @param len
	 * @return
	 */
	public static String removeSuffix(String fora, int len) {
		return fora.substring(0, fora.length() - len);
	}

	/**
	 * Removes the prefix of the string, where prefix is of the given length
	 * 
	 * @param fora
	 * @param len
	 * @return
	 */
	public static String removePrefix(String fora, int len) {
		return fora.substring(len);
	}

	/**
	 * Represents a stream as a string in "set format", i.e. {..., ..., ...,...}.
	 * Obviously, closes the stream
	 * 
	 * @param stream
	 * @return
	 */
	public static String formatAsSetFromStream(Stream<?> stream) {
		return stream.map(Object::toString).reduce("{", (result, element) -> result + "," + element,
				(el1, el2) -> el1 + ", " + el2) + "}";
	}

}
