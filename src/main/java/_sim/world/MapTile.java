package _sim.world;

import _sim.dimension.IDimensionTag;

/**
 * A class representing a piece of information to represent an individual tile
 * on a map
 */
public class MapTile {

	/** default name of contiguous map tiles */
	public static final String DEFAULT_NAME = "<map>";
	private int row = -1;
	private int col = -1;
	private boolean isContiguous;
	private IDimensionTag dimension;
	private String name;

	/**
	 * New non-contiguous tile
	 * 
	 * @param tag
	 */
	public MapTile(IDimensionTag tag, String name) {
		this.dimension = tag;
		this.name = name;
		this.isContiguous = false;
	}

	/**
	 * New contiguous tile at position. ThinkerWill be named as
	 * {@value #DEFAULT_NAME}
	 * 
	 * @param tag
	 * @param r
	 * @param c
	 */
	public MapTile(IDimensionTag tag, int r, int c) {
		this(tag, DEFAULT_NAME);
		if (r < 0 || c < 0) {
			throw new IllegalArgumentException(r + " " + c);
		}
		this.row = r;
		this.col = c;
		this.isContiguous = true;
	}

	public IDimensionTag getDimension() {
		return dimension;
	}

	public String getName() {
		return name;
	}

	public boolean isContiguous() {
		return isContiguous;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof MapTile mt) {
			return this.isContiguous == mt.isContiguous
					&& (this.isContiguous ? (this.row == mt.row && this.col == mt.col) : (this.name.equals(mt.name)))
					&& this.dimension.equals(mt.dimension);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ((isContiguous ? row * col : name.hashCode())) + this.dimension.hashCode();
	}

	@Override
	public String toString() {
		if (this.isContiguous) {
			return "|[R=" + row + ",C=" + col + ",D=" + this.dimension.getId() + "]|";
		} else {

			return "|[N=" + this.name + ",D=" + this.dimension.getId() + "]|";
		}
	}

	/**
	 * Returns just the basic info of this map tile in (...,...,...) or (...,...)
	 * format
	 * 
	 * @return
	 */
	public String bareString() {
		if (this.isContiguous) {
			return "(" + row + "," + col + "," + this.dimension.getId() + ")";
		} else {
			return "(" + name + "," + dimension.getId() + ")";
		}
	}

}
