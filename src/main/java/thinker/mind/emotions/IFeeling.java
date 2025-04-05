package thinker.mind.emotions;

import _utilities.property.IProperty;
import thinker.concepts.IConcept;
import thinker.mind.memory.IFeelingReason;
import thinker.mind.perception.IPerceptor;

/** Something which can be felt in the mind as a goal or desire */
public interface IFeeling extends IProperty<Float>, IConcept, IFeelingReason {

	@Override
	default FeelingReasonType getReasonType() {
		return FeelingReasonType.FEELING;
	}

	/**
	 * If this is one of the fundamental motivations of the body, i.e. Satisfaction,
	 * Vigor, and so on
	 * 
	 * @return
	 */
	public boolean isAffect();

	/**
	 * If this is one of the complex feelings that are higher than affects
	 * 
	 * @return
	 */
	public boolean isEmotion();

}
