package _utilities.couplets;

import java.util.Map;

/**
 * @Immutable
 * 
 * @author borah
 *
 * @param <A> first parameter type
 * @param <B> second parameter type
 */
public class Pair<A, B> implements Map.Entry<A, B>, Cloneable {

	private A first;
	private B second;
	private String firstLabel;
	private String secondLabel;

	// private static final Pair EMPTY = new Pair(null, null);

	/**
	 * Labels are ignored when checking for equality; they purely exist for a nicer
	 * toString representation
	 * 
	 * @param <A>
	 * @param <B>
	 * @param label1
	 * @param first
	 * @param label2
	 * @param second
	 * @return
	 */
	public static final <A, B> Pair<A, B> of(String label1, A first, String label2, B second) {
		Pair<A, B> pair = of(first, second);
		pair.firstLabel = label1;
		pair.secondLabel = label2;
		return pair;
	}

	public static final <A, B> Pair<A, B> of(A first, B second) {
		// if (first == null && second == null) {
		// return (Pair<A, B>) EMPTY;
		// }
		return new Pair<>(first, second);
	}

	private Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	public String getFirstLabel() {
		return firstLabel;
	}

	public String getSecondLabel() {
		return secondLabel;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	public A getX() {
		return first;
	}

	public B getY() {
		return second;
	}

	public void setX(A x) {
		this.setFirst(x);
	}

	public void setY(B y) {
		this.setSecond(y);
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	@Override
	public String toString() {
		if (firstLabel != null) {
			return "<" + firstLabel + "=" + first + ", " + secondLabel + "=" + second + ">";
		}
		return "<" + first + "," + second + ">";
	}

	@Override
	public A getKey() {
		return this.first;
	}

	@Override
	public B getValue() {
		return this.second;
	}

	public A left() {
		return this.first;
	}

	public B right() {
		return this.second;
	}

	@Override
	public B setValue(B value) {
		B s = second;
		this.setSecond(value);
		return s;
	}

	@Override
	public int hashCode() {
		return (first != null ? first.hashCode() : 0) + (second != null ? second.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair pair) {
			return (this.first != null ? pair.first != null && this.first.equals(pair.first) : pair.first == null)
					&& (this.second != null ? pair.second != null && this.second.equals(pair.second)
							: pair.second == null);
		}
		return super.equals(obj);
	}

	@Override
	public Pair<A, B> clone() {

		Pair<A, B> pa = Pair.of(first, second);
		pa.firstLabel = this.firstLabel;
		pa.secondLabel = this.secondLabel;
		return pa;
	}

}
