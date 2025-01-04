package sim;

import sim.world.IDimensionTag;

/**
 * A location vector
 * 
 * @author borah
 *
 */
public class VectorLoc implements IVector, Cloneable {

	private int x;
	private double xd;
	private int y;
	private double yd;
	private MapLayer layer;
	private IDimensionTag dimension;

	public VectorLoc(int x, int y) {
		this.x = x;
		this.y = y;
		this.xd = x;
		this.yd = y;
	}

	public VectorLoc(int x, int y, MapLayer layer) {
		this(x, y);
		this.layer = layer;
	}

	public VectorLoc(int x, int y, IDimensionTag tag) {
		this(x, y);
		this.dimension = tag;
	}

	public VectorLoc(int x, int y, MapLayer layer, IDimensionTag tag) {
		this(x, y);
		this.layer = layer;
		this.dimension = tag;
	}

	public VectorLoc(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
		this.xd = x;
		this.yd = y;
	}

	public VectorLoc(double x, double y, MapLayer layer) {
		this(x, y);
		this.layer = layer;
	}

	public VectorLoc(double x, double y, IDimensionTag tag) {
		this(x, y);
		this.dimension = tag;
	}

	public VectorLoc(double x, double y, MapLayer layer, IDimensionTag tag) {
		this(x, y);
		this.layer = layer;
		this.dimension = tag;
	}

	@Override
	public IDimensionTag getDimension() {
		return dimension;
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
	public IVector withDimension(IDimensionTag tag) {
		if (this.dimension == tag)
			return this;
		VectorLoc two2 = this.clone();
		two2.dimension = tag;
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
		if (xd == 0 && this.yd == 0 && this.dimension == null && this.layer == null)
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
		if (x == 0 && y == 0 && this.dimension == null && this.layer == null)
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
		if (y == 0 && this.xd == 0 && this.dimension == null && this.layer == null)
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
		if (obj instanceof IVector loc) {
			return this.x == loc.getX() && this.y == loc.getY() && this.dimension == loc.getDimension()
					&& this.layer == loc.getLayer();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.x * this.y + (this.dimension != null ? this.dimension.getId().hashCode() : 0)
				+ (this.layer != null ? this.layer.hashCode() : 0);
	}

	@Override
	public String toString() {
		return "(X:" + (this.xd != this.x ? this.xd : this.x) + ",Y:" + (this.yd != this.y ? this.yd : this.y)
				+ (layer != null ? ",L:" + layer : "") + (dimension != null ? ",D:" + this.dimension : "") + ")";
	}

}
