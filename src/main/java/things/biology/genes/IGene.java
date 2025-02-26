package things.biology.genes;

import utilities.property.IProperty;

/**
 * A gene is a unique thing that controls traits in an organism
 * 
 * @author borah
 *
 * @param <E>
 */
public interface IGene<E> extends IProperty<E> {

	/**
	 * Return the unique string representing this value in this gene. Ideally,
	 * should be one uppercase letter, but may include a lowercase letter if that is
	 * not possible.
	 * 
	 * @param value
	 * @return
	 */
	public String getChar(E value);

	/**
	 * Return all possible values of this gene
	 * 
	 * @return
	 */
	public Iterable<E> allValues();

	/**
	 * Number of possible values this gene has
	 * 
	 * @return
	 */
	public int numValues();

	/**
	 * If this value is an acceptable value for this gene
	 * 
	 * @param value
	 * @return
	 */
	public boolean isValidValue(E value);
}
