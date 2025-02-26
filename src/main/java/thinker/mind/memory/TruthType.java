package thinker.mind.memory;

/**
 * Describes what kind of truth a connection is
 * 
 * @author borah
 *
 */
public enum TruthType {
	/**
	 * Describes something which is held to only be true currently, or at the time
	 * that it is connected to
	 */
	TEMPORAL,
	/**
	 * Describes something that is held to only be true in certain repeating periods
	 */
	PERIODIC,
	/** Describes something that is always true */
	GNOMIC,
	/**
	 * Describes something to be of counterfactual truth status, i.e. a fancy or
	 * fiction
	 */
	COUNTERFACTUAL
}