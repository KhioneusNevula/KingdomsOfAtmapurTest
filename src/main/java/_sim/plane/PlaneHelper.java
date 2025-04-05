package _sim.plane;

import java.util.Collection;

import _utilities.MathUtils;

public class PlaneHelper {
	private PlaneHelper() {
	}

	private static int index = 0;

	/**
	 * Return index for next created planee and increment the indices by 1
	 * 
	 * @return
	 */
	public static int nextIndex() {
		return MathUtils.getNthPrime(index++);
	}

	/**
	 * check if two composite plane numbers can interact
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static boolean canInteract(int p1, int p2) {
		if (p1 == 0 || p2 == 0)
			return true;
		return MathUtils.findFirstPrimeFactor(p1, p2) != 1;
	}

	/**
	 * check if two collections of planes can interact
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean canInteract(Collection<IPlane> o1, Collection<IPlane> o2) {
		int p1 = 1;
		for (IPlane p : o1)
			p1 *= p.getPrime();
		int p2 = 1;
		for (IPlane p : o2)
			p2 *= p.getPrime();
		return MathUtils.findFirstPrimeFactor(p1, p2) != 1;
	}

}
