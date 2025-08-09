package thinker.goals;

public class GoalMemoryConcept implements IGoalMemoryConcept {

	private IGoalConcept goal;
	private long time;
	private GoalFailureReason failreas;

	/**
	 * A successful memory constructor
	 * 
	 * @param goal
	 * @param atTime
	 */
	public GoalMemoryConcept(IGoalConcept goal, long atTime) {
		this(goal, atTime, GoalFailureReason.SUCCESS);
	}

	/**
	 * A constructor with a given failure reason
	 * 
	 * @param goal
	 * @param atTime
	 * @param rason
	 */
	public GoalMemoryConcept(IGoalConcept goal, long atTime, GoalFailureReason rason) {
		this.goal = goal;
		this.time = atTime;
		this.failreas = rason;

	}

	@Override
	public GoalFailureReason getFailureReason() {
		return failreas;
	}

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public String getUnderlyingName() {
		return (failreas == GoalFailureReason.SUCCESS ? "fulfilled_"
				: "unfulfilled_" + failreas.toString().toLowerCase()) + "goal_memory_" + goal.getUnderlyingName() + "_"
				+ this.time;
	}

	@Override
	public IGoalConcept getGoal() {
		return goal;
	}

	@Override
	public boolean fulfilled() {
		return this.failreas == GoalFailureReason.SUCCESS;
	}

	@Override
	public String toString() {
		return "GoalMemoryConcept(t=" + this.time + ",reason=" + failreas + ",g=" + this.goal + ")";
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj instanceof IGoalMemoryConcept igmc && this.goal.equals(igmc.getGoal())
				&& this.time == igmc.getTime() && this.failreas == igmc.getFailureReason());
	}

	@Override
	public int hashCode() {
		return goal.hashCode() + Long.hashCode(time) * (this.failreas.ordinal() + 1);
	}

}
