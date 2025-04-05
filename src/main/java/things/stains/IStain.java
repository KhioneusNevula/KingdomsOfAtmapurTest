package things.stains;

import things.form.channelsystems.IResource;
import things.form.material.IMaterial;

public interface IStain {

	/** Represents a nonexistent stain */
	public static final IStain EMPTY_STAIN = new IStain() {

		@Override
		public IMaterial getSubstance() {
			return IMaterial.NONE;
		}

		@Override
		public int getAmount() {
			return 0;
		}
	};

	/** Amount of fluid units per full unit of embedded material resource */
	public static final int FLUID_UNITS_PER_MATERIAL_UNIT = 15;

	/** Creates a stain from a material that functions as a resourc */
	public static IStain fromResourceMaterial(IResource<?> resource, IMaterial mat, Comparable<?> amount) {
		if (amount instanceof Float f) {
			return new Stain(mat, (int) (f.floatValue() / ((Float) resource.getMaxValue()).floatValue()
					* FLUID_UNITS_PER_MATERIAL_UNIT));
		}
		return new Stain(mat, FLUID_UNITS_PER_MATERIAL_UNIT);
	}

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
