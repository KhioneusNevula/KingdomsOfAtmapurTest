package things.physics_and_chemistry;

/**
 * Result of applying a force
 * 
 * @author borah
 *
 */
public enum ForceResult {
	/** The force moved the entity */
	MOVED,
	/** The force changed the structure or material-state of a part */
	DESTROYED,
	/** The force divided one part into multiple */
	CUT_PART,
	/** The force damaged the integrity of a part of an entity */
	DAMAGED_PART,
	/** The force damaged a connection in the entity's form */
	DAMAGED_CONNECTION,
	/** the force punctured a hole in the entity */
	MADE_HOLE,
	/** The force severed a connection in the entity */
	SEVERED,
	/** The force harmlessly slipped between two parts of the entity */
	SLIPPED_THROUGH,
	/** The force did nothing */
	NOTHING,
	/** some other result */
	OTHER
}
