package thinker.mind.emotions;

import java.util.Collection;

public interface IEmotion extends IFeeling {

	/**
	 * Return which affects this emotion increases
	 * 
	 * @return
	 */
	public Collection<? extends IAffect> affectsIncreased();

	/**
	 * Return which affects this emotion decreases
	 * 
	 * @return
	 */
	public Collection<? extends IAffect> affectsDecreased();

	@Override
	default boolean isAffect() {
		return false;
	}

	@Override
	default boolean isEmotion() {
		return true;
	}
}
