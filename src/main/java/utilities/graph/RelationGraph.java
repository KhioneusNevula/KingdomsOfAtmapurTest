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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Predicates;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.collect.Table;

import utilities.MathUtils;
import utilities.collections.CollectionUtils;
import utilities.collections.ImmutableSetView;
import utilities.couplets.Pair;
import utilities.couplets.Triplet;
import utilities.property.IProperty;

/**
 * 
 * @author borah
 *
 * @param <E> the type of node in the graph
 * @param <R> the type of edge-relation in the graph
 */
public class RelationGraph<E, R extends IInvertibleRelationType> implements IModifiableRelationGraph<E, R>, Cloneable {

	protected interface INode<NType, RType extends IInvertibleRelationType> {
		/**
		 * Get value of this node
		 * 
		 * @return
		 */
		public NType getValue();

		/**
		 * Set the value of this node
		 * 
		 * @param type
		 */
		public void setValue(NType type);

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

		public boolean hasEdgeTo(Object node, Object rel);

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
		public <E> E getPropertyValue(IProperty<E> prop);

		/**
		 * Set a property value of this edge
		 * 
		 * @param <E>
		 * @param prop
		 * @param value
		 */
		public <E> void setPropertyValue(IProperty<E> prop, E value);

		/**
		 * If this edge has an instance of this property
		 * 
		 * @param prop
		 * @return
		 */
		public <E> boolean hasProperty(IProperty<E> prop);

		/**
		 * Return a collection of the properties in this edge
		 * 
		 * @return
		 */
		public Set<IProperty<?>> getProperties();

		/**
		 * Return a map of this edge's properties
		 * 
		 * @return
		 */
		public Map<IProperty<?>, Object> getPropertiesMap();

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

		/**
		 * ToString method which uses a functin to convert node representations
		 * 
		 * @param converter
		 * @return
		 */
		public String convertToString(Function<NType, String> converter);

		/**
		 * ToString method which uses a function to convert node and edgetype
		 * representations
		 * 
		 * @param converter
		 * @return
		 */
		public String convertToString(Function<NType, String> converter, Function<RType, String> econverter);

	}

	private class Edge implements IInvertibleEdge<E, R> {

		private INode<E, R> start;
		private INode<E, R> end;
		private R type;
		private Map<IProperty<?>, Object> properties;
		private InvertedEdge invert;
		private Triplet<E, R, E> trip;

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
		public Triplet<E, R, E> asTriplet() {
			if (trip == null) {
				this.trip = Triplet.of(getStart().getValue(), this.getType(), getEnd().getValue());
			}
			return trip;
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
		public <E> E getPropertyValue(IProperty<E> prop) {
			return (E) properties.get(prop);
		}

		@Override
		public <F> void setPropertyValue(IProperty<F> prop, F value) {
			properties.put(prop, value);
		}

		@Override
		public Set<IProperty<?>> getProperties() {
			return this.properties.keySet();
		}

		@Override
		public Map<IProperty<?>, Object> getPropertiesMap() {
			return this.properties;
		}

		@Override
		public <X> boolean hasProperty(IProperty<X> prop) {
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
		public String convertToString(Function<E, String> converter, Function<R, String> econverter) {

			return "(" + converter.apply(this.getStart().getValue()) + ")>==[" + econverter.apply(this.getType()) + "]"
					+ (properties.isEmpty() ? "" : properties) + "==>(" + converter.apply(this.getEnd().getValue())
					+ ")";
		}

		@Override
		public String convertToString(Function<E, String> converter) {
			return this.convertToString(converter, Object::toString);
		}

		@Override
		public String toString() {
			return this.convertToString(Object::toString);
		}

		private class InvertedEdge implements IInvertibleEdge<E, R> {

			private Triplet<E, R, E> trip;

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
			public Triplet<E, R, E> asTriplet() {
				if (trip == null) {
					this.trip = Triplet.of(getStart().getValue(), this.getType(), getEnd().getValue());
				}
				return trip;
			}

			@Override
			public <X> X getPropertyValue(IProperty<X> prop) {
				return Edge.this.getPropertyValue(prop);
			}

			@Override
			public <X> void setPropertyValue(IProperty<X> prop, X value) {
				Edge.this.setPropertyValue(prop, value);
			}

			@Override
			public Map<IProperty<?>, Object> getPropertiesMap() {
				return Edge.this.getPropertiesMap();
			}

			@Override
			public Set<IProperty<?>> getProperties() {
				return Edge.this.getProperties();
			}

			@Override
			public <X> boolean hasProperty(IProperty<X> prop) {
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
			public String convertToString(Function<E, String> converter) {
				return convertToString(converter, Object::toString);
			}

			@Override
			public String convertToString(Function<E, String> converter, Function<R, String> econverter) {
				return "(" + converter.apply(this.getStart().getValue()) + ")<==[" + econverter.apply(this.getType())
						+ "]" + (properties.isEmpty() ? "" : properties) + "==<("
						+ converter.apply(this.getEnd().getValue()) + ")";
			}

			@Override
			public String toString() {
				return convertToString(Object::toString);
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
		public void setValue(E value) {
			this.value = value;
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
		public boolean hasEdgeTo(Object node, Object rel) {
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

	protected Map<IProperty<?>, Supplier<Object>> edgeProperties;
	private Map<E, INode<E, R>> V;
	private Multimap<R, IInvertibleEdge<E, R>> E;
	private WeakHashMap<SubGraphView, Boolean> weaks;

	public RelationGraph() {
		this.edgeProperties = Collections.emptyMap();
		this.V = new HashMap<>();
		this.E = MultimapBuilder.hashKeys().hashSetValues().build();
		this.weaks = new WeakHashMap<>();
	}

	/**
	 * 
	 * @param edgeProperties map of properties that should return their default
	 *                       value if not initialized; all others return null
	 */
	public RelationGraph(Set<IProperty<?>> edgeProperties) {
		this.edgeProperties = ImmutableMap.copyOf(Maps.asMap(edgeProperties, (prop) -> prop::defaultValue));
		this.V = new HashMap<>();
		this.E = MultimapBuilder.hashKeys().hashSetValues().build();
		this.weaks = new WeakHashMap<>();
	}

	/**
	 * 
	 * @param edgeProperties map of property -> supplier of default value that every
	 *                       edge should return if it has not initialized the
	 *                       property
	 */
	public RelationGraph(Map<IProperty<?>, Supplier<Object>> edgeProperties) {
		this.edgeProperties = ImmutableMap.copyOf(edgeProperties);
		this.V = new HashMap<>();
		this.E = MultimapBuilder.hashKeys().hashSetValues().build();
		this.weaks = new WeakHashMap<>();

	}

	public RelationGraph(IRelationGraph<? extends E, ? extends R> other) {
		this();
		if (other instanceof RelationGraph rg) {
			this.edgeProperties = rg.edgeProperties;
		}
		this.addAll(other);
	}

	public <E2, R2 extends IInvertibleRelationType> RelationGraph(IRelationGraph<E2, R2> other, Function<E2, E> nod,
			Function<R2, R> edg) {
		this();
		this.addAll(other.mapCopy(nod, edg));
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
	 * Return Null and do nothing if there is already an edge; else return the newly
	 * made edge
	 * 
	 * @param node1
	 * @param type
	 * @param node2
	 * @return
	 */
	protected IInvertibleEdge<E, R> addNewEdgeOrNothing(INode<E, R> node1, R type, INode<E, R> node2) {
		if (node1.getValue().equals(node2.getValue())) {
			String nodee = node1.toString();
			String nodea = node2.toString();
			throw new IllegalArgumentException(
					"Cannot connect " + node1 + " to itself" + (nodee.equals(nodea) ? "" : " (" + node2 + ")"));
		}
		if (node1.getEdge(node2.getValue(), type) != null) {
			return null;
		}
		Edge edge = new Edge(type, node1, node2);
		node1.joinEdge(edge);
		node2.joinEdge(edge.invert());
		this.E.put(type, edge);
		return edge;

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
		IProperty prop : edge.getProperties()) {
			newedge.setPropertyValue(prop, edge.getPropertyValue(prop));
		}

		this.E.put(newedge.getType(), newedge);
		return newedge;
	}

	@Override
	public boolean addEdge(E first, R type, E second) {
		return addNewEdgeOrNothing(node(first, 1), type, node(second, 2)) != null;
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

	@Override
	public E get(Object of) {
		INode<E, R> node = V.get(of);
		if (node == null) {
			return null;
		}
		return node.getValue();
	}

	@Override
	public void set(Object node, E newNode) {
		INode<E, R> nod = node(node);
		nod.setValue(newNode);
		this.V.remove(node);
		this.V.put(newNode, nod);
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
		this.weaks.keySet().forEach((sub) -> {
			edges.forEach((edge) -> sub.edgeadd.remove(edge.asTriplet()));
			edges.forEach((edge) -> sub.edgeadd.remove(edge.invert().asTriplet()));
		});
		disconnectEnds(edges);
		boolean removed = false;
		for (IInvertibleEdge<E, R> edge : edges) {
			removed = E.remove(edge.getType(), edge) || removed;
		}
		if (!removed) {
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
		this.weaks.keySet().forEach((sub) -> {
			edges.forEach((edge) -> sub.edgeadd.remove(edge.asTriplet()));
			edges.forEach((edge) -> sub.edgeadd.remove(edge.invert().asTriplet()));
		});
		disconnectEnds(edges);
		boolean removed = false;
		for (IInvertibleEdge<E, R> edge : edges) {
			removed = E.remove(edge.getType(), edge) || removed;
		}
		if (!removed) {
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
		this.weaks.keySet().forEach((sub) -> {
			edges.forEach((edge) -> sub.edgeadd.remove(edge.asTriplet()));
			edges.forEach((edge) -> sub.edgeadd.remove(edge.invert().asTriplet()));
		});
		disconnectEnds(edges);
		boolean removed = false;
		for (IInvertibleEdge<E, R> edge : edges) {
			removed = E.remove(edge.getType(), edge) || removed;
		}
		if (!removed) {
			throw new IllegalArgumentException("Edgelist of graph does not contain all edges from " + node + ": \n"
					+ edges + "\nare not all contained in\n" + E);
		}
		return edges;
	}

	/**
	 * Deletes edge from the two nodes at each end, but not from the edge set E.
	 * Also deletes edge from any SubGraphs
	 * 
	 * @param node
	 * @param type
	 * @param other
	 * @return
	 */
	protected IInvertibleEdge<E, R> removeDetailsOfEdge(INode<E, R> node, R type, INode<E, R> other) {
		this.weaks.keySet().forEach((sub) -> {
			sub.edgeadd.remove(Triplet.of(node.getValue(), type, other.getValue()));
			sub.edgeadd.remove(Triplet.of(other.getValue(), type.invert(), node.getValue()));
		});
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

		return edge;
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
		IInvertibleEdge<E, R> edge = removeDetailsOfEdge(node, type, other);
		if (edge != null && !E.remove(type, edge)) {
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
	public Set<E> getNodeSetImmutable() {
		return ImmutableSetView.from(V.keySet());
	}

	@Override
	public Iterable<E> getBareNodes() {
		return (Iterable<E>) () -> V.values().stream().filter((a) -> a.getAllEdges().isEmpty()).map((a) -> a.getValue())
				.iterator();
	}

	@Override
	public void removeBareNodes() {
		if (V.isEmpty())
			return;
		Iterator<INode<E, R>> iterator = V.values().iterator();
		for (INode<E, R> node = iterator.next(); iterator.hasNext(); node = iterator.next()) {
			if (node.getAllEdges().isEmpty()) {
				iterator.remove();
			}
		}

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
	public <X> X getProperty(Triplet<E, R, E> triplet, IProperty<X> prop, boolean computeIfAbsent) {
		return getProperty(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), prop, computeIfAbsent);
	}

	@Override
	public <X> X getProperty(E one, R type, E two, IProperty<X> prop) {
		return getProperty(one, type, two, prop, false);
	}

	@Override
	public <X> X getProperty(E one, R type, E two, IProperty<X> prop, boolean computeIfAbsent) {
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
			} else {
				obj = prop.defaultValue();
			}
			if (computeIfAbsent) {
				edge.setPropertyValue(prop, obj);
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
	public <X> X setProperty(Triplet<E, R, E> triplet, IProperty<X> prop, X val) {
		return setProperty(triplet.getFirst(), triplet.getSecond(), triplet.getThird(), prop, val);
	}

	@Override
	public <X> X setProperty(E one, R type, E two, IProperty<X> prop, X val) {
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
	public String edgeToString(E first, R second, E third, boolean includeEnds) {
		if (!contains(first) || !contains(third) || !containsEdge(first, second, third)) {
			if (includeEnds) {
				return "[" + second + "]";
			}
			return new Edge(second, new Node(first), new Node(third)).toString();
		}
		IInvertibleEdge<E, R> edge = node(first).getEdge(third, second);
		if (edge == null) {
			throw new IllegalArgumentException("No edge (" + first + ")[" + second + "](" + third + ")");
		}
		if (includeEnds) {
			return edge.toString();
		} else {
			return "[" + edge.getType() + "]" + (edge.getProperties().isEmpty() ? "" : edge.getPropertiesMap());
		}
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get) {
		this.forEachEdgeProperty(one, two, prop, (a) -> {
			get.accept(a);
			return null;
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get) {
		this.forEachEdgeProperty(one, prop, (a) -> {
			get.accept(a);
			return null;
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get) {
		this.forEachEdgeProperty(one, type, prop, (a) -> {
			get.accept(a);
			return null;
		});
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Function<X, X> getSet) {
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
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Function<X, X> getSet) {
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
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Function<X, X> getSet) {
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
	public boolean containsEdge(Object one, Object type, Object two) {
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
		return E.values().size();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return new EdgeIterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes) {
		return new EdgeIterator(forTypes, V, Predicates.alwaysTrue());
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode) {
		return CollectionUtils.mappedIterator(node(forNode).getAllEdges().iterator(), IInvertibleEdge::asTriplet);
	}

	public Collection<? extends Triplet<E, R, E>> edgeCollection() {
		return new EdgeCollection();
	}

	public Collection<? extends Triplet<E, R, E>> edgeCollection(Collection<? extends R> forTypes) {
		return new EdgeCollection(forTypes);
	}

	@Override
	public Iterator<E> iterator() {
		return new NodeIterator();
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
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes,
				constructPred(applyAcrossObject, this.edgeProperties), true);
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
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes,
				constructPred(applyAcrossObject, this.edgeProperties), false);
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
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return new NodeTraversalIterator(startPoint, allowedEdgeTypes,
				constructPred(applyAcrossObject, this.edgeProperties), true);
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
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return new NodeTraversalIterator(startPoint, allowedEdgeTypes,
				constructPred(applyAcrossObject, this.edgeProperties), false);
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
			Consumer<Triplet<E, R, E>> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		EdgeTraversalIterator iter = new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject,
				this.edgeProperties, true);
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
			Consumer<Triplet<E, R, E>> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		EdgeTraversalIterator iter = new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject,
				this.edgeProperties, false);
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
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, this.edgeProperties, true);
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
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return new EdgeTraversalIterator(startPoint, allowedEdgeTypes, applyAcrossObject, this.edgeProperties, false);
	}

	public <X extends Number> RelationGraph<E, R> singleSourceShortestPathDijkstra(E startPoint,
			Collection<? extends R> allowedEdgeTypes, IProperty<X> property) {
		RelationGraph<E, R> visited = new RelationGraph<>();
		Map<E, Double> distances = new HashMap<>();
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

	public class NodeIterator implements Iterator<E> {
		private Iterator<INode<E, R>> noderator = V.values().iterator();
		private INode<E, R> former = null;

		public NodeIterator() {
		}

		public NodeIterator(Map<E, INode<E, R>> nodes) {
			noderator = Streams.stream(noderator).filter((as) -> !nodes.containsKey(as.getValue())).iterator();
		}

		@Override
		public boolean hasNext() {
			return noderator.hasNext();
		}

		@Override
		public E next() {
			return (former = noderator.next()).getValue();
		}

		@Override
		public void remove() {
			noderator.remove();
			if (former != null) {
				RelationGraph.this.remove(former);
			}
		}
	}

	public class EdgeCollection implements Collection<Triplet<E, R, E>> {

		private Collection<? extends R> forTypes = Collections.emptySet();

		EdgeCollection() {
		}

		EdgeCollection(Collection<? extends R> typ) {
			this.forTypes = typ.stream().flatMap((type) -> Stream.of(type, (R) type.invert()))
					.collect(Collectors.toSet());
		}

		@Override
		public int size() {
			return forTypes.isEmpty() ? edgeCount() : forTypes.stream().map(E::get).mapToInt(Collection::size).sum();
		}

		@Override
		public boolean isEmpty() {
			if (forTypes.isEmpty()) {
				return edgeCount() == 0;
			}
			for (R type : forTypes) {
				if (!E.get(type).isEmpty()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Triplet trip) {
				if (forTypes.isEmpty() || forTypes.contains(trip.center()))
					return containsEdge(trip);
			}
			return false;
		}

		@Override
		public Iterator<Triplet<E, R, E>> iterator() {
			return forTypes.isEmpty() ? edgeIterator()
					: CollectionUtils.of(forTypes,
							(type) -> E.get(type).stream().map((ed) -> ed.asTriplet()).iterator());
		}

		@Override
		public Object[] toArray() {
			return Iterators.toArray(iterator(), Object.class);
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return Iterators.toArray(this.stream().map((x) -> (T) x).iterator(),
					(Class<T>) a.getClass().getComponentType());
		}

		@Override
		public boolean add(Triplet<E, R, E> e) {
			if (!forTypes.isEmpty() && !forTypes.contains(e.center())) {
				throw new IllegalArgumentException("This collection only accepts edges of types:" + this.forTypes);
			}
			return addEdge(e);
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Triplet e) {
				if (forTypes.isEmpty() || forTypes.contains(e.center())) {
					return removeEdge(e);
				}
			}
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object x : c) {
				if (x instanceof Triplet) {
					if (this.forTypes.isEmpty()) {
						if (!containsEdge((Triplet) x)) {
							return false;
						}
					} else {
						if (!this.forTypes.contains(x) || !containsEdge((Triplet) x)) {
							return false;
						}
					}
				}
			}
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends Triplet<E, R, E>> c) {
			boolean mod = false;
			for (Triplet<E, R, E> trip : c) {
				mod = add(trip) || mod;
			}
			return mod;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean mod = false;
			for (Object o : c) {
				mod = remove(o) || mod;
			}
			return mod;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return this.removeIf(((Predicate<Triplet<E, R, E>>) (c::contains)).negate());
		}

		@Override
		public void clear() {
			if (forTypes.isEmpty()) {
				for (E node : V.keySet()) {
					removeAllConnections(node);
				}
			} else {
				for (E node : V.keySet()) {
					for (R type : forTypes) {
						removeAllConnections(node, type);
					}
				}
			}
		}

	}

	/**
	 * Iterator across all edges in the graph
	 * 
	 * @author borah
	 *
	 */
	public class EdgeIterator implements Iterator<Triplet<E, R, E>> {
		private Iterator<IInvertibleEdge<E, R>> edgerator;
		private IInvertibleEdge<E, R> former = null;

		public EdgeIterator() {
			edgerator = E.values().iterator();
		}

		private EdgeIterator(Collection<? extends R> types, Map<E, INode<E, R>> nodes,
				Predicate<IInvertibleEdge<E, R>> edges) {
			edgerator = (types.isEmpty() ? E.values().stream()
					: types.stream().flatMap((type) -> Stream.of(type, (R) type.invert()))
							.flatMap((type) -> E.get(type).stream()))
									.filter((a) -> nodes.containsKey(a.getStart().getValue())
											&& nodes.containsKey(a.getEnd().getValue()) && edges.test(a))
									.iterator();
		}

		@Override
		public boolean hasNext() {
			return edgerator.hasNext();
		}

		@Override
		public Triplet<E, R, E> next() {
			return (former = edgerator.next()).asTriplet();
		}

		@Override
		public void remove() {
			edgerator.remove();
			if (former != null) {
				removeDetailsOfEdge(former.getStart(), former.getType(), former.getEnd());
			}
		}
	}

	public class NodeTraversalIterator implements Iterator<E> {

		private RelationGraph<E, R> visited;
		private ArrayList<Pair<IInvertibleEdge<E, R>, INode<E, R>>> toVisit;
		private Stack<Pair<IInvertibleEdge<E, R>, INode<E, R>>> toVisit2;
		private Collection<? extends R> allowedEdgeTypes;
		private INode<E, R> priorNode;
		private Predicate<IInvertibleEdge<E, R>> propertyPredicate;
		private Collection<E> allowedNodes;

		public NodeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<IProperty<?>, Object> propertyPredicate, Map<IProperty<?>, Supplier<Object>> allowprop,
				boolean BFS, Collection<? extends E> allowedNodes) {
			this(firstNode, allowedEdgeTypes, constructPred(propertyPredicate, allowprop), BFS);
			this.allowedNodes = ImmutableSet.copyOf(allowedNodes);
			if (!allowedNodes.contains(firstNode)) {
				throw new IllegalArgumentException(firstNode + " --/--> " + allowedNodes);
			}
		}

		private NodeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				Predicate<IInvertibleEdge<E, R>> propertyPredicate, boolean BFS, Collection<? extends E> allowedNodes) {
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
				Predicate<IInvertibleEdge<E, R>> propertyPredicate, boolean BFS) {
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
				for (IProperty prop : pair.getFirst().getProperties()) {
					edgenew.setPropertyValue(prop, pair.getFirst().getPropertyValue(prop));
				}
			}
			for (R type : allowedEdgeTypes) {
				for (IInvertibleEdge<E, R> edge : firstNode.getEdgesOfType(type)) {
					if (!visited.contains(edge.getEnd().getValue())
							&& (allowedNodes == null || allowedNodes.contains(edge.getEnd().getValue()))) {

						if (!propertyPredicate.test(edge))
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

	private static <E, R extends IInvertibleRelationType> Predicate<IInvertibleEdge<E, R>> constructPred(
			BiPredicate<IProperty<?>, Object> propertyPredicate, Map<IProperty<?>, Supplier<Object>> ep) {
		return (edge) -> {
			for (IProperty<?> prop : Sets.union(edge.getProperties(), ep.keySet())) {
				Object va = edge.getPropertyValue(prop);
				if (va == null)
					va = ep.get(prop).get();
				if (!propertyPredicate.test(prop, va)) {
					return false;
				}
			}
			return true;
		};
	}

	public class EdgeTraversalIterator implements Iterator<Triplet<E, R, E>> {

		private RelationGraph<E, R> visited;
		private ArrayList<IInvertibleEdge<E, R>> toVisit;
		private Stack<IInvertibleEdge<E, R>> toVisit2;
		private Collection<? extends R> allowedEdgeTypes;
		private IInvertibleEdge<E, R> priorEdge;
		private Predicate<IInvertibleEdge<E, R>> propertyPredicate;
		private Collection<E> allowedNodes;

		public EdgeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<IProperty<?>, Object> propertyPredicate,
				Map<IProperty<?>, Supplier<Object>> forpropsDefaultVals, boolean BFS) {
			this(firstNode, allowedEdgeTypes, propertyPredicate, forpropsDefaultVals, BFS, null);
		}

		public EdgeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<IProperty<?>, Object> propertyPredicate,
				Map<IProperty<?>, Supplier<Object>> forpropsDefaultVals, boolean BFS,
				Collection<? extends E> allowedNodes) {
			this(firstNode, allowedEdgeTypes, constructPred(propertyPredicate, forpropsDefaultVals), BFS, allowedNodes);
		}

		private EdgeTraversalIterator(E firstNode, Collection<? extends R> allowedEdgeTypes,
				Predicate<IInvertibleEdge<E, R>> propertyPredicate, boolean BFS, Collection<? extends E> allowedNodes) {
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
					if (!propertyPredicate.test(edge))
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
			for (IProperty prop : firstEdge.getProperties()) {
				edge2.setPropertyValue(prop, firstEdge.getPropertyValue(prop));
			}
			for (R type : allowedEdgeTypes) {
				for (IInvertibleEdge<E, R> edge : firstEdge.getEnd().getEdgesOfType(type)) {
					if (allowedNodes != null && !allowedNodes.contains(edge.getEnd().getValue())) {
						continue;
					}
					Triplet<E, R, E> dir1 = edge.asTriplet();

					if (!this.propertyPredicate.test(edge))
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
		return this.representation(Object::toString);
	}

	private static final int TOO_LONG_LINE = 150;

	@Override
	public String representation(Function<E, String> converter) {
		return this.representation(converter, Object::toString);
	}

	@Override
	public String representation(Function<E, String> converter, Function<R, String> edgeConverter) {
		StringBuilder builder = new StringBuilder("{\n\tNodes (" + V.size() + ")={\n\t\t");

		int cols = MathUtils.largestPrimeFactor(V.size());
		Iterator<E> nodeit = V.keySet().iterator();
		int lincount = 0;
		for (int i = 0; i < V.size(); i++) {
			E node = nodeit.next();
			String nam = converter.apply(node);
			builder.append(converter.apply(node));
			if (i % cols == 0 || (lincount) > TOO_LONG_LINE) {
				builder.append("\n\t\t");
				lincount = 0;
			} else {
				builder.append(",\t");
				lincount += nam.length();
			}
		}
		builder.append("\n\t},");
		builder.append("\n\tEdges (" + E.size() + ")={\n\t\t");
		cols = MathUtils.largestPrimeFactor(E.size());
		Iterator<IInvertibleEdge<E, R>> edgeit = E.values().iterator();
		for (int i = 0; i < E.size(); i++) {
			IInvertibleEdge<E, R> edge = edgeit.next();
			builder.append(edge.convertToString(converter, edgeConverter));
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
	public IModifiableRelationGraph<E, R> subgraph(Iterable<? extends E> nodes) {
		return new SubGraphView(nodes);
	}

	@Override
	public IModifiableRelationGraph<E, R> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return new SubGraphView(nodes, edgePred);
	}

	@Override
	public boolean addAll(IRelationGraph<E, R> subgraph) {
		boolean[] mod = { false };
		mod[0] = this.addAll((Collection<? extends E>) subgraph);
		subgraph.edgeIterator().forEachRemaining((edge) -> mod[0] |= this.addEdge(edge));
		return mod[0];
	}

	@Override
	public IModifiableRelationGraph<E, R> copy() {
		return deepCopy(Function.identity());
	}

	@Override
	public IModifiableRelationGraph<E, R> deepCopy(Function<E, E> cloner) {
		RelationGraph<E, R> newGraph;
		try {
			newGraph = (RelationGraph<E, R>) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		newGraph.clear();
		newGraph.edgeProperties = this.edgeProperties;
		for (E node : V.keySet()) {
			E n = cloner.apply(node);
			newGraph.V.put(n, newGraph.new Node(n));
		}
		for (IInvertibleEdge<E, R> edge : E.values()) {
			INode<E, R> start = newGraph.V.get(edge.getStart().getValue());
			INode<E, R> end = newGraph.V.get(edge.getEnd().getValue());
			Edge newedge = newGraph.new Edge(edge.getType(), start, end);
			for (IProperty<?> prop : edge.getProperties()) {
				newedge.properties.put(prop, edge.getPropertyValue(prop));
			}
			start.joinEdge(newedge);
			end.joinEdge(newedge.invert());
			newGraph.E.put(newedge.type, newedge);

		}
		return newGraph;
	}

	@Override
	public <E2, R2 extends IInvertibleRelationType> IRelationGraph<E2, R2> mapCopy(Function<E, E2> nmapper,
			Function<R, R2> emapper) {
		RelationGraph<E2, R2> newGraph = new RelationGraph<>();
		newGraph.edgeProperties = this.edgeProperties;
		Map<E, E2> mappeds = null;
		for (E node : V.keySet()) {
			E2 n = nmapper.apply(node);
			newGraph.V.put(n, newGraph.new Node(n));

			if (node != n) {
				if (mappeds == null) {
					mappeds = new HashMap<>();
				}
				mappeds.put(node, n);
			}
		}
		for (IInvertibleEdge<E, R> edge : E.values()) {
			Object s1 = null;
			Object e1 = null;
			if (mappeds != null) {
				E2 ss = mappeds.get(edge.getStart().getValue());
				E2 ee = mappeds.get(edge.getEnd().getValue());
				if (ss != null)
					s1 = ss;
				else
					s1 = edge.getStart().getValue();
				if (ee != null)
					e1 = ee;
				else
					e1 = edge.getEnd().getValue();
			} else {
				s1 = edge.getStart().getValue();
				edge.getEnd().getValue();
			}
			INode<E2, R2> start = newGraph.V.get(s1);
			INode<E2, R2> end = newGraph.V.get(e1);
			RelationGraph<E2, R2>.Edge newedge = newGraph.new Edge(emapper.apply(edge.getType()), start, end);
			for (IProperty<?> prop : edge.getProperties()) {
				newedge.properties.put(prop, edge.getPropertyValue(prop));
			}
			start.joinEdge(newedge);
			end.joinEdge(newedge.invert());
			newGraph.E.put(newedge.type, newedge);

		}
		return newGraph;
	}

	protected class SubGraphView implements IModifiableRelationGraph<E, R>, Cloneable {

		private Map<E, INode<E, R>> nodes;
		private Predicate<Triplet<E, R, E>> edgepred;
		private Map<Triplet<E, R, E>, IInvertibleEdge<E, R>> edgeadd = new HashMap<>();
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
		public SubGraphView(Iterable<? extends E> nodes) {
			this(nodes, null);
		}

		/**
		 * @param nodes
		 */
		public SubGraphView(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edger) {
			this.edgepred = edger == null ? Predicates.alwaysTrue() : edger.or(edgeadd::containsKey);
			this.nodes = new HashMap<>(Maps.toMap(nodes.iterator(), self::node));
			if (!RelationGraph.this.containsAll(this.nodes.keySet())) {
				throw new IllegalArgumentException("Not all in graph: " + this.nodes.keySet());
			}
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
			IInvertibleEdge<E, R> x = self.addNewEdgeOrNothing(nodes.get(first), type, nodes.get(second));
			if (x != null && !this.edgepred.test(x.asTriplet()))
				this.edgeadd.put(x.asTriplet(), x);
			return x != null;
		}

		@Override
		public E get(Object of) {
			INode<E, R> node = this.nodes.get(of);
			if (node != null) {
				return node.getValue();
			}
			return null;
		}

		@Override
		public void set(Object oldNode, E newNode) {
			INode<E, R> nod = this.nodes.get(oldNode);
			nod.setValue(newNode);
			V.remove(oldNode);
			V.put(newNode, nod);
			nodes.remove(oldNode);
			nodes.put(newNode, nod);
		}

		@Override
		public void clear() {
			self.removeAll(nodes.keySet());
			Iterator<Triplet<E, R, E>> edger = self.edgeIterator();
			if (edger.hasNext()) {
				for (Triplet<E, R, E> triplet = edger.next(); edger.hasNext(); triplet = edger.next()) {
					if (this.edgepred.test(triplet))
						edger.remove();
				}
			}
			nodes.clear();
			edgeadd.clear();
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

			return self.getEdgeTypesBetween((E) one, (E) two).stream()
					.anyMatch((type) -> edgepred.test(Triplet.of((E) one, type, (E) two)));
		}

		@Override
		public boolean containsEdge(Object one, Object type, Object two) {
			if (!nodes.containsKey(one) || !nodes.containsKey(two))
				return false;
			Triplet edge = Triplet.of(one, type, two);
			return self.containsEdge(one, type, two) && edgepred.test(edge);
		}

		@Override
		public int degree(E node) {
			check(node);
			int deg = 0;
			for (IInvertibleEdge<E, R> e : nodes.get(node).getAllEdges()) {
				if (this.nodes.containsKey(e.getEnd().getValue()) && this.edgepred.test(e.asTriplet())) {
					deg++;
				}
			}
			return deg;
		}

		@Override
		public int degree(E node, R type) {
			check(node);
			int deg = 0;
			for (IInvertibleEdge<E, R> e : nodes.get(node).getEdgesOfType(type)) {
				if (this.nodes.containsKey(e.getEnd().getValue()) && this.edgepred.test(e.asTriplet())) {
					deg++;
				}
			}
			return deg;
		}

		@Override
		public int edgeCount() {
			return (int) E.values().stream().filter((edge) -> nodes.containsKey(edge.getStart().getValue())
					&& nodes.containsKey(edge.getEnd().getValue()) && edgepred.test(edge.asTriplet())).count();
		}

		@Override
		public Iterator<Triplet<E, R, E>> edgeIterator() {
			return new EdgeIterator(Collections.emptySet(), nodes, (edge) -> this.edgepred.test(edge.asTriplet()));
		}

		@Override
		public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes) {
			return new EdgeIterator(forTypes, nodes, (edge) -> this.edgepred.test(edge.asTriplet()));
		}

		@Override
		public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode) {
			check(forNode);
			return self.node(forNode).getAllEdges().stream()
					.filter((edg) -> nodes.containsKey(edg.getEnd().getValue()) && edgepred.test(edg.asTriplet()))
					.map(IInvertibleEdge::asTriplet).iterator();
		}

		@Override
		public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint,
				Collection<? extends R> allowedEdgeTypes, BiPredicate<IProperty<?>, Object> applyAcrossObject) {

			return new EdgeTraversalIterator(startPoint, allowedEdgeTypes,
					(edge) -> (RelationGraph.<E, R>constructPred(applyAcrossObject, edgeProperties).test(edge)
							&& this.edgepred.test(edge.asTriplet())),
					true, nodes.keySet());
		}

		@Override
		public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint,
				Collection<? extends R> allowedEdgeTypes, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
			return new EdgeTraversalIterator(startPoint, allowedEdgeTypes,
					(edge) -> (RelationGraph.<E, R>constructPred(applyAcrossObject, edgeProperties).test(edge)
							&& this.edgepred.test(edge.asTriplet())),
					false, nodes.keySet());
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IRelationGraph g) {
				return this.nodes.keySet().equals(g.getNodeSetImmutable())
						&& Sets.newHashSet(this.edgeIterator()).equals(Sets.newHashSet(g.edgeIterator()));
			}
			return super.equals(obj);
		}

		@Override
		public Collection<R> getConnectingEdgeTypes(E node) {
			check(node);
			return nodes.get(node).getAllEdges().stream()
					.filter((a) -> edgepred.test(a.asTriplet()) && nodes.containsKey(a.getEnd().getValue()))
					.map((a) -> a.getType()).collect(Collectors.toSet());
		}

		@Override
		public Collection<E> getNeighbors(E node) {
			check(node);
			return nodes.get(node).getAllEdges().stream()
					.filter((edge) -> edgepred.test(edge.asTriplet()) && nodes.containsKey(edge.getEnd().getValue()))
					.map((edge) -> edge.getEnd().getValue()).collect(Collectors.toSet());
		}

		@Override
		public Collection<R> getEdgeTypesBetween(E one, E two) {
			check(one);
			check(two);
			return self.getEdgeTypesBetween(one, two).stream().filter((r) -> edgepred.test(Triplet.of(one, r, two)))
					.collect(Collectors.toSet());
		}

		@Override
		public String edgeToString(E first, R second, E third, boolean includeEnds) {

			return edgeToString(first, second, third, includeEnds);
		}

		@Override
		public Collection<E> getNeighbors(E node, R type) {
			check(node);
			return nodes.get(node).getEdgesOfType(type).stream()
					.filter((edge) -> edgepred.test(edge.asTriplet()) && nodes.containsKey(edge.getEnd().getValue()))
					.map((edge) -> edge.getEnd().getValue()).collect(Collectors.toSet());
		}

		@Override
		public Set<E> getNodeSetImmutable() {
			return ImmutableSetView.from(this.nodes.keySet());
		}

		@Override
		public Iterable<E> getBareNodes() {
			return (Iterable<E>) () -> nodes.values().stream()
					.filter((a) -> a.getAllEdges().stream().noneMatch(
							(edge) -> edgepred.test(edge.asTriplet()) && nodes.containsKey(edge.getEnd().getValue())))
					.map((a) -> a.getValue()).iterator();
		}

		@Override
		public void removeBareNodes() {
			if (nodes.isEmpty())
				return;
			Iterator<INode<E, R>> nod = nodes.values().iterator();
			for (INode<E, R> node = nod.next(); nod.hasNext(); node = nod.next()) {
				if (node.getAllEdges().stream().noneMatch(
						(edge) -> edgepred.test(edge.asTriplet()) && nodes.containsKey(edge.getEnd().getValue()))) {
					nod.remove();
					remove(node.getValue());
				}
			}
		}

		@Override
		public <X> X getProperty(E one, R type, E two, IProperty<X> prop) {
			check(one);
			check(two);
			if (!edgepred.test(Triplet.of(one, type, two))) {
				throw new IllegalArgumentException("No such edge");
			}
			return self.getProperty(one, type, two, prop);
		}

		@Override
		public <X> X getProperty(E one, R type, E two, IProperty<X> prop, boolean computeIfAbsent) {
			check(one);
			check(two);
			if (!edgepred.test(Triplet.of(one, type, two))) {
				throw new IllegalArgumentException("No such edge");
			}
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
			return new NodeIterator(nodes);
		}

		@Override
		public boolean nodeHasConnections(Object one, R type) {
			if (!nodes.containsKey(one))
				return false;
			return nodes.get(one).getEdgesOfType(type).stream()
					.anyMatch((edge) -> edgepred.test(edge.asTriplet()) && nodes.containsKey(edge.getEnd().getValue()));
		}

		@Override
		public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<IProperty<?>, Object> applyAcrossObject) {
			return new NodeTraversalIterator(startPoint, allowedEdgeTypes,
					(edge) -> (RelationGraph.<E, R>constructPred(applyAcrossObject, edgeProperties).test(edge)
							&& this.edgepred.test(edge.asTriplet())),
					true, nodes.keySet());
		}

		@Override
		public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
				BiPredicate<IProperty<?>, Object> applyAcrossObject) {
			return new NodeTraversalIterator(startPoint, allowedEdgeTypes,
					(edge) -> (RelationGraph.<E, R>constructPred(applyAcrossObject, edgeProperties).test(edge)
							&& this.edgepred.test(edge.asTriplet())),
					false, nodes.keySet());
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
			Set<E> diff = Sets.difference(nodes.keySet(), Sets.newHashSet(c));
			if (nodes.keySet().removeAll(diff)) {
				return self.removeAll(diff);
			}
			return false;
		}

		@Override
		public int size() {
			return nodes.size();
		}

		@Override
		public <X> X setProperty(E one, R type, E two, IProperty<X> prop, X val) {
			check(one);
			check(two);
			if (!edgepred.test(Triplet.of(one, type, two))) {
				throw new IllegalArgumentException("No such edge");
			}
			return self.setProperty(one, type, two, prop, val);
		}

		@Override
		public SubGraphView subgraph(Iterable<? extends E> nodes) {
			Set<E> elements = Sets.newHashSet(nodes);
			if (!this.nodes.keySet().containsAll(elements)) {
				throw new IllegalArgumentException(elements + " not in graph: " + this.nodes.keySet());
			}
			return new SubGraphView(elements, edgepred);
		}

		@Override
		public SubGraphView subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
			Set<E> elements = Sets.newHashSet(nodes);
			if (!this.nodes.keySet().containsAll(elements)) {
				throw new IllegalArgumentException(elements + " not in graph: " + this.nodes.keySet());
			}
			return new SubGraphView(elements, edgepred.and(edgePred));
		}

		@Override
		public boolean addAll(IRelationGraph<E, R> subgraph) {
			boolean x = self.addAll(subgraph);
			subgraph.forEach((el) -> this.nodes.put(el, self.V.get(el)));
			return x;
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
				Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
			NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes,
					(edge) -> (RelationGraph.<E, R>constructPred(applyAcrossObject, edgeProperties).test(edge)
							&& this.edgepred.test(edge.asTriplet())),
					true, this.nodes.keySet());
			iter.forEachRemaining((a) -> {
			});
			return iter.visited;
		}

		@Override
		public IModifiableRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
				Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
			NodeTraversalIterator iter = new NodeTraversalIterator(startPoint, allowedEdgeTypes,
					(edge) -> (RelationGraph.<E, R>constructPred(applyAcrossObject, edgeProperties).test(edge)
							&& this.edgepred.test(edge.asTriplet())),
					false, this.nodes.keySet());
			iter.forEachRemaining((a) -> {
			});
			return iter.visited;
		}

		@Override
		public String representation() {
			return representation(Object::toString);
		}

		@Override
		public String representation(Function<E, String> converter) {
			return representation(converter, Object::toString);
		}

		@Override
		public String representation(Function<E, String> converter, Function<R, String> edgeConverter) {
			Set<IInvertibleEdge<E, R>> E = Sets.newHashSet(self.E
					.values().stream().filter((e) -> edgepred.test(e.asTriplet())
							&& nodes.containsKey(e.getStart().getValue()) && nodes.containsKey(e.getEnd().getValue()))
					.iterator());
			StringBuilder builder = new StringBuilder("{\n\tNodes (" + nodes.size() + "/" + self.size() + ")=");
			builder.append(String.join(",", (Iterable<String>) () -> nodes.keySet().stream().map(converter).iterator()))
					.append(", ");
			builder.append("\n\tEdges (" + E.size() + "/" + self.edgeCount() + ")={\n\t\t");
			int cols = MathUtils.largestPrimeFactor(E.size());
			Iterator<IInvertibleEdge<E, R>> edgeit = E.iterator();
			for (int i = 0; i < E.size(); i++) {
				IInvertibleEdge<E, R> edge = edgeit.next();
				builder.append(edge.convertToString(converter, edgeConverter));
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
			Set<IInvertibleEdge<E, R>> E = Sets.newHashSet(self.E
					.values().stream().filter((e) -> edgepred.test(e.asTriplet())
							&& nodes.containsKey(e.getStart().getValue()) && nodes.containsKey(e.getEnd().getValue()))
					.iterator());
			return this.getClass().getSimpleName() + "[V=" + this.nodes.size() + "/" + self.size() + ",E=" + E.size()
					+ "/" + self.edgeCount() + "]";
		}

		@Override
		public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get) {
			check(one);
			check(two);
			this.nodes.get(one).getAllEdgesTo(two).forEach((edge) -> {
				if (edgepred.test(edge.asTriplet())) {
					self.forEachEdgeProperty(one, two, prop, get);
				}
			});
		}

		@Override
		public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Function<X, X> getSet) {
			check(one);
			check(two);

			this.nodes.get(one).getAllEdgesTo(two).forEach((edge) -> {
				if (edgepred.test(edge.asTriplet())) {
					self.forEachEdgeProperty(one, two, prop, getSet);
				}
			});
		}

		@Override
		public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get) {
			check(one);

			nodes.get(one).getAllEdges().stream()
					.filter((a) -> edgepred.test(a.asTriplet()) && nodes.containsKey(a.getEnd().getValue()))
					.forEach((e) -> get.accept(e.getPropertyValue(prop)));
		}

		@Override
		public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Function<X, X> getSet) {
			check(one);
			nodes.get(one).getAllEdges().stream()
					.filter((a) -> edgepred.test(a.asTriplet()) && nodes.containsKey(a.getEnd().getValue()))
					.map((e) -> Pair.of(e, getSet.apply(e.getPropertyValue(prop))))
					.forEach((pair) -> pair.getFirst().setPropertyValue(prop, pair.getSecond()));
		}

		@Override
		public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get) {
			check(one);
			nodes.get(one).getEdgesOfType(type).stream()
					.filter((a) -> edgepred.test(a.asTriplet()) && nodes.containsKey(a.getEnd().getValue()))
					.forEach((e) -> get.accept(e.getPropertyValue(prop)));
		}

		@Override
		public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Function<X, X> getSet) {
			check(one);
			nodes.get(one).getEdgesOfType(type).stream()
					.filter((a) -> edgepred.test(a.asTriplet()) && nodes.containsKey(a.getEnd().getValue()))
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
				newGraph.V.put(n, newGraph.new Node(n));
			}
			for (IInvertibleEdge<E, R> edge : E.values()) {
				if (!edgepred.test(edge.asTriplet()) || !nodes.containsKey(edge.getStart().getValue())
						|| !nodes.containsKey(edge.getEnd().getValue())) {
					continue;
				}
				INode<E, R> start = newGraph.V.get(edge.getStart().getValue());
				INode<E, R> end = newGraph.V.get(edge.getEnd().getValue());
				Edge newedge = newGraph.new Edge(edge.getType(), start, end);
				for (IProperty<?> prop : edge.getProperties()) {
					newedge.properties.put(prop, edge.getPropertyValue(prop));
				}
				start.joinEdge(newedge);
				end.joinEdge(newedge.invert());
				newGraph.E.put(newedge.type, newedge);

			}
			return newGraph;
		}

		@Override
		public <E2, R2 extends IInvertibleRelationType> IModifiableRelationGraph<E2, R2> mapCopy(
				Function<E, E2> nmapper, Function<R, R2> emapper) {
			RelationGraph<E2, R2> newGraph = new RelationGraph<>();
			newGraph.edgeProperties = self.edgeProperties;
			Map<E, E2> mappeds = null;
			for (E node : this.nodes.keySet()) {
				E2 n = nmapper.apply(node);
				newGraph.V.put(n, newGraph.new Node(n));

				if (node != n) {
					if (mappeds == null) {
						mappeds = new HashMap<>();
					}
					mappeds.put(node, n);
				}
			}
			for (IInvertibleEdge<E, R> edge : E.values()) {
				if (!edgepred.test(edge.asTriplet()) || !nodes.containsKey(edge.getStart().getValue())
						|| !nodes.containsKey(edge.getEnd().getValue())) {
					continue;
				}
				Object s1 = null;
				Object e1 = null;
				if (mappeds != null) {
					E2 ss = mappeds.get(edge.getStart().getValue());
					E2 ee = mappeds.get(edge.getEnd().getValue());
					if (ss != null)
						s1 = ss;
					else
						s1 = edge.getStart().getValue();
					if (ee != null)
						e1 = ee;
					else
						e1 = edge.getEnd().getValue();
				} else {
					s1 = edge.getStart().getValue();
					edge.getEnd().getValue();
				}
				INode<E2, R2> start = newGraph.V.get(s1);
				INode<E2, R2> end = newGraph.V.get(e1);
				RelationGraph<E2, R2>.Edge newedge = newGraph.new Edge(emapper.apply(edge.getType()), start, end);
				for (IProperty<?> prop : edge.getProperties()) {
					newedge.properties.put(prop, edge.getPropertyValue(prop));
				}
				start.joinEdge(newedge);
				end.joinEdge(newedge.invert());
				newGraph.E.put(newedge.type, newedge);

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
