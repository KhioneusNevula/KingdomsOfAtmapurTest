package _utilities.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.Iterators;

import _utilities.couplets.Triplet;
import _utilities.property.IProperty;

public class ImmutableGraphView<E, R extends IInvertibleRelationType> implements IRelationGraph<E, R> {

	private IRelationGraph<E, R> inner;

	/**
	 * Returns an immutable graph view. If the given graph is an immutable graph,
	 * then just return it again to avoid unnecessary recursions.
	 * 
	 * @param <E>
	 * @param <R>
	 * @param other
	 * @return
	 */
	public static <E, R extends IInvertibleRelationType> ImmutableGraphView<E, R> of(IRelationGraph<E, R> other) {
		if (other instanceof ImmutableGraphView) {
			return (ImmutableGraphView<E, R>) other;
		}
		return new ImmutableGraphView<>(other);
	}

	private ImmutableGraphView(IRelationGraph<E, R> inner) {
		this.inner = inner;
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
		return inner.contains(o);
	}

	@Override
	public Set<E> getNodeSetImmutable() {
		return inner.getNodeSetImmutable();
	}

	@Override
	public Iterable<E> getBareNodes() {
		return inner.getBareNodes();
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.unmodifiableIterator(inner.iterator());
	}

	@Override
	public Object[] toArray() {
		return inner.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return inner.toArray(a);
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return inner.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <X> X getProperty(E one, R type, E two, IProperty<X> prop) {
		return inner.getProperty(one, type, two, prop);
	}

	@Override
	public String edgeToString(E first, R second, E third, boolean includeEnds) {
		return inner.edgeToString(first, second, third, includeEnds);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(one, two, prop, get);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(one, prop, get);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(one, type, prop, get);
	}

	@Override
	public Set<E> getNeighbors(E node) {
		return inner.getNeighbors(node);
	}

	@Override
	public Set<E> getNeighbors(E node, R type) {
		return inner.getNeighbors(node, type);
	}

	@Override
	public E get(Object of) {
		return inner.get(of);
	}

	@Override
	public boolean containsEdge(Object one, Object two) {
		return inner.containsEdge(one, two);
	}

	@Override
	public boolean containsEdge(Object one, Object type, Object two) {
		return inner.containsEdge(one, type, two);
	}

	@Override
	public int degree(E node) {
		return inner.degree(node);
	}

	@Override
	public int degree(E node, R type) {
		return inner.degree(node, type);
	}

	@Override
	public boolean nodeHasConnections(Object one, R type) {
		return inner.nodeHasConnections(one, type);
	}

	@Override
	public Set<R> getEdgeTypesBetween(E one, E two) {
		return inner.getEdgeTypesBetween(one, two);
	}

	@Override
	public Set<R> getConnectingEdgeTypes(E node) {
		return inner.getConnectingEdgeTypes(node);
	}

	@Override
	public int edgeCount() {
		return inner.edgeCount();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return Iterators.unmodifiableIterator(inner.edgeIterator());
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes) {
		return Iterators.unmodifiableIterator(inner.edgeIterator(forTypes));
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode) {
		return Iterators.unmodifiableIterator(inner.outgoingEdges(forNode));
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode, R type) {
		return Iterators.unmodifiableIterator(inner.outgoingEdges(forNode, type));
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return Iterators
				.unmodifiableIterator(inner.edgeTraversalIteratorBFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return Iterators
				.unmodifiableIterator(edgeTraversalIteratorDFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return Iterators
				.unmodifiableIterator(nodeTraversalIteratorBFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return Iterators
				.unmodifiableIterator(nodeTraversalIteratorDFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public IRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return inner.traverseBFS(startPoint, allowedEdgeTypes, forEachNode, applyAcrossObject);
	}

	@Override
	public IRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return inner.traverseDFS(startPoint, allowedEdgeTypes, forEachNode, applyAcrossObject);
	}

	@Override
	public IRelationGraph<E, R> copy() {
		return ImmutableGraphView.of(inner.copy());
	}

	@Override
	public IRelationGraph<E, R> deepCopy(Function<E, E> cloner) {

		return ImmutableGraphView.of(inner.deepCopy(cloner));
	}

	@Override
	public <E2, R2 extends IInvertibleRelationType> ImmutableGraphView<E2, R2> mapCopy(Function<E, E2> nodeMapper,
			Function<R, R2> edgeMapper) {
		return ImmutableGraphView.of(inner.mapCopy(nodeMapper, edgeMapper));
	}

	@Override
	public ImmutableGraphView<E, R> subgraph(Iterable<? extends E> nodes) {
		return ImmutableGraphView.of(inner.subgraph(nodes));
	}

	@Override
	public ImmutableGraphView<E, R> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return ImmutableGraphView.of(inner.subgraph(nodes, edgePred));
	}

	@Override
	public String representation() {
		return this.representation(Object::toString);
	}

	@Override
	public String representation(Function<E, String> converter) {
		return "ImmutableView{" + inner.representation(converter) + "}";
	}

	@Override
	public String representation(Function<E, String> converter, Function<R, String> edgeConverter) {

		return "ImmutableView{" + inner.representation(converter, edgeConverter) + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImmutableGraphView gv) {
			return inner.equals(gv.inner);
		}
		return inner.equals(obj);
	}

	@Override
	public int hashCode() {
		return inner.hashCode();
	}

	@Override
	public String toString() {
		return "ImmutableView{" + inner.toString() + "}";
	}

}
