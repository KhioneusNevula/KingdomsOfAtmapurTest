package things.interfaces;

import _sim.vectors.IVector;

public interface ILocatable {

	/**
	 * Return the location of this thing
	 * 
	 * @return
	 */
	public IVector getPosition();

	/** Distance to another entity */
	default double distance(ILocatable a) {
		return IVector.distance(getPosition(), a.getPosition());
	}
}
