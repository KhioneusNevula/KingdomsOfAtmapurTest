package _utilities.graph;

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

	/**
	 * Return either
	 * <ol>
	 * <li>null if the given object is permitted to be the node at the other end of
	 * this relation type (assumed that it has already been checked to be of the
	 * type of {@link #getEndClass()};
	 * <li>an Integer if the object is permitted, BUT only the given number of the
	 * given object are permitted
	 * <li>A string error message for what is expected
	 * </ol>
	 */
	public Object checkEndType(Object node);

	/**
	 * The name of this relation
	 * 
	 * @return
	 */
	public String name();

}