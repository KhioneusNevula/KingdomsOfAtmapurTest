package things.form.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import _utilities.collections.MappedSet;
import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import things.form.IForm;
import things.form.IPart;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.form.visage.ISensableProperty;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;

/**
 * TODO Add sensable traits? make into just form condition?
 * 
 * @author borah
 *
 */
public class FormCondition implements IFormCondition {

	private IRelationGraph<Object, IFormRelationType> graph;
	private Set<String> partMatches;
	private Set<IChannelSystem> channels;

	public FormCondition(IRelationGraph<? extends Object, ? extends IFormRelationType> gra, IChannelSystem... sys) {
		this.graph = (IRelationGraph<Object, IFormRelationType>) gra;
		this.partMatches = gra.stream().filter((a) -> a instanceof String).map((a) -> (String) a)
				.collect(Collectors.toUnmodifiableSet());
		this.channels = ImmutableSet.copyOf(sys);
	}

	public FormCondition addChannelSystems(Iterable<IChannelSystem> sys) {
		this.channels = ImmutableSet.<IChannelSystem>builder().addAll(channels).addAll(sys).build();
		return this;
	}

	/**
	 * Get matches for part sensable properties
	 * 
	 * @param t
	 * @param allparts
	 * @param stat
	 * @param relation
	 * @param value
	 * @return
	 */
	private Set<IPart> getPartSensableMatches(IRelationGraph<? extends IPart, IPartConnection> t,
			Multimap<String, IPart> allparts, ISensableProperty<?> stat, IFormRelationType relation, Object value) {
		if (relation instanceof FormRelationType srtRelation) {
			if (relation == FormRelationType.EQUALS) {
				return t.stream()
						.filter((a) -> a.getSensableProperty(stat) != null && a.getSensableProperty(stat).equals(value))
						.collect(Collectors.toSet());
			} else if (relation == FormRelationType.NOT_EQUALS) {
				return t.stream().filter(
						(a) -> a.getSensableProperty(stat) == null || !a.getSensableProperty(stat).equals(value))
						.collect(Collectors.toSet());
			} else if (value instanceof Comparable comparer) {
				ISensableProperty<? extends Comparable> statC = (ISensableProperty<? extends Comparable>) stat;
				if (value instanceof Number number) {
					switch (srtRelation) {
					case FACTOR_OF:
						return t.stream()
								.filter((cp) -> cp.getSensableProperty(stat) != null
										&& number.intValue() % ((Number) cp.getSensableProperty(stat)).intValue() == 0)
								.collect(Collectors.toSet());
					case MULTIPLE_OF:
						return t.stream()
								.filter((cp) -> cp.getSensableProperty(stat) != null
										&& ((Number) cp.getSensableProperty(stat)).intValue() % number.intValue() == 0)
								.collect(Collectors.toSet());
					}
				}
				switch (srtRelation) {

				case GREATER_THAN:
					return t.stream()
							.filter((cp) -> cp.getSensableProperty(stat) != null
									&& cp.getSensableProperty(statC).compareTo(comparer) > 0)
							.collect(Collectors.toSet());
				case GREATER_THAN_OR_EQUAL_TO:
					return t.stream()
							.filter((cp) -> cp.getSensableProperty(stat) != null
									&& cp.getSensableProperty(statC).compareTo(comparer) >= 0)
							.collect(Collectors.toSet());
				case LESS_THAN:
					return t.stream()
							.filter((cp) -> cp.getSensableProperty(stat) != null
									&& cp.getSensableProperty(statC).compareTo(comparer) < 0)
							.collect(Collectors.toSet());
				case LESS_THAN_OR_EQUAL_TO:
					return t.stream()
							.filter((cp) -> cp.getSensableProperty(stat) != null
									&& cp.getSensableProperty(statC).compareTo(comparer) <= 0)
							.collect(Collectors.toSet());
				default:
					throw new IllegalArgumentException("Cannot handle this relation for a comparable/numeric.");
				}
			} else {
				throw new UnsupportedOperationException("Cannot yet handle this relation type.");

			}
		} else {
			throw new UnsupportedOperationException("Cannot handle this relation yet");

		}
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
	private Set<IPart> getPartStatMatches(IRelationGraph<? extends IPart, IPartConnection> t,
			Multimap<String, IPart> allparts, IPartStat stat, IFormRelationType relation, Number value) {
		if (relation instanceof FormRelationType srtRelation) {
			switch (srtRelation) {
			case EQUALS:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> cp.getStat(stat).equals(value)).collect(Collectors.toSet());
			case NOT_EQUALS:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> !cp.getStat(stat).equals(value)).collect(Collectors.toSet());
			case FACTOR_OF:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> value.intValue() % cp.<Number>getStat(stat).intValue() == 0)
						.collect(Collectors.toSet());
			case MULTIPLE_OF:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> cp.<Number>getStat(stat).intValue() % value.intValue() == 0)
						.collect(Collectors.toSet());
			case GREATER_THAN:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> cp.<Number>getStat(stat).doubleValue() > value.doubleValue())
						.collect(Collectors.toSet());
			case GREATER_THAN_OR_EQUAL_TO:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> cp.<Number>getStat(stat).doubleValue() >= value.doubleValue())
						.collect(Collectors.toSet());
			case LESS_THAN:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> cp.<Number>getStat(stat).doubleValue() < value.doubleValue())
						.collect(Collectors.toSet());
			case LESS_THAN_OR_EQUAL_TO:
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((cp) -> cp.<Number>getStat(stat).doubleValue() <= value.doubleValue())
						.collect(Collectors.toSet());
			default:
				throw new UnsupportedOperationException("Cannot handle this relation yet");
			}
		} else {

			throw new UnsupportedOperationException("Cannot handle this relation yet");
		}
	}

	/** gets parts that match the given relation */
	private <T extends IPart> Set<IPart> getMatches(IRelationGraph<T, IPartConnection> t,
			Triplet<Object, IFormRelationType, Object> triplet) {
		if (triplet.getThird() instanceof IConnectorConcept ilc) {
			if (ilc.getConnectorType() == ConnectorType.OR) {
				return graph.getNeighbors(ilc, triplet.getSecond()).stream()
						.map((a) -> Triplet.of(triplet.getFirst(), triplet.getSecond(), a))
						.flatMap((a) -> getMatches(t, a).stream()).collect(Collectors.toSet());
			} else if (ilc.getConnectorType() == ConnectorType.AND) {
				return graph.getNeighbors(ilc, triplet.getSecond()).stream()
						.map((a) -> Triplet.of(triplet.getFirst(), triplet.getSecond(), a)).map((a) -> getMatches(t, a))
						.reduce(new MappedSet<>(t, (a) -> (T) a, (b) -> b, IPart.class), (a, b) -> {
							Set<IPart> xa = new HashSet<>(a);
							xa.retainAll(b);
							return xa;
						});

			}
		}
		Multimap<String, IPart> allparts = MultimapBuilder.hashKeys().hashSetValues().build();
		t.stream().forEach((a) -> allparts.put(a.getName(), a));
		if (triplet.getSecond() instanceof FormRelationType type) {
			switch (type) {
			case HAS_ABILITY: {
				return t.stream().filter((a) -> a instanceof IComponentPart).map((a) -> (IComponentPart) a)
						.filter((a) -> a.getAbilities().contains(triplet.getThird())).collect(Collectors.toSet());
			}
			case HAS_SENSABLE_TRAIT:
			case HAS_STAT: {
				if (triplet.getThird() instanceof IConnectorConcept ilc && ilc.isPropertyAndValue()) {
					Object stat = null;
					IFormRelationType relation = null;
					Object value = null;
					Iterable<Triplet<Object, IFormRelationType, Object>> goedgeitble = () -> graph.outgoingEdges(ilc);
					for (Triplet<Object, IFormRelationType, Object> edge2 : goedgeitble) {
						if (edge2.getSecond().equals(type)) {
							if (edge2.getSecond().equals(FormRelationType.HAS_STAT)
									&& edge2.getThird() instanceof IPartStat
									|| edge2.getSecond().equals(FormRelationType.HAS_SENSABLE_TRAIT)
											&& edge2.getThird() instanceof ISensableProperty) {
								if (stat == null) {
									stat = edge2.getThird();
								} else { // if we have a stat already
									throw new IllegalArgumentException("Too many stat relations for part "
											+ triplet.getFirst() + ": " + stat + " and " + edge2.getThird());
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
								if (edge2.getSecond().equals(FormRelationType.HAS_STAT)) { // stats only permit numbers
									if (edge2.getThird() instanceof Number i) {
										value = i;
									} else {
										throw new IllegalArgumentException(
												"Illegal relation: " + graph.edgeToString(edge2, true));
									}
								} else {
									value = edge2.getThird(); // traits permit anything (for now)
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
					if (value == null) {
						throw new IllegalArgumentException("No value for part " + triplet.getFirst());
					}
					if (stat instanceof IPartStat st) {
						return getPartStatMatches(t, allparts, st, relation, (Number) value);
					} else {
						ISensableProperty<?> prop = (ISensableProperty<?>) stat;
						if (prop.getType().isAssignableFrom(value.getClass())) {
							return getPartSensableMatches(t, allparts, prop, relation, value);
						} else {
							throw new IllegalArgumentException("Illegal relation: " + prop + " to " + value);
						}
					}

				} else { // if the relation doesn't connect to a logical bifurcator
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
			}
			case EQUALS: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
				return allparts.asMap().entrySet().stream().filter((a) -> triplet.right().equals(a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet());
			}
			case NOT_EQUALS: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
				return allparts.asMap().entrySet().stream().filter((a) -> !triplet.right().equals(a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet());
			}
			case FACTOR_OF: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) % (a.getValue().size()) == 0)
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case GREATER_THAN: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) < (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case GREATER_THAN_OR_EQUAL_TO: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) <= (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case LESS_THAN: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) > (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case LESS_THAN_OR_EQUAL_TO: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
				}
				return (allparts.asMap().entrySet().stream()
						.filter((a) -> ((Integer) triplet.right()) >= (a.getValue().size()))
						.flatMap((a) -> a.getValue().stream()).collect(Collectors.toSet()));
			}
			case MULTIPLE_OF: {
				if (!(triplet.right() instanceof Integer)) {
					throw new IllegalArgumentException("Illegal relation: " + triplet.getFirst() + " , "
							+ triplet.getSecond() + " , " + triplet.getThird());
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
	public boolean test(IForm<?> f) {
		if (!this.getChannelSystemRequirements().isEmpty()) {
			if (f instanceof ISoma soma) {
				if (!soma.getChannelSystems().containsAll(this.getChannelSystemRequirements())) {
					return false;
				}
			} else {
				return false;
			}
		}
		if (graph.isEmpty()) {
			return true;
		}
		IRelationGraph<? extends IPart, IPartConnection> t = f.getPartGraph();
		if (t.isEmpty()) {
			return false;
		}
		Multimap<String, IPart> bestMatches = MultimapBuilder.hashKeys().hashSetValues().build();
		for (String part : partMatches) {
			Set<IPart> matches = new HashSet<>(t);
			for (Triplet<Object, IFormRelationType, Object> triplet : (Iterable<Triplet<Object, IFormRelationType, Object>>) () -> graph
					.outgoingEdges((Object) part)) {
				Collection<IPart> set = getMatches(t, triplet);
				if (set != null) {
					matches.retainAll(set);
				}
			}
			bestMatches.putAll(part, matches);
		}

		for (Triplet<Object, IFormRelationType, Object> connectChecker : (Iterable<Triplet<Object, IFormRelationType, Object>>) () -> graph
				.edgeIterator(Collections.singleton(FormRelationType.CONNECTED))) {
			if (connectChecker.getFirst() instanceof String part1
					&& connectChecker.getThird() instanceof String part2) {
				Set<IPart> permissibles = new HashSet<>();
				for (IPart pmatch1 : bestMatches.get(part1)) {
					for (IPart pmatch2 : bestMatches.get(part2)) {
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
	public IRelationGraph<?, ? extends IFormRelationType> getConditionGraph() {
		return graph;
	}

	@Override
	public Collection<String> getParts() {
		return this.partMatches;
	}

	@Override
	public Predicate<IForm<?>> and(Predicate<? super IForm<?>> other) {
		if (other instanceof IFormCondition sc) {
			RelationGraph grap = new RelationGraph(graph);
			grap.addAll(sc.getConditionGraph());
			Set<IChannelSystem> syses = new HashSet<>(channels);
			syses.addAll(sc.getChannelSystemRequirements());
			return new FormCondition(grap).addChannelSystems(syses);
		}
		return IFormCondition.super.and(other);
	}

	@Override
	public int hashCode() {
		return graph.hashCode() + channels.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof IFormCondition sc) {
			return this.graph.equals(sc.getConditionGraph()) && this.channels.equals(sc.getChannelSystemRequirements());
		}
		return false;
	}

	@Override
	public String toString() {
		return "FC{graph=" + this.graph + ",channelSystems=" + this.channels + "}";
	}

	@Override
	public Collection<IChannelSystem> getChannelSystemRequirements() {
		return this.channels;
	}

}
