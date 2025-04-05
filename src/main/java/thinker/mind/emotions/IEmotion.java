package thinker.mind.emotions;

import java.util.Collection;

import thinker.mind.memory.IFeelingReason;
import thinker.mind.perception.IPerceptor;

public interface IEmotion extends IFeeling, IPerceptor {

	@Override
	default PerceptorType getPerceptorType() {
		return PerceptorType.EMOTION;
	}

	@Override
	default boolean isAffect() {
		return false;
	}

	@Override
	default boolean isEmotion() {
		return true;
	}
}
