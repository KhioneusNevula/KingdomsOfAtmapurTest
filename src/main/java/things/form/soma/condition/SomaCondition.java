package things.form.soma.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;

import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import thinker.concepts.general_types.ILogicConcept;
import thinker.concepts.general_types.ILogicConcept.LogicType;
import utilities.couplets.Triplet;
import utilities.graph.IRelationGraph;
import utilities.graph.RelationGraph;

public class SomaCondition implements ISomaCondition {

	private IRelationGraph<Object, ISomaRelationType> graph;
	private Set<String> partMatches;

	public SomaCondition(IRelationGraph<? extends Object, ? extends ISomaRelationType> gra) {
		this.graph = (IRelationGraph<Object, ISomaRelationType>) gra;
		this.partMatches = gra.stream().filter((a) -> a instanceof String).map((a) -> (String) a)
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Get matches for part stats
	 * 
	 * @param t
	 * @param allparts
	 * @param stat
	 * @param relation
	 * @param value
	 * @return
	 */
	private Set<IComponentPart> getPartStatMatches(IRelationGraph<IComponentPart, IPartConnection> t,
			Multimap<String, IComponentPart> allparts, IPartStat stat, ISomaRelationType relation, Number value) {
		if (relation instanceof SomaRelationType srtRelation) {
			switch (srtRelation) {
			case EQUALS:
				return t.stream().filter((cp) -> cp.getStat(stat).equals(value)).collect(Collectors.toSet());
			case NOT_EQUALS:
				return t.stream().filter((cp) -> !cp.getStat(stat).equals(value)).collect(Collectors.toSet());
			case FACTOR_OF:
				return t.stream().filter((cp) -> value.intValue() % cp.<Number>getStat(stat).intValue() == 0)
						.collect(Collectors.toSet());
			case MULTIPLE_OF:
				return t.stream().filter((cp) -> cp.<Number>getStat(stat).intValue() % value.intValue() == 0)
						.collect(Collectors.toSet());
			case GREATER_THAN:
				return t.stream().filter((cp) -> cp.<Number>getStat(stat).doubleValue() > value.doubleValue())
						.collect(Collectors.toSet());
			case GREATER_THAN_OR_EQUAL_TO:
				return t.stream().filter((cp) -> cp.<Number>getStat(stat).doubleValue() >= value.doubleValue())
						.collect(Collectors.toSet());
			case LESS_THAN:
				return t.stream().filter((cp) -> cp.<Number>getStat(stat).doubleValue() < value.doubleValue())
						.collect(Collectors.toSet());
			case LESS_THAN_OR_EQUAL_TO:
				return t.stream().filter((cp) -> cp.<Number>getStat(stat).doubleValue() <= value.doubleValue())
						.collect(Collectors.toSet());
			default:
				throw new UnsupportedOperationException("Cannot handle this relation yet");
			}
		} else {

			throw new UnsupportedOperationException("Cannot handle this relation yet");
		}
	}

	/** gets parts that match the given relation */
	private Set<IComponentPart> getMatches(IRelationGraph<IComponentPart, IPartConnection> t,
			Triplet<Object, ISomaRelationType, Object> triplet) {
		if (triplet.getThird() instanceof ILogicConcept ilc) {
			if (ilc.getLogicType() == LogicType.OR) {
				return graph.getNeighbors(ilc, triplet.getSecond()).stream()
						.map((a) -> Triplet.of(triplet.getFirst(), triplet.getSecond(), a))
						.flatMap((a) -> getMatches(t, a).stream()).collect(Collectors.toSet());
			} else if (ilc.getLogicType() == LogicType.AND) {
				return graph.getNeighbors(ilc, triplet.getSecond()).stream()
						.map((a) -> Triplet.of(triplet.getFirst(), triplet.getSecond(), a)).map((a) -> getMatches(t, a))
						.reduce(t.getNodeSetImmutable(),
								(a, b) -> a.size() < b.size() ? Sets.intersection(a, b) : Sets.intersection(b, a));

			}
		}
		Multimap<String, IComponentPart> allparts = MultimapBuilder.hashKeys().hashSetValues().build();
		t.stream().forEach((a) -> allparts.put(a.getName(), a));
		if (triplet.getSecond() instanceof SomaRelationType type) {
			switch (type) {
			case HAS_ABILITY: {
				return t.stream().filter((a) -> a.getAbilities().contains(triplet.getThird()))
						.collect(Collectors.toSet());
			}
			case HAS_STAT: {
				if (triplet.getThird() instanceof ILogicConcept ilc && ilc.isPropertyAndValue()) {
					IPartStat stat = null;
					ISomaRelationType relation = null;
					Number value = null;
					Iterable<Triplet<Object, ISomaRelationType, Object>> goedgeitble = () -> graph.outgoingEdges(ilc);
					for (Triplet<Object, ISomaRelationType, Object> edge2 : goedgeitble) {
						if (edge2.getSecond().equals(SomaRelationType.HAS_STAT)) {
							if (edge2.getThird() instanceof IPartStat ips) {
								if (stat == null) {
									stat = ips;
								} else { // if we have a stat already
									throw new IllegalArgumentException("Too many stat relations for part "
											+ triplet.getFirst() + ": " + stat + " and " + ips);
								}
							} else { // if we don't have a stat to check
								throw new IllegalArgumentException(
										"Illegal relation: " + graph.edgeToString(edge2, true));
							}
						} else if (edge2.getSecond().characterizesValue()) {
							if (relation != null) {
								throw new IllegalArgumentException("Too many value relations for part "
										+ triplet.getFirst() + ": " + relation + " and " + edge2.getSecond());
							} else { // if we already have a relation to analyze
								relation = edge2.getSecond();
								if (edge2.getThird() instanceof Number i) {
									value = i;
								} else {
									throw new IllegalArgumentException(
											"Illegal relation: " + graph.edgeToString(edge2, true));
								}
							}
						} else { // if we have a relation that isn't a stat relation and doesn't characterize a
									// value
							throw new IllegalArgumentException("Illegal relation: " + graph.edgeToString(edge2, true));
						}
					}

					if (stat == null) {
						throw new IllegalArgumentException("No stat relation for part " + triplet.getFirst());
					}
					if (relation == null) {
						throw new IllegalArgumentException("No relation for part " + triplet.getFirst());
					}
					return getPartStatMatches(t, allparts, stat, relation, value);

				} else { // if the relation doesn't connect to a logical bifurcator
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
			}
			case EQUALS: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return allparts.asMap().entrySet().stream().filter((a) -> triplet.right().equals(a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet());
			}
			case NOT_EQUALS: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return allparts.asMap().entrySet().stream().filter((a) -> !triplet.right().equals(a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet());
			}
			case FACTOR_OF: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) % (a.getValue().size()) == 0)
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case GREATER_THAN: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) < (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case GREATER_THAN_OR_EQUAL_TO: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) <= (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case LESS_THAN: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) > (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case LESS_THAN_OR_EQUAL_TO: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) >= (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case MULTIPLE_OF: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: "
							+ graph.edgeToString(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), true));
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> (a.getValue().size()) % ((Integer) triplet.right()) == 0)
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			default: {

				// TODO handle later
				throw new UnsupportedOperationException("Cannot handle this relation yet");
			}
			}
		}
		throw new UnsupportedOperationException("Cannot handle this relation yet");
	}

	@Override
	public boolean test(IRelationGraph<IComponentPart, IPartConnection> t) {
		Multimap<String, IComponentPart> bestMatches = MultimapBuilder.hashKeys().hashSetValues().build();
		for (String part : partMatches) {
			Set<IComponentPart> matches = new HashSet<>(t);
			for (Triplet<Object, ISomaRelationType, Object> triplet : (Iterable<Triplet<Object, ISomaRelationType, Object>>) () -> graph
					.outgoingEdges((Object) part)) {
				Collection<IComponentPart> set = getMatches(t, triplet);
				if (set != null) {
					matches.retainAll(set);
				}
			}
			bestMatches.putAll(part, matches);
		}
		Iterable<Triplet<Object, ISomaRelationType, Object>> connectedIter = () -> graph
				.edgeIterator(Collections.singleton(SomaRelationType.CONNECTED));
		for (Triplet<Object, ISomaRelationType, Object> connectChecker : connectedIter) {
			if (connectChecker.getFirst() instanceof String part1
					&& connectChecker.getThird() instanceof String part2) {
				Set<IComponentPart> permissibles = new HashSet<>();
				for (IComponentPart pmatch1 : bestMatches.get(part1)) {
					for (IComponentPart pmatch2 : bestMatches.get(part2)) {
						for (PartConnection att : PartConnection.attachments()) {
							if (t.containsEdge(pmatch1, att, pmatch2)) {
								permissibles.add(pmatch1);
								permissibles.add(pmatch2);
							}
						}
					}
				}
				bestMatches.get(part1).retainAll(permissibles);
				bestMatches.get(part2).retainAll(permissibles);
			} else {
				throw new IllegalStateException("Illegal relation: " + graph.edgeToString(connectChecker, true));
			}
		}
		return bestMatches.asMap().entrySet().stream().allMatch((a) -> !a.getValue().isEmpty());
	}

	@Override
	public IRelationGraph<?, ? extends ISomaRelationType> getConditionGraph() {
		return graph;
	}

	@Override
	public Collection<String> getParts() {
		return this.partMatches;
	}

	@Override
	public Predicate<IRelationGraph<IComponentPart, IPartConnection>> and(
			Predicate<? super IRelationGraph<IComponentPart, IPartConnection>> other) {
		if (other instanceof ISomaCondition sc) {
			RelationGraph grap = new RelationGraph(graph);
			grap.addAll(sc.getConditionGraph());
			return new SomaCondition(grap);
		}
		return ISomaCondition.super.and(other);
	}

	@Override
	public int hashCode() {
		return graph.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof ISomaCondition sc) {
			return this.graph.equals(sc.getConditionGraph());
		}
		return false;
	}

	@Override
	public String toString() {
		return "SomaCondition{" + this.graph + "}";
	}

}
