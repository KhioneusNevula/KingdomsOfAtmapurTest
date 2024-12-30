package utilities.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import com.google.common.collect.Iterators;

import utilities.Triplet;

public class ImmutableGraphView<E, R extends IInvertibleRelationType> implements IRelationGraph<E, R> {

	private IRelationGraph<E, R> inner;

	public ImmutableGraphView(IRelationGraph<E, R> inner) {
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
	public <X> X getProperty(E one, R type, E two, EdgeProperty<X> prop) {
		return inner.getProperty(one, type, two, prop);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(one, two, prop, get);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(one, prop, get);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Consumer<X> get) {
		inner.forEachEdgeProperty(one, type, prop, get);
	}

	@Override
	public Collection<E> getNeighbors(E node) {
		return inner.getNeighbors(node);
	}

	@Override
	public Collection<E> getNeighbors(E node, R type) {
		return inner.getNeighbors(node, type);
	}

	@Override
	public boolean containsEdge(E one, E two) {
		return inner.containsEdge(one, two);
	}

	@Override
	public boolean containsEdge(E one, R type, E two) {
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
	public boolean nodeHasConnections(E one, R type) {
		return inner.nodeHasConnections(one, type);
	}

	@Override
	public Collection<R> getEdgeTypesBetween(E one, E two) {
		return inner.getEdgeTypesBetween(one, two);
	}

	@Override
	public Collection<R> getConnectingEdgeTypes(E node) {
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
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {

		return Iterators
				.unmodifiableIterator(inner.edgeTraversalIteratorBFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		return Iterators
				.unmodifiableIterator(edgeTraversalIteratorDFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {

		return Iterators
				.unmodifiableIterator(nodeTraversalIteratorBFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		return Iterators
				.unmodifiableIterator(nodeTraversalIteratorDFS(startPoint, allowedEdgeTypes, applyAcrossObject));
	}

	@Override
	public IRelationGraph<E, R> traverseBFS(E startPoint, Collection<R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {

		return inner.traverseBFS(startPoint, allowedEdgeTypes, forEachNode, applyAcrossObject);
	}

	@Override
	public IRelationGraph<E, R> traverseDFS(E startPoint, Collection<R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		return inner.traverseDFS(startPoint, allowedEdgeTypes, forEachNode, applyAcrossObject);
	}

	@Override
	public String representation() {
		return "ImmutableView{" + inner.representation() + "}";
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
