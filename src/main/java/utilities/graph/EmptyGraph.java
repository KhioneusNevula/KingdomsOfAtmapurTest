package utilities.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import utilities.Triplet;

public class EmptyGraph<E, R extends IInvertibleRelationType> implements IRelationGraph<E, R> {
	public static final EmptyGraph INSTANCE = new EmptyGraph();

	public static <E, R extends IInvertibleRelationType> EmptyGraph<E, R> instance() {
		return (EmptyGraph<E, R>) INSTANCE;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {

		return true;
	}

	@Override
	public boolean contains(Object o) {
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Object[] toArray() {

		return Collections.emptySet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return Collections.emptySet().toArray(a);
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
		return c.isEmpty() ? true : false;
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
		throw new IllegalArgumentException(one + " " + two);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Consumer<X> get) {
		throw new IllegalArgumentException(one + " " + two);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Consumer<X> get) {
		throw new IllegalArgumentException(one + "");
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Consumer<X> get) {
		throw new IllegalArgumentException(one + "");
	}

	@Override
	public Collection<E> getNeighbors(E node) {
		throw new IllegalArgumentException(node + "");
	}

	@Override
	public Collection<E> getNeighbors(E node, R type) {
		throw new IllegalArgumentException(node + "");
	}

	@Override
	public boolean containsEdge(E one, E two) {

		return false;
	}

	@Override
	public boolean containsEdge(E one, R type, E two) {

		return false;
	}

	@Override
	public int degree(E node) {
		throw new IllegalArgumentException(node + "");
	}

	@Override
	public int degree(E node, R type) {
		throw new IllegalArgumentException(node + "");
	}

	@Override
	public boolean nodeHasConnections(E one, R type) {
		return false;
	}

	@Override
	public Collection<R> getEdgeTypesBetween(E one, E two) {
		throw new IllegalArgumentException(one + "" + two);
	}

	@Override
	public Collection<R> getConnectingEdgeTypes(E node) {
		throw new IllegalArgumentException(node + "");
	}

	@Override
	public int edgeCount() {
		return 0;
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public IRelationGraph<E, R> traverseBFS(E startPoint, Collection<R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public IRelationGraph<E, R> traverseDFS(E startPoint, Collection<R> allowedEdgeTypes, Consumer<E> forEachNode,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public String representation() {
		return "EmptyGraph{}";
	}

	@Override
	public String toString() {
		return representation();
	}

}
