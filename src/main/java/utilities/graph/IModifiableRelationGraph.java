package utilities.graph;

import java.util.function.Function;

/**
 * Generic interface for graph-style structures with modifiable functionality
 * 
 * @author borah
 *
 * @param <E>
 * @param <R>
 */
public interface IModifiableRelationGraph<E, R extends IInvertibleRelationType> extends IRelationGraph<E, R> {

	/**
	 * Add new edge. Return true if there was no existing edge. Return false and do
	 * nothing if there is an existing edge
	 * 
	 * @param first
	 * @param type
	 * @param second
	 * @return
	 */
	boolean addEdge(E first, R type, E second);

	/**
	 * Remove all edges coming from this node. Return false if no connections were
	 * removed (because the node had none)
	 * 
	 * @param value
	 * @return
	 */
	boolean removeAllConnections(E value);

	/**
	 * Remove all connections of this type from the given node; return true if any
	 * edges were removed
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	boolean removeAllConnections(E value, R type);

	/**
	 * Remove all connections between these two nodes; return true if any edges were
	 * removed
	 * 
	 * @param value
	 * @param other
	 * @return
	 */
	boolean removeAllConnections(E value, E other);

	/**
	 * Remove the edge of the given type between these nodes, return true if an edge
	 * was removed at all
	 * 
	 * @param value
	 * @param type
	 * @param other
	 * @return
	 */
	boolean removeEdge(E value, R type, E other);

	/**
	 * Get the value of a specified property on a specified edge. Return null if no
	 * such property was assigned for this edge. Return default value from
	 * {@link #edgeProperties} if no such property was assigned but a default value
	 * is available.
	 * 
	 * @param <E>
	 * @param one
	 * @param type
	 * @param two
	 * @param prop
	 * @param computeIfAbsent whether to add the property to the edge if it is
	 *                        absent. E.g. if accessing a property that is some kind
	 *                        of list, it would be useful to add the list into the
	 *                        map
	 * @return
	 */
	<X> X getProperty(E one, R type, E two, EdgeProperty<X> prop, boolean computeIfAbsent);

	/**
	 * Set the property and return the prior value, if any
	 * 
	 * @param <X>
	 * @param one
	 * @param type
	 * @param two
	 * @param prop
	 * @param obj
	 * @return
	 */
	<X> X setProperty(E one, R type, E two, EdgeProperty<X> prop, X val);

	/**
	 * For each edge between these two nodes, run getSet on it for the given
	 * property-- the input of getSet is the current value of the property for the
	 * edge we iterated to, and the output is the new value it should be changed to,
	 * or null if no change should be made.
	 * 
	 * @param one
	 * @param two
	 * @param prop
	 * @param getSet
	 */
	<X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Function<X, X> getSet);

	/**
	 * For each edge connecting from this node to any other, run getSet on it for
	 * the given property-- the input of getSet is the current value of the property
	 * for the edge we iterated to, and the output is the new value it should be
	 * changed to, or null if no change should be made.
	 * 
	 * @param one
	 * @param two
	 * @param prop
	 * @param getSet
	 */
	<X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Function<X, X> getSet);

	/**
	 * For each edge from this node of the given type, run getSet on it for the
	 * given property-- the input of getSet is the current value of the property for
	 * the edge we iterated to, and the output is the new value it should be changed
	 * to, or null if no change should be made.
	 * 
	 * @param one
	 * @param two
	 * @param prop
	 * @param getSet
	 */
	<X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Function<X, X> getSet);

}
