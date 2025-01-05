package utilities.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import utilities.MathUtils;
import utilities.collections.ImmutableCollection;
import utilities.couplets.Pair;
import utilities.couplets.Triplet;

/**
 * 
 * @author borah
 *
 * @param <E> the type of node in the graph
 * @param <R> the type of edge-relation in the graph
 */
public class RelationGraph<E, R extends IInvertibleRelationType> implements IModifiableRelationGraph<E, R> {

	protected interface INode<NType, RType extends IInvertibleRelationType> {
		/**
		 * Get value of this node
		 * 
		 * @return
		 */
		public NType getValue();

		/**
		 * Get all edges connected to this node
		 * 
		 * @return
		 */
		public Collection<? extends IInvertibleEdge<NType, RType>> getAllEdges();

		/**
		 * Get all edges of the given type connected to this node
		 * 
		 * @param rel
		 * @return
		 */
		public Collection<? extends IInvertibleEdge<NType, RType>> getEdgesOfType(RType rel);

		/**
		 * Get all edges to the given node
		 * 
		 * @param node
		 * @return
		 */
		public Collection<? extends IInvertibleEdge<NType, RType>> getAllEdgesTo(NType node);

		public boolean hasEdgeTo(Object node);

		public boolean hasEdgeTo(Object node, RType rel);

		/**
		 * Get the kinds of edges between these two nodes
		 * 
		 * @param node
		 * @return
		 */
		public Collection<RType> getEdgeTypesTo(NType node);

		/**
		 * Get the kinds of edges connecting from this node
		 * 
		 * @param node
		 * @return
		 */
		public Collection<RType> getEdgeTypes();

		/**
		 * Get the data points connected to this one by edges
		 * 
		 * @param node
		 * @return
		 */
		public Collection<NType> getNeighborNodes();

		/**
		 * Get the data points connected to this one by edges of the given type
		 * 
		 * @param edge
		 * @return
		 */
		public Collection<NType> getNeighborNodes(RType edge);

		/**
		 * Get the edge of the given type connecting to the given node
		 * 
		 * @param to
		 * @param type
		 * @return
		 */
		public IInvertibleEdge<NType, RType> getEdge(NType to, RType type);

		/**
		 * Join an edge to this node; treat this node as the Start node <br>
		 * Return an edge if one was replaced
		 * 
		 * @param edge
		 * @return
		 */
		public IInvertibleEdge<NType, RType> joinEdge(IInvertibleEdge<NType, RType> edge);

		/**
		 * Remove (and return) the edge of the given type to the given node
		 * 
		 * @param to
		 * @param type
		 * @return
		 */
		public IInvertibleEdge<NType, RType> removeEdge(NType to, RType type);

		/**
		 * Remove all edges and return true if successful
		 * 
		 * @return
		 */
		public Collection<? extends IInvertibleEdge<NType, RType>> removeAllEdges();

		/**
		 * Remove all edges of given type and return true if successful
		 * 
		 * @return
		 */
		public Collection<? extends IInvertibleEdge<NType, RType>> removeAllEdges(RType rel);

		/**
		 * Remove all edges to given node and return true if successful
		 * 
		 * @return
		 */
		public Collection<? extends IInvertibleEdge<NType, RType>> removeAllEdges(NType node);

	}

	protected interface IInvertibleEdge<NType, RType extends IInvertibleRelationType> {

		public INode<NType, RType> getStart();

		public INode<NType, RType> getEnd();

		public RType getType();

		/**
		 * Return the inverted version of this edge
		 * 
		 * @return
		 */
		public IInvertibleEdge<NType, RType> invert();

		/**
		 * Return the property of this edge with the given key
		 * 
		 * @param <E>
		 * @param prop
		 * @return
		 */
		public <E> E getPropertyValue(EdgeProperty<E> prop);

		/**
		 * Set a property value of this edge
		 * 
		 * @param <E>
		 * @param prop
		 * @param value
		 */
		public <E> void setPropertyValue(EdgeProperty<E> prop, E value);

		/**
		 * If this edge has an instance of this property
		 * 
		 * @param prop
		 * @return
		 */
		public <E> boolean hasProperty(EdgeProperty<E> prop);

		/**
		 * Return a collection of the properties in this edge
		 * 
		 * @return
		 */
		public Set<EdgeProperty<?>> getProperties();

		/**
		 * Whether this edge is a juncture or not <br>
		 * TODO figure out junctures or a workaround
		 * 
		 * @return
		 */
		public boolean isJuncture();

		/**
		 * Whether this edge represents only the inversion of another edge
		 * 
		 * @return
		 */
		public boolean isInverse();

		/**
		 * Create a triplet from this pair of (first, type, second)
		 * 
		 * @return
		 */
		public default Triplet<NType, RType, NType> asTriplet() {
			return Triplet.of(getStart().getValue(), getType(), getEnd().getValue());
		}

	}

	private class Edge implements IInvertibleEdge<E, R> {

		private INode<E, R> start;
		private INode<E, R> end;
		private R type;
		private Map<EdgeProperty<?>, Object> properties;
		private InvertedEdge invert;

		private Edge(R type, INode<E, R> start, INode<E, R> end) {
			this.start = start;
			this.end = end;
			this.type = type;
			this.properties = new HashMap<>(edgeProperties.size());
			this.invert = new InvertedEdge();
		}

		@Override
		public INode<E, R> getEnd() {
			return end;
		}

		@Override
		public INode<E, R> getStart() {
			return start;
		}

		@Override
		public InvertedEdge invert() {
			return invert;
		}

		@Override
		public boolean isInverse() {
			return false;
		}

		@Override
		public <E> E getPropertyValue(EdgeProperty<E> prop) {
			return (E) properties.get(prop);
		}

		@Override
		public <F> void setPropertyValue(EdgeProperty<F> prop, F value) {
			properties.put(prop, value);
		}

		@Override
		public Set<EdgeProperty<?>> getProperties() {
			return this.properties.keySet();
		}

		@Override
		public <X> boolean hasProperty(EdgeProperty<X> prop) {
			return this.properties.containsKey(prop);
		}

		@Override
		public R getType() {
			return type;
		}

		@Override
		public boolean isJuncture() {
			return false;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IInvertibleEdge edge) {
				return this.start.getValue().equals(edge.getStart().getValue())
						&& this.end.getValue().equals(edge.getEnd().getValue()) && this.type.equals(edge.getType());
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return (this.start.hashCode() + this.end.hashCode()) * this.type.hashCode();
		}

		@Override
		public String toString() {
			return "(" + this.getStart().getValue() + ")>==[" + this.getType() + "]"
					+ (properties.isEmpty() ? "" : properties) + "==>(" + this.getEnd().getValue() + ")";
		}

		private class InvertedEdge implements IInvertibleEdge<E, R> {

			@Override
			public INode<E, R> getStart() {
				return Edge.this.end;
			}

			@Override
			public INode<E, R> getEnd() {
				return Edge.this.start;
			}

			@Override
			public Edge invert() {
				return Edge.this;
			}

			@Override
			public boolean isInverse() {
				return true;
			}

			@Override
			public <X> X getPropertyValue(EdgeProperty<X> prop) {
				return Edge.this.getPropertyValue(prop);
			}

			@Override
			public <X> void setPropertyValue(EdgeProperty<X> prop, X value) {
				Edge.this.setPropertyValue(prop, value);
			}

			@Override
			public Set<EdgeProperty<?>> getProperties() {
				return Edge.this.getProperties();
			}

			@Override
			public <X> boolean hasProperty(EdgeProperty<X> prop) {
				return Edge.this.hasProperty(prop);
			}

			@Override
			public R getType() {
				return (R) Edge.this.type.invert();
			}

			@Override
			public boolean isJuncture() {
				return false;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof IInvertibleEdge edge) {
					return this.getStart().getValue().equals(edge.getStart().getValue())
							&& this.getEnd().getValue().equals(edge.getEnd().getValue())
							&& this.getType().equals(edge.getType());
				}
				return super.equals(obj);
			}

			@Override
			public int hashCode() {
				return (this.getStart().hashCode() + this.getEnd().hashCode()) * this.getType().hashCode();
			}

			@Override
			public String toString() {
				return "(" + this.getStart().getValue() + ")<==[" + this.getType() + "]"
						+ (properties.isEmpty() ? "" : properties) + "==<(" + this.getEnd().getValue() + ")";
			}

		}
	}

	private class Node implements INode<E, R> {

		private E value;
		private Table<R, E, IInvertibleEdge<E, R>> edges;

		public Node(E value) {
			this.value = value;
			this.edges = HashBasedTable.create();
		}

		public E getValue() {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof INode n) {
				return this.value.equals(n.getValue());
			} else {
				return this.value.equals(obj);
			}
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public Collection<? extends IInvertibleEdge<E, R>> getAllEdges() {
			return edges.values();
		}

		@Override
		public Collection<? extends IInvertibleEdge<E, R>> getEdgesOfType(R rel) {
			return edges.row(rel).values();
		}

		@Override
		public Collection<? extends IInvertibleEdge<E, R>> getAllEdgesTo(E node) {
			return edges.column(node).values();
		}

		@Override
		public boolean hasEdgeTo(Object node) {
			return edges.containsColumn(node);
		}

		@Override
		public boolean hasEdgeTo(Object node, R rel) {
			return edges.contains(rel, node);
		}

		@Override
		public IInvertibleEdge<E, R> getEdge(E to, R type) {
			return edges.get(type, to);
		}

		@Override
		public Collection<R> getEdgeTypesTo(E node) {
			return edges.column(node).keySet();
		}

		@Override
		public Collection<R> getEdgeTypes() {
			return edges.rowKeySet();
		}

		@Override
		public Collection<E> getNeighborNodes() {
			return edges.columnKeySet();
		}

		@Override
		public Collection<E> getNeighborNodes(R edge) {
			return edges.row(edge).keySet();
		}

		@Override
		public IInvertibleEdge<E, R> joinEdge(IInvertibleEdge<E, R> edge) {
			if (!this.equals(edge.getStart())) {
				throw new IllegalArgumentException("Ill-constructed edge " + edge);
			}
			E key = edge.getEnd().getValue();
			R rel = edge.getType();
			return edges.put(rel, key, edge);
		}

		@Override
		public IInvertibleEdge<E, R> removeEdge(E to, R type) {
			return edges.remove(type, to);
		}

		@Override
		public Collection<IInvertibleEdge<E, R>> removeAllEdges() {
			Collection<IInvertibleEdge<E, R>> size = new HashSet<>(edges.values());
			edges.clear();
			return size;
		}

		@Override
		public Collection<IInvertibleEdge<E, R>> removeAllEdges(E node) {
			Map<R, IInvertibleEdge<E, R>> mapa = edges.column(node);
			Collection<IInvertibleEdge<E, R>> size = new HashSet<>(mapa.size());
			mapa.clear();
			return size;
		}

		@Override
		public Collection<IInvertibleEdge<E, R>> removeAllEdges(R rel) {
			Map<E, IInvertibleEdge<E, R>> mapa = edges.row(rel);
			Collection<IInvertibleEdge<E, R>> size = new HashSet<>(mapa.size());
			mapa.clear();
			return size;
		}

		@Override
		public String toString() {
			return "(" + this.value + ")";
		}

	}

	private Map<EdgeProperty<?>, Supplier<Object>> edgeProperties;
	private Map<E, INode<E, R>> V;
	private Set<IInvertibleEdge<E, R>> E;
	private WeakHashMap<SubGraphView, Boolean> weaks;

	public RelationGraph() {
		this.edgeProperties = Collections.emptyMap();
		this.V = new HashMap<>();
		this.E = new HashSet<>();
		this.weaks = new WeakHashMap<>();
	}

	/**
	 * 
	 * @param edgeProperties map of property -> supplier of default value that every
	 *                       edge should return if it has not initialized the
	 *                       property
	 */
	public RelationGraph(Map<EdgeProperty<?>, Supplier<Object>> edgeProperties) {
		this.edgeProperties = ImmutableMap.copyOf(edgeProperties);
		this.V = new HashMap<>();
		this.E = new HashSet<>();

	}

	/**
	 * Add a node to this graph with the given value
	 */
	@Override
	public boolean add(E e) {
		INode<E, R> node = this.V.get(e);
		if (node == null) {
			node = new Node(e);
			this.V.put(e, node);
			return true;
		}
		return false;
	}

	/**
	 * Return false and do nothing if there is already an edge
	 * 
	 * @param node1
	 * @param type
	 * @param node2
	 * @return
	 */
	protected boolean addNewEdgeOrNothing(INode<E, R> node1, R type, INode<E, R> node2) {
		if (node1.getValue().equals(node2.getValue())) {
			String nodee = node1.toString();
			String nodea = node2.toString();
			throw new IllegalArgumentException(
					"Cannot connect " + node1 + " to itself" + (nodee.equals(nodea) ? "" : " (" + node2 + ")"));
		}
		if (node1.getEdge(node2.getValue(), type) != null) {
			return false;
		}
		Edge edge = new Edge(type, node1, node2);
		node1.joinEdge(edge);
		node2.joinEdge(edge.invert());
		this.E.add(edge);
		return true;

	}

	/**
	 * Copy this edge into the graph; return the copied edge
	 * 
	 * @param edge
	 */
	@SuppressWarnings("unchecked")
	protected IInvertibleEdge<E, R> connectEdge(IInvertibleEdge<E, R> edge) {
		INode<E, R> start = V.get(edge.getStart().getValue());
		INode<E, R> end = V.get(edge.getEnd().getValue());
		if (start == null || end == null) {
			throw new NodeNotFoundException("Nonexistent node: " + edge.getStart() + " or " + edge.getEnd());
		}
		IInvertibleEdge<E, R> newedge = new Edge(edge.getType(), start, end);
		start.joinEdge(newedge);
		end.joinEdge(newedge.invert());

		for (@SuppressWarnings("rawtypes")
		EdgeProperty prop : edge.getProperties()) {
			newedge.setPropertyValue(prop, edge.getPropertyValue(prop));
		}

		this.E.add(newedge);
		return newedge;
	}

	@Override
	public boolean addEdge(E first, R type, E second) {
		return addNewEdgeOrNothing(node(first, 1), type, node(second, 2));
	}

	/**
	 * Add new edge. Return true if there was no existing edge. Return false and do
	 * nothing if there is an existing edge
	 * 
	 * @param first
	 * @param type
	 * @param second
	 * @return
	 */
	public boolean addEdge(Triplet<E, R, E> triplet) {
		return addEdge(triplet.getFirst(), triplet.getSecond(), triplet.getThird());
	}

	/**
	 * Get a node or throw an exception
	 */
	protected INode<E, R> node(Object value) {
		INode<E, R> node = V.get(value);
		if (node == null) {
			throw new NodeNotFoundException(value);
		}
		return node;
	}

	/**
	 * 
	 * same as {@link #node(Object)} but allows to specify which ordinal when using
	 * multiple arguments. 0 ordinal does not include text
	 */
	protected INode<E, R> node(Object value, int ordinal) {
		INode<E, R> node = V.get(value);
		if (node == null) {
			throw new NodeNotFoundException(value, ordinal);
		}
		return node;
	}

	@Override
	public boolean removeAllConnections(Object value) {
		return removeConnections(node(value)).size() != 0;
	}

	@Override
	public boolean removeAllConnections(Object value, R type) {
		return removeConnections(node(value), type).size() != 0;
	}

	@Override
	public boolean removeAllConnections(Object value, Object other) {
		return removeConnections(node(value, 1), node(other, 2)).size() != 0;
	}

	@Override
	public boolean removeEdge(Object value, R type, Object other) {
		return removeConnection(node(value, 1), type, node(other, 2)) != null;
	}

	/**
	 * Remove the edge of the given type between these nodes, return true if an edge
	 * was removed at all
	 * 
	 * @param value
	 * @param type
	 * @param other
	 * @return
	 */
	public boolean removeEdge(Triplet<E, R, E> triplet) {
		return removeEdge(triplet.getFirst(), triplet.getSecond(), triplet.getThird());
	}

	/**
	 * Disconnect every edge in this given collection from the node at its end()
	 * point. Throw IllegalStateException if an edge could not be pruned
	 */
	protected void disconnectEnds(Iterable<? extends IInvertibleEdge<E, R>> edges) {
		for (IInvertibleEdge<E, R> edge : edges) {
			IInvertibleEdge<? extends E, R> edga = edge.getEnd().removeEdge(edge.getStart().getValue(),
					(R) edge.getType().invert());
			if (edga == null) {
				throw new IllegalStateException(
						"Tried to remove " + edge + " from " + edge.getEnd() + " but was not connected");
			}
		}
	}

	/**
	 * Remove and return all edges from a node
	 * 
	 * @param node
	 * @return
	 */
	protected Collection<? extends IInvertibleEdge<E, R>> removeConnections(INode<E, R> node) {
		Collection<? extends IInvertibleEdge<E, R>> edges = node.removeAllEdges();
		disconnectEnds(edges);
		if (!E.removeAll(edges)) {
			throw new IllegalArgumentException("Edgelist of graph does not contain all edges from " + node + ": \n"
					+ edges + "\nare not all contained in\n" + E);
		}
		return edges;
	}

	/**
	 * Remove and return all edges from a node of a given type
	 * 
	 * @param node
	 * @return
	 */
	protected Collection<? extends IInvertibleEdge<E, R>> removeConnections(INode<E, R> node, R type) {
		Collection<? extends IInvertibleEdge<E, R>> edges = node.removeAllEdges(type);
		disconnectEnds(edges);
		if (!E.removeAll(edges)) {
			throw new IllegalArgumentException("Edgelist of graph does not contain all edges from " + node + ": \n"
					+ edges + "\nare not all contained in\n" + E);
		}
		return edges;
	}

	/**
	 * Remove and return all connections between two nodes
	 * 
	 * @param node
	 * @param other
	 * @return
	 */
	protected Collection<? extends IInvertibleEdge<E, R>> removeConnections(INode<E, R> node, INode<E, R> other) {
		Collection<? extends IInvertibleEdge<E, R>> edges = node.removeAllEdges(other.getValue());
		disconnectEnds(edges);
		if (!E.removeAll(edges)) {
			throw new IllegalArgumentException("Edgelist of graph does not contain all edges from " + node + ": \n"
					+ edges + "\nare not all contained in\n" + E);
		}
		return edges;
	}

	/**
	 * Remove a specific edge from the graph
	 * 
	 * @param node
	 * @param type
	 * @param other
	 * @return
	 */
	protected IInvertibleEdge<E, R> removeConnection(INode<E, R> node, R type, INode<E, R> other) {
		IInvertibleEdge<E, R> edge = node.removeEdge(other.getValue(), type);
		IInvertibleEdge<E, R> check = other.removeEdge(node.getValue(), (R) type.invert());
		if (check == null && edge != null) {
			throw new IllegalStateException(
					"Node " + node + " connected to " + other + " by " + edge + " but not vice versa");
		}
		if (edge == null && check != null) {
			throw new IllegalStateException(
					"Node " + other + " connected to " + node + " by " + check + " but not vice versa");
		}
		if (edge != null && !E.remove(edge)) {
			throw new IllegalStateException(
					"Node " + node + " has edge " + edge + " but graph edgelist does not have this edge");
		}
		return edge;
	}

	/**
	 * Remove the node with the given value
	 */
	@Override
	public boolean remove(Object o) {
		INode<E, R> node = V.remove(o);
		for (SubGraphView view : this.weaks.keySet()) {
			view.nodes.remove(o);
		}
		if (node == null) {
			return false;
		} else {
			removeConnections(node);
			return true;
		}
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean mod = false;
		for (E e : c) {
			INode<E, R> node = V.get(e);
			if (node == null) {
				node = new Node(e);
				V.put(e, node);
				mod = true;
			}
		}
		return mod;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean mod = false;
		for (Object o : c) {
			mod |= this.remove(o);
		}
		return mod;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Iterator<INode<E, R>> nodeI = V.values().iterator();
		boolean mod = false;
		while (nodeI.hasNext()) {
			INode<E, R> node = nodeI.next();
			if (!c.contains(node.getValue())) {
				nodeI.remove();
				for (SubGraphView view : this.weaks.keySet()) {
					view.nodes.remove(node.getValue());
				}
				removeConnections(node);
				mod = true;
			}
		}
		return mod;
	}

	@Override
	public void clear() {
		V.clear();
		E.clear();
		for (SubGraphView view : this.weaks.keySet()) {
			view.nodes.clear();
		}
	}

	@Override
	public int size() {
		return V.size();
	}

	@Override
	public boolean isEmpty() {
		return V.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return V.containsKey(o);
	}

	@Override
	public Collection<E> getNodesImmutable() {
		return ImmutableCollection.from(V.keySet());
	}

	@Override
	public Iterable<E> getBareNodes() {
		return (Iterable<E>) () -> V.values().stream().filter((a) -> a.getAllEdges().isEmpty()).map((a) -> a.getValue())
				.iterator();
	}

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
	public <X> X getProperty(Triplet<E, R, E> triplet, EdgeProperty<X> prop, boolean computeIfAbsent) {
		return getProperty(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), prop, computeIfAbsent);
	}

	@Override
	public <X> X getProperty(E one, R type, E two, EdgeProperty<X> prop) {
		return getProperty(one, type, two, prop, false);
	}

	@Override
	public <X> X getProperty(E one, R type, E two, EdgeProperty<X> prop, boolean computeIfAbsent) {
		IInvertibleEdge<E, R> edge = node(one, 1).getEdge(two, type);
		node(two, 2);
		if (edge == null) {
			throw new IllegalArgumentException("No edge of type " + type + " between " + one + " and " + two);
		}
		X obj = edge.getPropertyValue(prop);
		if (obj == null) {
			Supplier<Object> sup = edgeProperties.get(prop);
			if (sup != null) {
				obj = (X) sup.get();
				if (computeIfAbsent) {
					edge.setPropertyValue(prop, obj);
				}
			}
		}
		return obj;
	}

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
	public <X> X setProperty(Triplet<E, R, E> triplet, EdgeProperty<X> prop, X val) {
		return setProperty(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), prop, val);
	}

	@Override
	public <X> X setProperty(E one, R type, E two, EdgeProperty<X> prop, X val) {
		IInvertibleEdge<E, R> edge = node(one, 1).getEdge(two, type);
		node(two, 2);
		if (edge == null) {
			throw new IllegalArgumentException("No edge of type " + type + " between " + one + " and " + two);
		}
		X obj = edge.getPropertyValue(prop);
		edge.setPropertyValue(prop, val);
		return obj;
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Consumer<X> get) {
		this.forEachEdgeProperty(one, two, prop, (a) -> {
			get.accept(a);
			return null;
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Consumer<X> get) {
		this.forEachEdgeProperty(one, prop, (a) -> {
			get.accept(a);
			return null;
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Consumer<X> get) {
		this.forEachEdgeProperty(one, type, prop, (a) -> {
			get.accept(a);
			return null;
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Function<X, X> getSet) {
		Collection<? extends IInvertibleEdge<E, R>> edges = node(one, 1).getAllEdgesTo(two);
		node(two, 2);
		edges.forEach((edge) -> {
			X val = edge.getPropertyValue(prop);
			X output = getSet.apply(val);
			if (output != null && output != val) {
				edge.setPropertyValue(prop, output);
			}
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Function<X, X> getSet) {
		Collection<? extends IInvertibleEdge<E, R>> edges = node(one, 1).getAllEdges();
		edges.forEach((edge) -> {
			X val = edge.getPropertyValue(prop);
			X output = getSet.apply(val);
			if (output != null && output != val) {
				edge.setPropertyValue(prop, output);
			}
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Function<X, X> getSet) {
		Collection<? extends IInvertibleEdge<E, R>> edges = node(one, 1).getEdgesOfType(type);
		edges.forEach((edge) -> {
			X val = edge.getPropertyValue(prop);
			X output = getSet.apply(val);
			if (output != null && output != val) {
				edge.setPropertyValue(prop, output);
			}
		});
	}

	@Override
	public Collection<E> getNeighbors(E node) {
		return this.node(node).getNeighborNodes();
	}

	@Override
	public Collection<E> getNeighbors(E node, R type) {
		return this.node(node).getNeighborNodes(type);
	}

	@Override
	public boolean containsEdge(Object one, Object two) {
		try {
			return node(one).hasEdgeTo(two);
		} catch (NodeNotFoundException e) {
			return false;
		}
	}

	/**
	 * If an edge of this type exists between these nodes
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public boolean containsEdge(Triplet<E, R, E> triplet) {
		return containsEdge(triplet.getFirst(), triplet.getSecond(), triplet.getThird());
	}

	@Override
	public boolean containsEdge(Object one, R type, Object two) {
		try {
			node(two, 2);
			return node(one, 1).hasEdgeTo(two, type);
		} catch (NodeNotFoundException e) {
			return false;
		}
	}

	@Override
	public int degree(E node) {
		return this.node(node).getAllEdges().size();
	}

	@Override
	public int degree(E node, R type) {
		return this.node(node).getEdgesOfType(type).size();
	}

	@Override
	public boolean nodeHasConnections(Object one, R type) {
		return !node(one).getEdgesOfType(type).isEmpty();
	}

	@Override
	public Collection<R> getEdgeTypesBetween(E one, E two) {
		node(two, 2);
		return node(one, 1).getEdgeTypesTo(two);
	}

	@Override
	public Collection<R> getConnectingEdgeTypes(E node) {
		return this.node(node).getEdgeTypes();
	}

	@Override
	public int edgeCount() {
		return E.size();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return this.E.stream().map((a) -> Triplet.of(a.getStart().getValue(), a.getType(), a.getEnd().getValue()))
				.iterator();
	}

	@Override
	public Iterator<E> iterator() {
		return V.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return V.keySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return V.keySet().toArray(a);
	}

	@Override
	public <T> T[] toArray(IntFunction<T[]> generator) {
		return V.keySet().toArray(generator);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return V.keySet().containsAll(c);
	}

	/**
	 * Traverse graph via BFS using only allowed edges, and return spanning tree of
	 * visited nodes and edges as a graph. A for-each function to do something on
	 * each node
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	@Override
	public RelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, true);
		iter.forEachRemaining(forEachNode);
		return iter.visited;

	}

	/**
	 * Traverse graph via DFS using only allowed edges, and return spanning tree of
	 * visited nodes and edges as a graph. A for-each function to do something on
	 * each node
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	@Override
	public RelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, false);
		iter.forEachRemaining(forEachNode);
		return iter.visited;

	}

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * nodes via BFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		return new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, true);
	}

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * nodes via DFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	@Override
	public NodeTraversalIterator nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		return new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, false);
	}

	/**
	 * Traverse graph EDGES via BFS using only allowed edges, and return all visited
	 * edges as triplets. A for-each function to do something on each node
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */

	public RelationGraph<E, R> traverseEdgesBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<Triplet<E, R, E>> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		EdgeTraversalIterator iter = new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, true);
		iter.forEachRemaining(forEachNode);
		return iter.visited;

	}

	/**
	 * Traverse graph edges via DFS using only allowed edges, and return a graph
	 * constructed from the traversed edges. A for-each function to do something on
	 * each edge
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	public RelationGraph<E, R> traverseEdgesDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<Triplet<E, R, E>> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		EdgeTraversalIterator iter = new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, false);
		iter.forEachRemaining(forEachNode);
		return iter.visited;

	}

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * edges as triplets via BFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		return new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, true);
	}

	/**
	 * Return an iterator that traverses all allowed edges and returns all visited
	 * edges as triplets via DFS
	 * 
	 * @param startPoint
	 * @param allowedEdgeTypes
	 * @return
	 */
	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		return new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, false);
	}

	public <X extends Number> RelationGraph<E, R> singleSourceShortestPathDijkstra(E startPoint,
			Collection<? extends R> allowedEdgeTypes, EdgeProperty<X> property) {
		RelationGraph<E, R> visited = new RelationGraph<>();
		Map<E, Double> distances = new HashMap<>();
		Map<E, E> predecessors = new HashMap<>();
		distances.put(startPoint, 0d);
		PriorityQueue<E> unvisited = new PriorityQueue<>((a, b) -> {
			double a1 = distances.getOrDefault(a, Double.POSITIVE_INFINITY);
			double b1 = distances.getOrDefault(b, Double.POSITIVE_INFINITY);
			return Double.compare(a1, b1);
		});
		unvisited.offer(startPoint);
		int i = 0;
		while (!unvisited.isEmpty() && i < V.size()) {
			E u = unvisited.poll();

			for (R r : allowedEdgeTypes) {
				for (E e : getNeighbors(u, r)) {
					if (!visited.contains(e)) {
						unvisited.offer(e);
					}
				}
			}
			// TODO write dijkstra's if needed

			i++;
		}
		return visited;
	}

	public class NodeTraversalIterator implements Iterator<E> {

		private RelationGraph<E, R> visited;
		private ArrayList<Pair<IInvertibleEdge<E, R>, INode<E, R>>> toVisit;
		private Stack<Pair<IInvertibleEdge<E, R>, INode<E, R>>> toVisit2;
		private Collection<? extends R> allowedEdgeTypes;
		private INode<E, R> priorNode;
		private BiPredicate<EdgeProperty<?>, Object> propertyPredicate;
		private Collection<E> allowedNodes;

		public NodeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<EdgeProperty<?>, Object> propertyPredicate, boolean BFS,
				Collection<? extends E> allowedNodes) {
			this(firstNode, allowedEdgeTypes, propertyPredicate, BFS);
			this.allowedNodes = ImmutableSet.copyOf(allowedNodes);
			if (!allowedNodes.contains(firstNode)) {
				throw new IllegalArgumentException(firstNode + " --/--> " + allowedNodes);
			}
		}

		/**
		 * 
		 * @param firstNode
		 * @param allowedEdgeTypes
		 * @param propertyPredicate a predicate to apply to each edge (applied for each
		 *                          property). If this returns false for any property,
		 *                          ignore that edge
		 * @param BFS
		 */
		public NodeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<EdgeProperty<?>, Object> propertyPredicate, boolean BFS) {
			this.visited = new RelationGraph<>();
			this.allowedEdgeTypes = allowedEdgeTypes;
			this.propertyPredicate = propertyPredicate;
			if (BFS) {
				this.toVisit = new ArrayList<>();
				toVisit.add(Pair.of(null, node(firstNode)));
			} else {
				this.toVisit2 = new Stack<>();
				toVisit2.push(Pair.of(null, node(firstNode)));
			}
		}

		/**
		 * Return a view of all already-visited nodes
		 * 
		 * @return
		 */
		public RelationGraph<E, R> getVisited() {
			return visited;
		}

		/**
		 * Removes the current node from graph
		 */
		@Override
		public void remove() {
			if (priorNode == null) {
				throw new IllegalStateException();
			}
			visited.remove(priorNode.getValue());
			RelationGraph.this.remove(priorNode.getValue());
		}

		@Override
		public boolean hasNext() {
			return toVisit == null ? !toVisit2.isEmpty() : !toVisit.isEmpty();
		}

		@Override
		public E next() {
			if (toVisit == null ? toVisit2.isEmpty() : toVisit.isEmpty()) {
				throw new NoSuchElementException();
			}
			Pair<IInvertibleEdge<E, R>, INode<E, R>> pair = toVisit == null ? toVisit2.pop() : toVisit.remove(0);
			INode<E, R> firstNode = pair.getSecond();
			priorNode = firstNode;
			visited.add(firstNode.getValue());
			if (pair.getFirst() != null) {
				visited.addEdge(pair.getFirst().getStart().getValue(), pair.getFirst().getType(),
						pair.getSecond().getValue());
				IInvertibleEdge<E, R> edgenew = visited.node(pair.getFirst().getStart().getValue())
						.getEdge(pair.getFirst().getEnd().getValue(), pair.getFirst().getType());
				for (EdgeProperty prop : pair.getFirst().getProperties()) {
					edgenew.setPropertyValue(prop, pair.getFirst().getPropertyValue(prop));
				}
			}
			for (R type : allowedEdgeTypes) {
				for (IInvertibleEdge<E, R> edge : firstNode.getEdgesOfType(type)) {
					if (!visited.contains(edge.getEnd().getValue())
							&& (allowedNodes == null || allowedNodes.contains(edge.getEnd().getValue()))) {
						boolean propertiesPermitted = true;
						for (EdgeProperty<?> prop : Sets.union(edge.getProperties(),
								RelationGraph.this.edgeProperties.keySet())) {
							if (!propertyPredicate.test(prop, RelationGraph.this.getProperty(edge.getStart().getValue(),
									type, edge.getEnd().getValue(), prop, false))) {
								propertiesPermitted = false;
								break;
							}
						}
						if (!propertiesPermitted)
							continue;
						if (toVisit == null) {
							toVisit2.push(Pair.of(edge, edge.getEnd()));
						} else {
							toVisit.add(Pair.of(edge, edge.getEnd()));
						}
					}
				}
			}
			return firstNode.getValue();
		}

	}

	public class EdgeTraversalIterator implements Iterator<Triplet<E, R, E>> {

		private RelationGraph<E, R> visited;
		private ArrayList<IInvertibleEdge<E, R>> toVisit;
		private Stack<IInvertibleEdge<E, R>> toVisit2;
		private Collection<? extends R> allowedEdgeTypes;
		private IInvertibleEdge<E, R> priorEdge;
		private BiPredicate<EdgeProperty<?>, Object> propertyPredicate;
		private Collection<E> allowedNodes;

		public EdgeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<EdgeProperty<?>, Object> propertyPredicate, boolean BFS) {
			this(firstNode, allowedEdgeTypes, propertyPredicate, BFS, null);
		}

		public EdgeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<EdgeProperty<?>, Object> propertyPredicate, boolean BFS,
				Collection<? extends E> allowedNodes) {
			this.visited = new RelationGraph<>();
			this.allowedEdgeTypes = allowedEdgeTypes;
			this.propertyPredicate = propertyPredicate;

			if (BFS) {
				this.toVisit = new ArrayList<>();
			} else {
				this.toVisit2 = new Stack<>();
			}

			if (allowedNodes != null) {
				if (!allowedNodes.contains(firstNode)) {
					throw new IllegalArgumentException(firstNode + " --/--> " + allowedNodes);
				}
				this.allowedNodes = ImmutableSet.copyOf(allowedNodes);
			}

			INode<E, R> node1 = node(firstNode);

			for (R type : allowedEdgeTypes) {
				for (IInvertibleEdge<E, R> edge : node1.getEdgesOfType(type)) {
					if (allowedNodes != null && !allowedNodes.contains(edge.getEnd().getValue())) {
						continue;
					}
					boolean propertiesPermitted = true;
					for (EdgeProperty<?> prop : Sets.union(edge.getProperties(),
							RelationGraph.this.edgeProperties.keySet())) {
						if (!propertyPredicate.test(prop, RelationGraph.this.getProperty(edge.getStart().getValue(),
								type, edge.getEnd().getValue(), prop, false))) {
							propertiesPermitted = false;
							break;
						}
					}
					if (!propertiesPermitted)
						continue;
					if (toVisit == null) {
						toVisit2.push(edge);
					} else {
						toVisit.add(edge);
					}

				}
			}
		}

		/**
		 * Return a graph of all already-visited nodes
		 * 
		 * @return
		 */
		public RelationGraph<E, R> getVisited() {
			return visited;
		}

		/**
		 * Removes the current node from graph
		 */
		@Override
		public void remove() {
			if (priorEdge == null) {
				throw new IllegalStateException();
			}
			visited.removeConnection(priorEdge.getStart(), priorEdge.getType(), priorEdge.getEnd());
			RelationGraph.this.removeConnection(priorEdge.getStart(), priorEdge.getType(), priorEdge.getEnd());
		}

		@Override
		public boolean hasNext() {
			return toVisit == null ? !toVisit2.isEmpty() : !toVisit.isEmpty();
		}

		@Override
		public Triplet<E, R, E> next() {
			if (toVisit == null ? toVisit2.isEmpty() : toVisit.isEmpty()) {
				throw new NoSuchElementException();
			}
			IInvertibleEdge<E, R> firstEdge = toVisit == null ? toVisit2.pop() : toVisit.remove(0);
			priorEdge = firstEdge;
			Triplet<E, R, E> edgerep = Triplet.of(firstEdge.getStart().getValue(), firstEdge.getType(),
					firstEdge.getEnd().getValue());
			if (!visited.contains(edgerep.getFirst())) {
				visited.add(edgerep.getFirst());
			}
			if (!visited.contains(edgerep.getThird())) {
				visited.add(edgerep.getThird());
			}
			visited.addEdge(edgerep);
			IInvertibleEdge<E, R> edge2 = visited.node(edgerep.getFirst()).getEdge(edgerep.getThird(),
					edgerep.getSecond());
			for (EdgeProperty prop : firstEdge.getProperties()) {
				edge2.setPropertyValue(prop, firstEdge.getPropertyValue(prop));
			}
			for (R type : allowedEdgeTypes) {
				for (IInvertibleEdge<E, R> edge : firstEdge.getEnd().getEdgesOfType(type)) {
					if (allowedNodes != null && !allowedNodes.contains(edge.getEnd().getValue())) {
						continue;
					}
					Triplet<E, R, E> dir1 = edge.asTriplet();
					boolean propertiesPermitted = true;
					for (EdgeProperty<?> prop : Sets.union(edge.getProperties(),
							RelationGraph.this.edgeProperties.keySet())) {
						if (!propertyPredicate.test(prop, RelationGraph.this.getProperty(edge.getStart().getValue(),
								type, edge.getEnd().getValue(), prop, false))) {
							propertiesPermitted = false;
							break;
						}
					}
					if (!propertiesPermitted)
						continue;
					if (!visited.containsEdge(dir1)) {
						if (toVisit == null) {
							toVisit2.push(edge);
						} else {
							toVisit.add(edge);
						}
					}
				}
			}
			return edgerep;
		}

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RelationGraph rg) {
			return this.V.keySet().equals(rg.V.keySet()) && this.E.equals(rg.E);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.V.keySet().hashCode() + this.E.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[V=" + this.V.size() + ",E=" + this.E.size() + "]";
	}

	@Override
	public String representation() {
		StringBuilder builder = new StringBuilder("{\n\tNodes (" + V.size() + ")=");
		builder.append(this.V.keySet()).append(", ");
		builder.append("\n\tEdges (" + E.size() + ")={\n\t\t");
		int cols = MathUtils.largestPrimeFactor(E.size());
		Iterator<IInvertibleEdge<E, R>> edgeit = E.iterator();
		for (int i = 0; i < E.size(); i++) {
			IInvertibleEdge<E, R> edge = edgeit.next();
			builder.append(edge);
			if (i % cols == 0) {
				builder.append("\n\t\t");
			} else {
				builder.append(",\t");
			}
		}
		builder.append("\n\t}");
		return builder.append("\n}").toString();

	}

	@Override
	public IModifiableRelationGraph<E, R> subgraph(Collection<? extends E> nodes) {
		if (!this.V.keySet().containsAll(nodes))
			throw new IllegalArgumentException(nodes + " not in graph: " + this.V);
		return new SubGraphView(nodes);
	}

	@Override
	public IModifiableRelationGraph<E, R> copy() {
		return deepCopy(Function.identity());
	}

	@Override
	public IModifiableRelationGraph<E, R> deepCopy(Function<E, E> cloner) {
		RelationGraph<E, R> newGraph = new RelationGraph<>();
		newGraph.edgeProperties = this.edgeProperties;
		for (E node : V.keySet()) {
			E n = cloner.apply(node);
			newGraph.V.put(n, new Node(n));
		}
		for (IInvertibleEdge<E, R> edge : E) {
			INode<E, R> start = newGraph.V.get(edge.getStart().getValue());
			INode<E, R> end = newGraph.V.get(edge.getEnd().getValue());
			Edge newedge = new Edge(edge.getType(), start, end);
			for (EdgeProperty<?> prop : edge.getProperties()) {
				newedge.properties.put(prop, edge.getPropertyValue(prop));
			}
			start.joinEdge(newedge);
			end.joinEdge(newedge.invert());
			newGraph.E.add(newedge);

		}
		return newGraph;
	}

	protected class SubGraphView implements IModifiableRelationGraph<E, R> {

		private Map<E, INode<E, R>> nodes;
		private RelationGraph<E, R> self = RelationGraph.this;

		private void check(Object nodea) {
			node(nodea);
			if (!nodes.containsKey(nodea)) {
				throw new NodeNotFoundException(nodea);
			}
		}

		/**
		 * @param nodes
		 */
		public SubGraphView(Collection<? extends E> nodes) {
			this.nodes = new HashMap<>(Maps.toMap(nodes.iterator(), self::node));
			self.weaks.put(this, true);
		}

		@Override
		public boolean add(E e) {
			self.add(e);
			boolean con = nodes.containsKey(e);
			this.nodes.put(e, node(e));
			return con;
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			self.addAll(c);
			boolean con = nodes.keySet().containsAll(c);
			this.nodes.putAll(Maps.toMap(c.iterator(), self::node));
			return !con;
		}

		@Override
		public boolean addEdge(E first, R type, E second) {
			check(first);
			check(second);
			return self.addEdge(first, type, second);
		}

		@Override
		public void clear() {
			self.removeAll(nodes.keySet());
			nodes.clear();
		}

		@Override
		public boolean contains(Object o) {
			if (!nodes.containsKey(o))
				return false;
			return self.contains(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			if (!nodes.keySet().containsAll(c))
				return false;
			return self.containsAll(c);
		}

		@Override
		public boolean containsEdge(Object one, Object two) {
			if (!nodes.containsKey(one) || !nodes.containsKey(two))
				return false;
			return self.containsEdge(one, two);
		}

		@Override
		public boolean containsEdge(Object one, R type, Object two) {
			if (!nodes.containsKey(one) || !nodes.containsKey(two))
				return false;
			return self.containsEdge(one, type, two);
		}

		@Override
		public int degree(E node) {
			check(node);
			int deg = 0;
			for (E e : nodes.get(node).getNeighborNodes()) {
				if (this.nodes.containsKey(e)) {
					deg++;
				}
			}
			return deg;
		}

		@Override
		public int degree(E node, R type) {
			check(node);
			int deg = 0;
			for (E e : nodes.get(node).getNeighborNodes(type)) {
				if (this.nodes.containsKey(e)) {
					deg++;
				}
			}
			return deg;
		}

		@Override
		public int edgeCount() {
			Set<IInvertibleEdge<E, R>> edges = new HashSet<>();
			for (INode<E, R> node : nodes.values()) {
				node.getAllEdges().stream().filter((a) -> !a.isInverse()).forEach(edges::add);
			}
			return edges.size();
		}

		@Override
		public Iterator<Triplet<E, R, E>> edgeIterator() {
			return E.stream().filter(
					(e) -> nodes.containsKey(e.getStart().getValue()) && nodes.containsKey(e.getEnd().getValue()))
					.map(IInvertibleEdge::asTriplet).iterator();
		}

		@Override
		public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint,
				Collection<? extends R> allowedEdgeTypes, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
			return new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, true, nodes.keySet());
		}

		@Override
		public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint,
				Collection<? extends R> allowedEdgeTypes, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
			return new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, false, nodes.keySet());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IRelationGraph g) {
				return this.nodes.keySet().equals(g.getNodesImmutable())
						&& Sets.newHashSet(this.edgeIterator()).equals(Sets.newHashSet(g.edgeIterator()));
			}
			return super.equals(obj);
		}

		@Override
		public Collection<R> getConnectingEdgeTypes(E node) {
			check(node);
			return nodes.get(node).getAllEdges().stream().filter((a) -> nodes.containsKey(a.getEnd().getValue()))
					.map((a) -> a.getType()).collect(Collectors.toSet());
		}

		@Override
		public Collection<E> getNeighbors(E node) {
			check(node);
			return self.getNeighbors(node).stream().filter(nodes::containsKey).collect(Collectors.toSet());
		}

		@Override
		public Collection<R> getEdgeTypesBetween(E one, E two) {
			check(one);
			check(two);
			return self.getEdgeTypesBetween(one, two);
		}

		@Override
		public Collection<E> getNeighbors(E node, R type) {
			check(node);
			return self.getNeighbors(node, type).stream().filter(nodes::containsKey).collect(Collectors.toSet());
		}

		@Override
		public Collection<E> getNodesImmutable() {
			return ImmutableCollection.from(this.nodes.keySet());
		}

		@Override
		public Iterable<E> getBareNodes() {
			return (Iterable<E>) () -> nodes.values().stream().filter((a) -> a.getAllEdges().isEmpty())
					.map((a) -> a.getValue()).iterator();
		}

		@Override
		public <X> X getProperty(E one, R type, E two, EdgeProperty<X> prop) {
			check(one);
			check(two);
			return self.getProperty(one, type, two, prop);
		}

		@Override
		public <X> X getProperty(E one, R type, E two, EdgeProperty<X> prop, boolean computeIfAbsent) {
			check(one);
			check(two);
			return self.getProperty(one, type, two, prop, computeIfAbsent);
		}

		@Override
		public int hashCode() {
			return this.nodes.hashCode() + self.E.hashCode();
		}

		@Override
		public boolean isEmpty() {
			return this.nodes.isEmpty();
		}

		@Override
		public Iterator<E> iterator() {
			return this.nodes.keySet().iterator();
		}

		@Override
		public boolean nodeHasConnections(Object one, R type) {
			if (!nodes.containsKey(one))
				return false;
			return nodes.get(one).getEdgesOfType(type).stream()
					.anyMatch((edge) -> nodes.containsKey(edge.getEnd().getValue()));
		}

		@Override
		public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
			return new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, true, nodes.keySet());
		}

		@Override
		public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
			return new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, false, nodes.keySet());
		}

		@Override
		public boolean remove(Object o) {
			if (nodes.remove(o) != null) {
				return self.remove(o);
			}
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			if (nodes.keySet().removeAll(c)) {
				return self.removeAll(c);
			}
			return false;
		}

		@Override
		public boolean removeAllConnections(Object value) {
			if (nodes.containsKey(value)) {
				return self.removeAllConnections(value);
			}
			return false;
		}

		@Override
		public boolean removeAllConnections(Object value, Object other) {
			if (nodes.containsKey(value) && nodes.containsKey(other)) {
				return self.removeAllConnections(value, other);
			}
			return false;
		}

		@Override
		public boolean removeAllConnections(Object value, R type) {
			if (nodes.containsKey(value)) {
				return self.removeAllConnections(value, type);
			}
			return false;
		}

		@Override
		public boolean removeEdge(Object value, R type, Object other) {
			if (nodes.containsKey(value) && nodes.containsKey(other)) {
				return self.removeEdge(value, type, other);
			}
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			if (nodes.keySet().retainAll(c)) {
				return self.retainAll(c);
			}
			return false;
		}

		@Override
		public int size() {
			return nodes.size();
		}

		@Override
		public <X> X setProperty(E one, R type, E two, EdgeProperty<X> prop, X val) {
			check(one);
			check(two);
			return self.setProperty(one, type, two, prop, val);
		}

		@Override
		public IModifiableRelationGraph<E, R> subgraph(Collection<? extends E> nodes) {
			if (!this.nodes.keySet().containsAll(nodes)) {
				throw new IllegalArgumentException(nodes + " not in graph: " + this.nodes.keySet());
			}
			return new SubGraphView(nodes);
		}

		@Override
		public Object[] toArray() {
			return nodes.keySet().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return nodes.keySet().toArray(a);
		}

		@Override
		public IModifiableRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
				Consumer<E> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
			NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject,
					true, this.nodes.keySet());
			iter.forEachRemaining((a) -> {
			});
			return iter.visited;
		}

		@Override
		public IModifiableRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
				Consumer<E> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
			NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject,
					false, this.nodes.keySet());
			iter.forEachRemaining((a) -> {
			});
			return iter.visited;
		}

		@Override
		public String representation() {
			Set<IInvertibleEdge<E, R>> E = Sets.newHashSet(self.E.stream().filter(
					(e) -> nodes.containsKey(e.getStart().getValue()) && nodes.containsKey(e.getEnd().getValue()))
					.iterator());
			StringBuilder builder = new StringBuilder("{\n\tNodes (" + nodes.size() + "/" + self.size() + ")=");
			builder.append(nodes.keySet()).append(", ");
			builder.append("\n\tEdges (" + E.size() + "/" + self.edgeCount() + ")={\n\t\t");
			int cols = MathUtils.largestPrimeFactor(E.size());
			Iterator<IInvertibleEdge<E, R>> edgeit = E.iterator();
			for (int i = 0; i < E.size(); i++) {
				IInvertibleEdge<E, R> edge = edgeit.next();
				builder.append(edge);
				if (i % cols == 0) {
					builder.append("\n\t\t");
				} else {
					builder.append(",\t");
				}
			}
			builder.append("\n\t}");
			return builder.append("\n}").toString();
		}

		@Override
		public String toString() {
			Set<IInvertibleEdge<E, R>> E = Sets.newHashSet(self.E.stream().filter(
					(e) -> nodes.containsKey(e.getStart().getValue()) && nodes.containsKey(e.getEnd().getValue()))
					.iterator());
			return this.getClass().getSimpleName() + "[V=" + this.nodes.size() + ",E=" + E.size() + "]";
		}

		@Override
		public <X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Consumer<X> get) {
			check(one);
			check(two);
			self.forEachEdgeProperty(one, two, prop, get);
		}

		@Override
		public <X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Function<X, X> getSet) {
			check(one);
			check(two);
			self.forEachEdgeProperty(one, two, prop, getSet);
		}

		@Override
		public <X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Consumer<X> get) {
			check(one);
			nodes.get(one).getAllEdges().stream().filter((a) -> nodes.containsKey(a.getEnd().getValue()))
					.forEach((e) -> get.accept(e.getPropertyValue(prop)));
		}

		@Override
		public <X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Function<X, X> getSet) {
			check(one);
			nodes.get(one).getAllEdges().stream().filter((a) -> nodes.containsKey(a.getEnd().getValue()))
					.map((e) -> Pair.of(e, getSet.apply(e.getPropertyValue(prop))))
					.forEach((pair) -> pair.getFirst().setPropertyValue(prop, pair.getSecond()));
		}

		@Override
		public <X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Consumer<X> get) {
			check(one);
			nodes.get(one).getEdgesOfType(type).stream().filter((a) -> nodes.containsKey(a.getEnd().getValue()))
					.forEach((e) -> get.accept(e.getPropertyValue(prop)));
		}

		@Override
		public <X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Function<X, X> getSet) {
			check(one);
			nodes.get(one).getEdgesOfType(type).stream().filter((a) -> nodes.containsKey(a.getEnd().getValue()))
					.map((e) -> Pair.of(e, getSet.apply(e.getPropertyValue(prop))))
					.forEach((pair) -> pair.getFirst().setPropertyValue(prop, pair.getSecond()));
		}

		@Override
		public IModifiableRelationGraph<E, R> copy() {
			return deepCopy(Function.identity());
		}

		@Override
		public IModifiableRelationGraph<E, R> deepCopy(Function<E, E> cloner) {
			RelationGraph<E, R> newGraph = new RelationGraph<>();
			newGraph.edgeProperties = self.edgeProperties;
			for (E node : this.nodes.keySet()) {
				E n = cloner.apply(node);
				newGraph.V.put(n, new Node(n));
			}
			for (IInvertibleEdge<E, R> edge : E) {
				if (!nodes.containsKey(edge.getStart().getValue()) || !nodes.containsKey(edge.getEnd().getValue())) {
					continue;
				}
				INode<E, R> start = newGraph.V.get(edge.getStart().getValue());
				INode<E, R> end = newGraph.V.get(edge.getEnd().getValue());
				Edge newedge = new Edge(edge.getType(), start, end);
				for (EdgeProperty<?> prop : edge.getProperties()) {
					newedge.properties.put(prop, edge.getPropertyValue(prop));
				}
				start.joinEdge(newedge);
				end.joinEdge(newedge.invert());
				newGraph.E.add(newedge);

			}
			return newGraph;
		}

		class SubGraphNodeIterator implements Iterator<E> {
			private Iterator<E> nodes = SubGraphView.this.nodes.keySet().iterator();
			private E prevNode = null;

			@Override
			public boolean hasNext() {
				return nodes.hasNext();
			}

			@Override
			public E next() {
				prevNode = nodes.next();
				return prevNode;
			}

			@Override
			public void remove() {
				if (prevNode == null) {
					throw new IllegalStateException();
				}
				nodes.remove();
				RelationGraph.this.remove(prevNode);

			}

		}

	}

}
