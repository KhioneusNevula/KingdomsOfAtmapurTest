package thinker.mind.emotions;

import thinker.mind.perception.IPerceptor;

/**
 * One of the fundamental motivations
 * 
 * @author borah
 *
 */
public interface IAffect extends IFeeling {

	/**
	 * Whether this affect is one that a being wants to increase, i.e. an affect
	 * that is not linked to behavior btu rather acts as a goal
	 */
	public boolean wantsToIncrease();

	/**
	 * Whether this affect is one that a being wants to decrease, i.e. an affect
	 * that is not linked to behavior btu rather acts as a goal
	 */
	public boolean wantsToDecrease();

	@Override
	default boolean isAffect() {
		return true;
	}

	@Override
	default boolean isEmotion() {
		return false;
	}

}
