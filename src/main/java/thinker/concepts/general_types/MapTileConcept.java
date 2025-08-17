package thinker.concepts.general_types;

import _sim.world.MapTile;

public class MapTileConcept implements IMapTileConcept {

	private MapTile mapTile;

	MapTileConcept(MapTile tile) {
		this.mapTile = tile;
	}

	@Override
	public String getUnderlyingName() {
		return "tile_" + mapTile.bareString();
	}

	@Override
	public MapTile getMapTile() {
		return mapTile;
	}

	@Override
	public String toString() {
		return "MapTileConcept" + this.mapTile;
	}

	@Override
	public int hashCode() {
		return this.mapTile.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof IMapTileConcept imc) {
			return this.mapTile.equals(imc.getMapTile());
		}
		return false;
	}

}
