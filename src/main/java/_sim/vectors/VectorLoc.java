package _sim.vectors;

import _sim.MapLayer;
import _sim.world.MapTile;

/**
 * A location vector with a layer and dimension
 * 
 * @author borah
 *
 */
class VectorLoc implements IVector, Cloneable {

	private int x;
	private double xd;
	private int y;
	private double yd;
	private MapLayer layer;
	private MapTile tile;

	VectorLoc(int x, int y) {
		this.x = x;
		this.y = y;
		this.xd = x;
		this.yd = y;
	}

	VectorLoc(int x, int y, MapLayer layer) {
		this(x, y);
		this.layer = layer;
	}

	VectorLoc(int x, int y, MapTile tag) {
		this(x, y);
		this.tile = tag;
	}

	VectorLoc(int x, int y, MapLayer layer, MapTile tag) {
		this(x, y);
		this.layer = layer;
		this.tile = tag;
	}

	VectorLoc(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
		this.xd = x;
		this.yd = y;
	}

	VectorLoc(double x, double y, MapLayer layer) {
		this(x, y);
		this.layer = layer;
	}

	VectorLoc(double x, double y, MapTile tag) {
		this(x, y);
		this.tile = tag;
	}

	VectorLoc(double x, double y, MapLayer layer, MapTile tag) {
		this(x, y);
		this.layer = layer;
		this.tile = tag;
	}

	@Override
	public MapTile getTile() {
		return tile;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public double getUnadjustedX() {
		return xd;
	}

	@Override
	public double getUnadjustedY() {
		return yd;
	}

	@Override
	public IVector withTile(MapTile tag) {
		if (this.tile == tag)
			return this;
		VectorLoc two2 = this.clone();
		two2.tile = tag;
		return two2;
	}

	@Override
	public IVector withLayer(MapLayer layer) {
		if (this.layer == layer)
			return this;
		VectorLoc two2 = this.clone();
		two2.layer = layer;
		return two2;
	}

	@Override
	public IVector withX(double xd) {
		if (this.xd == xd)
			return this;
		if (xd == 0 && this.yd == 0 && this.tile == null && this.layer == null)
			return ZERO;
		VectorLoc two2 = this.clone();
		two2.x = (int) xd;
		two2.xd = xd;
		return two2;
	}

	@Override
	public IVector withXY(double x, double y) {
		if (this.xd == x && this.yd == y)
			return this;
		if (x == 0 && y == 0 && this.tile == null && this.layer == null)
			return ZERO;
		VectorLoc two2 = this.clone();
		two2.x = (int) x;
		two2.xd = x;
		two2.y = (int) y;
		two2.yd = y;

		return two2;
	}

	@Override
	public IVector withY(double y) {
		if (y == 0 && this.xd == 0 && this.tile == null && this.layer == null)
			return ZERO;
		VectorLoc two2 = this.clone();
		two2.y = (int) y;
		two2.yd = y;
		return two2;
	}

	@Override
	protected VectorLoc clone() {
		try {
			return (VectorLoc) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public MapLayer getLayer() {
		return layer;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof IVector loc) {
			return this.x == loc.getX() && this.y == loc.getY() && this.tile == loc.getTile()
					&& this.layer == loc.getLayer();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.x * this.y + (this.tile != null ? this.tile.hashCode() : 0)
				+ (this.layer != null ? this.layer.hashCode() : 0);
	}

	@Override
	public String toString() {
		return "(X:" + (this.xd != this.x ? this.xd : this.x) + ",Y:" + (this.yd != this.y ? this.yd : this.y)
				+ (layer != null ? ",L:" + layer : "") + (tile != null ? ",T:" + this.tile.toString() : "") + ")";
	}

}
