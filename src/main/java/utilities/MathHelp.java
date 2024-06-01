package utilities;

import java.io.InputStream;
import java.util.Scanner;

public class MathHelp {

	private static final int primeCount = 2000;

	private static int[] initPrimes() {
		InputStream stream = MathHelp.class.getResourceAsStream("/math/primes.txt");
		int[] primes = new int[primeCount];
		Scanner scan = new Scanner(stream);
		int i = 0;
		try (scan) {
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] nums = line.split(" ");
				for (String x : nums) {
					if (x.isBlank())
						continue;
					primes[i++] = Integer.parseInt(x);
				}
			}
		}
		return primes;
	}

	private static final int[] primes = initPrimes(); /*
														 * { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53,
														 * 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113,
														 * 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181,
														 * 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251,
														 * 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317,
														 * 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397,
														 * 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463,
														 * 467, 479, 487, 491, 499, 503, 509, 521, 523, 541 };
														 */

	public static int clamp(int a, int b, int value) {
		if (a >= b)
			throw new IllegalArgumentException(a + ", " + b);
		return Math.min(Math.max(value, a), b);
	}

	public static double clamp(double a, double b, double value) {
		if (a >= b)
			throw new IllegalArgumentException(a + ", " + b);
		return Math.min(Math.max(value, a), b);
	}

	public static float clamp(float a, float b, float value) {
		if (a >= b)
			throw new IllegalArgumentException(a + ", " + b);
		return Math.min(Math.max(value, a), b);
	}

	/**
	 * Gets nth prime number from a {@value #primeCount} number list; loops back
	 * around if n is too big
	 * 
	 * @param index
	 * @return
	 */
	public static int getNthPrime(int index) {
		return primes[index % primes.length];
	}

}
