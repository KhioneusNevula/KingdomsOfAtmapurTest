package party.collective.group;

import java.util.Collection;

import _sim.world.MapTile;

/**
 * A civilization that extends across different map tiles, as a group
 * 
 * @author borah
 *
 */
public interface ICivilizationGroup extends IGroup {

	/**
	 * The sum of all the map tiles this civilization is considered to inhabit
	 * 
	 * @return
	 */
	public Collection<MapTile> getMapTiles();

	/**
	 * Add a new tile to this civilization's range of inhabitance
	 * 
	 * @param tile
	 */
	public void addTile(MapTile tile);

	/**
	 * Remove tile from this civilization's range of inhabitance
	 * 
	 * @param tile
	 */
	public void removeTile(MapTile tile);

	/**
	 * Return true if this civilization contains the given tile
	 * 
	 * @param tile
	 * @return
	 */
	public default boolean containsTile(MapTile tile) {
		return getMapTiles().contains(tile);
	}
}
