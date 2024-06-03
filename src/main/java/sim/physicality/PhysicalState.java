package sim.physicality;

public enum PhysicalState {
	HARD_SOLID_WHOLE, HARD_SOLID_DAMAGED, HARD_SOLID_POWDER, FLEXIBLE_SOLID, RIPPED_FLEXIBLE_SOLID, SQUISHY_SOLID_WHOLE,
	SQUISHY_SOLID_DAMAGED, SQUISHY_SOLID_PULPED, SQUISHY_SOLID_PASTE, LIQUID, GAS, PLASMA,
	/** for things with multiple parts, to indicate it has missing parts */
	GONE,
	/**
	 * for things that do not have physical properties
	 */
	METAPHYSICAL;

	/**
	 * If this is a hard solid
	 * 
	 * @return
	 */
	public boolean isHard() {
		return this == HARD_SOLID_WHOLE || this == HARD_SOLID_DAMAGED || HARD_SOLID_POWDER == this;
	}

	/**
	 * Whether this substance is solid
	 * 
	 * @return
	 */
	public boolean isSolid() {
		return this.isHard() || this.isFlexible() || this.isSquishy();
	}

	/**
	 * Whether this substance is a soft pliable sort of solid
	 * 
	 * @return
	 */
	public boolean isSquishy() {
		return this == SQUISHY_SOLID_WHOLE || this == SQUISHY_SOLID_DAMAGED || this == SQUISHY_SOLID_PULPED
				|| this == SQUISHY_SOLID_PASTE;
	}

	/**
	 * whether this substance is liquid
	 * 
	 * @return
	 */
	public boolean isLiquid() {
		return this == LIQUID;
	}

	/**
	 * whether this substance is gas
	 * 
	 * @return
	 */
	public boolean isGas() {
		return this == GAS;
	}

	/**
	 * If this is plasma
	 * 
	 * @return
	 */
	public boolean isPlasma() {
		return this == PLASMA;
	}

	/**
	 * If this substance is no longer present
	 * 
	 * @return
	 */
	public boolean gone() {
		return this == GONE;
	}

	/**
	 * IF the substance is now metaphysical
	 * 
	 * @return
	 */
	public boolean metaphysical() {
		return this == METAPHYSICAL;
	}

	/**
	 * Whether this substance is damaged (i.e. damaged, pulped, powder, or paste)
	 * 
	 * @return
	 */
	public boolean isDamaged() {
		return this == HARD_SOLID_DAMAGED || this == SQUISHY_SOLID_DAMAGED || this == SQUISHY_SOLID_PULPED
				|| this == SQUISHY_SOLID_PASTE || this == HARD_SOLID_POWDER || this == RIPPED_FLEXIBLE_SOLID;
	}

	/**
	 * Whether this substance is pulped
	 * 
	 * @return
	 */
	public boolean isPulped() {
		return this == SQUISHY_SOLID_PULPED;
	}

	/**
	 * Whether this substance is paste
	 */
	public boolean isPaste() {
		return this == SQUISHY_SOLID_PASTE;
	}

	/**
	 * Whether this substance is powder
	 * 
	 * @return
	 */
	public boolean isPowder() {
		return this == HARD_SOLID_POWDER;
	}

	/**
	 * Whether this substance is composed of flexible strands
	 * 
	 * @return
	 */
	public boolean isFlexible() {
		return this == FLEXIBLE_SOLID || this == RIPPED_FLEXIBLE_SOLID;
	}
}