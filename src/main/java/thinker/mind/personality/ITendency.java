package thinker.mind.personality;

import _utilities.property.IProperty;

/**
 * A trait of the tendencies of an agent that governs an element of how it acts
 * 
 * @author borah
 *
 */
public interface ITendency extends IProperty<Float> {

	/** Max value of this trat */
	public default float getMax() {
		return 1f;
	}

	/** min value of this trait */
	public default float getMin() {
		return 0f;
	}

	public default Class<? super Float> getType() {
		return float.class;
	}

	/**
	 * Returns the name of the "opposite" of this trait (only relevant if the trait
	 * can become negative)
	 */
	public String getOppositeName();

	public String getDescription();
}
