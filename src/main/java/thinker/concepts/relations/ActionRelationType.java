package thinker.concepts.relations;

/**
 * 
 * Action Relations encode state changes in the relational space, as opposed to
 * ConceptRelations which indicate static states. Usually meant to be drawn from
 * the "THIS_ACTION" or "SATISFIER" concept to something else, or an action to
 * something else in the Concept Space.
 * 
 * @author borah
 *
 */
public enum ActionRelationType implements IConceptRelationType {
	/** describes that the action or Process satisfies Y need/condition */
	SATISFIES, SATISFIED_BY(SATISFIES),
	/** Describes that an action relation creates things with Y concept */
	CREATES, CREATED_BY(CREATES),
	/** Describes that an action relation fixes things with Y concept */
	FIXES, FIXED_BY(FIXES),
	/**
	 * To convey a relation which causes a Thing with label Y to change location
	 */
	MOVES, MOVED_BY(MOVES),
	/**
	 * describes that the action relation destroys Y thing
	 */
	DESTROYS, DESTROYED_BY(DESTROYS),
	/** Describes that the action relation damages Y thing */
	DAMAGES, DAMAGED_BY(DAMAGES),
	/** Describes an action that relabels something of concept Y */
	RELABELS, RELABELED_BY(RELABELS),
	/** Describes an action that transforms the nature of something of concept Y */
	TRANSFORMS, TRANSFORMED_BY(TRANSFORMS),
	/** For objects the action uses to complete itself */
	USES, USED_BY(USES),
	/** what the action increases, which may be a feeling or a stat */
	INCREASES, INCREASED_BY(INCREASES),
	/** what the action decreases, which may be a feeling or a stat */
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
