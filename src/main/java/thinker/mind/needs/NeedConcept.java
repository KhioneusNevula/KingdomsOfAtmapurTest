package thinker.mind.needs;

import things.form.channelsystems.IChannelNeed;
import thinker.goals.IGoalConcept;
import thinker.goals.NeedGoalConcept;

/**
 * A need concept designed to represent a {@link IChannelNeed} or something else
 */
public class NeedConcept implements INeedConcept {

	private String name;
	private NeedGoalConcept cond;
	private boolean isBiological;

	/**
	 * @param cneed will not store the given channel need, but will use it to
	 *              construct a name
	 */
	public NeedConcept(IChannelNeed cneed) {
		this(cneed.getName(), true);
	}

	/** @param bio If this need is biological, i.e. a channel system */
	public NeedConcept(String name, boolean bio) {
		this.name = name;
		this.cond = new NeedGoalConcept(this);
		this.isBiological = bio;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.NEED;
	}

	@Override
	public String getUnderlyingName() {
		return "need_" + this.name;
	}

	@Override
	public boolean isBiological() {
		return isBiological;
	}

	@Override
	public String getSimpleName() {
		return this.name;
	}

	@Override
	public IGoalConcept getRequirements() {
		return cond;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() * (this.isBiological ? 2 : 1);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof INeedConcept inc) {
			return this.name.equals(inc.getSimpleName()) && this.isBiological() == inc.isBiological();
		}
		return false;
	}

	@Override
	public String toString() {
		return "[[" + this.getClass().getSimpleName() + ":" + this.name + "]]";
	}

}
