package _utilities.couplets;

import com.google.common.collect.Table;

/**
 * @Immutable
 * 
 * @author borah
 *
 * @param <A> first parameter type
 * @param <B> second parameter type
 */
public class Triplet<A, B, C> implements Table.Cell<A, B, C> {

	private A first;
	private B second;
	private C third;
	private String firstLabel;
	private String secondLabel;
	private String thirdLabel;

	private static final Triplet EMPTY = new Triplet(null, null, null);

	public static final <A, B, C> Triplet<A, B, C> of(A first, B second, C third) {
		if (first == null && second == null && third == null) {
			return (Triplet<A, B, C>) EMPTY;
		}
		return new Triplet<>(first, second, third);
	}

	public static final <A, B, C> Triplet<A, B, C> of(String al, A first, String bl, B second, String cl, C third) {
		return new Triplet<>(al, first, bl, second, cl, third);
	}

	private Triplet(String al, A first, String bl, B second, String cl, C third) {
		this.first = first;
		this.firstLabel = al;
		this.second = second;
		this.secondLabel = bl;
		this.third = third;
		this.thirdLabel = cl;
	}

	private Triplet(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	/** First item in this triplet */
	public A getFirst() {
		return first;
	}

	/** Alias of {@link #getFirst()} to represent a graph edge */
	public A getEdgeStart() {
		return first;
	}

	public String getFirstLabel() {
		return firstLabel;
	}

	/** Alias of {@link #getFirst()} to represent a directional triplet */
	public A left() {
		return first;
	}

	/** Alias of {@link #getSecond()} to represent a directional triplet */
	public B center() {
		return second;
	}

	/** Alias of {@link #getSecond()} to represent a graph edge */
	public B getEdgeType() {
		return second;
	}

	/** alias of {@link #getThird()} to represent a directional triplet */
	public C right() {
		return third;
	}

	/** alias of {@link #getThird()} to represent a graph edge */
	public C getEdgeEnd() {
		return third;
	}

	/** Alias of {@link #getFirst()} to represent a coordinate */
	public A getX() {
		return first;
	}

	/** Alias of {@link #getSecond()} to represent a coordinate */
	public B getY() {
		return second;
	}

	/** Alias of {@link #getThird()} to represent a coordinate */
	public C getZ() {
		return third;
	}

	/** Second item in this triplet */
	public B getSecond() {
		return second;
	}

	public String getSecondLabel() {
		return secondLabel;
	}

	/** Third item in this triplet */
	public C getThird() {
		return third;
	}

	public String getThirdLabel() {
		return thirdLabel;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setSecond(B second) {
		this.second = second;
	}

	public void setThird(C third) {
		this.third = third;
	}

	@Override
	public String toString() {
		return "<" + (firstLabel != null ? firstLabel + "=" : "") + first + ","
				+ (secondLabel != null ? secondLabel + "=" : "") + second + ","
				+ (thirdLabel != null ? thirdLabel + "=" : "") + third + ">";
	}

	@Override
	public int hashCode() {
		return (first != null ? first.hashCode() : 0) + (second != null ? second.hashCode() : 0)
				+ (third != null ? third.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Triplet tr) {
			return (this.first != null ? tr.first != null && this.first.equals(tr.first) : tr.first == null)
					&& (this.second != null ? tr.second != null && this.second.equals(tr.second) : tr.second == null)
					&& (this.third != null ? tr.third != null && this.third.equals(tr.third) : tr.third == null);
		}
		return super.equals(obj);
	}

	/**
	 * Alias of {@link #getFirst()} in keeping with the guava {@link Table}
	 * interface.
	 */
	@Override
	public B getColumnKey() {
		return this.second;
	}

	/**
	 * Alias of {@link #getSecond()} in keeping with the guava {@link Table}
	 * interface.
	 */
	@Override
	public A getRowKey() {
		return this.first;
	}

	/**
	 * Alias of {@link #getThird()} in keeping with the guava {@link Table}
	 * interface.
	 */
	@Override
	public C getValue() {
		return this.third;
	}

}
