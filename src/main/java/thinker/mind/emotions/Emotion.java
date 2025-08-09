package thinker.mind.emotions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Streams;

import _utilities.couplets.Pair;

public enum Emotion implements IEmotion {
	HAPPINESS, ANGER, FRUSTRATION, GRIEF, FEAR, HORROR, DISGUST, ANNOYANCE, SADNESS, CONFUSION, SURPRISE, DESIRE,
	EMBARRASSMENT, CURIOSITY, AWE;

	/**
	 * 
	 */
	private Emotion() {// Object... aff) {
		/*
		 * for (Object ob : aff) { if (ob instanceof Pair pair) { if ((Boolean)
		 * pair.getSecond()) { aD.add((IAffect) pair.getFirst()); } else {
		 * aI.add((IAffect) pair.getFirst()); } } else if (ob instanceof IAffect iaf) {
		 * aI.add(iaf); } else { throw new
		 * IllegalArgumentException(Arrays.toString(aff)); } }
		 */
	}

	@Override
	public String getPropertyName() {
		return "emotion_" + this.name();
	}

	@Override
	public Float defaultValue() {
		return 0f;
	}

	@Override
	public Class<? super Float> getType() {
		return float.class;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.FEELING;
	}

	@Override
	public String getUnderlyingName() {
		return this.getPropertyName();
	}

	@Override
	public PerceptorType getPerceptorType() {
		return PerceptorType.EMOTION;
	}

}
