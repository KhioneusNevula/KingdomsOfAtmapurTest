package thinker.mind.perception;

import things.form.channelsystems.IChannelNeed;

import thinker.mind.emotions.IAffect;
import thinker.mind.perception.sensation.ISensation;
import things.form.sensing.sensors.ISensor;

/**
 * A {@link IPerceptor} is (usually; i.e. with the exception of
 * {@link PerceptorType#EMOTION}) something that can be perceived by a
 * {@link IPerception}. This includes {@link ISensor}s, {@link ISensation}s, and
 * {@link IChannelNeed}s.
 */
public interface IPerceptor {

	/** Return the type of this perceptor */
	public PerceptorType getPerceptorType();

	/** Types of perceptors that can contribute to one's perception */
	public static enum PerceptorType {
		/** A physiological need, as a perceptor */
		NEED,
		/** A physiological sensation, as a Perceptor */
		SENSATION,
		/** A sensor, as a Perceptor */
		SENSOR,
		/** An emotion, as a perceptor */
		EMOTION,
		/** some other perceptor */
		OTHER
	}

}
