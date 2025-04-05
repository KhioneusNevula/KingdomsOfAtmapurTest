package _utilities.graph;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import _utilities.collections.CollectionUtils;
import _utilities.collections.MappedCollection;
import _utilities.collections.MappedSet;
import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import _utilities.property.IProperty;

public class MappedGraphView<E, R extends IInvertibleRelationType, EI, RI extends IInvertibleRelationType>
		implements IModifiableRelationGraph<E, R> {

	private IRelationGraph<EI, RI> inner;
	private Function<E, EI> nodeExToIn;
	private Function<EI, E> nodeInToEx;
	private Function<R, RI> edgeExToIn;
	private Function<RI, R> edgeInToEx;
	private Class<? super E> nodeClass;
	private Class<? super R> edgeClass;
	private Class<? super EI> innerNodeClass;
	private Class<? super RI> innerEdgeClass;

	public MappedGraphView(IRelationGraph<EI, RI> inner, Function<E, EI> nodeExToIn, Function<EI, E> nodeInToEx) {
		this(inner, nodeExToIn, nodeInToEx, (Class) null, null);
	}

	public MappedGraphView(IRelationGraph<EI, RI> inner, Function<E, EI> nodeExToIn, Function<EI, E> nodeInToEx,
			Function<R, RI> edgeExToIn, Function<RI, R> edgeInToEx) {
		this(inner, nodeExToIn, nodeInToEx, null, null, edgeExToIn, edgeInToEx, null, null);
	}

	public MappedGraphView(IRelationGraph<EI, RI> inner, Function<E, EI> nodeExToIn, Function<EI, E> nodeInToEx,
			Class<? super E> nodeClass, Class<? super EI> innerNodeClass, Function<R, RI> edgeExToIn,
			Function<RI, R> edgeInToEx, Class<? super R> edgeClass, Class<? super RI> innerEdgeClass) {
		this.inner = inner;
		this.nodeExToIn = nodeExToIn;
		this.nodeInToEx = nodeInToEx;
		this.edgeExToIn = edgeExToIn;
		this.edgeInToEx = edgeInToEx;
		this.nodeClass = nodeClass;
		this.edgeClass = edgeClass;
		this.innerNodeClass = innerNodeClass;
		this.innerEdgeClass = innerEdgeClass;
	}

	public MappedGraphView(IRelationGraph<EI, RI> inner, Function<E, EI> nodeExToIn, Function<EI, E> nodeInToEx,
			Class<? super E> nodeClass, Class<? super EI> innerNodeClass) {
		this(inner, nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, (e) -> (RI) e, (e) -> (R) e,
				IInvertibleRelationType.class, IInvertibleRelationType.class);
	}

	@Override
	public int size() {

		return inner.size();
	}

	@Override
	public boolean isEmpty() {
		return inner.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (nodeClass == null || nodeClass.isInstance(o)) {
			return inner.contains(this.nodeExToIn.apply((E) o));
		}
		return false;
	}

	@Override
	public Set<E> getNodeSetImmutable() {
		return new MappedSet<>(inner.getNodeSetImmutable(), nodeExToIn, nodeInToEx, nodeClass);
	}

	@Override
	public Iterable<E> getBareNodes() {
		return CollectionUtils.mappedIterable(inner.getBareNodes(), nodeInToEx);
	}

	@Override
	public Iterator<E> iterator() {
		return CollectionUtils.mappedIterator(inner.iterator(), nodeInToEx);
	}

	@Override
	public Object[] toArray() {
		return inner.stream().map(nodeInToEx).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return inner.stream().map(nodeInToEx)
				.toArray((x) -> (T[]) Array.newInstance(a.getClass().getComponentType(), x));
	}

	@Override
	public boolean isEditable() {
		return inner.isEditable();
	}

	@Override
	public boolean add(E e) {
		return inner.add(nodeExToIn.apply(e));
	}

	@Override
	public boolean addAll(IRelationGraph<E, R> subgraph) {
		if (inner instanceof IModifiableRelationGraph) {
			return ((IModifiableRelationGraph<EI, RI>) inner).addAll(new MappedGraphView<>(subgraph, nodeInToEx,
					nodeExToIn, innerNodeClass, nodeClass, edgeInToEx, edgeExToIn, innerEdgeClass, edgeClass));
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addEdge(E first, R type, E second) {
		if (inner instanceof IModifiableRelationGraph) {
			return ((IModifiableRelationGraph<EI, RI>) inner).addEdge(nodeExToIn.apply(first), edgeExToIn.apply(type),
					nodeExToIn.apply(second));
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public Pair<Object, Object> checkEdgeEndsPermissible(Object first, IInvertibleRelationType type, Object second) {
		if (inner instanceof IModifiableRelationGraph inner2) {
			return inner2.checkEdgeEndsPermissible(nodeExToIn.apply((E) first), edgeExToIn.apply((R) type),
					nodeExToIn.apply((E) second));
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAllConnections(Object value) {
		if (inner instanceof IModifiableRelationGraph) {
			if (nodeClass == null || nodeClass.isInstance(value)) {
				return ((IModifiableRelationGraph<EI, RI>) inner).removeAllConnections(nodeExToIn.apply((E) value));
			}
			throw new NodeNotFoundException(value);
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAllConnections(Object value, Object other) {
		if (inner instanceof IModifiableRelationGraph) {
			if (nodeClass != null && !nodeClass.isInstance(value))
				throw new NodeNotFoundException(value, 1);
			if (nodeClass != null && !nodeClass.isInstance(other))
				throw new NodeNotFoundException(other, 2);
			return ((IModifiableRelationGraph<EI, RI>) inner).removeAllConnections(nodeExToIn.apply((E) value),
					nodeExToIn.apply((E) other));
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAllConnections(Object value, R type) {
		if (inner instanceof IModifiableRelationGraph) {
			if (nodeClass == null || nodeClass.isInstance(value)) {
				return ((IModifiableRelationGraph<EI, RI>) inner).removeAllConnections(nodeExToIn.apply((E) value),
						edgeExToIn.apply(type));
			}
			throw new NodeNotFoundException(value);
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeBareNodes() {
		if (inner instanceof IModifiableRelationGraph) {
			((IModifiableRelationGraph<EI, RI>) inner).removeBareNodes();
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeEdge(Object value, R type, Object other) {
		if (inner instanceof IModifiableRelationGraph) {
			if (nodeClass != null && !nodeClass.isInstance(value))
				throw new NodeNotFoundException(value, 1);
			if (nodeClass != null && !nodeClass.isInstance(other))
				throw new NodeNotFoundException(other, 2);
			return ((IModifiableRelationGraph<EI, RI>) inner).removeEdge(nodeExToIn.apply((E) value),
					edgeExToIn.apply(type), nodeExToIn.apply((E) other));
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		if (nodeClass == null || nodeClass.isInstance(o)) {
			return inner.remove(nodeExToIn.apply((E) o));
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object x : c) {
			if (!this.contains(x))
				return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean mod = false;
		for (E x : c) {
			mod = this.add(x) || mod;
		}
		return mod;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean mod = false;
		for (Object x : c) {
			mod = this.remove(x) || mod;
		}
		return mod;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean mod = false;
		for (EI x : inner) {
			if (!c.contains(nodeInToEx.apply(x))) {
				mod = inner.remove(x) || mod;
			}
		}
		return mod;
	}

	@Override
	public void clear() {
		inner.clear();
	}

	@Override
	public <X> X getProperty(E one, R type, E two, IProperty<X> prop) {
		return inner.getProperty(nodeExToIn.apply(one), edgeExToIn.apply(type), nodeExToIn.apply(two), prop);
	}

	@Override
	public <X> X getProperty(E one, R type, E two, IProperty<X> prop, boolean computeIfAbsent) {
		if (inner instanceof IModifiableRelationGraph) {
			return ((IModifiableRelationGraph<EI, RI>) inner).getProperty(nodeExToIn.apply(one), edgeExToIn.apply(type),
					nodeExToIn.apply(two), prop, computeIfAbsent);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public <X> X setProperty(E one, R type, E two, IProperty<X> prop, X val) {
		if (inner instanceof IModifiableRelationGraph) {
			return ((IModifiableRelationGraph<EI, RI>) inner).setProperty(nodeExToIn.apply(one), edgeExToIn.apply(type),
					nodeExToIn.apply(two), prop, val);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public String edgeToString(E first, R second, E third, boolean includeEnds) {
		return inner.edgeToString(nodeExToIn.apply(first), edgeExToIn.apply(second), nodeExToIn.apply(third),
				includeEnds);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(nodeExToIn.apply(one), nodeExToIn.apply(two), prop, get);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(nodeExToIn.apply(one), prop, get);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(nodeExToIn.apply(one), edgeExToIn.apply(type), prop, get);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Function<X, X> getSet) {
		if (inner instanceof IModifiableRelationGraph) {
			((IModifiableRelationGraph<EI, RI>) inner).forEachEdgeProperty(nodeExToIn.apply(one), nodeExToIn.apply(two),
					prop, getSet);
			;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Function<X, X> getSet) {
		if (inner instanceof IModifiableRelationGraph) {
			((IModifiableRelationGraph<EI, RI>) inner).forEachEdgeProperty(nodeExToIn.apply(one), prop, getSet);
			;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Function<X, X> getSet) {
		if (inner instanceof IModifiableRelationGraph) {
			((IModifiableRelationGraph<EI, RI>) inner).forEachEdgeProperty(nodeExToIn.apply(one),
					edgeExToIn.apply(type), prop, getSet);
			;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Set<E> getNeighbors(E node) {
		return new MappedCollection<>(inner.getNeighbors(nodeExToIn.apply(node)), nodeExToIn, nodeInToEx, nodeClass);
	}

	@Override
	public Set<E> getNeighbors(E node, R type) {
		return new MappedCollection<>(inner.getNeighbors(nodeExToIn.apply(node), edgeExToIn.apply(type)), nodeExToIn,
				nodeInToEx, nodeClass);
	}

	@Override
	public E get(Object of) {
		if (nodeClass == null || nodeClass.isInstance(of)) {
			return nodeInToEx.apply(inner.get(nodeExToIn.apply((E) of)));
		}
		return nodeInToEx.apply(inner.get(of));
	}

	@Override
	public void set(Object oldNode, E newNode) {
		if (inner instanceof IModifiableRelationGraph) {
			if (nodeClass == null || nodeClass.isInstance(oldNode)) {
				((IModifiableRelationGraph<EI, RI>) inner).set(nodeExToIn.apply((E) oldNode),
						nodeExToIn.apply(newNode));
			} else {

				((IModifiableRelationGraph<EI, RI>) inner).set(oldNode, nodeExToIn.apply(newNode));
			}
		}
	}

	@Override
	public boolean containsEdge(Object one, Object two) {
		if (nodeClass == null || nodeClass.isInstance(one) && nodeClass.isInstance(two)) {
			return inner.containsEdge(nodeExToIn.apply((E) one), nodeExToIn.apply((E) two));
		}
		return false;
	}

	@Override
	public boolean containsEdge(Object one, Object type, Object two) {
		if ((nodeClass == null || nodeClass.isInstance(one) && nodeClass.isInstance(two))
				&& (edgeClass == null || edgeClass.isInstance(type))) {
			return inner.containsEdge(nodeExToIn.apply((E) one), edgeExToIn.apply((R) type), nodeExToIn.apply((E) two));
		}
		return false;
	}

	@Override
	public int degree(E node) {
		return inner.degree(nodeExToIn.apply(node));
	}

	@Override
	public int degree(E node, R type) {
		return inner.degree(nodeExToIn.apply(node), edgeExToIn.apply(type));
	}

	@Override
	public boolean nodeHasConnections(Object one, R type) {
		if (nodeClass == null || nodeClass.isInstance(one)) {
			return inner.nodeHasConnections(nodeExToIn.apply((E) one), edgeExToIn.apply(type));
		}
		return false;
	}

	@Override
	public Set<R> getEdgeTypesBetween(E one, E two) {
		return new MappedCollection<R, RI>(inner.getEdgeTypesBetween(nodeExToIn.apply(one), nodeExToIn.apply(two)),
				edgeExToIn, edgeInToEx, edgeClass);
	}

	@Override
	public Set<R> getOutgoingEdgeTypes(E node) {
		return new MappedCollection<R, RI>(inner.getOutgoingEdgeTypes(nodeExToIn.apply(node)), edgeExToIn, edgeInToEx,
				edgeClass);
	}

	@Override
	public int edgeCount() {
		return inner.edgeCount();
	}

	private Iterator<Triplet<E, R, E>> edgeMappedIterator(Iterator<Triplet<EI, RI, EI>> from) {
		return CollectionUtils.mappedIterator(from, (trip) -> Triplet.of(nodeInToEx.apply(trip.getFirst()),
				edgeInToEx.apply(trip.getSecond()), nodeInToEx.apply(trip.getThird())));
	}

	private Function<Triplet<E, R, E>, Triplet<EI, RI, EI>> tripletExToInFunction() {
		return (trip) -> Triplet.of(nodeExToIn.apply(trip.getFirst()), edgeExToIn.apply(trip.getSecond()),
				nodeExToIn.apply(trip.getThird()));
	}

	private Function<Triplet<EI, RI, EI>, Triplet<E, R, E>> tripletInToExFunction() {
		return (trip) -> Triplet.of(nodeInToEx.apply(trip.getFirst()), edgeInToEx.apply(trip.getSecond()),
				nodeInToEx.apply(trip.getThird()));
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return edgeMappedIterator(inner.edgeIterator());
	}

	@Override
	public Collection<R> getEdgeTypes() {
		return new MappedCollection<>(inner.getEdgeTypes(), edgeExToIn, edgeInToEx, this.edgeClass);
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes) {
		return edgeMappedIterator(inner.edgeIterator(forTypes.stream().map(edgeExToIn).collect(Collectors.toSet())));
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode) {
		return edgeMappedIterator(inner.outgoingEdges(nodeExToIn.apply(forNode)));
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode, R type) {
		return edgeMappedIterator(inner.outgoingEdges(nodeExToIn.apply(forNode), edgeExToIn.apply(type)));
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return edgeMappedIterator(inner.edgeTraversalIteratorBFS(nodeExToIn.apply(startPoint), allowedEdgeTypes == null
				? null
				: CollectionUtils.immutableMappedCollection(allowedEdgeTypes, innerEdgeClass, edgeInToEx, edgeExToIn),
				applyAcrossObject));
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return edgeMappedIterator(inner.edgeTraversalIteratorDFS(nodeExToIn.apply(startPoint), allowedEdgeTypes == null
				? null
				: CollectionUtils.immutableMappedCollection(allowedEdgeTypes, innerEdgeClass, edgeInToEx, edgeExToIn),
				applyAcrossObject));
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return CollectionUtils.mappedIterator(inner.nodeTraversalIteratorBFS(nodeExToIn.apply(startPoint),
				allowedEdgeTypes == null ? null
						: CollectionUtils.immutableMappedCollection(allowedEdgeTypes, innerEdgeClass, edgeInToEx,
								edgeExToIn),
				applyAcrossObject), nodeInToEx);
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return CollectionUtils.mappedIterator(inner.nodeTraversalIteratorDFS(nodeExToIn.apply(startPoint),
				allowedEdgeTypes == null ? null
						: CollectionUtils.immutableMappedCollection(allowedEdgeTypes, innerEdgeClass, edgeInToEx,
								edgeExToIn),
				applyAcrossObject), nodeInToEx);
	}

	@Override
	public IModifiableRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return new MappedGraphView<>(
				inner.traverseBFS(nodeExToIn.apply(startPoint),
						allowedEdgeTypes == null ? null
								: CollectionUtils.immutableMappedCollection(allowedEdgeTypes, innerEdgeClass,
										edgeInToEx, edgeExToIn),
						(m) -> forEachNode.accept(nodeInToEx.apply(m)), applyAcrossObject),
				nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn, edgeInToEx, edgeClass, innerEdgeClass);
	}

	@Override
	public IModifiableRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return new MappedGraphView<>(
				inner.traverseDFS(nodeExToIn.apply(startPoint),
						allowedEdgeTypes == null ? null
								: CollectionUtils.immutableMappedCollection(allowedEdgeTypes, innerEdgeClass,
										edgeInToEx, edgeExToIn),
						(m) -> forEachNode.accept(nodeInToEx.apply(m)), applyAcrossObject),
				nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn, edgeInToEx, edgeClass, innerEdgeClass);
	}

	@Override
	public IModifiableRelationGraph<E, R> copy() {

		return new MappedGraphView<>(inner.copy(), nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn,
				edgeInToEx, edgeClass, innerEdgeClass);
	}

	@Override
	public IModifiableRelationGraph<E, R> deepCopy(Function<E, E> cloner) {

		return new MappedGraphView<>(inner.copy(), nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn,
				edgeInToEx, edgeClass, innerEdgeClass);
	}

	@Override
	public <E2, R2 extends IInvertibleRelationType> IRelationGraph<E2, R2> mapCopy(Function<E, E2> nodeMapper,
			Function<R, R2> edgeMapper) {
		IRelationGraph<E2, R2> gra = inner.mapCopy((node) -> nodeMapper.apply(nodeInToEx.apply(node)),
				(edge) -> edgeMapper.apply(edgeInToEx.apply(edge)));
		return gra;
	}

	@Override
	public MappedGraphView<E, R, EI, RI> subgraph(Iterable<? extends E> nodes) {
		return new MappedGraphView<E, R, EI, RI>(inner.subgraph(CollectionUtils.mappedIterable(nodes, nodeExToIn)),
				nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn, edgeInToEx, edgeClass, innerEdgeClass);
	}

	@Override
	public MappedGraphView<E, R, EI, RI> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return new MappedGraphView<E, R, EI, RI>(
				inner.subgraph(CollectionUtils.mappedIterable(nodes, nodeExToIn),
						(edg) -> edgePred.test(tripletInToExFunction().apply(edg))),
				nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn, edgeInToEx, edgeClass, innerEdgeClass);
	}

	@Override
	public IRelationGraph<E, R> subgraph(Predicate<? super E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return new MappedGraphView<E, R, EI, RI>(
				inner.subgraph((nod) -> nodes.test(nodeInToEx.apply(nod)),
						(edg) -> edgePred.test(tripletInToExFunction().apply(edg))),
				nodeExToIn, nodeInToEx, nodeClass, innerNodeClass, edgeExToIn, edgeInToEx, edgeClass, innerEdgeClass);
	}

	@Override
	public String representation() {
		return this.representation(Object::toString);
	}

	@Override
	public String representation(Function<E, String> converter, Function<R, String> edgeConverter) {
		return "MappedView{" + inner.representation((e) -> converter.apply(nodeInToEx.apply(e)),
				(e) -> edgeConverter.apply(edgeInToEx.apply(e))) + "}";
	}

	@Override
	public String representation(Function<E, String> converter,
			BiFunction<Triplet<E, R, E>, Map<IProperty<?>, Object>, String> edgeConverter) {
		return "MappedView{" + inner.representation((e) -> converter.apply(nodeInToEx.apply(e)),
				(e, p) -> edgeConverter.apply(Triplet.of(nodeInToEx.apply(e.getFirst()),
						edgeInToEx.apply(e.getSecond()), nodeInToEx.apply(e.getThird())), p))
				+ "}";
	}

	@Override
	public String representation(Function<E, String> converter) {
		return representation(converter, Object::toString);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof IRelationGraph gv) {
			if (this.size() != gv.size() || this.edgeCount() != gv.edgeCount()) {
				return false;
			}
			if (!gv.containsAll(this)) {
				return false;
			}
			for (Triplet<E, R, E> trip : (Iterable<Triplet<E, R, E>>) () -> this.edgeIterator()) {
				if (!gv.containsEdge(trip.getFirst(), trip.getSecond(), trip.getThird())) {
					return false;
				}
			}
			return true;
		}
		return inner.equals(obj);
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + inner.toString();
	}

}
