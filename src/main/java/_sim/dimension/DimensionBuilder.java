package _sim.dimension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import _sim.world.MapTile;
import utilities.property.IProperty;

/**
 * A simple object to "design" dimensions; for now, just need to pass in the
 * dimension name and the tiles and their positions. <br>
 * TODO a way to add more robust settings and also per-mapp tile settings
 * 
 * @author borah
 *
 */
public class DimensionBuilder {

	private IDimensionTag dimension;

	private Set<MapTile> tiles;
	private Map<IProperty<?>, Object> properties;

	public static DimensionBuilder of(IDimensionTag dimension) {
		return new DimensionBuilder(dimension);
	}

	private DimensionBuilder(IDimensionTag dimension) {
		this.dimension = dimension;
		this.tiles = new HashSet<>();
		this.properties = new HashMap<>();
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

	public <E> DimensionBuilder addProp(IProperty<E> pr, E val) {
		properties.put(pr, val);
		return this;
	}

	public <E> E getProperty(IProperty<E> forProp) {
		return (E) properties.getOrDefault(forProp, forProp.defaultValue());
	}

}
