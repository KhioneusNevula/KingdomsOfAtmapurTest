package _utilities.temperature;

/** Temperature calculation utility */
public class Temperature {

	public static float FtoC(float F) {
		return (F - 32) * 5f / 9;
	}

	public static float CtoF(float C) {
		return C * 9f / 5 + 32;
	}

	public static float FtoK(float F) {
		return (F - 32) * 5f / 9 + 273.15f;
	}

	public static float KtoF(float K) {
		return (K - 273.15f) * 9f / 5 + 32;
	}

	public static float CtoK(float C) {
		return C + 273.15f;
	}

	public static float KtoC(float K) {
		return K - 273.15f;
	}

}
