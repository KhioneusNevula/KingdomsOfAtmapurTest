package thinker.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Streams;

import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.TypeProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.concepts.relations.actional.IEventRelationType;
import thinker.concepts.relations.descriptive.IDescriptiveRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.util.RelationsHelper;
import thinker.goals.IGoalConcept;
import thinker.knowledge.base.IKnowledgeBase;

/** Utilities for actions (particular regarding knowledge) */
public class ActionsHelper {

	/**
	 * A map of all action relation types that can be used to deal with certain
	 * concept relation types
	 */
	private static final Multimap<IDescriptiveRelationType, IEventRelationType> STATE_ADDRESSERS = MultimapBuilder
			.hashKeys().hashSetValues().build();

	static {
		// if something has to be at a location, it can be moved there or created there
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.AT_LOCATION,
				Set.of(EventRelationType.MOVED_BY, EventRelationType.CREATED_BY));
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.PLACE_WHERE, Set.of(EventRelationType.IS_TARGET_OF_MOVE));
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.HELD_BY,
				Set.of(EventRelationType.GRABBED_BY, EventRelationType.CREATED_BY));
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.HOLDER_OF, Set.of(EventRelationType.IS_TARGET_OF_PUT));
		// if something has to change its quantity, creating/destroying/transforming can
		// do that
		STATE_ADDRESSERS.putAll(PropertyRelationType.QUANTIFIED_AS,
				Set.of(EventRelationType.DESTROYED_BY, EventRelationType.CREATED_BY, EventRelationType.TRANSFORMED_BY));
		// if something needs to be something else, transforming can do that
		STATE_ADDRESSERS.putAll(PropertyRelationType.HAS_TRAIT, Set.of(EventRelationType.TRANSFORMED_BY));
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.IS, Set.of(EventRelationType.TRANSFORMED_BY));
		// if something needs to feel like something
		STATE_ADDRESSERS.putAll(PropertyRelationType.FEELS_LIKE, Set.of(EventRelationType.HAS_FEELINGS_CHANGED_BY));
		// if something needs to be of a certain principle, we need to transform it
		STATE_ADDRESSERS.putAll(PropertyRelationType.OF_PRINCIPLE, Set.of(EventRelationType.TRANSFORMED_BY));
		// if something needs to have a certain kind of social bond, we need to interact
		// it
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.HAS_SOCIAL_BOND_TO,
				Set.of(EventRelationType.HAS_SOCIAL_OPINIONS_INFLUENCED_BY));
		// if we need some kind of membership, change membership
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.MEMBER_OF,
				Set.of(EventRelationType.HAS_MEMBERSHIP_CHANGED_BY));
		// if we need some kind of ability, grant abilities
		STATE_ADDRESSERS.putAll(PropertyRelationType.HAS_ABILITY_TO, Set.of(EventRelationType.GRANTED_ABILITY_BY));
		// if we need some kind of knowledge, give knowledge
		STATE_ADDRESSERS.putAll(PropertyRelationType.KNOWS, Set.of(EventRelationType.RECEIVES_KNOWLEDGE_BY));
		// if we need a body to have a specific spirit, give that spirit
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.EMBODIES, Set.of(EventRelationType.SPIRIT_TETHERED_BY));
		// if we need a spirit to have a specific body, give that body
		STATE_ADDRESSERS.putAll(ProfileInterrelationType.EMBODIED_AS, Set.of(EventRelationType.REEMBODIED_BY));
	}

	private ActionsHelper() {
	}

	/**
	 * Returns all actionrelation types that can address the issue posed by the
	 * given concept relation
	 */
	public static Collection<IEventRelationType> getAddressableActionRelations(IDescriptiveRelationType forC) {
		return STATE_ADDRESSERS.get(forC);
	}

	/**
	 * Returns all actions from the given knowledge-storage that perfectly (the left
	 * element of the pair) or partially (the right element of the pair) match the
	 * given goal
	 * 
	 * @param condition
	 * @param knowledge
	 * @return
	 */
	public static Pair<Collection<IActionConcept>, Collection<IActionConcept>> getActionsMatchingGoal(
			IRelationGraph<IConcept, IConceptRelationType> condition, IKnowledgeBase knowledge) {
		Set<IActionConcept> perfectMatches = new HashSet<>();
		Set<IActionConcept> partialMatches = new HashSet<>();

		for (Triplet<IConcept, IConceptRelationType, IConcept> edge : (Iterable<Triplet<IConcept, IConceptRelationType, IConcept>>) () -> condition
				.edgeIterator()) {
			IConceptRelationType edgeType = edge.getEdgeType();
			if (edgeType == EventRelationType.ANSWERS) {
				for (IConcept q : condition.getNeighbors(IGoalConcept.SATISFIER, edgeType)) {
					IWhQuestionConcept question = (IWhQuestionConcept) q;
					question.getDescriptiveTypes().stream().map(IProfile::anyOf).forEach((indefinite) -> {
						// TODO questionsy
					});
				}
			} else if (edgeType instanceof EventRelationType relation) {
				Set<IConcept> connects = RelationsHelper.allConceptsMatchingPredicate(condition, IGoalConcept.SATISFIER,
						Set.of(edgeType), (a, b) -> true, RelationsHelper.eqTypes(RelationsHelper.IS_EQ), (a) -> true);
				for (IConcept connect : connects) {

					Set<IActionConcept> actos = new HashSet<>();
					RelationsHelper
							.allConceptsMatchingPredicate(knowledge.getMappedConceptGraphView(), connect,
									Set.of(edgeType.invert()), (a, b) -> true,
									RelationsHelper.eqTypes(RelationsHelper.IS_EQ), (a) -> true)
							.stream().filter((a) -> a instanceof IActionConcept).map((a) -> (IActionConcept) a)
							.forEach((a) -> {
								partialMatches.add(a);
								actos.add(a);
							});
					// TODO what is a full match tf
				}

			} else {
				throw new IllegalArgumentException("Why are we constructing relations from SATISFIER of the type "
						+ edgeType + "? In graph: " + condition.representation());
			}
		}

		return Pair.of(perfectMatches, partialMatches);
	}

	/** Returns the possible body concepts for a given Info */
	public static Set<IProfile> getBodyConcepts(IKnowledgeBase info) {
		IConcept self = info.getSelfConcept();
		Set<IProfile> bodies = Streams.stream(info.getConnectedConcepts(self, ProfileInterrelationType.EMBODIED_AS))
				.map(IProfile.class::cast).collect(Collectors.toSet());
		Streams.stream(info.getConnectedConcepts(self, ProfileInterrelationType.KIND_OF))
				.flatMap((con) -> Streams.stream(info.getConnectedConcepts(con, ProfileInterrelationType.EMBODIED_AS)))
				.map(IProfile.class::cast).forEach((a) -> bodies.add(a));
		return bodies;
	}

	/** Returns the possible part concepts for a given body concept */
	public static Set<IProfile> getBodyPartConcepts(IProfile body, IKnowledgeBase info) {
		Set<IProfile> bodies = Streams.stream(info.getConnectedConcepts(body, ProfileInterrelationType.HAS_PART))
				.map(IProfile.class::cast).collect(Collectors.toSet());
		Streams.stream(info.getConnectedConcepts(info.getSelfConcept(), ProfileInterrelationType.KIND_OF))
				.flatMap((con) -> Streams.stream(info.getConnectedConcepts(con, ProfileInterrelationType.EMBODIED_AS)))
				.flatMap((s) -> Streams.stream(info.getConnectedConcepts(s, ProfileInterrelationType.HAS_PART)))
				.map(IProfile.class::cast).forEach((a) -> bodies.add(a));
		return bodies;
	}

	/**
	 * Returns all {@link TypeProfile}s that have a doer-relation to the given event
	 * (e.g. the eater of an eating action)
	 */
	public static Collection<IProfile> getDoerOfEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge) {
		return RelationsHelper
				.allConceptsMatchingPredicate(knowledge, event, Set.of(EventRelationType.DONE_BY), (rel, con) -> true,
						RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue())
				.stream().map((a) -> (IProfile) a).collect(Collectors.toSet());
	}

	/**
	 * Returns all {@link TypeProfile}s that have an object relation to the given
	 * event (e.g. the eating targets of an eating action)
	 */
	public static Collection<IProfile> getObjectsOfEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge) {
		return RelationsHelper
				.allConceptsMatchingPredicate(knowledge, event, Set.of(EventRelationType.ACTS_ON), (rel, con) -> true,
						RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue())
				.stream().map((a) -> (IProfile) a).collect(Collectors.toSet());
	}

	/**
	 * Returns all {@link TypeProfile}s that have a used-by relation to the given
	 * event (e.g. the tools of an eating action)
	 */
	public static Collection<IProfile> getInstrumentsOfEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge) {
		return RelationsHelper
				.allConceptsMatchingPredicate(knowledge, event, Set.of(EventRelationType.USES), (rel, con) -> true,
						RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue())
				.stream().map((a) -> (IProfile) a).collect(Collectors.toSet());
	}

	/**
	 * Returns all {@link TypeProfile}s that have a locational relation to the given
	 * event
	 */
	public static Collection<IProfile> getLocationsOfEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge) {
		return RelationsHelper
				.allConceptsMatchingPredicate(knowledge, event, Set.of(EventRelationType.HAPPENS_AT),
						(rel, con) -> true, RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue())
				.stream().map((a) -> (IProfile) a).collect(Collectors.toSet());
	}

	/**
	 * Returns all {@link TypeProfile}s created by an event
	 */
	public static Collection<IProfile> getProductOfEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge) {
		return RelationsHelper
				.allConceptsMatchingPredicate(knowledge, event, Set.of(EventRelationType.CREATES), (rel, con) -> true,
						RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue())
				.stream().map((a) -> (IProfile) a).collect(Collectors.toSet());
	}

	/**
	 * Returns all {@link TypeProfile}s that are parts of the self used in an event
	 */
	public static Collection<IProfile> getPartsUsedInEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge) {
		return RelationsHelper
				.allConceptsMatchingPredicate(knowledge, event, Set.of(EventRelationType.USES_PART), (rel, con) -> true,
						RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue())
				.stream().map((a) -> (IProfile) a).collect(Collectors.toSet());
	}

	/**
	 * Returns all (superclass-level) needs that have a satisfied-by relation to the
	 * given event (e.g. the eating need of an eating action)
	 */
	public static Collection<IConcept> getSatisfiedNeedsOfEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge) {
		return RelationsHelper.allConceptsMatchingPredicate(knowledge, event, Set.of(EventRelationType.SATISFIES),
				(rel, con) -> true, RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue());
	}

	/**
	 * Returns all (type) profiles that fit a given action relation type
	 */
	public static Collection<IConcept> getThematicRoleOfEvent(IConcept event,
			IRelationGraph<IConcept, IConceptRelationType> knowledge, IEventRelationType relType) {
		return RelationsHelper.allConceptsMatchingPredicate(knowledge, event, Set.of(relType), (rel, con) -> true,
				RelationsHelper.eqTypes(RelationsHelper.IS_EQ), Predicates.alwaysTrue());
	}

}
