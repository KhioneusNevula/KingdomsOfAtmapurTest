package thinker.helpers;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;

import _utilities.couplets.Triplet;
import _utilities.function.TriPredicate;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.ITypePatternConcept;
import thinker.concepts.general_types.IValueConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventCategory;
import thinker.concepts.relations.actional.IEventRelationType;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.knowledge.IKnowledgeRepresentation;
import thinker.knowledge.base.IKnowledgeBase;

/**
 * Helper methods for action stuff
 * 
 * @author borah
 *
 */
public class RelationsHelper {
	private RelationsHelper() {
	}

	private static final BiConsumer<Triplet<Set<IConcept>, Set<IConcept>, Set<IConcept>>, Triplet<Set<IConcept>, Set<IConcept>, Set<IConcept>>> unrelations_biconsumer = (
			trip1, trip2) -> {
		trip1.getFirst().addAll(trip2.getFirst());
		trip1.getSecond().addAll(trip2.getSecond());
		trip1.getThird().addAll(trip2.getThird());
	};

	private static BiConsumer<Triplet<Set<IConcept>, Set<IConcept>, Set<IConcept>>, IConcept> makeunrelaccum(
			TriPredicate<IConcept, IConceptRelationType, IConcept> notcheck,
			TriPredicate<IConcept, IConceptRelationType, IConcept> oppcheck, IConcept source,
			IConceptRelationType type) {
		return (trip, con) -> {
			if (notcheck.test(source, type, con)) {
				trip.getSecond().add(con);
			} else if (notcheck.test(source, type, con)) {
				trip.getThird().add(con);
			} else {
				trip.getFirst().add(con);
			}
		};
	}

	/**
	 * Relation valences for edges
	 * 
	 * @author borah
	 *
	 */
	public static enum RelationValence {
		IS((edge, base) -> base.is(edge.getFirst(), edge.getSecond(), edge.getThird()),
				(edge, base) -> base.addConfidentRelation(edge.getFirst(), edge.getSecond(), edge.getThird()),
				(edge, base) -> base.removeRelation(edge.getFirst(), edge.getSecond(), edge.getThird()),
				(edge, base) -> {
					base.addConfidentRelation(edge.getFirst(), edge.getSecond(), edge.getThird());
					base.setOpposite(edge.getFirst(), edge.getSecond(), edge.getThird());
				}),
		OPPOSITE((edge, base) -> base.isOpposite(edge.getFirst(), edge.getSecond(), edge.getThird()), IS.oppose,
				(edge, base) -> base.removeRelation(edge.getFirst(), edge.getSecond(), edge.getThird()), IS.set),
		NOT((edge, base) -> base.isNot(edge.getFirst(), edge.getSecond(), edge.getThird()),
				(edge, base) -> base.removeRelation(edge.getFirst(), edge.getSecond(), edge.getThird()),
				(edge, base) -> base.addConfidentRelation(edge.getFirst(), edge.getSecond(), edge.getThird()), IS.set),
		IS_AND_OPPOSITE(IS, OPPOSITE), OPPOSITE_AND_NOT(OPPOSITE, NOT), IS_AND_NOT(IS, NOT), ALL(IS, OPPOSITE, NOT);

		private Set<RelationValence> valences = Collections.singleton(this);
		BiPredicate<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> pre = null;
		BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> set = null;
		BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> remove = null;
		BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> oppose = null;

		private RelationValence(RelationValence... others) {
			valences = ImmutableSet.copyOf(others);
		}

		private RelationValence(
				BiPredicate<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> pre,
				BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> set,
				BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> remove,
				BiConsumer<Triplet<IConcept, IConceptRelationType, IConcept>, IKnowledgeRepresentation> oppose) {
			this.pre = pre;
			this.set = set;
			this.remove = remove;
			this.oppose = oppose;
		}

		/**
		 * Check a relation's valence
		 * 
		 * @param edge
		 * @param base
		 * @return
		 */
		public boolean checkRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge,
				IKnowledgeRepresentation base) {
			if (pre == null) {
				return valences.stream().allMatch((a) -> a.pre.test(edge, base));
			}
			return pre.test(edge, base);
		}

		/**
		 * Change relation's valence and create it if needed
		 * 
		 * @param edge
		 * @param base
		 */
		public void establishRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge,
				IKnowledgeRepresentation base) {
			if (set == null) {
				throw new UnsupportedOperationException(
						"Cannot set a relation when there are two or more conflicting valence types");
			}
			set.accept(edge, base);
		}

		/**
		 * Remove relation; if this is "NOT", then it creates the relation instead
		 * 
		 * @param edge
		 * @param base
		 */
		public void removeRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge,
				IKnowledgeRepresentation base) {
			if (remove == null) {
				throw new UnsupportedOperationException(
						"Cannot remove a relation when there are two or more conflicting valence types");
			}
			remove.accept(edge, base);
		}

		/**
		 * For {@link #IS}, this method turns a relationship into an OPPOSITE
		 * relationship. For {@link #OPPOSITE} and {@link #NOT}, it turns the
		 * reelationship into an IS relationship
		 * 
		 * @param edge
		 * @param base
		 */
		public void invertRelation(Triplet<IConcept, IConceptRelationType, IConcept> edge,
				IKnowledgeRepresentation base) {
			if (oppose == null) {
				throw new UnsupportedOperationException(
						"Cannot invert a relation when there are two or more conflicting valence types");
			}
			oppose.accept(edge, base);
		}

		/**
		 * Return true if there are multiple valence types here
		 * 
		 * @return
		 */
		public boolean isMulti() {
			return valences.size() > 1;
		}

		/**
		 * Return what 'smaller' valences compose this one
		 * 
		 * @return
		 */
		public Set<RelationValence> getValences() {
			return valences;
		}
	}

	/**
	 * Returns connected concepts to the given source knowledgebase, but divides NOT
	 * relations and OPPOSITE relations into the second and third parts of the
	 * triplet
	 * 
	 * @param source
	 * @param iterable
	 * @return
	 */
	public static Triplet<Stream<IConcept>, Stream<IConcept>, Stream<IConcept>> getConnectedConceptsAndUnrelations(
			IConcept source, IConceptRelationType type, IKnowledgeRepresentation relt) {

		return Triplet.of(
				Streams.stream(relt.getConnectedConcepts(source, type)).filter((a) -> relt.is(source, type, a))
						.map(Functions.identity()),
				Streams.stream(relt.getConnectedConcepts(source, type)).filter((a) -> relt.isNot(source, type, a))
						.map(Functions.identity()),
				Streams.stream(relt.getConnectedConcepts(source, type)).filter((a) -> relt.isOpposite(source, type, a))
						.map(Functions.identity()));
		/*
		 * return Streams.stream(relt.getConnectedConcepts(source, type)).collect( () ->
		 * Triplet.of(new HashSet<>(), new HashSet<>(), new HashSet<>()),
		 * makeunrelaccum(relt::isNot, relt::isOpposite, source, type),
		 * unrelations_biconsumer);
		 */
	}

	/**
	 * Get a stream of all patterns of a given type profile
	 * 
	 * @param profila
	 * @param base
	 * @return
	 */
	public static Stream<ITypePatternConcept> getProfilePatterns(IProfile focus, IKnowledgeRepresentation base) {
		return Streams.stream(base.getConnectedConcepts(focus, KnowledgeRelationType.T_HAS_PATTERN))
				.filter(ITypePatternConcept.class::isInstance).map(ITypePatternConcept.class::cast);
	}

	/**
	 * Get the profile this pattern belongs to, or null if this pattern is not
	 * childed to a profile for some reason
	 * 
	 * @param profila
	 * @param base
	 * @return
	 */
	public static IProfile getPatternProfile(ITypePatternConcept focus, IKnowledgeRepresentation base) {
		return Streams.stream(base.getConnectedConcepts(focus, KnowledgeRelationType.T_PATTERN_OF))
				.filter(IProfile.class::isInstance).map(IProfile.class::cast).findAny().orElse(null);
	}

	/**
	 * Return traits/properties of this profile
	 * 
	 * @param focus
	 * @param base
	 * @return
	 */
	public static ProfilePropertyMap getProfileProperties(IProfile focus, IKnowledgeRepresentation base,
			RelationValence valence) {
		return new ProfilePropertyMap(focus, base, valence);
	}

	/**
	 * Equivalent to
	 * {@link #profilesWithTrait(IPropertyConcept, IValueConcept, IKnowledgeBase)},
	 * but the profile is merely checked for the presence of a trait, rather than
	 * fully having the trait. "distance" indicates the maximum distance the
	 * profiles can be at from the {@link IConcept#ENVIRONMENT} concept; if it is
	 * negative, then ignore; if 0, search the whole map but no further
	 * 
	 * @param property
	 * @param base
	 * @return
	 */
	public static Stream<IProfile> profilesWithTrait(IPropertyConcept property, IKnowledgeRepresentation base,
			double distance) {
		if (distance >= 0) {
			Stream<IProfile> stream = getConnectedConceptsAndUnrelations(IConcept.ENVIRONMENT,
					KnowledgeRelationType.EXISTS_IN, base).getFirst().filter((a) -> a instanceof IProfile)
							.map((con) -> (IProfile) con);
			if (distance > 0) {
				stream = stream.filter((c) -> base.getDistance(c) <= distance);
			}
			return stream.filter((c) -> getProfileProperties(c, base, RelationValence.IS).containsKey(property));
		}

		return getConnectedConceptsAndUnrelations(property, PropertyRelationType.IS_TRAIT_OF, base).getFirst()
				.filter((con) -> con instanceof IProfile).map((con) -> (IProfile) con);
	}

	/**
	 * Return all (unique) profiles which are "subsets" of the given profile (which
	 * is presumed to be a type profile or pattern). fully having the trait.
	 * "distance" indicates the maximum distance the profiles can be at from the
	 * {@link IConcept#ENVIRONMENT} concept; if it is negative, then ignore; if it
	 * is 0, search the whole map but no further.
	 * 
	 * @param otherProfile
	 * @param base
	 * @return
	 */
	public static Stream<IProfile> findProfilesWithSubsetOf(IProfile otherProfile, IKnowledgeRepresentation base,
			double distance) {
		return profilesWithTraits(base, getProfileProperties(otherProfile, base, RelationValence.IS),
				getProfileProperties(otherProfile, base, RelationValence.OPPOSITE), distance);
	}

	/**
	 * Return all profiles and patterns which include this exact combination of
	 * traits. If posProperties is empty, (or both are empty), throw error.
	 * "distance" indicates the maximum distance the profiles can be at from the
	 * {@link IConcept#ENVIRONMENT} concept; if it is negative, then ignore; if 0,
	 * search the whole map but no further.
	 * 
	 * @param base
	 * @param posProperties traits the profiles included must have
	 * @param negProperties traits the profiles included must not have
	 * @return
	 */
	public static Stream<IProfile> profilesWithTraits(IKnowledgeRepresentation base,
			Map<IPropertyConcept, IValueConcept> posProperties, Map<IPropertyConcept, IValueConcept> negProperties,
			double distance) {
		if (posProperties.isEmpty())
			throw new IllegalArgumentException("PosProperties is empty");
		IPropertyConcept firstCheck = posProperties.keySet().iterator().next();
		Stream<IProfile> profiles = profilesWithTrait(firstCheck, posProperties.get(firstCheck), base, distance);
		for (IPropertyConcept prop : posProperties.keySet()) {
			if (prop == firstCheck)
				continue;
			profiles = profiles.filter((prof) -> getProfileProperties(prof, base, RelationValence.IS).contains(prop,
					posProperties.get(prop)));
		}
		for (IPropertyConcept prop : negProperties.keySet()) {
			profiles = profiles.filter((prof) -> !getProfileProperties(prof, base, RelationValence.IS).contains(prop,
					negProperties.get(prop)));
		}
		return profiles;
	}

	/**
	 * Return a stream of all profiles with the given trait with the given value. If
	 * the value is {@link IValueConcept#PRESENT}, return traits which are just
	 * present
	 * 
	 * @param property
	 * @param value
	 * @param base
	 * @return
	 */
	public static Stream<IProfile> profilesWithTrait(IPropertyConcept property, IValueConcept value,
			IKnowledgeRepresentation base, double distance) {
		if (value == IValueConcept.PRESENT) {
			return profilesWithTrait(property, base, distance);
		}
		if (distance >= 0) {
			return getConnectedConceptsAndUnrelations(IConcept.ENVIRONMENT, KnowledgeRelationType.EXISTS_IN, base)
					.getFirst().filter((a) -> a instanceof IProfile).map((con) -> (IProfile) con)
					.filter((c) -> base.getDistance(c) <= distance)
					.filter((c) -> getProfileProperties(c, base, RelationValence.IS).contains(property, value));
		}
		return getConnectedConceptsAndUnrelations(property, PropertyRelationType.IS_TRAIT_OF, base).getFirst()
				.filter((a) -> a instanceof IConnectorConcept icc && (icc.isPropertyAndValue()
						&& base.hasAnyValenceRelation(icc, PropertyRelationType.HAS_VALUE, value)))
				.flatMap((a) -> Streams.stream(base.getConnectedConcepts(a, PropertyRelationType.IS_TRAIT_OF)))
				.map((a) -> (IProfile) a);

	}

	/**
	 * Return a map of the concepts this action acts on with the appropriate
	 * valences. If EventCategory is null, return all things; otherwise, only return
	 * things which fit the given event type
	 * 
	 * @param action
	 * @param base
	 * @param eventType
	 * @return
	 */
	public static Multimap<IConceptRelationType, IConcept> getObjectsOfAction(IActionConcept action,
			IKnowledgeRepresentation base, EventCategory eventType, RelationValence valence) {
		return new ConceptRelationsMap(action, base, valence, (a) -> a.getEdgeType() instanceof IEventRelationType ev
				&& ev.getEventType() == eventType); /*
													 * Streams.stream(base.getOutgoingEdges(action)) .filter((a) ->
													 * a.getSecond() instanceof IEventRelationType && (eventType != null
													 * ? ((IEventRelationType) a).getEventType() == eventType : true))
													 * .map((edge) -> Pair.of((IEventRelationType) edge.center(),
													 * edge.getEdgeEnd())) .collect(Collectors.toMap(Pair::getKey,
													 * Pair::getValue));
													 */
	}

	/**
	 * Whether the second argument's profile has traits that make it inclusive of
	 * the first argument (i.e. a superset), e.g. if the first argument has the
	 * trait "Happy" and "Red" and the second profile has the trait "Happy", it is
	 * inclusive. However, if the second profile has traits "Happy", "Red", and
	 * "Broken," it is not inclusive. Opposite traits also factor in; if the second
	 * arg has opposite traits the first arg has, or vice versa, they don't count
	 * 
	 * If a profile has numeric traits, then the question becomes more complex. For
	 * example, if the first profile is "Happiness>1" and the second has
	 * "Happiness>0" then the second is inclusive.
	 * 
	 * TODO numeric traits
	 * 
	 * @param superset
	 * @param subset
	 * @return
	 */
	public static boolean areProfileTraitsSuperset(IProfile check, IProfile superset,
			IKnowledgeRepresentation checkBase, IKnowledgeRepresentation superbase) {
		ProfilePropertyMap checkerMap = getProfileProperties(check, checkBase, RelationValence.IS);
		// TODO getProfileGreaterThanProperties
		// getProfileLessThanProperties
		ProfilePropertyMap superMap = getProfileProperties(superset, superbase, RelationValence.IS);
		ProfilePropertyMap checkerOppMap = getProfileProperties(check, checkBase, RelationValence.OPPOSITE);
		// TODO getProfileGreaterThanProperties
		// getProfileLessThanProperties
		ProfilePropertyMap superOppMap = getProfileProperties(superset, superbase, RelationValence.OPPOSITE);
		for (IPropertyConcept proco : superMap.keyIterable()) { // check normal traits
			if (!checkerMap.containsKey(proco)) {
				return false;
			}
			if (!superMap.get(proco).equals(checkerMap.get(proco))) {
				return false;
			} // TODO and also check greater/less/idk
		}
		for (IPropertyConcept proco : superOppMap.keyIterable()) { // check opposite traits
			if (!checkerOppMap.containsKey(proco)) { // if the checker does not have the trait, we dont have to
														// wrory about it
				continue;
			}
			if (superOppMap.get(proco).equals(checkerOppMap.get(proco))) {
				return false;
			} // TODO also check greater/less/idk

		}
		return true;
	}

}
