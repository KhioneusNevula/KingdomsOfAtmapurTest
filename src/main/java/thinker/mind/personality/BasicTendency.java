package thinker.mind.personality;

/**
 * Basic tendencies to different things
 * 
 * @author borah
 *
 */
public enum BasicTendency implements ITendency {
	/** How likely a being is to modify its physical form to complete a task */
	FORM_MUTABILITY(0.1f, 0, 1, "FORM_IMMUTABILITY"),
	/**
	 * How likely a being is to move itself or pick up/let go of something to
	 * complete a task
	 */
	POSITION_MUTABILITY(0.9f, 0, 1, "POSITION_IMMUTABILITY"),
	/** How likely a being is to change its social relations to complete a task */
	SOCIAL_MUTABILITY(0.5f, 0, 1, "SOCIAL_IMMUTABILITY"),
	/** How likely to change the tether of its spirit to complete a task */
	SPIRIT_MUTABILITY(0.0f, 0, 1, "SPIRIT_IMMUTABILITY");

	private String oppositen;
	private float defval;
	private float min = 0;
	private float max = 1;

	private BasicTendency(float dv, float min, float max, String oppositen) {
		this.defval = dv;
		this.min = min;
		this.max = max;
		this.oppositen = oppositen;
	}

	private BasicTendency(float dv) {
		this.defval = dv;
		this.oppositen = name() + "(opposite)";
	}

	@Override
	public String getPropertyName() {
		return "bt_" + this.name();
	}

	@Override
	public Float defaultValue() {
		return defval;
	}

	@Override
	public float getMin() {
		return min;
	}

	@Override
	public float getMax() {
		return max;
	}

	@Override
	public String getOppositeName() {
		return oppositen;
	}

	@Override
	public String getDescription() {
		return "Basic tendency of " + this.name().toLowerCase();
	}

}
