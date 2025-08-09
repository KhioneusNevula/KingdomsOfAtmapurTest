package thinker.actions.expectations;

import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import _utilities.couplets.Pair;
import _utilities.function.TriFunction;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.concepts.relations.actional.EventCategory;
import thinker.concepts.relations.descriptive.IDescriptiveRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.util.RelationProperties;
import thinker.goals.IGoalConcept;

/**
 * A class to convert relations into graphs of event relations
 * 
 * @author borah
 *
 */
public class EventRelationConversion {

	private static final Table<IDescriptiveRelationType, EventCategory, IEventRelationConverter> erconverters = HashBasedTable
			.create();
	private static final Table<IDescriptiveRelationType, EventCategory, IEventRelationConverter> nerconverters = HashBasedTable
			.create();

	private static void reger(IDescriptiveRelationType reltype, EventCategory type, IEventRelationConverter converter) {
		if (erconverters.contains(reltype.invert(), type)) {
			throw new IllegalArgumentException(
					"Already present for " + type + ": inverse of " + reltype + ": " + reltype.invert());
		}
		erconverters.put(reltype, type, converter);
		erconverters.put(reltype.invert(), type, (l, r) -> converter.convertRelation(r, l));
	}

	private static void nreger(IDescriptiveRelationType reltype, EventCategory type, IEventRelationConverter converter) {
		if (nerconverters.contains(reltype.invert(), type)) {
			throw new IllegalArgumentException(
					"Already present for " + type + ": inverse of " + reltype + ": " + reltype.invert());
		}
		nerconverters.put(reltype, type, converter);
		nerconverters.put(reltype.invert(), type, (l, r) -> converter.convertRelation(r, l));
	}

	private static void breger(IDescriptiveRelationType reltype, EventCategory type,
			TriFunction<IConcept, IConcept, Boolean, IRelationGraph<IConcept, IConceptRelationType>> converter) {
		reger(reltype, type, (l, r) -> converter.apply(l, r, false));
		nreger(reltype, type, (l, r) -> converter.apply(l, r, true));
	}

	static {

		IConcept SAT = IGoalConcept.SATISFIER;

		// generic move for AT_LOCATION or !AT_LOCATION
		breger(ProfileInterrelationType.AT_LOCATION, EventCategory.POSITIONING,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.REPOSITIONS, l).plusEdge(SAT,
								n ? EventRelationType.MOVES_TARGET_AWAY_FROM : EventRelationType.POSITIONS_TARGET_AT,
								r));
		// create something for AT_LOCATION
		reger(ProfileInterrelationType.AT_LOCATION, EventCategory.GENERATION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.POSITIONS_TARGET_AT, r)
						.plusEdge(SAT, EventRelationType.CREATES, l));
		// destroy something for !AT_LOCATION
		nreger(ProfileInterrelationType.AT_LOCATION, EventCategory.DESTRUCTION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.MOVES_TARGET_AWAY_FROM, r)
						.plusEdge(SAT, EventRelationType.DESTROYS, l));
		// transform something for AT_LOCATION or !AT_LOCATION
		breger(ProfileInterrelationType.AT_LOCATION, EventCategory.TRANSFORMATION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>().plusEdge(SAT,
						EventRelationType.TRANSFORMS_INTO, l, Pair.of(RelationProperties.NOT, n)));

		// change spirit tethering for IS_BODY_OF
		breger(ProfileInterrelationType.IS_BODY_OF, EventCategory.CONNECTION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.CHANGES_BODY_OF, r)
						.plusEdge(SAT, n ? EventRelationType.DISEMBODIES_FROM : EventRelationType.EMBODIES_AS, l));
		// create spirit for embodies
		reger(ProfileInterrelationType.IS_BODY_OF, EventCategory.GENERATION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.CREATES, l).plusEdge(SAT, EventRelationType.EMBODIES_AS, r));
		// destroy spirit for embodies
		nreger(ProfileInterrelationType.IS_BODY_OF, EventCategory.DESTRUCTION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.DESTROYS, r)
						.plusEdge(SAT, EventRelationType.DISEMBODIES_FROM, l));

		// change membership for member_of
		breger(ProfileInterrelationType.MEMBER_OF, EventCategory.CONNECTION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, n ? EventRelationType.REMOVES_MEMBER : EventRelationType.ADDS_MEMBER, r)
						.plusEdge(SAT, n ? EventRelationType.REMOVES_MEMBERSHIP : EventRelationType.ADDS_MEMBERSHIP,
								l));

		// change part attachment
		breger(ProfileInterrelationType.PART_OF, EventCategory.CONNECTION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, n ? EventRelationType.DETACHES : EventRelationType.ATTACHES, l)
						.plusEdge(SAT, n ? EventRelationType.REMOVE_FROM : EventRelationType.DETACHES, r));

		// create part
		reger(ProfileInterrelationType.PART_OF, EventCategory.GENERATION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.CREATES, l).plusEdge(SAT, EventRelationType.APPEND_TO, r));

		// destroy part
		nreger(ProfileInterrelationType.PART_OF, EventCategory.DESTRUCTION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.DESTROYS, l).plusEdge(SAT, EventRelationType.REMOVE_FROM, r));

		// transform part
		breger(ProfileInterrelationType.PART_OF, EventCategory.TRANSFORMATION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>().plusEdge(SAT,
						EventRelationType.TRANSFORMS_INTO, l, Pair.of(RelationProperties.NOT, n)));

		// change social bond
		breger(ProfileInterrelationType.HAS_SOCIAL_BOND_TO, EventCategory.TRANSFORMATION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.CHANGES_KNOWLEDGE_OF, l)
						.plusEdge(SAT, EventRelationType.GIVES_KNOWLEDGE_ABOUT, r));

		// move held thing around
		breger(ProfileInterrelationType.HELD_BY, EventCategory.POSITIONING,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.PUTS_PART, l).plusEdge(SAT,
								n ? EventRelationType.PUTS_TARGET_AWAY_FROM : EventRelationType.PUTS_TARGET_AT, r));

		// create held thing
		reger(ProfileInterrelationType.HELD_BY, EventCategory.GENERATION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.CREATES, l)
						.plusEdge(SAT, EventRelationType.PUTS_TARGET_AT, r));

		// destroy held thing
		nreger(ProfileInterrelationType.HELD_BY, EventCategory.DESTRUCTION,
				(l, r) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.DESTROYS, l)
						.plusEdge(SAT, EventRelationType.PUTS_TARGET_AWAY_FROM, r));

		// change traits f something
		breger(PropertyRelationType.HAS_TRAIT, EventCategory.TRANSFORMATION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.TRANSFORMS, l)
						.plusEdge(SAT, EventRelationType.TRANSFORMS_INTO, r));

		// change abilities of something
		breger(PropertyRelationType.HAS_ABILITY_TO, EventCategory.TRANSFORMATION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, n ? EventRelationType.REVOKE_ABILITY_FROM : EventRelationType.GRANTS_ABILITY_TO,
								l)
						.plusEdge(SAT, n ? EventRelationType.REVOKE_ABILITY : EventRelationType.GRANTS_ABILITY, r));
		// change feeling
		breger(PropertyRelationType.FEELS_LIKE, EventCategory.TRANSFORMATION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.CHANGES_FEELINGS_OF, l)
						.plusEdge(SAT, n ? EventRelationType.DECREASES_VALUE : EventRelationType.INCREASES_VALUE, r));

		// change knowledge
		breger(PropertyRelationType.KNOWS, EventCategory.TRANSFORMATION,
				(l, r, n) -> new RelationGraph<IConcept, IConceptRelationType>()
						.plusEdge(SAT, EventRelationType.CHANGES_KNOWLEDGE_OF, l).plusEdge(SAT,
								n ? EventRelationType.REMOVES_KNOWLEDGE_ABOUT : EventRelationType.GIVES_KNOWLEDGE_ABOUT,
								r));

	}

	/**
	 * CConverts a relation to a possible series of event types and event converters
	 * 
	 * @param left
	 * @param rel
	 * @param right
	 * @param opposite whether the relation is marked as opposite
	 * @return
	 */
	public static Map<EventCategory, IEventRelationConverter> convertRelationToEvent(IConcept left,
			IDescriptiveRelationType rel, IConcept right, boolean opposite) {
		if (opposite) {
			return nerconverters.row(rel);
		}
		return erconverters.row(rel);
	}

}
