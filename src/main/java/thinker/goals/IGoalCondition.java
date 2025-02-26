package thinker.goals;

import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.social.IParty;
import thinker.social.PartyRelationGraph;
import thinker.social.systems.IRole;
import utilities.graph.EmptyGraph;
import utilities.graph.IRelationGraph;
import utilities.property.IProperty;

/**
 * A general goal condition, i.e. a state of affairs that is desired
 * 
 * @author borah
 *
 */
public interface IGoalCondition {

	/**
	 * Whether a relation in this goal is obligatory (as opposed to just preferred)
	 */
	public static final IProperty<Boolean> OBLIGATORY = IProperty.make("obligatory", boolean.class, true);

	/** A Concept representing the satisfier of a goal */
	public static final IConcept SATISFIER = new IConcept() {
		public ConceptType getConceptType() {
			return ConceptType.THE_CONCEPT_ITSELF;
		}

		@Override
		public String getUnderlyingName() {
			return "goal_satisfier";
		}

		public String toString() {
			return "concept_goal_satisfier";
		}
	};

	public static final IGoalCondition NONE = new IGoalCondition() {
		@Override
		public IRelationGraph<IConcept, IConceptRelationType> getConditionsGraph() {
			return EmptyGraph.instance();
		}

		@Override
		public boolean modifyRole(IRole role, IParty requester, PartyRelationGraph partyRelations) {
			return false;
		}

		@Override
		public String toString() {
			return "{(goal_condition_none)}";
		}
	};

	/**
	 * If false, then this is the "default" condition, which does not check for
	 * anything (i.e. empty graph)
	 */
	public default boolean hasConditions() {
		return true;
	}

	/**
	 * Whether the given relation is obligatory
	 * 
	 * @param one
	 * @param relation
	 * @param two
	 * @return
	 */
	public default boolean isObligatoryRelation(IConcept one, IConceptRelationType relation, IConcept two) {
		return this.getConditionsGraph().getProperty(one, relation, two, OBLIGATORY);
	}

	/**
	 * Return graph of all conditions in this goal
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getConditionsGraph();

	/**
	 * Add relations to a role that was created to fulfill this goal; return false
	 * if the role cannot be changed to fit this goal. Assume both the given role
	 * and party exist in the relations graph given
	 * 
	 * @param forGroup
	 * @param partyRelations
	 * @return
	 */
	public boolean modifyRole(IRole role, IParty requester, PartyRelationGraph partyRelations);
}