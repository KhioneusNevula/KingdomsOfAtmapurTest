package thinker.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import _sim.world.MapTile;
import _utilities.couplets.Pair;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IMapTileConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.ITypePatternConcept;
import thinker.concepts.general_types.IValueConcept;
import thinker.concepts.general_types.IVectorConcept;
import thinker.concepts.profile.IProfile;
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
			Stream<IProfile> stream = new ConceptRelationsMap(IConcept.ENVIRONMENT, base)
					.get(KnowledgeRelationType.THERE_EXISTS).stream().filter((a) -> a instanceof IProfile)
					.map((con) -> (IProfile) con);
			if (distance > 0) {
				stream = stream.filter((c) -> base.getDistance(c) <= distance);
			}
			return stream.filter((c) -> getProfileProperties(c, base, RelationValence.IS).containsKey(property));
		}

		return new ConceptRelationsMap(property, base).get(PropertyRelationType.IS_TRAIT_OF).stream()
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
	 * @param access       whether to mark each property relation utilized as
	 *                     "accessed"
	 * @return
	 */
	public static Stream<IProfile> findProfilesWithSubsetOf(IProfile otherProfile, IKnowledgeRepresentation base,
			double distance, boolean access) {
		return profilesWithTraits(base, getProfileProperties(otherProfile, base, RelationValence.IS),
				getProfileProperties(otherProfile, base, RelationValence.OPPOSITE), distance, access);
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
	 * @param access        whether to mark each property relation utilized as
	 *                      "accessed"
	 * @return
	 */
	public static Stream<IProfile> profilesWithTraits(IKnowledgeRepresentation base,
			Map<IPropertyConcept, IValueConcept> posProperties, Map<IPropertyConcept, IValueConcept> negProperties,
			double distance, boolean access) {
		if (posProperties.isEmpty())
			throw new IllegalArgumentException("PosProperties is empty");
		IPropertyConcept firstCheck = posProperties.keySet().iterator().next();
		Stream<IProfile> profiles = profilesWithTrait(firstCheck, posProperties.get(firstCheck), base, distance);
		for (IPropertyConcept prop : posProperties.keySet()) {
			if (prop == firstCheck)
				continue;
			profiles = profiles.filter((prof) -> getProfileProperties(prof, base, RelationValence.IS).contains(prop,
					posProperties.get(prop)));
			if (access) {
				profiles = profiles.map((a) -> {
					getProfileProperties(a, base, RelationValence.IS).access(prop);
					return a;
				});
			}
		}
		for (IPropertyConcept prop : negProperties.keySet()) {
			profiles = profiles.filter((prof) -> !getProfileProperties(prof, base, RelationValence.IS).contains(prop,
					negProperties.get(prop)));
			if (access) {
				profiles = profiles.map((a) -> {
					getProfileProperties(a, base, RelationValence.IS).access(prop);
					return a;
				});
			}
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
	 * @param access
	 * @return
	 */
	public static Stream<IProfile> profilesWithTrait(IPropertyConcept property, IValueConcept value,
			IKnowledgeRepresentation base, double distance) {
		if (value == IValueConcept.PRESENT) {
			return profilesWithTrait(property, base, distance);
		}
		if (distance >= 0) {
			return new ConceptRelationsMap(IConcept.ENVIRONMENT, base).get(KnowledgeRelationType.THERE_EXISTS).stream()
					.filter((a) -> a instanceof IProfile).map((con) -> (IProfile) con)
					.filter((c) -> base.getDistance(c) <= distance)
					.filter((c) -> getProfileProperties(c, base, RelationValence.IS).contains(property, value));
		}
		return new ConceptRelationsMap(property, base).get(PropertyRelationType.IS_TRAIT_OF).stream()
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
	public static ConceptRelationsMap getObjectsOfAction(IActionConcept action, IKnowledgeRepresentation base,
			EventCategory eventType, RelationValence valence) {
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
	 * Returns the possible positions of a profile as {@link IVectorConcept}s and
	 * the confidence value of the relations for each possible position as floats
	 * 
	 * @param profile
	 * @param base
	 * @return
	 */
	public static Map<IVectorConcept, Float> getPossiblePositionsOfProfile(IProfile profile,
			IKnowledgeRepresentation base) {
		Map<IVectorConcept, Float> vectra = new HashMap<>();
		ConceptRelationsMap map = new ConceptRelationsMap(profile, base, RelationValence.IS);
		map.get(KnowledgeRelationType.L_POSITION_AT).stream()
				.map((a) -> Pair.of(((IVectorConcept) a), map.getConfidence(KnowledgeRelationType.L_POSITION_AT, a)))
				.forEach((a) -> {
					vectra.put(a.getKey(), a.getValue());
				});
		return vectra;
	}

	/**
	 * Returns the possible map tile positions of a profile as
	 * {@link IMapTileConcept}s and the confidence value of the relations for each
	 * possible tile as floats
	 * 
	 * @param profile
	 * @param base
	 * @return
	 */
	public static Map<IMapTileConcept, Float> getPossibleTilesOfProfile(IProfile profile,
			IKnowledgeRepresentation base) {
		Map<IMapTileConcept, Float> vectra = new HashMap<>();
		ConceptRelationsMap map = new ConceptRelationsMap(profile, base, RelationValence.IS);
		map.get(KnowledgeRelationType.L_IN_MAP).stream()
				.map((a) -> Pair.of(((IMapTileConcept) a), map.getConfidence(KnowledgeRelationType.L_IN_MAP, a)))
				.forEach((a) -> {
					vectra.put(a.getKey(), a.getValue());
				});
		return vectra;

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
	 * @param whether  to mark all utilized relations as "accessed"
	 * @return
	 */
	public static boolean areProfileTraitsSuperset(IProfile check, IProfile superset,
			IKnowledgeRepresentation checkBase, IKnowledgeRepresentation superbase, boolean access) {
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
			if (access) {
				checkerMap.access(proco);
				if (superMap != checkerMap) {
					superMap.access(proco);
				}
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
			if (access) {
				checkerOppMap.access(proco);
				if (superOppMap != checkerOppMap) {
					superOppMap.access(proco);
				}
			}
			if (superOppMap.get(proco).equals(checkerOppMap.get(proco))) {
				return false;
			} // TODO also check greater/less/idk

		}
		return true;
	}

}
