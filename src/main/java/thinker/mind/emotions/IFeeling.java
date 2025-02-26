package thinker.mind.emotions;

import thinker.concepts.IConcept;
import utilities.property.IProperty;

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
