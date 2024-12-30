package things.interfaces;

import sim.MapLayer;
import sim.world.GameMap;
import sim.world.IDimensionTag;

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
}
