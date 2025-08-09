package thinker.mind.will.thoughts.actions;

import _utilities.graph.IRelationGraph;
import things.form.condition.IFormCondition;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.thoughts.IThought;

/** A thought used to perform an action */
public interface IActionThought extends IThought {

	/** Returns the type of this action */
	public IActionConcept getActionType();

	/**
	 * Returns what body condition is needed for this action to execute. Basically
	 * the output of {@link IActionConcept#bodyConditions(IBeingAccess)}. Return
	 * {@link IFormCondition#TRUE} if this condition is always true
	 */
	public IFormCondition getBodyCondition();

	/**
	 * Returns the condition this action needs in order to execute, or an empty
	 * graph if nothing is needed. Basically the output of
	 * {@link IActionConcept#generateCondition(IBeingAccess, IRelationGraph, IRelationGraph)}
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getCondition();

	/**
	 * Returns the condition for what *knowledge* must be known to execute, or an
	 * empty graph if nothing is needed. Basically the output of
	 * {@link IActionConcept#generateKnowledgeCondition(IBeingAccess, IRelationGraph, IRelationGraph)}
	 * 
	 * @param intention
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getKnowledgeCondition();

	/**
	 * Returns a graph of the event of this action, i.e. with relations to the
	 * agent, patient, tools, and so on; used to construct memories and such
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getEventStructure();

}
