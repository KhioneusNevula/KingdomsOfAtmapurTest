package things.physical_form.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import sim.RelativeSide;
import things.physical_form.IPart;
import things.physical_form.graph.CoverageType.CoverageDirection;
import utilities.graph.EdgeProperty;
import utilities.graph.RelationGraph;

/**
 * TODO add parameters for the nature of the cover (solidness, transparency, and
 * so on)
 * 
 * @author borah
 *
 */
public class CoverageGraph<E extends IPart> extends RelationGraph<E, CoverageType> {

	public static final EdgeProperty<Float> COVERAGE_PERCENT = new EdgeProperty<>("coverage_percent", float.class);

	public CoverageGraph() {
		super(ImmutableMap.of(COVERAGE_PERCENT, () -> 0f));
	}

	/**
	 * Return (immutable) set representing the coverage between these two parts
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public Collection<RelativeSide> getCoverage(E one, E two) {
		if (!this.containsEdge(one, two))
			return Collections.emptySet();
		return this.getEdgeTypesBetween(one, two).stream().filter((a) -> a.covers()).map((a) -> a.getSide())
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Return the percent of a specific side of Covered that Coverer covers
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public float percentCovered(E coverer, E covered, RelativeSide side) {
		return this.getProperty(coverer, CoverageType.covers(side), covered, COVERAGE_PERCENT);
	}

	/**
	 * Return the total percent of Covered's sides that Coverer covers
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public float percentCovered(E coverer, E covered) {
		float total = 0;
		for (RelativeSide side : RelativeSide.values()) {
			total += this.getProperty(coverer, CoverageType.covers(side), covered, COVERAGE_PERCENT);
		}
		return total / RelativeSide.values().length;
	}

	/**
	 * Returns what sides of this part have coverage of any kind
	 * 
	 * @param part
	 * @return
	 */
	public Set<RelativeSide> getOverallCoverage(E part) {
		Set<RelativeSide> sides = new HashSet<>();
		for (RelativeSide side : RelativeSide.values()) {
			if (!this.traverseBFS(part, CoverageType.getCoverageTypes(CoverageDirection.COVERED_ON),
					Function.identity()::apply, (prop, obj) -> true).isEmpty()) {
				sides.add(side);
			}
		}
		return sides;
	}

}
