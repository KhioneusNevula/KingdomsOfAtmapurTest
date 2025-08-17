package thinker.concepts.general_types;

import _sim.world.MapTile;
import thinker.concepts.IConcept;

/**
 * A concept representing a unique map tile
 * 
 * @author borah
 *
 */
public interface IMapTileConcept extends IConcept {

	@Override
	default ConceptType getConceptType() {
		return ConceptType.MAP_TILE;
	}

	/**
	 * Return the map tile of this concept
	 * 
	 * @return
	 */
	public MapTile getMapTile();

	public static IMapTileConcept of(MapTile mt) {
		return new MapTileConcept(mt);
	}

}
