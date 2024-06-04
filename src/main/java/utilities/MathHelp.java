package utilities;

import java.io.InputStream;
import java.util.Scanner;

public final class MathHelp {
	private MathHelp() {
	}

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

	/**
	 * Finds the first shared prime factor between the numbers. for primes greater
	 * than the {@value #primeCount}th prime, it won't work
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static int findFirstPrimeFactor(int n1, int n2) {
		for (int prime : primes) {
			if (n1 % prime == 0 && n2 % prime == 0)
				return prime;
		}
		return 1;
	}

	/**
	 * Whether point x,y is in the rect with corner rx, ry and width w and height h
	 * 
	 * @param x
	 * @param y
	 * @param rx
	 * @param ry
	 * @param w
	 * @param h
	 * @return
	 */
	public static boolean pointInRect(float x, float y, float rx, float ry, float w, float h) {
		return x >= rx && x <= rx + w && y >= ry && y <= ry + h;
	}

	/**
	 * check if the first rect fully contains (or is coterminous with) the second
	 * 
	 * @return
	 */
	public static boolean firstRectContains(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		float[] r2x = { x2, x2 + w2, x2 + w2, x2 };
		float[] r2y = { y2, y2, y2 + h2, y2 + h2 };
		for (int i = 0; i < r2x.length; i++) {
			float x = r2x[i];
			float y = r2y[i];
			if (!(x >= x1 && x <= x1 + w1 || y >= y1 && y <= y1 + h1))
				return false;
		}
		return true;
	}

	/**
	 * Check if either rect contains the other
	 * 
	 * @param x1
	 * @param y1
	 * @param w1
	 * @param h1
	 * @param x2
	 * @param y2
	 * @param w2
	 * @param h2
	 * @return
	 */
	public static boolean eitherRectContains(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		return firstRectContains(x1, y1, w1, h1, x2, y2, w2, h2) || firstRectContains(x2, y2, w2, h2, x1, y1, w1, h1);
	}

	public static boolean rectsIntersect(float x1, float y1, float w1, float h1, float x2, float y2, float w2,
			float h2) {
		float[] r1x = { x1, x1 + w1, x1 + w1, x1 };
		float[] r1y = { y1, y1, y1 + h1, y1 + h1 };
		for (int i = 0; i < r1x.length; i++) {
			float x = r1x[i];
			float y = r1y[i];
			if (x >= x2 && x <= x2 + w2 || y >= y2 && y <= y2 + h2)
				return true;
		}
		return false;
	}

	/**
	 * shortest distance from point (Ex, Ey) to line AB
	 * 
	 * @param px
	 * @param py
	 * @param lx1
	 * @param ly1
	 * @param lx2
	 * @param ly2
	 * @return
	 */
	public static double pointToLineDistance(double Ex, double Ey, double Ax, double Ay, double Bx, double By) {

		// vector AB
		Pair<Double, Double> AB = Pair.of(Bx - Ax, By - Ay);

		// vector BE
		Pair<Double, Double> BE = Pair.of(Ex - Bx, Ey - By);

		// vector AE
		Pair<Double, Double> AE = Pair.of(Ex - Ax, Ey - Ay);

		// Variables to store dot product
		double AB_BE, AB_AE;

		// Calculating the dot product
		AB_BE = (AB.getFirst() * BE.getFirst() + AB.getSecond() * BE.getSecond());
		AB_AE = (AB.getFirst() * AE.getFirst() + AB.getSecond() * AE.getSecond());

		// Minimum distance from
		// point E to the line segment
		double reqAns = 0;

		// Case 1
		if (AB_BE > 0) {

			// Finding the magnitude
			double y = Ey - By;
			double x = Ex - Bx;
			reqAns = Math.sqrt(x * x + y * y);
		}

		// Case 2
		else if (AB_AE < 0) {
			double y = Ey - Ay;
			double x = Ex - Ax;
			reqAns = Math.sqrt(x * x + y * y);
		}

		// Case 3
		else {

			// Finding the perpendicular distance
			double x1 = AB.getFirst();
			double y1 = AB.getSecond();
			double x2 = AE.getFirst();
			double y2 = AE.getSecond();
			double mod = Math.sqrt(x1 * x1 + y1 * y1);
			reqAns = Math.abs(x1 * y2 - y1 * x2) / mod;
		}
		return reqAns;
	}

	/**
	 * Check if a circle and rect intersect
	 * 
	 * @param circleX
	 * @param circleY
	 * @param circleRadius
	 * @param rectX
	 * @param rectY
	 * @param rectWidth
	 * @param rectHeight
	 * @return
	 */
	public static boolean circleRectIntersects(float circleX, float circleY, float circleRadius, float rectX,
			float rectY, float rectWidth, float rectHeight) {
		if (eitherRectContains(circleX - circleRadius, circleY - circleRadius, circleRadius * 2, circleRadius * 2,
				rectX, rectY, rectWidth, rectHeight))
			return true;
		float[][] rSides = { { rectX, rectY, rectX + rectWidth, rectY },
				{ rectX + rectWidth, rectY, rectX + rectWidth, rectY + rectHeight },
				{ rectX + rectWidth, rectY + rectHeight, rectX, rectY + rectHeight },
				{ rectX, rectY, rectX, rectY + rectHeight } }; // {{x1, y1, x2, y2}}
		for (float[] side : rSides) {
			if (pointToLineDistance(circleX, circleY, side[0], side[1], side[2], side[3]) <= circleRadius) {
				return true;
			}
		}
		return false;

	}

}
