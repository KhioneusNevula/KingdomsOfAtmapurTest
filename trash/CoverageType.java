package things.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import utilities.graph.IInvertibleRelationType;

public interface CoverageType extends IInvertibleRelationType, Iterable<RelativeSide> {

	/**
	 * Whether this indicates the forward relation, i.e. START covers END
	 * 
	 * @return
	 */
	public boolean covers();

	/**
	 * Whether this indicates the backwards relation, i.e. END covers START
	 * 
	 * @return
	 */
	public boolean coveredBy();

	/**
	 * Get sides that are covered by this CoverageType
	 * 
	 * @return
	 */
	public Collection<RelativeSide> getCoverageSides();

	/**
	 * Return a coverage with the given covered sides removed
	 * 
	 * @param sides
	 * @return
	 */
	public CoverageType removeSides(Set<RelativeSide> sides);

	/**
	 * Return a coverage with only sides that are present in both this and the given
	 * set
	 * 
	 * @param sides
	 * @return
	 */
	public CoverageType retainSides(Set<RelativeSide> sides);

	/**
	 * Return a coverage with the given covered sides added
	 * 
	 * @param sides
	 * @return
	 */
	public CoverageType addSides(Set<RelativeSide> sides);

	public static CoversType coversNothing() {
		return CoversType.coverages.computeIfAbsent(Collections.EMPTY_SET, (se) -> new CoversType(se));
	}

	public static CoversType covers(Set<RelativeSide> sides) {
		return CoversType.coverages.computeIfAbsent(sides, (se) -> new CoversType(se));
	}

	public static CoversType.CoveredByInverseType coveredBy(Set<RelativeSide> sides) {
		return CoversType.coverages.computeIfAbsent(new InverseSet(sides), (se) -> new CoversType(se)).invert();
	}

	public static class CoversType implements CoverageType {

		private Set<RelativeSide> sides;
		private static final Map<Set<RelativeSide>, CoversType> coverages = new HashMap<>();
		private CoveredByInverseType inverse;

		private CoversType(Set<RelativeSide> con) {
			this.sides = EnumSet.copyOf(con);
			this.inverse = new CoveredByInverseType();
		}

		@Override
		public CoversType addSides(Set<RelativeSide> sides) {
			EnumSet<RelativeSide> set = EnumSet.copyOf(Sets.union(this.sides, sides));
			return coverages.computeIfAbsent(set, (se) -> new CoversType(se));
		}

		@Override
		public boolean bidirectional() {
			return false;
		}

		@Override
		public boolean coveredBy() {
			return false;
		}

		@Override
		public boolean covers() {
			return true;
		}

		@Override
		public Collection<RelativeSide> getCoverageSides() {
			return sides;
		}

		@Override
		public CoveredByInverseType invert() {
			return inverse;
		}

		@Override
		public CoverageType removeSides(Set<RelativeSide> sides) {
			EnumSet<RelativeSide> set = EnumSet.copyOf(Sets.difference(this.sides, sides));
			return coverages.computeIfAbsent(set, (se) -> new CoversType(se));
		}

		@Override
		public CoverageType retainSides(Set<RelativeSide> sides) {
			EnumSet<RelativeSide> set = EnumSet.copyOf(Sets.intersection(this.sides, sides));
			return coverages.computeIfAbsent(set, (se) -> new CoversType(se));
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CoverageType ct) {
				return ct.covers() && this.sides.equals(ct.getCoverageSides());
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return this.sides.hashCode();
		}

		@Override
		public Iterator<RelativeSide> iterator() {
			return sides.iterator();
		}

		@Override
		public String toString() {
			return "COVERS[" + RelativeSide.firstLetters(sides) + "]";
		}

		public class CoveredByInverseType implements CoverageType {

			private Set<RelativeSide> inverseSides;

			public CoveredByInverseType() {
				inverseSides = new InverseSet(sides);
			}

			@Override
			public CoversType invert() {
				return CoversType.this;
			}

			@Override
			public boolean bidirectional() {
				return false;
			}

			@Override
			public boolean covers() {
				return false;
			}

			@Override
			public boolean coveredBy() {
				return true;
			}

			@Override
			public Collection<RelativeSide> getCoverageSides() {
				return inverseSides;
			}

			@Override
			public CoverageType removeSides(Set<RelativeSide> sides) {
				EnumSet<RelativeSide> set = EnumSet
						.copyOf(Sets.difference(CoversType.this.sides, new InverseSet(sides)));
				return coverages.computeIfAbsent(set, (se) -> new CoversType(se)).invert();
			}

			@Override
			public CoverageType retainSides(Set<RelativeSide> sides) {
				EnumSet<RelativeSide> set = EnumSet
						.copyOf(Sets.intersection(CoversType.this.sides, new InverseSet(sides)));
				return coverages.computeIfAbsent(set, (se) -> new CoversType(se)).invert();
			}

			@Override
			public CoverageType addSides(Set<RelativeSide> sides) {
				EnumSet<RelativeSide> set = EnumSet.copyOf(Sets.union(CoversType.this.sides, new InverseSet(sides)));
				return coverages.computeIfAbsent(set, (se) -> new CoversType(se)).invert();
			}

			@Override
			public Iterator<RelativeSide> iterator() {
				return inverseSides.iterator();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof CoverageType ct) {
					return ct.covers() && this.inverseSides.equals(ct.getCoverageSides());
				}
				return super.equals(obj);
			}

			@Override
			public int hashCode() {
				return -this.inverseSides.hashCode();
			}

			@Override
			public String toString() {
				return "COVERED_BY[" + RelativeSide.firstLetters(sides) + "]";
			}

		}

	}

	public static class InverseSet implements Set<RelativeSide> {

		private Set<RelativeSide> sides;

		public InverseSet(Set<RelativeSide> sides) {
			this.sides = sides;
		}

		@Override
		public int size() {
			return sides.size();
		}

		@Override
		public boolean isEmpty() {
			return sides.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof RelativeSide rs) {
				return sides.contains(rs.opposite());
			}
			return false;
		}

		@Override
		public Iterator<RelativeSide> iterator() {
			return new Iter();
		}

		@Override
		public Object[] toArray() {
			List<RelativeSide> sides = new ArrayList<>(size());
			for (RelativeSide side : this) {
				sides.add(side);
			}

			return sides.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			List<RelativeSide> sides = new ArrayList<>(size());
			for (RelativeSide side : this) {
				sides.add(side);
			}

			return sides.toArray(a);
		}

		@Override
		public boolean add(RelativeSide e) {
			throw new UnsupportedOperationException(e + "");
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException(o + "");
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (o instanceof RelativeSide rs) {
					if (!sides.contains(rs.opposite())) {
						return false;
					}
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends RelativeSide> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		private class Iter implements Iterator<RelativeSide> {

			private Iterator<RelativeSide> inter;

			public Iter() {
				inter = sides.iterator();
			}

			@Override
			public boolean hasNext() {
				return inter.hasNext();
			}

			@Override
			public RelativeSide next() {
				return inter.next().opposite();
			}

		}

	}
}
