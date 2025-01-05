package things.stains;

import things.form.material.IMaterial;

public interface IStain {

	/**
	 * The substance of a stain
	 * 
	 * @return
	 */
	public IMaterial getSubstance();

	/**
	 * Get the amount of the stain in fluid units
	 * 
	 * @return
	 */
	public int getAmount();
}
