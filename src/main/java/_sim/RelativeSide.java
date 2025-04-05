package _sim;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import _sim.vectors.IVector;
import _utilities.graph.IInvertibleRelationType;

/**
 * L: {@link #LEFT} <br>
 * R: {@link #RIGHT} <br>
 * T: {@link #TOP} <br>
 * B: {@link #BOTTOM} <br>
 * F: {@link #FRONT} <br>
 * R: {@link #HIND} <br>
 * 
 * @author borah
 *
 */
public enum RelativeSide implements IInvertibleRelationType {
	LEFT, RIGHT(LEFT), TOP, BOTTOM(TOP), FRONT, HIND(FRONT);

	private RelativeSide reverse;
	private static final List<RelativeSide> horizontals;
	private static final List<RelativeSide> verticals;

	static {
		horizontals = ImmutableList.of(LEFT, RIGHT, FRONT, HIND);
		verticals = ImmutableList.of(TOP, BOTTOM);
	}

	private RelativeSide() {
		this.reverse = this;
	}

	private RelativeSide(RelativeSide reverse) {
		this.reverse = reverse;
		reverse.reverse = this;
	}

	public static List<RelativeSide> horizontals() {
		return horizontals;
	}

	public static List<RelativeSide> verticals() {
		return verticals;
	}

	public boolean vertical() {
		return this == TOP || this == BOTTOM;
	}

	public boolean horizontal() {
		return this == LEFT || this == RIGHT || this == FRONT || this == HIND;
	}

	/**
	 * Get the opposite side
	 * 
	 * @return
	 */
	public RelativeSide opposite() {
		return reverse;
	}

	@Override
	public String checkEndType(Object node) {
		return null;
	}

	@Override
	public boolean bidirectional() {
		return reverse == this;
	}

	/**
	 * Same as {@link #opposite()}
	 * 
	 * @return
	 */
	@Override
	public RelativeSide invert() {
		return reverse;
	}

	/**
	 * Return a string that is only the first letters of each side given
	 * 
	 * @param sides
	 * @return
	 */
	public static String firstLetters(Set<RelativeSide> sides) {
		StringBuilder sb = new StringBuilder();
		for (RelativeSide s : sides) {
			sb.append(s.name().charAt(0));
		}
		return sb.toString();
	}

	/**
	 * Return the vector representing the direction of motion represented in this
	 * side. For Top/Bottom, return a 0, 0 vector. Note that +y is back and -y is
	 * front, while -x is left and +x is right
	 * 
	 * @return
	 */
	public IVector getChangeVector() {
		switch (this) {
		case BOTTOM:
			return IVector.ZERO;
		case TOP:
			return IVector.ZERO;
		case FRONT:
			return IVector.of(0, -1);
		case HIND:
			return IVector.of(0, 1);
		case LEFT:
			return IVector.of(-1, 0);
		case RIGHT:
			return IVector.of(1, 0);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Return the general direction of a vector. Ambiguous values are rounded
	 * clockwise. 0, 0 evaluates to Left
	 * 
	 * @param force
	 * @return
	 */
	public static RelativeSide fromVector(IVector force) {
		IVector unit = force.normalize();
		double x = unit.getUnadjustedX();
		double y = unit.getUnadjustedY();
		if (x > 0 && y > 0) {
			if (x > y) {
				return RIGHT;
			} else {
				return HIND;
			}
		} else if (x < 0 && y > 0) {
			if (-x >= y) {
				return LEFT;
			} else {
				return HIND;
			}
		} else if (x < 0 && y < 0) {
			if (-x > -y) {
				return LEFT;
			} else {
				return FRONT;
			}
		} else if (x > 0 && y < 0) {
			if (x >= -y) {
				return RIGHT;
			} else {
				return FRONT;
			}
		} else if (x > 0 && y == 0)
			return RIGHT;
		else if (x < 0 && y == 0)
			return LEFT;
		else if (y > 0 && x == 0)
			return HIND;
		else if (y < 0 && x == 0)
			return FRONT;
		return LEFT;
	}

}
