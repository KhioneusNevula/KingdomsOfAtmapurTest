package sim.world;

public class MapTile {

	/** default name of contiguous map tiles */
	public static final String DEFAULT_NAME = "<map>";
	private int r = -1;
	private int c = -1;
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
	 * New contiguous tile at position. Will be named as {@value #DEFAULT_NAME}
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
		this.r = r;
		this.c = c;
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
		return r;
	}

	public int getCol() {
		return c;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MapTile mt) {
			return this.name.equals(mt.name) && this.r == mt.r && this.c == mt.c
					&& this.isContiguous == mt.isContiguous;
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return (name.hashCode() + r * c) * (isContiguous ? 1 : -1);
	}

}
