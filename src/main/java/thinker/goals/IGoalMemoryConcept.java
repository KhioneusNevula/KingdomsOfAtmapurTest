package thinker.goals;

import thinker.concepts.general_types.IMemoryConcept;

/** A memory of a fulfilled or unfulfilled goal */
public interface IGoalMemoryConcept extends IMemoryConcept {

	/** Possible reasons why a goal could have failed */
	public static enum GoalFailureReason {
		/** No actions to address the goal are known to the actor */
		NO_AVAILABLE_ACTIONS,
		/** All actions that could address the goal were not deemed good enough */
		ACTIONS_DID_NOT_MEET_STANDARDS,
		/** Action was tried and failed */
		ACTION_FAILURE,
		/** All available actions were tried and every single one failed */
		ALL_ACTIONS_FAILED,
		/** A failure due to memory not having correct connections or something */
		MEMORY_FAILURE,
		/** No failuer */
		SUCCESS
	}

	/**
	 * Why this goal failed (if it failed)
	 * 
	 * @return
	 */
	public GoalFailureReason getFailureReason();

	/** Return what goal this was */
	public IGoalConcept getGoal();

	/**
	 * Whether the goal was fulfilled (i.e. {@link #getFailureReason()} ==
	 * {@link GoalFailureReason#SUCCESS})
	 */
	public boolean fulfilled();

	/**
	 * Whether the goal was unfulfilled (i.e. {@link #getFailureReason()} !=
	 * {@link GoalFailureReason#SUCCESS})
	 */
	public default boolean unfulfilled() {
		return !fulfilled();
	}

}
