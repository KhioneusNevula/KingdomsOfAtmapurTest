package thinker.mind.perception;

/**
 * This indicates the level of something in a {@link IPerceptor} that is being
 * experienced, on a scale from {@link #COMPLETE} (0.0) to {@link #NO} (1.0).
 */
public enum PerceptorLevel {

	/**
	 * The perceptor senses a complete amount (i.e. complete pain, complete
	 * satiation), i.e. 1.0f
	 */
	COMPLETE(1.0f),
	/**
	 * The perceptor senses high amounts of something (i.e. high pain, high
	 * satiation), i.e. 0.8f
	 */
	HIGH(0.8f),
	/**
	 * The perceptor senses medial amounts of something (i.e. medium pain, medium
	 * satiation), i.e. 0.6f
	 */

	MEDIUM(0.6f),
	/**
	 * The perceptor senses moderate amounts (i.e. moderate pain, moderate
	 * satiation), i.e. 0.4f
	 */
	MODERATE(0.4f),
	/**
	 * The perceptor senses a low amount something (i.e. depleted pain, depleted
	 * satiation), i.e. 0.2f
	 */
	LOW(0.2f),
	/**
	 * The perceptor senses nothing (i.e. a lack of satiation, or a lack of pain),
	 * i.e. 0.0f
	 */
	NO(0f),

	/** The perceptor cannot be interpreted */
	UNKNOWN(Float.MIN_VALUE);

	private float amt;

	private PerceptorLevel(float amt) {
		this.amt = amt;
	}

	public static PerceptorLevel fromAmount(float amt) {
		if (amt < 0)
			return UNKNOWN;
		if (amt == 0)
			return NO;
		if (amt >= 1)
			return COMPLETE;
		PerceptorLevel found = COMPLETE;
		for (PerceptorLevel nl : values()) {
			if (amt > nl.amt && nl.amt < found.amt) {
				found = nl;
				break;
			}
		}
		return found;
	}

	/** Gets the "inverse" of this, i.e. {@link #COMPLETE} if this is {@link #NO} */
	public PerceptorLevel getInverse() {
		if (this == UNKNOWN)
			return UNKNOWN;
		return fromAmount(1f - this.amt);
	}

	/** Gets the lower threshold for this need */
	public float getThreshold() {
		return amt;
	}

}
