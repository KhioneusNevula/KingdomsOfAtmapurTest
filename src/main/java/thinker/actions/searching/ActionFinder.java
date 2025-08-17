package thinker.actions.searching;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Streams;
import com.google.common.collect.Table;

import _utilities.UnimplementedException;
import _utilities.couplets.Pair;
import party.util.IAgentAccess;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.IEventRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.goals.IGoalConcept;
import thinker.helpers.ProfilePropertyMap;
import thinker.helpers.RelationValence;
import thinker.helpers.RelationsHelper;

/**
 * Basic implementation of {@link IActionFinder}
 * 
 * @author borah
 *
 */
public class ActionFinder implements IActionFinder {

	/**
	 * For subclasses to override
	 */
	protected void assessOtherCriteria(Multimap<IActionConcept, IConceptRelationType> foundActions,
			Table<IActionConcept, IActionCriterion, Float> actionTable, IGoalConcept focusGoal, UUID pid,
			IAgentAccess info) {

	}

	@Override
	public Table<IActionConcept, IActionCriterion, Float> findAction(IAgentAccess info, IGoalConcept focusGoal,
			UUID processID, boolean doAccesses) {
		// pairs of relations and profiles involvedi n this goal
		Multimap<IConceptRelationType, IConcept> involvedConcepts = Streams
				.stream(focusGoal.getConditionsGraph().getOutgoingEdges(IGoalConcept.SATISFIER))
				.filter((trip) -> trip.getEdgeType() instanceof IEventRelationType)
				.map((trip) -> Pair.of(trip.getEdgeType(), trip.getEdgeEnd()))
				.collect(Multimaps.toMultimap((a) -> a.getFirst(), (b) -> b.getSecond(),
						() -> MultimapBuilder.hashKeys().hashSetValues().build()));
		List<IConceptRelationType> relashuffle = involvedConcepts.keySet().stream().collect(Collectors.toList());
		Collections.shuffle(relashuffle);

		/** map of all actions found, and how many relations the action can address */
		Multimap<IActionConcept, IConceptRelationType> foundActions = MultimapBuilder.hashKeys().hashSetValues()
				.build();

		for (IConceptRelationType eventRelation : relashuffle) {
			if (involvedConcepts.get(eventRelation).size() != 1) {
				throw new IllegalStateException("For goal " + focusGoal.toString() + " edge " + eventRelation
						+ " has too many or too few connections: " + involvedConcepts.get(eventRelation));
			}
			Set<IProfile> alreadyCheckedProfiles = new HashSet<>(); // profiles already checked for traits
			IConcept expectedTargetConcept = involvedConcepts.get(eventRelation).stream().findFirst().get();
			if (expectedTargetConcept instanceof IProfile targetProfile) { // if this expectation targets a profile
				if (targetProfile.isUniqueProfile()) { // if this expectation targets a unique thing
					// TODO figure out unique profile requirements
					throw new UnimplementedException(
							"UniqueProfileInCondition not implemented (" + expectedTargetConcept + ")");
				} else if (targetProfile.isTypeProfile()) { // if this expectation targets a type of thing
					ProfilePropertyMap targetProperties = RelationsHelper.getProfileProperties(targetProfile,
							info.knowledge(), RelationValence.IS);

					// cycle through properties of expected target
					for (IPropertyConcept targetProperty : targetProperties.keyIterable()) {
						Stream<IProfile> profilesMatchingTargetProperty = RelationsHelper.profilesWithTrait(
								targetProperty, targetProperties.get(targetProperty), info.knowledge(), -1f);
						// for each property, find all profiles with this property
						for (IProfile profileMatchingProperty : (Iterable<IProfile>) () -> profilesMatchingTargetProperty
								.filter((a) -> !alreadyCheckedProfiles.contains(a)).iterator()) {

							if (profileMatchingProperty.isTypeProfile()) { // if we found a type profile
								if (RelationsHelper.areProfileTraitsSuperset(targetProfile, profileMatchingProperty,
										focusGoal.getConditionsGraph(), info.knowledge(), doAccesses)) { // if the
																											// profile
																											// fits
									Streams.stream(info.knowledge().getConnectedConcepts(profileMatchingProperty,
											eventRelation.invert())).map((a) -> (IActionConcept) a)
											.forEach((a) -> foundActions.put(a, eventRelation));
								}
								alreadyCheckedProfiles.add(profileMatchingProperty);

							} else if (profileMatchingProperty.isUniqueProfile()) { // if we find a unique profile???
								// TODO figure out unique profile as result of action
								throw new UnimplementedException("UniqueProfileMatchedForCondition not implemented ("
										+ profileMatchingProperty + ")");
							} else if (profileMatchingProperty.isAnyMatcher()) {
								throw new IllegalStateException(
										"Why does the any-matcher " + profileMatchingProperty + " have the trait "
												+ targetProperty + "(" + targetProperties.get(targetProperty) + ")...");
							}
							// questions and stuff get ignored
						}
					}
					// check for actions which target anymatchers
					IProfile anyMatcher = IProfile.anyOf(targetProfile.getDescriptiveType());
					Streams.stream(info.knowledge().getConnectedConcepts(anyMatcher, eventRelation.invert()))
							.map((a) -> (IActionConcept) a).forEach((a) -> {
								foundActions.put(a, eventRelation);
								if (doAccesses)
									info.knowledge().access(anyMatcher, eventRelation.invert(), a);
							});
				} else if (targetProfile.isAnyMatcher()) {
					// if the expectation has an any-matcher, just look for all profiles with that
					// unique type
					for (IProfile profileOfUniqueType : (Iterable<IProfile>) () -> Streams
							.stream(info.knowledge().getConnectedConcepts(targetProfile,
									ProfileInterrelationType.IS_SUPERTYPE_OF))
							.map((a) -> (IProfile) a).filter((a) -> !alreadyCheckedProfiles.contains(a)).iterator()) {
						if (profileOfUniqueType.isTypeProfile()) { // if we found a type profile
							if (doAccesses)
								info.knowledge().access(targetProfile, ProfileInterrelationType.IS_SUPERTYPE_OF,
										profileOfUniqueType);
							Streams.stream(
									info.knowledge().getConnectedConcepts(profileOfUniqueType, eventRelation.invert()))
									.map((a) -> (IActionConcept) a).forEach((a) -> {
										foundActions.put(a, eventRelation);
										if (doAccesses)
											info.knowledge().access(profileOfUniqueType, eventRelation.invert(), a);
									});
							alreadyCheckedProfiles.add(profileOfUniqueType);
						} else if (profileOfUniqueType.isUniqueProfile()) { // if we find a unique profile???
							// TODO figure out unique profile as result of action
							throw new UnimplementedException(
									"UniqueProfileMatchedForCondition not implemented (" + profileOfUniqueType + ")");
						} // questions and stuff get ignored
					}
				} else {
					throw new IllegalArgumentException("Profile attached from " + eventRelation
							+ " is not a type profile or definite profile; this is not allowed for condition: "
							+ focusGoal);
				}
			} else { // if it's any other kind of concept, just find actions directly affecting it
				Streams.stream(info.knowledge().getConnectedConcepts(expectedTargetConcept, eventRelation.invert()))
						.forEach((act) -> {
							foundActions.put((IActionConcept) act, eventRelation);
							if (doAccesses)
								info.knowledge().access(expectedTargetConcept, eventRelation.invert(), act);
						});

			}
		}

		// now with our found actions, determine which are best
		Table<IActionConcept, IActionCriterion, Float> actionTable = HashBasedTable.create();

		for (IActionConcept action : foundActions.keySet()) {
			// BasicActionCriterion.INCOMPLETION
			actionTable.put(action, BasicActionCriterion.INCOMPLETION,
					foundActions.get(action).size() * 1.0f / involvedConcepts.keySet().size());
			// TODO BasicActionCriterion.EFFORT

			// TODO BasicActionCriterion.DRAINAGE

			// TODO BasicActionCriterion.UNCERTAINTY
		}

		this.assessOtherCriteria(foundActions, actionTable, focusGoal, processID, info);

		return actionTable;
	}

}
