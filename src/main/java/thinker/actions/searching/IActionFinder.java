package thinker.actions.searching;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Table;

import party.util.IAgentAccess;
import thinker.actions.IActionConcept;
import thinker.goals.IGoalConcept;
import thinker.knowledge.IKnowledgeRepresentation;
import thinker.mind.util.IBeingAccess;

/**
 * Finds actions to execute
 * 
 * @author borah
 *
 */
public interface IActionFinder {

	/** Metric used to evaluate an action's efficacy */
	public static interface IActionCriterion {
		/** Name of criterion */
		public String name();
	}

	/**
	 * Finds a suitable set of actions to satisfy a condition, and sets criterion
	 * values based on how good the action is for a goal. Return an empty table if
	 * no actions woere found
	 * 
	 * @param doAccesses whether to perform "access" actions while checking
	 */
	public Table<IActionConcept, IActionCriterion, Float> findAction(IAgentAccess info, IGoalConcept expectation,
			UUID processID, boolean doAccesses);

	// TODO learnActionInfo

}
