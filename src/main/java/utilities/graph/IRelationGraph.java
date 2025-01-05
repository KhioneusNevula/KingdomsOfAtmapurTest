package utilities.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

import utilities.couplets.Triplet;

/**
 * An interface representing relations-graph with no writing functionality
 * 
 * @author borah
 *
 * @param <E>
 * @param <R>
 */
public interface IRelationGraph<E, R extends IInvertibleRelationType> extends Collection<E> {

	/**
	 * Return the subgraph with only the given nodes of this graph. This subgraph is
	 * a *view* of the main graph, and reflects changes made to it. Similarly, a
	 * modifiable version of this graph should modify the parent.
	 * 
	 * @param nodes
	 * @return
	 */
	public IRelationGraph<E, R> subgraph(Collection<? extends E> nodes);

	/**
	 * Get the value of a specified property on a specified edge. Return null if no
	 * such property was assigned for this edge. Return default value from
	 * {@link #edgeProperties} if no such property was assigned but a default value
	 * is available. The returned item may not be represented in the actual graph
	 * 
	 * @param <E>
	 * @param one
	 * @param type
	 * @param two
	 * @param prop
	 * @return
	 */
	<X> X getProperty(E one, R type, E two, EdgeProperty<X> prop);

	/**
	 * For each edge between these two nodes, run Get on it for the given property--
	 * the input of Get is the current value of the property for the edge we
	 * iterated to
	 * 
	 * @param one
	 * @param two
	 * @param prop
	 * @param getSet
	 */
	<X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Consumer<X> get);

	/**
	 * For each edge connecting from this node to any other, run Get on it for the
	 * given property-- the input of Get is the current value of the property for
	 * the edge we iterated to
	 * 
	 * @param one
	 * @param two
	 * @param prop
	 * @param getSet
	 */
	<X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Consumer<X> get);

	/**
	 * For each edge from this node of the given type, run Get on it for the given
	 * property-- the input of getSet is the current value of the property for the
	 * edge we iterated to
	 * 
	 * @param one
	 * @param two
	 * @param prop
	 * @param getSet
	 */
	<X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Consumer<X> get);

	/**
	 * Gets all nodes which are connected to this one
	 * 
	 * @param node
	 * @return
	 */
	Collection<E> getNeighbors(E node);

	/**
	 * Gets all nodes which are connected to this one by the given type
	 * 
	 * @param node
	 * @return
	 */
	Collection<E> getNeighbors(E node, R type);

	/**
	 * Whether any edges exist between these two
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	boolean containsEdge(Object one, Object two);

	/**
	 * If an edge of this type exists between these nodes
	 * 
	 * @param one
	 * @param type
	 * @param two
	 * @return
	 */
	boolean containsEdge(Object one, R type, Object two);

	/**
	 * Number of edges connecting to/from this node
	 * 
	 * @param node
	 * @return
	 */
	int degree(E node);

	/**
	 * Number of edges of given type connecting to/from this node
	 * 
	 * @param node
	 * @return
	 */
	int degree(E node, R type);

	/**
	 * If this node has any outgoing connections of the given type; throw exception
	 * if node is nonexistent
	 * 
	 * @param one
	 * @param type
	 * @return
	 */
	boolean nodeHasConnections(Object one, R type);

	/**
	 * Return a collection of all the edge types between the given two edges
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	Collection<R> getEdgeTypesBetween(E one, E two);

	/**
	 * Return all types of edges that connect from this node to other nodes
	 * 
	 * @param node
	 * @return
	 */
	Collection<R> getConnectingEdgeTypes(E node);

	/**
	 * Number of edges in graph
	 * 
	 * @return
	 */
	int edgeCount();

	/**
	 * Return all nodes as an immutable collection
	 * 
	 * @return
	 */
	public Collection<E> getNodesImmutable();

	/**
	 * Return an iterable of all bare nodes in this graph
	 * 
	 * @return
	 */
	public Iterable<E> getBareNodes();

	/**
	 * Return an iterator over edges in this graph
	 * 
	 * @return
	 */
	Iterator<Triplet<E, R, E>> edgeIterator();

	/**
	 * Returns a string showing every connection in this graph
	 * 
	 * @return
	 */
	String representation();

	/**
	 * Traverse graph via BFS using only allowed edges, and return spanning tree of
	 * visited nodes and edges as a graph. A for-each function to do something on
	 * each node
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	IRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject);

	/**
	 * Traverse graph via DFS using only allowed edges, and return spanning tree of
	 * visited nodes and edges as a graph. A for-each function to do something on
	 * each node
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	IRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * nodes via BFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * nodes via DFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * edges as triplets via BFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * edges as triplets via DFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject);

	/**
	 * Make copy of this graph
	 * 
	 * @return
	 */
	public IRelationGraph<E, R> copy();

	/**
	 * Copy this graph and also try to make copies of the values of each node using
	 * the given function.
	 * 
	 * @return
	 */
	public IRelationGraph<E, R> deepCopy(Function<E, E> cloner);

}
