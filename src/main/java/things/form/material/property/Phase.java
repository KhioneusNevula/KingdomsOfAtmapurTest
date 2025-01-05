package things.form.material.property;

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
	/** a gas */
	GAS,
	/** the highest "destroyed" state, a complete state of destroyedness */
	PLASMA,
	/** other stuff; merely acts as a default */
	OTHER;

	/**
	 * Whether this is a solid or granular, i.e. a solid in physics terms
	 * 
	 * @return
	 */
	public boolean isSolidOrGranular() {
		return this == SOLID || this == GRANULAR;
	}

	/**
	 * Whether this would behave as a fluid block; applies to all except Solid (and
	 * Other)
	 * 
	 * @return
	 */
	public boolean isFluid() {
		return this == LIQUID || this == GAS || this == PLASMA || this == GRANULAR;
	}

	/**
	 * Whether this is gaseous, i.e. gas or plasma
	 * 
	 * @return
	 */
	public boolean isGaseous() {
		return this == GAS || this == PLASMA;
	}

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
		case GAS:
			return PLASMA;
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
		case PLASMA:
			return GAS;
		default:
			return this;
		}
	}
}