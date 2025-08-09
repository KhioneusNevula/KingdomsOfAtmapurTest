package thinker.constructs;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

import _sim.vectors.IVector;
import things.interfaces.IUnique;
import things.interfaces.UniqueType;

/**
 * Concept of a place of interest
 * 
 * @author borah
 *
 */
public interface IPlace extends IUnique {

	@Override
	default UniqueType getUniqueType() {
		return UniqueType.PLACE;
	}

	/** The location of the center of this place */
	public IVector getLocationCenter();

	/** Returns what region of area this place covers as rectangles */
	public Collection<Rectangle2D> getAreaCovered();

}
