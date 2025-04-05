package _utilities.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import _utilities.couplets.Triplet;
import _utilities.property.IProperty;

/**
 * An interface representing relations-graph with no writing functionality
 * 
 * @author borah
 *
 * @param <E>
 * @param <R>
 */
public interface IRelationGraph<E, R extends IInvertibleRelationType> extends Set<E> {

	/**
	 * Return the subgraph with only the given nodes of this graph. This subgraph is
	 * a *view* of the main graph, and reflects changes made to it. Similarly, a
	 * modifiable version of this graph should modify the parent.
	 * 
	 * @param nodes
	 * @return
	 */
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes);

	/**
	 * Return the subgraph with only the given nodes of this graph and edges only
	 * selected by the given condition. This subgraph is a *view* of the main graph,
	 * and reflects changes made to it, Similarly, a modifiable version of this
	 * graph should modify the parent.
	 * 
	 * @param nodes
	 * @return
	 */
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred);

	/**
	 * Return the subgraph with only nodes selected by the given condition and edges
	 * only selected by the given condition. This subgraph is a *view* of the main
	 * graph, and reflects changes made to it, INCLUDING new nodes being added to
	 * the main graph that fit this condition. However, the subbgraph must ALSO
	 * retain a record of the nodes which are added to it that DON'T fit the
	 * condition. Similarly, a modifiable version of this graph should modify the
	 * parent.
	 * 
	 * @param nodes
	 * @return
	 */
	public IRelationGraph<E, R> subgraph(Predicate<? super E> nodes, Predicate<Triplet<E, R, E>> edgePred);

	/**
	 * Only relevant for subgraphs; this removes a node from a subgraph's
	 * representation, but not from a full and proper graph
	 */
	public default boolean subgraphRemove(E node) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a mapped VIEW of this graph, but not a copy. Changes made to this
	 * graph will be reflected in the mapped view, and changes to the map view will
	 * be applied to this graph using the given mapping functions
	 * 
	 * @param <E2>
	 * @param <R2>
	 * @param nodeExToIn
	 * @param nodeInToEx
	 * @param nodeClass
	 * @param innerNodeClass
	 * @param edgeExToIn
	 * @param edgeInToEx
	 * @param edgeClass
	 * @param innerEdgeClass
	 * @return
	 */
	public default <E2, R2 extends IInvertibleRelationType> IRelationGraph<E2, R2> mappedView(
			Function<E2, E> nodeExToIn, Function<E, E2> nodeInToEx, Class<? super E2> nodeClass,
			Class<? super E> innerNodeClass, Function<R2, R> edgeExToIn, Function<R, R2> edgeInToEx,
			Class<? super R2> edgeClass, Class<? super R> innerEdgeClass) {
		return new MappedGraphView<>(this, nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn, edgeInToEx,
				edgeClass, innerEdgeClass);
	}

	/**
	 * Equivalent to
	 * {@link #mappedView(Function, Function, Class, Class, Function, Function, Class, Class)}
	 * but without the Classes, to lighten the expectation a bit. Note that if you
	 * try to run "remove," "contains," and so on on unfit objects, a
	 * classcastexception may be thrown
	 * 
	 * @param <E2>
	 * @param <R2>
	 * @param nodeExToIn
	 * @param nodeInToEx
	 * @param edgeExToIn
	 * @param edgeInToEx
	 * @return
	 */
	public default <E2, R2 extends IInvertibleRelationType> IRelationGraph<E2, R2> mappedView(
			Function<E2, E> nodeExToIn, Function<E, E2> nodeInToEx, Function<R2, R> edgeExToIn,
			Function<R, R2> edgeInToEx) {

		return new MappedGraphView<>(this, nodeExToIn, nodeInToEx, edgeExToIn, edgeInToEx);
	}

	/**
	 * Get the value of a specified property on a specified edge. Return null if no
	 * such property was assigned for this edge or if edge is nonexistent. Return
	 * default value from {@link #edgeProperties} if no such property was assigned
	 * but a default value is available. The returned item may not be represented in
	 * the actual graph
	 * 
	 * @param <E>
	 * @param one
	 * @param type
	 * @param two
	 * @param prop
	 * @return
	 */
	<X> X getProperty(E one, R type, E two, IProperty<X> prop);

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
	<X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get);

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
	<X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get);

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
	<X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get);

	/**
	 * Gets all nodes which are connected to this one
	 * 
	 * @param node
	 * @return
	 */
	Set<E> getNeighbors(E node);

	/**
	 * Gets all nodes which are connected to this one by the given type
	 * 
	 * @param node
	 * @return
	 */
	Set<E> getNeighbors(E node, R type);

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
	boolean containsEdge(Object one, Object type, Object two);

	/**
	 * Number of edges connecting to/from this node. Return 0 for nodes that don't
	 * exist.
	 * 
	 * @param node
	 * @return
	 */
	int degree(E node);

	/**
	 * Number of edges of given type connecting to/from this node. Return 0 for
	 * nodes that don't exist.
	 * 
	 * @param node
	 * @return
	 */
	int degree(E node, R type);

	/**
	 * If this node has any outgoing connections of the given type; return false if
	 * node is nonexistent
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
	Set<R> getEdgeTypesBetween(E one, E two);

	/**
	 * Return all types of edges that connect from this node to other nodes
	 * 
	 * @param node
	 * @return
	 */
	Set<R> getOutgoingEdgeTypes(E node);

	/**
	 * Number of edges in graph
	 * 
	 * @return
	 */
	int edgeCount();

	/**
	 * Return all nodes as an immutable set
	 * 
	 * @return
	 */
	public Set<E> getNodeSetImmutable();

	/**
	 * Return an iterable of all bare nodes in this graph
	 * 
	 * @return
	 */
	public Iterable<E> getBareNodes();

	/**
	 * This method is used for stability purposes; if an object is given to this
	 * function which matches an item in this graph, then this will return the item
	 * in the graph as it is stored in the graph; otherwise it will return null.
	 * This allows you to create a dummy object that matches an item in the graph to
	 * access graph functionality, but still be able to access the actual item in
	 * the graph
	 * 
	 * @param of
	 * @return
	 */
	public E get(Object of);

	/**
	 * Return an iterator over edges in this graph that should permit removal
	 * operations (if possible)
	 * 
	 * @return
	 */
	Iterator<Triplet<E, R, E>> edgeIterator();

	/**
	 * Same as {@link #edgeIterator()}, but takes in a collection of relation types
	 * to construct an iterator that only iterates those relations
	 * 
	 * @param forTypes
	 * @return
	 */
	public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes);

	/**
	 * Returns an iterator for the edges coming out of the given node
	 * 
	 * @param forNode
	 * @return
	 */
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode);

	/**
	 * Returns an iterator for the edges coming out of the given node
	 * 
	 * @param forNode
	 * @return
	 */
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode, R type);

	/**
	 * Returns a string showing every connection in this graph
	 * 
	 * @return
	 */
	String representation();

	/**
	 * Returns a string showing every connection in this graph; each item is passed
	 * through the given function so that it might have a more complex
	 * representation as well
	 * 
	 * @param converter
	 * @return
	 */
	String representation(Function<E, String> converter);

	/**
	 * Returns a string showing every connection in this graph; each item is passed
	 * through the given function, and each edgeType through the other function, so
	 * that it might have a more complex representation as well
	 * 
	 * @param converter
	 * @return
	 */
	String representation(Function<E, String> converter, Function<R, String> edgeConverter);

	/**
	 * Returns a string showing every connection in this graph; each item is passed
	 * through the given function, and each edge and its properties through the
	 * other function, so that it might have a more complex representation as well
	 * 
	 * @param converter
	 * @return
	 */
	String representation(Function<E, String> converter,
			BiFunction<Triplet<E, R, E>, Map<IProperty<?>, Object>, String> edgeConverter);

	/**
	 * Traverse graph via BFS using only allowed edges, and return spanning tree of
	 * visited nodes and edges as a graph. A for-each function to do something on
	 * each node. Traversal should return a copy, not subgraph
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	IRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<IProperty<?>, Object> applyAcrossObject);

	/**
	 * Traverse graph via DFS using only allowed edges, and return spanning tree of
	 * visited nodes and edges as a graph. A for-each function to do something on
	 * each node. Traversal should return a copy, nto subgraph.
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	IRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<IProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * nodes via BFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * nodes via DFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * edges as triplets via BFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject);

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * edges as triplets via DFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject);

	/**
	 * Make copy of this graph
	 * 
	 * @return
	 */
	public IRelationGraph<E, R> copy();

	/**
	 * Returns if this graph can be edited
	 * 
	 * @return
	 */
	public default boolean isEditable() {
		return this instanceof IModifiableRelationGraph;
	}

	/**
	 * Returns an independent copy of this graph that is editable. For a graph which
	 * is already editable, this behaves identical to {@link #copy()}
	 * 
	 * @return
	 */
	public default IModifiableRelationGraph<E, R> editableCopy() {
		return new RelationGraph<>(this);
	}

	/**
	 * If this is an editable graph, return this graph, but casted to an editable
	 * form
	 * 
	 * @return
	 */
	public default IModifiableRelationGraph<E, R> editable() {
		if (this.isEditable()) {
			return (IModifiableRelationGraph<E, R>) this;
		}
		throw new IllegalStateException("Non-editable");
	}

	/**
	 * If this is an editable graph, return this graph, but casted to an editable
	 * form; otherwise, return an editable copy
	 * 
	 * @return
	 */
	public default IModifiableRelationGraph<E, R> editableOrCopy() {
		if (this.isEditable()) {
			return (IModifiableRelationGraph<E, R>) this;
		}
		return this.editableCopy();
	}

	/** An editable copy formed using the given mapping */
	public default <E2, R2 extends IInvertibleRelationType> IModifiableRelationGraph<E2, R2> mappedEditableCopy(
			Function<E, E2> nodeMapper, Function<R, R2> edgeMapper) {
		return this.mappedView((e) -> (E) e, nodeMapper, (r) -> (R) r, edgeMapper).editableCopy();
	}

	/**
	 * Copy this graph and also try to make copies of the values of each node using
	 * the given function.
	 * 
	 * @return
	 */
	public IRelationGraph<E, R> deepCopy(Function<E, E> cloner);

	/**
	 * copy this graph and also map each of the elements and edge types in it using
	 * the two given mapping functions
	 * 
	 * @param <E2>
	 * @param <R2>
	 * @param nodeMapper
	 * @param edgeMapper
	 * @return
	 */
	public <E2, R2 extends IInvertibleRelationType> IRelationGraph<E2, R2> mapCopy(Function<E, E2> nodeMapper,
			Function<R, R2> edgeMapper);

	/**
	 * Return a string representation of the given edge, including all properties as
	 * well.
	 * 
	 * @param first
	 * @param second
	 * @param third
	 * @param includeEnds whether to just print the edge and its properties or to
	 *                    also include the endpoints when printing
	 * @return
	 */
	public String edgeToString(E first, R second, E third, boolean includeEnds);

	/**
	 * Return a string representation of the given edge, including all properties as
	 * well. Throw an error if edge does not exist
	 * 
	 * @param first
	 * @param second
	 * @param third
	 * @param includeEnds whether to just print the edge and its properties or to
	 *                    also include the endpoints when printing
	 * @return
	 */
	public default String edgeToString(Triplet<E, R, E> trip, boolean includeEnds) {
		return edgeToString(trip.getFirst(), trip.getSecond(), trip.getThird(), includeEnds);
	}

	/** All existing types of edges in this graph */
	public Collection<R> getEdgeTypes();

}
