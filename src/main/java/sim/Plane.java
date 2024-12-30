package sim;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum Plane implements IPlane {
	/**
	 * for things which can touch on any plane or sense on any plane
	 */
	OMNIPLANE(0),
	/**
	 * for things which are completely untouchable on all planes, or things which
	 * cannot sense on any plane
	 */
	NO_PLANE(1),
	/**
	 * normal physical plane
	 */
	PHYSICAL,
	/**
	 * the plane accessed by phase shifting
	 */
	PHASE_SHIFT,
	/**
	 * the spiritual plane
	 */
	SPIRITUAL,
	/**
	 * the plane of ghosts and the dead
	 */
	SPECTRAL,
	/**
	 * the plane of gods and the divine
	 */
	DIVINE;

	private int prime;

	private Plane(int p) {
		this.prime = p;
	}

	private Plane() {
		this.prime = PlaneHelper.nextIndex();
	}

	@Override
	public String getName() {
		return "base_" + name();
	}

	@Override
	public int getPrime() {
		return prime;
	}

	/**
	 * Whether this number only contains the planes in the given set and no others
	 * 
	 * @param number
	 * @param planes
	 * @return
	 */
	public static boolean matches(int number, Iterable<Plane> planes) {
		int num = number;
		for (Plane p : planes) {
			boolean is = false;
			while (num % p.prime == 0) {
				num /= p.prime;
				is = true;
			}
			if (!is) {
				return false;
			}
		}
		return num == 1;
	}

	/**
	 * Whether this number contains the given plane
	 * 
	 * @param number
	 * @param plane
	 * @return
	 */
	public static boolean contains(int number, Plane plane) {
		return number % plane.prime == 0;
	}

	/**
	 * Whether this number contains all the given planes
	 * 
	 * @param number
	 * @param planes
	 * @return
	 */
	public static boolean contains(int number, Iterable<Plane> planes) {
		for (Plane p : planes) {
			if (number % p.prime != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Separate a given number into a collection of planes from this particular enum
	 * 
	 * @param number
	 * @return
	 */
	public static Collection<Plane> separate(int number) {

		Set<Plane> planes = new HashSet<>(values().length);
		for (Plane p : values()) {
			if (number % p.prime == 0) {
				planes.add(p);
			}
		}
		return planes;

	}

	/**
	 * return a number that is the product of the given planes
	 * 
	 * @param planes
	 * @return
	 */
	public static int combine(Iterable<Plane> planes) {
		int num = 1;
		for (Plane p : planes) {
			num *= p.prime;
		}
		return num;
	}

	/**
	 * return a number that is the product of the given planes
	 * 
	 * @param planes
	 * @return
	 */
	public static int combine(Plane... planes) {
		int num = 1;
		for (Plane p : planes) {
			num *= p.prime;
		}
		return num;
	}
}
