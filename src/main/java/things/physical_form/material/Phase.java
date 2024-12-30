package things.physical_form.material;

/**
 * The physical phase of a material
 * 
 * @author borah
 *
 */
public enum Phase {
	/** a full solid object */
	SOLID,
	/** a fluid-like collection of powder */
	GRANULAR,
	/** a liquid */
	LIQUID,
	/** aa gas */
	GAS,
	/** idk yet */
	PLASMA,
	/** other unusual stuff */
	OTHER;

	/**
	 * Typical next state when something of this state is heated
	 * 
	 * @return
	 */
	public Phase heated() {
		switch (this) {
		case SOLID:
		case GRANULAR:
			return LIQUID;
		case LIQUID:
			return GAS;
		default:
			return this;
		}
	}

	/**
	 * Typical next state when something of this state is cooled
	 * 
	 * @return
	 */
	public Phase cooled() {
		switch (this) {
		case LIQUID:
			return SOLID;
		case GAS:
			return LIQUID;
		default:
			return this;
		}
	}
}