package thinker.social.group;

import _sim.vectors.IVector;
import _sim.world.MapTile;

/**
 * An individual settlement, as a group
 * 
 * @author borah
 *
 */
public interface ISettlementGroup extends IGroup {

	/**
	 * The map tile this settlement is located at
	 * 
	 * @return
	 */
	public MapTile getMapTile();

	/**
	 * Get the position of the center of this settlement, in the map tile
	 * 
	 * @return
	 */
	public IVector getCenter();
}
