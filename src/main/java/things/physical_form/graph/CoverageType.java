package things.physical_form.graph;

import java.util.Collection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import sim.RelativeSide;
import utilities.graph.IInvertibleRelationType;

public class CoverageType implements IInvertibleRelationType {

	public static enum CoverageDirection {
		/** X covers the given side of Y */
		COVERS,
		/** X is covered on the given side by Y */
		COVERED_ON;

		public boolean covers() {
			return this == COVERS;
		}

		public boolean coveredOn() {
			return this == COVERED_ON;
		}

		public CoverageDirection opposite() {
			switch (this) {
			case COVERS:
				return COVERED_ON;
			default:
				return COVERS;
			}
		}
	}

	private static final Table<RelativeSide, CoverageDirection, CoverageType> coverages;

	private RelativeSide side;
	private CoverageDirection dir;

	/**
	 * Get all coverage types in th given direction
	 * 
	 * @param direction
	 * @return
	 */
	public static Collection<CoverageType> getCoverageTypes(CoverageDirection direction) {
		return coverages.column(direction).values();
	}

	/**
	 * Returns all coverage-types
	 * 
	 * @return
	 */
	public static Collection<CoverageType> getCoverageTypes() {
		return coverages.values();
	}

	/**
	 * Relation indicating that X covers the given side of Y
	 * 
	 * @param side
	 * @return
	 */
	public static CoverageType covers(RelativeSide side) {
		return coverages.get(side, CoverageDirection.COVERS);
	}

	/**
	 * Relation indicating that X is covered on the given side by Y
	 * 
	 * @param side
	 * @return
	 */
	public static CoverageType coveredOn(RelativeSide side) {
		return coverages.get(side, CoverageDirection.COVERED_ON);
	}

	private CoverageType(RelativeSide s, CoverageDirection d) {
		side = s;
		dir = d;
	}

	static {
		Table<RelativeSide, CoverageDirection, CoverageType> map = HashBasedTable.create();
		for (CoverageDirection dir : CoverageDirection.values()) {
			for (RelativeSide side : RelativeSide.values()) {
				map.put(side, dir, new CoverageType(side, dir));
			}
		}
		coverages = ImmutableTable.copyOf(map);
	}

	@Override
	public IInvertibleRelationType invert() {
		return coverages.get(side, dir.opposite());
	}

	@Override
	public boolean bidirectional() {
		return false;
	}

	public String toString() {
		return dir + "_" + side;
	}

	public RelativeSide getSide() {
		return side;
	}

	/**
	 * If this relation indicates that X covers SIDE of Y
	 * 
	 * @return
	 */
	public boolean covers() {
		return dir.covers();
	}

	/**
	 * If this relation indicates that X is covered on SIDE by Y
	 * 
	 * @return
	 */
	public boolean coveredOn() {
		return dir.coveredOn();
	}

}
