package things.physics_and_chemistry;

/**
 * Describe different types of applications of force to an object
 * 
 * @author borah
 *
 */
public enum ForceType {
	/**
	 * A force that acts as a violent strike, typically changing the shape of a part
	 * it hits and capable of either shattering it (if it is crystalline) or
	 * crumbling it into a granular substance, or turning a shape from shaped into
	 * amorphous
	 */
	BLUNT,
	/**
	 * A force that pushes something in a certain direction without doing any sort
	 * of damage to it
	 */
	PUSH,
	/**
	 * A force that acts as a violent penetrating, typically creating a hole in a
	 * part
	 */
	STAB,
	/** A force that typically severs a connection */
	SLICE,
	/**
	 * A light force that can draw forth some embedded material but doesn’t notably
	 * damage something
	 */
	SCRATCH
}
