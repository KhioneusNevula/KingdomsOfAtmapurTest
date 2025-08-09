package _sim.world;

import _sim.vectors.IVector;
import metaphysics.being.IFigure;

/**
 * A figure representing a persistent representaton of a being, which is storedd
 * and loaded and such
 */
public interface IPersistentFigure extends IFigure {
	/** Get the name of the file this should be saved as (just name, not path) */
	public String getSaveFilename();

	/**
	 * If this figure is physical, return the most recent location of its body, or
	 * null otherwise
	 */
	public IVector getLastLocation();
}
