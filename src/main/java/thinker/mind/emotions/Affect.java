package thinker.mind.emotions;

/** Basic affects of typical living things */
public enum Affect implements IAffect {
	/**
	 * The basic positive affect of all, this is the affect that people want to
	 * increase
	 */
	SATISFACTION(0.5f),
	/**
	 * The basic negative affect of all, this is the affect that people want to
	 * decrease
	 */
	SUFFERING,
	/**
	 * This affect governs how much a person can think; if this is decreased, the
	 * mind decreases in its ability to think. It is usually at a level of 0.5, but
	 * can increase or decrease
	 */
	FOCUS(0.5f),
	/** This affect governs how much a person is willing to do actions in general */
	VIGOR(0.5f),
	/** this affect governs how much a person instinctively avoids things? */
	REVULSION,
	/** This affect governs how much a person seeks knowledge */
	WONDER(0.5f),
	/**
	 * This affect governs how much a person's actions gear toward protecting their
	 * survival. Similar to fear, as an idea
	 */
	SELF_PRESERVATION(0.5f),
	/** This affect governs how much a person does actions that are desirous */
	LUST,
	/** This affect governs how much a person does actions that are violent */
	AGGRESSION;

	private float dv = 0f;

	private Affect() {
	}

	private Affect(float f) {
		this.dv = f;
	}

	@Override
	public String getPropertyName() {
		return "affect_" + name();
	}

	@Override
	public Float defaultValue() {
		return dv;
	}

	@Override
	public boolean wantsToDecrease() {
		return this == SUFFERING;
	}

	@Override
	public boolean wantsToIncrease() {
		return this == SATISFACTION;
	}

	@Override
	public Class<? super Float> getType() {
		return float.class;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.FEELING;
	}

	@Override
	public String getUnderlyingName() {
		return this.getPropertyName();
	}

}
