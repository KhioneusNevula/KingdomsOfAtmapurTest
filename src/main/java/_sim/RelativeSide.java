package _sim;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import utilities.graph.IInvertibleRelationType;

/**
 * L: {@link #LEFT} <br>
 * R: {@link #RIGHT} <br>
 * T: {@link #TOP} <br>
 * B: {@link #BOTTOM} <br>
 * F: {@link #FRONT} <br>
 * R: {@link #REAR} <br>
 * 
 * @author borah
 *
 */
public enum RelativeSide implements IInvertibleRelationType {
	LEFT, RIGHT(LEFT), TOP, BOTTOM(TOP), FRONT, REAR(FRONT);

	private RelativeSide reverse;
	private static final List<RelativeSide> horizontals;
	private static final List<RelativeSide> verticals;

	static {
		horizontals = ImmutableList.of(LEFT, RIGHT, FRONT, REAR);
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
		return this == LEFT || this == RIGHT || this == FRONT || this == REAR;
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
	public boolean bidirectional() {
		return reverse == this;
	}

	/**
	 * Same as {@link #opposite()}
	 * 
	 * @return
	 */
	@Override
	public IInvertibleRelationType invert() {
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

}
