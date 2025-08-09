package thinker.helpers;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Functions;
import com.google.common.collect.Streams;

import _utilities.couplets.Pair;
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
	 * Return (both positive and opposite) traits/properties of this profile
	 * 
	 * @param focus
	 * @param base
	 * @return
	 */
	public static ProfilePropertyMap getProfileProperties(IProfile focus, IKnowledgeRepresentation base) {
		return new ProfilePropertyMap(focus, base, true, true);
	}

	/**
	 * Return POSITIVE traits/properties of this profile
	 * 
	 * @param focus
	 * @param base
	 * @return
	 */
	public static ProfilePropertyMap getProfilePositiveProperties(IProfile focus, IKnowledgeRepresentation base) {
		return new ProfilePropertyMap(focus, base, true, false);
	}

	/**
	 * Return OPPOSITE/NEGATIVE traits/properties of this profile
	 * 
	 * @param focus
	 * @param base
	 * @return
	 */
	public static ProfilePropertyMap getProfileNegativeProperties(IProfile focus, IKnowledgeRepresentation base) {
		return new ProfilePropertyMap(focus, base, false, true);
	}

	/**
	 * Equivalent to
	 * {@link #profilesWithTrait(IPropertyConcept, IValueConcept, IKnowledgeBase)},
	 * but the profile is merely checked for the presence of a trait, rather than
	 * fully having the trait. "distance" indicates the maximum distance the
	 * profiles can be at from the {@link IConcept#ENVIRONMENT} concept; if it is
	 * negative, then ignore
	 * 
	 * @param property
	 * @param base
	 * @return
	 */
	public static Stream<IProfile> profilesWithTrait(IPropertyConcept property, IKnowledgeRepresentation base,
			double distance) {
		if (distance >= 0) {
			return getConnectedConceptsAndUnrelations(IConcept.ENVIRONMENT, KnowledgeRelationType.EXISTS_IN, base)
					.getFirst().filter((a) -> a instanceof IProfile).map((con) -> (IProfile) con)
					.filter((c) -> base.getDistance(c) <= distance)
					.filter((c) -> getProfilePositiveProperties(c, base).containsKey(property));
		}

		return getConnectedConceptsAndUnrelations(property, PropertyRelationType.IS_TRAIT_OF, base).getFirst()
				.filter((con) -> con instanceof IProfile).map((con) -> (IProfile) con);
	}

	/**
	 * Return all (unique) profiles which are "subsets" of the given profile (which
	 * is presumed to be a type profile or pattern). fully having the trait.
	 * "distance" indicates the maximum distance the profiles can be at from the
	 * {@link IConcept#ENVIRONMENT} concept; if it is negative, then ignore
	 * 
	 * @param otherProfile
	 * @param base
	 * @return
	 */
	public static Stream<IProfile> findProfilesWithSubsetOf(IProfile otherProfile, IKnowledgeRepresentation base,
			double distance) {
		return profilesWithTraits(base, getProfilePositiveProperties(otherProfile, base),
				getProfileNegativeProperties(otherProfile, base), distance);
	}

	/**
	 * Return all profiles and patterns which include this exact combination of
	 * traits. If posProperties is empty, (or both are empty), throw error.
	 * "distance" indicates the maximum distance the profiles can be at from the
	 * {@link IConcept#ENVIRONMENT} concept; if it is negative, then ignore
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
			profiles = profiles
					.filter((prof) -> getProfilePositiveProperties(prof, base).contains(prop, posProperties.get(prop)));
		}
		for (IPropertyConcept prop : negProperties.keySet()) {
			profiles = profiles.filter(
					(prof) -> !getProfilePositiveProperties(prof, base).contains(prop, negProperties.get(prop)));
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
					.filter((c) -> getProfilePositiveProperties(c, base).contains(property, value));
		}
		return getConnectedConceptsAndUnrelations(property, PropertyRelationType.IS_TRAIT_OF, base).getFirst()
				.filter((a) -> a instanceof IConnectorConcept icc && (icc.isPropertyAndValue()
						&& base.hasAnyValenceRelation(icc, PropertyRelationType.HAS_VALUE, value)))
				.flatMap((a) -> Streams.stream(base.getConnectedConcepts(a, PropertyRelationType.IS_TRAIT_OF)))
				.map((a) -> (IProfile) a);

	}

	/**
	 * Return every concept this action acts on. If EventCategory is null, return
	 * all things; otherwise, only return things which fit the given event type
	 * 
	 * @param action
	 * @param base
	 * @param eventType
	 * @return
	 */
	public static Map<IEventRelationType, IConcept> getObjectsOfAction(IActionConcept action,
			IKnowledgeRepresentation base, EventCategory eventType) {
		return Streams.stream(base.getOutgoingEdges(action))
				.filter((a) -> a.getSecond() instanceof IEventRelationType
						&& (eventType != null ? ((IEventRelationType) a).getEventType() == eventType : true))
				.map((edge) -> Pair.of((IEventRelationType) edge.center(), edge.getEdgeEnd()))
				.collect(Collectors.toMap(Pair::getKey, Pair::getValue));
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
		ProfilePropertyMap checkerMap = getProfilePositiveProperties(check, checkBase);
		// TODO getProfileGreaterThanProperties
		// getProfileLessThanProperties
		ProfilePropertyMap superMap = getProfilePositiveProperties(superset, superbase);
		ProfilePropertyMap checkerOppMap = getProfileNegativeProperties(check, checkBase);
		// TODO getProfileGreaterThanProperties
		// getProfileLessThanProperties
		ProfilePropertyMap superOppMap = getProfileNegativeProperties(superset, superbase);
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
