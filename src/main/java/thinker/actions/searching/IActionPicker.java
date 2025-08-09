package thinker.actions.searching;

import java.util.Set;

import com.google.common.collect.Table;

import party.util.IAgentAccess;
import thinker.actions.IActionConcept;
import thinker.actions.searching.IActionFinder.IActionCriterion;

/**
 * Picks actions for a ThinkerWill
 * 
 * @author borah
 *
 */
public interface IActionPicker {

	/**
	 * Picks an action from the table based on the criteria and personality of
	 * picker. Excludes actions from the given blacklist. If no action was found,
	 * return {@link IActionConcept#NO_ACTION}. If all found actions are in the
	 * blacklist, return null.
	 */
	public IActionConcept pickAction(Table<IActionConcept, IActionCriterion, Float> actionTable, IAgentAccess info,
			Set<IActionConcept> blacklist);

}
