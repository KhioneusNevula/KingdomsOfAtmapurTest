package _utilities.graph;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import _utilities.property.IProperty;

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
	 * For some element {@literal oldNode} in a graph, this function will replace
	 * {oldNode} with {newNode} such that all the connections that used to connect
	 * to {oldNode} now connect to {newNode}, and {oldNode} no longer exists in the
	 * graph.
	 * 
	 * @param oldNode the node that will be changed
	 * @param newNode the new value to put in place of it
	 */
	void set(Object oldNode, E newNode);

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
	 * Checks if the classes of the two endpoints of the edge are permissible based
	 * on {@link IInvertibleRelationType#checkEndType(Object)}, and return the
	 * results of this
	 */
	Pair<Object, Object> checkEdgeEndsPermissible(Object first, IInvertibleRelationType type, Object second);

	/**
	 * Remove all edges coming from this node. Return false if no connections were
	 * removed (because the node had none). Will throw exception if the node doesn't
	 * exist
	 * 
	 * @param value
	 * @return
	 */
	boolean removeAllConnections(Object value);

	/**
	 * Remove all connections of this type from the given node; return true if any
	 * edges were removed
	 * 
	 * @param value
	 * @param type
	 * @return
	 */
	boolean removeAllConnections(Object value, R type);

	/**
	 * Remove all connections between these two nodes; return true if any edges were
	 * removed
	 * 
	 * @param value
	 * @param other
	 * @return
	 */
	boolean removeAllConnections(Object value, Object other);

	/**
	 * Removes all bare nodes from this graph
	 */
	void removeBareNodes();

	/**
	 * Remove the edge of the given type between these nodes, return true if an edge
	 * was removed at all
	 * 
	 * @param value
	 * @param type
	 * @param other
	 * @return
	 */
	boolean removeEdge(Object value, R type, Object other);

	/**
	 * Get the value of a specified property on a specified edge. Return null if no
	 * such property was assigned for this edge, or if edge is not real. Return
	 * default value if no such property was assigned but a default value is
	 * available.
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
	<X> X getProperty(E one, R type, E two, IProperty<X> prop, boolean computeIfAbsent);

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
	<X> X setProperty(E one, R type, E two, IProperty<X> prop, X val);

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
	<X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Function<X, X> getSet);

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
	<X> void forEachEdgeProperty(E one, IProperty<X> prop, Function<X, X> getSet);

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
	<X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Function<X, X> getSet);

	@Override
	public IModifiableRelationGraph<E, R> subgraph(Iterable<? extends E> nodes);

	@Override
	public IModifiableRelationGraph<E, R> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred);

	/**
	 * Adds all elements and edges of the given subgraph in this graph; return true
	 * if anything changed
	 */
	public boolean addAll(IRelationGraph<E, R> subgraph);

	@Override
	public IModifiableRelationGraph<E, R> copy();

	@Override
	public IModifiableRelationGraph<E, R> deepCopy(Function<E, E> cloner);

	@Override
	public IModifiableRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject);

	@Override
	public IModifiableRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject);

	/**
	 * Removes all edges
	 * 
	 * @return
	 */
	public default boolean clearEdges() {
		boolean mod = false;
		for (E node : this) {
			mod = mod || this.removeAllConnections(node);
		}
		return mod;
	}

}
