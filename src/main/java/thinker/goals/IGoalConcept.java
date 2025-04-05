package thinker.goals;

import _utilities.graph.EmptyGraph;
import _utilities.graph.IRelationGraph;
import _utilities.property.IProperty;
import party.IParty;
import party.PartyRelationGraph;
import party.systems.IRole;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.base.IKnowledgeBase;

/**
 * A general goal condition, i.e. a state of affairs that is desired
 * 
 * @author borah
 *
 */
public interface IGoalConcept extends IConcept {

	/**
	 * Whether a relation in this goal is obligatory (as opposed to just preferred)
	 */
	public static final IProperty<Boolean> OBLIGATORY = IProperty.make("obligatory", boolean.class, true);

	/**
	 * A Concept representing the {@link IActionConcept} that ought to be the
	 * satisfier of this goal
	 */
	public static final IConcept SATISFIER = IActionConcept.createGenericActionConcept("satisfier_action");

	public static final IGoalConcept NONE = new IGoalConcept() {
		@Override
		public IRelationGraph<IConcept, IConceptRelationType> getConditionsGraph(IKnowledgeBase knowledge) {
			return EmptyGraph.instance();
		}

		@Override
		public boolean modifyRole(IRole role, IParty requester, PartyRelationGraph partyRelations) {
			return false;
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.GOAL;
		}

		@Override
		public String getUnderlyingName() {
			return "goal_condition_none";
		}

		@Override
		public String toString() {
			return "{(goal_condition_none)}";
		}
	};

	@Override
	public default ConceptType getConceptType() {
		return ConceptType.GOAL;
	}

	/**
	 * If false, then this is the "default" condition, which does not check for
	 * anything (i.e. empty graph)
	 */
	public default boolean hasConditions() {
		return true;
	}

	/**
	 * Return graph of all conditions in this goal
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getConditionsGraph(IKnowledgeBase knowledge);

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