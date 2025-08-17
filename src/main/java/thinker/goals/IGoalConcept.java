package thinker.goals;

import _utilities.property.IProperty;
import party.IParty;
import party.PartyRelationGraph;
import party.systems.IRole;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.relations.actional.IEventRelationType;
import thinker.knowledge.IKnowledgeRepresentation;
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
	 * Overriden as true
	 */
	@Override
	default boolean doDecayChecks() {
		return true;
	}

	/**
	 * Whether this concept is an Expectation, i.e. an {@link IGoalConcept} which
	 * indicates an expected kind of action (as opposed to a goal which expects some
	 * circumstance) using {@link IEventRelationType}s
	 * 
	 * @return
	 */
	public boolean isExpectation();

	/**
	 * Return graph of all conditions in this goal
	 * 
	 * @return
	 */
	public IKnowledgeRepresentation getConditionsGraph();

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