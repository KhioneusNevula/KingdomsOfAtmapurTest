package thinker.concepts.relations;

/**
 * TODO Partial satisfactions, i.e. an action relation can have the Partial
 * trait to indicate the action only partially satisfies? <br>
 * Action Relations encode state changes in the relational space,
 * 
 * @author borah
 *
 */
public enum ActionRelationType implements IConceptRelationType {
	/** describes that the action or Process satisfies Y need/condition */
	SATISFIES, SATISFIED_BY(SATISFIES),
	/**
	 * describes that the action relation creates or brings Y thing closer to the
	 * doer
	 */
	ACQUIRES, ACQUIRED_BY(ACQUIRES),
	/**
	 * describes that the action relation destroys or brings Y thing further from
	 * the doer
	 */
	REMOVES, REMOVED_BY(REMOVES),
	/** Y is the target location that action X relation moves the target thing to */
	TARGET_TO, TARGET_LOCATION_OF(TARGET_TO),
	/** Y is the Part which the action X moves the target to */
	TARGET_TO_PART, TARGET_PART_OF(TARGET_TO_PART),
	/** For objects the action uses to complete itself */
	USES, USED_BY(USES),
	/** What the action moves the Self to */
	SELF_TO, SELF_TARGET_LOCATION_OF(SELF_TO),
	/** The action applies this label or trait to the target */
	APPLIES, APPLIED_BY(APPLIES),
	/** what kind of conditions the action expects of its targets */
	TARGETS, TARGETED_BY(TARGETS),
	/** what kind of conditions the action expects of the self */
	EXPECTS_SELF, SELF_EXPECTED_BY(EXPECTS_SELF),
	/** what the action increases */
	INCREASES, INCREASED_BY(INCREASES),
	/** what the action decreases */
	DECREASES, DECREASED_BY(DECREASES),
	/** What question the action answers */
	ANSWERS, ANSWERED_BY(ANSWERS);

	private ActionRelationType opposite;

	private ActionRelationType() {
		opposite = this;
	}

	private ActionRelationType(ActionRelationType opposite) {
		this.opposite = opposite;
		opposite.opposite = this;
	}

	@Override
	public IConceptRelationType invert() {
		return opposite;
	}

	@Override
	public boolean bidirectional() {
		return opposite == this;
	}

	@Override
	public boolean characterizesOther() {
		return false;
	}

	@Override
	public boolean isCharacterizedByOther() {
		return false;
	}

}
