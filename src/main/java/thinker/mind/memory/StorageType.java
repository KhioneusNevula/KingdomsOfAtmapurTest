package thinker.mind.memory;

/**
 * a status describing the way a connection, concept, or other thing is held in
 * memory
 * 
 * @author borah
 *
 */
public enum StorageType {
	/**
	 * This memory connection or node is slated to vanish after a few ticks, or the
	 * next time memories are pruned. How likely it is to vanish depends on its
	 * current CONFIDENCE.
	 */
	TEMPORARY,
	/**
	 * this memory connection is of unknown truth value and will be replaced wtih a
	 * more confident connection eventually
	 */
	UNKNOWN,
	/**
	 * this memory connection or node is of partial confidence and will be deleted
	 * if not verified
	 */
	DUBIOUS,
	/**
	 * this memory connection or node is of full confidence, and is not to be
	 * deleted
	 */
	CONFIDENT;

	@Override
	public String toString() {
		return this.name().charAt(0) + "";
	}
}