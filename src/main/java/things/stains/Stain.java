package things.stains;

import things.form.material.IMaterial;

public class Stain implements IStain {

	private IMaterial substance;
	private int amount;

	public Stain(IMaterial material, int amount) {
		this.substance = material;
		this.amount = amount;
	}

	@Override
	public IMaterial getSubstance() {
		return substance;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IStain st) {
			return this.substance.equals(st.getSubstance()) && this.amount == st.getAmount();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {

		return substance.hashCode() * amount;
	}

	@Override
	public String toString() {
		return "{(" + this.substance + "," + this.amount + "u)}";
	}

}
