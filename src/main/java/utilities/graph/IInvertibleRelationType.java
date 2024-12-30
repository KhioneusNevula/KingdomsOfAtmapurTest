package utilities.graph;

/**
 * Represents a relation (which can be inverted)
 * 
 * @author borah
 *
 */
public interface IInvertibleRelationType {
	/**
	 * Get the inverted version. Preferred to be same type as this relation, and
	 * graphs should ideally use a superclass of both types
	 * 
	 * @return
	 */
	public IInvertibleRelationType invert();

	/**
	 * Whether this relation is the same both ways
	 * 
	 * @return
	 */
	public boolean bidirectional();

}