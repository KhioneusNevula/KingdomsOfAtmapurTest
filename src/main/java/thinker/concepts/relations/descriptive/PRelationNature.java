package thinker.concepts.relations.descriptive;

/**
 * What domain of reality a relation spans
 * 
 * @author borah
 *
 */
public enum PRelationNature {

	/**
	 * For relations that encompass the nature of a thing in and of itself, i.e.
	 * usually immutable realtions (e.g. what something's Kind is)
	 */
	NATURAL,

	/**
	 * For relations between physical things
	 */
	PHYSICAL,
	/**
	 * For relations that cross physical and metaphysical
	 */
	METAPHYSICAL,
	/**
	 * For relations that are strictly social bonds
	 */
	SOCIAL
}
