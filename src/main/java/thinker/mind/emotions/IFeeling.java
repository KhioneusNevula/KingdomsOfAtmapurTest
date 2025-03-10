package thinker.mind.emotions;

import _utilities.property.IProperty;
import thinker.concepts.IConcept;

public interface IFeeling extends IProperty<Float>, IConcept {

	public float min();

	public float max();

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
