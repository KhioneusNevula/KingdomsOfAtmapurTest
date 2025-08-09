package thinker.mind.will;

import thinker.actions.searching.IActionFinder;
import thinker.actions.searching.IActionPicker;
import thinker.actions.searching.IProfileFinder;

public interface IWill {

	/**
	 * Returns the chance that a focused thought will get deleted randomly each
	 * tick. this should be 0 if the amount of focused thoughts is less than
	 * {@link #focusedThoughtsCap()}, and the chance increases with more focused
	 * thoughts above the cap (though it ought to never reach 1). It picks the
	 * focused thought that has been around for the shortest time.
	 */
	public float getMindStrainChance();

	/**
	 * Get the will apparatus that finds actions
	 * 
	 * @return
	 */
	public IActionFinder getActionFinder();

	/**
	 * Return an apparatus that picks actions
	 * 
	 * @return
	 */
	public IActionPicker getActionPicker();

	/**
	 * Return an apparatus that finds profilesets which could fit into an action's
	 * paradigm
	 * 
	 * @return
	 */
	public IProfileFinder getProfileFinder();

}
