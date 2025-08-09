package thinker.actions.searching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Table;

import party.util.IAgentAccess;
import thinker.actions.IActionConcept;
import thinker.actions.searching.IActionFinder.IActionCriterion;

/**
 * basic implementation of {@link IActionPicker}
 * 
 * @author borah
 *
 */
public class ActionPicker implements IActionPicker {

	/**
	 * Evaluate an individual action. Return 0 if this action does not meet
	 * standards; return 1 if the action is a possible result, and return 2 if the
	 * action should be returned right away as the correct action.
	 * 
	 * Override this to change evaluation behavior
	 * 
	 * @param actio
	 * @param actionTable
	 * @param info
	 * @return
	 */
	protected int evaluateAction(IActionConcept actio, Table<IActionConcept, IActionCriterion, Float> actionTable,
			IAgentAccess info) {
		// TODO more criteria
		Float inc = actionTable.get(actio, BasicActionCriterion.INCOMPLETION);
		if (inc != null) {
			if (inc <= 0) {
				return 2;
			} else if (inc == 0) {
				return 0;
			} else { // if not completionist, add to maybes
				return 1;
			}
		} else {
			return 0;
		}
	}

	@Override
	public IActionConcept pickAction(Table<IActionConcept, IActionCriterion, Float> actionTable, IAgentAccess info,
			Set<IActionConcept> blacklist) {
		boolean blocked = false;
		Set<IActionConcept> maybes = new HashSet<>();
		// randomized order
		List<IActionConcept> considerations = new ArrayList<>(actionTable.rowKeySet());
		Collections.shuffle(considerations);

		for (IActionConcept actio : considerations) {
			int evaluation = evaluateAction(actio, actionTable, info);
			if (evaluation == 2) {
				if (blacklist.contains(actio)) {
					blocked = true; // if blocked, mark as blocked
				} else {
					return actio; // if completionist return immediately
				}
			} else if (evaluation == 1) { // if not completionist, add to maybes
				maybes.add(actio);
			}
		}
		if (!maybes.isEmpty()) {
			return maybes.stream().filter((a) -> !blacklist.contains(a)).findAny().orElse(null);
		} else {
			if (blocked) {
				return null;
			}
			return IActionConcept.NO_ACTION;
		}
	}

}
