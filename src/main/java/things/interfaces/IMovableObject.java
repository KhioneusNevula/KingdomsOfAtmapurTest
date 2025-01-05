package things.interfaces;

import _sim.vectors.IVector;

public interface IMovableObject extends ILocatable {

	/**
	 * Move by the amount indicated by this {@link IVector}, numerically.
	 */
	public void move(IVector difference);

	/**
	 * Set the position of this object to the given place
	 * 
	 * @param position
	 */
	public void setPosition(IVector position);

}
