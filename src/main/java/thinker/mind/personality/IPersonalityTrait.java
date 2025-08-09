package thinker.mind.personality;

import thinker.mind.emotions.IAffect;
import thinker.mind.emotions.IEmotion;
import thinker.mind.memory.IFeelingReason;
import thinker.mind.perception.IPerceptor;

/**
 * A property governing the relation between one's personality {@link IAffect}s
 * and {@link IPerceptor}s or {@link IEmotion}s. Usually between 0 and 1, but
 * can be -1 to 1. See {@link IPersonality} for details
 * 
 * @author borah
 *
 */
public interface IPersonalityTrait extends ITendency {

	/**
	 * Whether this personality trait represents the default value of an affect
	 * (i.e., {@link #getReason()} is null
	 */
	public default boolean isDefault() {
		return getReason() == null;
	}

	/** the Reason that this personality trait utilizes */
	public IFeelingReason getReason();

	/** the affect this trait affects */
	public IAffect getRelevantAffect();

	/**
	 * Returns the multiplicagtion factor for this trait's affect, based on the
	 * given trait level and the level of the perceptor
	 */
	public float getAffectFactor(float traitLevel, float perceptorLevel);

	/**
	 * Returns the default value for this trait's affect based on the trait level,
	 * if this is a default-value trait
	 */
	public default float getAffectDefault(float traitLevel) {
		throw new UnsupportedOperationException();
	}

	public default String getDescription() {
		return isDefault() ? "Default value of " + this.getRelevantAffect()
				: "How much " + this.getReason() + " changes " + this.getRelevantAffect();
	}

}
