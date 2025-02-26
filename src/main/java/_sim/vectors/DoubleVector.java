package _sim.vectors;

import _sim.MapLayer;
import _sim.world.MapTile;

class DoubleVector implements IVector {

	private double x;
	private double y;

	DoubleVector(double x2, double y2) {
		this.x = x2;
		this.y = y2;
	}

	@Override
	public MapTile getTile() {
		return null;
	}

	@Override
	public MapLayer getLayer() {
		return null;
	}

	@Override
	public int getX() {
		return (int) x;
	}

	@Override
	public int getY() {
		return (int) y;
	}

	@Override
	public double getUnadjustedX() {
		return x;
	}

	@Override
	public double getUnadjustedY() {
		return y;
	}

	@Override
	public IVector withTile(MapTile tag) {
		return IVector.of(x, y, tag);
	}

	@Override
	public IVector withLayer(MapLayer layer) {
		return IVector.of(x, y, layer);
	}

	@Override
	public IVector withXY(double x, double y) {
		return IVector.of(x, y);
	}

	@Override
	public IVector withX(double x) {
		return IVector.of(x, y);
	}

	@Override
	public IVector withY(double y) {
		return IVector.of(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof IVector loc) {
			return this.x == loc.getUnadjustedX() && this.y == loc.getUnadjustedY();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Double.hashCode(this.x * this.y);
	}

	@Override
	public String toString() {
		return "(X:" + x + ",Y:" + y + ")";
	}

}
