package sim;

import static sim.IVector.of;

import sim.world.IDimensionTag;

/**
 * A vector of location or movement. Should be immutable.
 * 
 * @author borah
 *
 */

public interface IVector {

	public static final IVector ZERO = new IVector() {

		@Override
		public IVector withY(double y) {
			return of(0, y);
		}

		@Override
		public IVector withXY(double x, double y) {
			return of(x, y);
		}

		@Override
		public IVector withX(double x) {
			return of(x, 0);
		}

		@Override
		public IVector withLayer(MapLayer layer) {
			return of(0, 0, layer);
		}

		@Override
		public IVector withDimension(IDimensionTag tag) {
			return of(0, 0, tag);
		}

		@Override
		public int getY() {
			return 0;
		}

		@Override
		public int getX() {
			return 0;
		}

		@Override
		public double getUnadjustedY() {
			return 0;
		}

		@Override
		public double getUnadjustedX() {
			return 0;
		}

		@Override
		public MapLayer getLayer() {
			return null;
		}

		@Override
		public IDimensionTag getDimension() {
			return null;
		}

		@Override
		public String toString() {
			return "(X:0,Y:0)";
		}
	};

	/**
	 * Create vector with the given angle (degrees) and magnitude
	 * 
	 * @param angle
	 * @param mag
	 * @return
	 */
	public static IVector fromAngle(double angle, double mag) {
		double x = mag * Math.cos(Math.toRadians(angle));
		double y = mag * Math.sin(Math.toRadians(angle));
		return of(x, y);
	}

	public static IVector of(int x, int y) {
		if (x == 0 && y == 0)
			return ZERO;
		return new VectorLoc(x, y);
	}

	public static IVector of(double x, double y) {
		if (x == 0 && y == 0)
			return ZERO;
		return new VectorLoc(x, y);
	}

	public static IVector of(int x, int y, MapLayer layer, IDimensionTag dim) {
		if (x == 0 && y == 0 && layer == null && dim == null)
			return ZERO;
		return new VectorLoc(x, y, layer, dim);
	}

	public static IVector of(double x, double y, MapLayer layer, IDimensionTag dim) {
		if (x == 0 && y == 0 && layer == null && dim == null)
			return ZERO;
		return new VectorLoc(x, y, layer, dim);
	}

	public static IVector of(int x, int y, IDimensionTag dim) {
		if (x == 0 && y == 0 && dim == null)
			return ZERO;
		return new VectorLoc(x, y, dim);
	}

	public static IVector of(double x, double y, IDimensionTag dim) {
		if (x == 0 && y == 0 && dim == null)
			return ZERO;
		return new VectorLoc(x, y, dim);
	}

	public static IVector of(double x, double y, MapLayer layer) {
		if (x == 0 && y == 0 && layer == null)
			return ZERO;
		return new VectorLoc(x, y, layer);
	}

	public static IVector of(int x, int y, MapLayer layer) {
		if (x == 0 && y == 0 && layer == null)
			return ZERO;
		return new VectorLoc(x, y, layer);
	}

	/**
	 * Get the dimensional position
	 * 
	 * @return
	 */
	public IDimensionTag getDimension();

	/**
	 * Get the layer of the map this is in
	 * 
	 * @return
	 */
	public MapLayer getLayer();

	/**
	 * Get (block) X
	 * 
	 * @return
	 */
	public int getX();

	/**
	 * Get (block) Y
	 * 
	 * @return
	 */
	public int getY();

	/**
	 * Get the X position taking position relative to block into account
	 * 
	 * @return
	 */
	public double getUnadjustedX();

	/**
	 * Get the Y position taking position relative to block into account
	 * 
	 * @return
	 */
	public double getUnadjustedY();

	/**
	 * Return a new vector in the given dmiension
	 * 
	 * @param tag
	 * @return
	 */
	public IVector withDimension(IDimensionTag tag);

	/**
	 * Return a new vector on the given layer
	 * 
	 * @param layer
	 * @return
	 */
	public IVector withLayer(MapLayer layer);

	public IVector withXY(double x, double y);

	public IVector withX(double x);

	public IVector withY(double y);

	/**
	 * Add the given offset to this vector
	 */
	public default IVector add(IVector offset) {
		if (offset.getUnadjustedX() == 0 && offset.getUnadjustedY() == 0)
			return this;
		return this.withXY(this.getUnadjustedX() + offset.getUnadjustedX(),
				this.getUnadjustedY() + offset.getUnadjustedY());
	}

	/**
	 * Invert the direction of this vector
	 * 
	 * @return
	 */
	public default IVector invert() {
		return this.withXY(getUnadjustedX() * -1, getUnadjustedY() * -1);
	}

	/**
	 * Return a vector in the same direction but with the given magnitude
	 * 
	 * @param mag
	 * @return
	 */
	public default IVector withMagnitude(double mag) {
		if (mag == mag())
			return this;
		double m = mag();
		double x = this.getUnadjustedX();
		double y = this.getUnadjustedY();
		return this.withXY(x / m * mag, y / m * mag);
	}

	/**
	 * increase/decrease this vector's magnitude by the given amount
	 * 
	 * @param mag
	 * @return
	 */
	public default IVector add(double mag) {
		if (mag == 0)
			return this;
		return this.withMagnitude(mag() + mag);
	}

	/**
	 * decrease this vector's magnitude by the given amount but clamp its magnitude
	 * change at 0. If amount is negative, this function is equivalent to
	 * {@link #add(double)}
	 * 
	 * @param mag
	 * @return
	 */
	public default IVector clampSubtract(double mag) {
		if (mag == 0)
			return this;
		if (mag < 0) {
			return this.add(mag);
		}
		return this.withMagnitude(Math.max(0, mag() - mag));
	}

	/**
	 * Add the offset represented by x and y to this vector
	 */
	public default IVector add(double x, double y) {
		if (x == 0 && y == 0) {
			return this;
		}
		return this.withXY(this.getUnadjustedX() + x, this.getUnadjustedY() + y);
	}

	/**
	 * Return the magnitude of the vector
	 */
	public default double magnitude() {
		return Math.sqrt(Math.pow(this.getUnadjustedX(), 2) + Math.pow(this.getUnadjustedY(), 2));
	}

	/**
	 * same as {@link #magnitude()}, but shorter name
	 * 
	 * @return
	 */
	public default double mag() {
		return magnitude();
	}

	/**
	 * return a vector a layer below, or null if impossible
	 */
	public default IVector down() {
		if (this.getLayer() == null)
			return this;
		if (this.getLayer() == MapLayer.LOWEST) {
			return null;
		}

		return this.withLayer(MapLayer.values()[getLayer().ordinal() - 1]);
	}

	/**
	 * return a vector a layer above
	 */
	public default IVector up() {
		if (this.getLayer() == null)
			return this;
		if (this.getLayer() == MapLayer.getBlockLayers().get(MapLayer.numBlockLayers() - 1)) {
			return null;
		}

		return this.withLayer(MapLayer.values()[getLayer().ordinal() + 1]);
	}
}
