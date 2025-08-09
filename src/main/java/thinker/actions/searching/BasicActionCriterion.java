package thinker.actions.searching;

import thinker.actions.searching.IActionFinder.IActionCriterion;

public enum BasicActionCriterion implements IActionCriterion {
	/**
	 * What percentage of the goal this action leaves incomplete, usually either 50%
	 * or 0%
	 */
	INCOMPLETION,
	/** How physically effortful this action is */
	EFFORT,
	/** How much this action drains stats (i.e. magic power or something) */
	DRAINAGE,
	/**
	 * What percent of the action is unpredictable based on the action not having
	 * all information
	 */
	UNPREDICTABILITY
}