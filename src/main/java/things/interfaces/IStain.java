package things.interfaces;

import things.physical_form.material.IMaterial;

public interface IStain {

	/**
	 * The substance of a stain
	 * 
	 * @return
	 */
	public IMaterial getSubstance();

	/**
	 * Get the amount of the stain
	 * 
	 * @return
	 */
	public float getAmount();
}
