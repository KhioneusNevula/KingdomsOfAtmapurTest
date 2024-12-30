package sim.world;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple object to "design" dimensions; for now, just need to pass in the
 * dimension name and the tiles and their positions
 * 
 * @author borah
 *
 */
public class DimensionBuilder {

	private IDimensionTag dimension;

	private Set<MapTile> tiles;

	public static DimensionBuilder of(IDimensionTag dimension) {
		return new DimensionBuilder(dimension);
	}

	private DimensionBuilder(IDimensionTag dimension) {
		this.dimension = dimension;
		this.tiles = new HashSet<>();
	}

	public IDimensionTag getDimension() {
		return dimension;
	}

	public Set<MapTile> getTiles() {
		return tiles;
	}

	public DimensionBuilder createTileRectangle(int row, int col, int w, int h) {
		if (dimension == null) {
			throw new IllegalStateException();
		}
		for (int r = row; r < row + w; r++) {
			for (int c = col; c < col + h; c++) {
				tiles.add(new MapTile(dimension, r, c));
			}
		}
		return this;
	}

	public DimensionBuilder createNonContiguousTile(String name) {

		tiles.add(new MapTile(dimension, name));
		return this;
	}

}
