package things.interfaces;

import _sim.MapLayer;
import _sim.dimension.IDimensionTag;
import _sim.world.GameMap;

/**
 * Something that can exist physically in the world
 * 
 * @author borah
 *
 */
public interface IExistsInWorld {

	/**
	 * Return the dimension this thing is located in
	 * 
	 * @return
	 */
	public IDimensionTag getDimension();

	/**
	 * Gets the map that this thing is present in
	 * 
	 * @return
	 */
	public GameMap getMap();

	/**
	 * Return the layer of the map this thing is located in
	 * 
	 * @return
	 */
	public MapLayer getLayer();

	/**
	 * Whether this item is in a state wherein it must be removed, i.e. totally
	 * destroyed
	 */
	boolean needsToBeRemoved();
}
