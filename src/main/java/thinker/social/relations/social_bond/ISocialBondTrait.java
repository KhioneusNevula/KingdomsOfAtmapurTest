package thinker.social.relations.social_bond;

import _utilities.property.IProperty;

public interface ISocialBondTrait extends IProperty<Float> {

	/**
	 * Maximum possible value for this social bond trait. Should usually not be
	 * lower than -1
	 * 
	 * @return
	 */
	public float getMax();

	/**
	 * Minimum possible value for this social bond trait. Should usually not be
	 * higher than 1
	 * 
	 * @return
	 */
	public float getMin();

}
