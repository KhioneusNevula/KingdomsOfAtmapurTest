package _utilities.function;

import java.util.function.BiFunction;

public class FunctionUtils {
	private FunctionUtils() {
	}

	/** swaps arguments of function */
	public static <A, B, C> BiFunction<B, A, C> swap(BiFunction<A, B, C> func) {

		return (a, b) -> func.apply(b, a);
	}

}
