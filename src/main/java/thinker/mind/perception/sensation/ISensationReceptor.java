package thinker.mind.perception.sensation;

import thinker.mind.util.IBeingAccess;

/** An object/interface intended to indicate how to sense a sensation */
@FunctionalInterface
public interface ISensationReceptor {

	/**
	 * Returns a level for this sensation (the sensation itself is passed into the
	 * function as well) using the given part and what it accesses (and, if needed,
	 * sensitivity stats on the parts). Return 0 if the sensation cannot be felt.
	 */
	public float getSensationLevel(ISensation forSensation, IBeingAccess info);
}
