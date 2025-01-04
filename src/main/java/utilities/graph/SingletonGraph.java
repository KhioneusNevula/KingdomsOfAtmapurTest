package utilities.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import com.google.common.collect.Iterators;

import utilities.Triplet;

public class SingletonGraph<E, R extends IInvertibleRelationType> implements IRelationGraph<E, R> {

	private E single;
	private Collection<E> singleton;

	public SingletonGraph(E singleElement) {
		this.single = singleElement;
		this.singleton = Collections.singleton(singleElement);
	}

	@Override
	public int size() {
		return 1;
	}

	/**
	 * Get the one element of this graph
	 * 
	 * @return
	 */
	public E getSingleElement() {
		return single;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean contains(Object o) {
		return single.equals(o);
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.singletonIterator(single);
	}

	@Override
	public Object[] toArray() {
		return singleton.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return singleton.toArray(a);
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
		return singleton.containsAll(c);
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
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		if (!this.single.equals(two)) {
			throw new IllegalArgumentException(two + " vs " + single);
		}
		return null;
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, EdgeProperty<X> prop, Consumer<X> get) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		if (!this.single.equals(two)) {
			throw new IllegalArgumentException(two + " vs " + single);
		}
	}

	@Override
	public <X> void forEachEdgeProperty(E one, EdgeProperty<X> prop, Consumer<X> get) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, EdgeProperty<X> prop, Consumer<X> get) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
	}

	@Override
	public Collection<E> getNeighbors(E node) {
		if (!this.single.equals(node)) {
			throw new IllegalArgumentException(node + " vs " + single);
		}
		return Collections.emptySet();
	}

	@Override
	public Collection<E> getNeighbors(E one, R type) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		return Collections.emptySet();
	}

	@Override
	public boolean containsEdge(Object one, Object two) {

		return false;
	}

	@Override
	public boolean containsEdge(Object one, R type, Object two) {

		return false;
	}

	@Override
	public int degree(E one) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		return 0;
	}

	@Override
	public int degree(E one, R type) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		return 0;
	}

	@Override
	public boolean nodeHasConnections(Object one, R type) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		return false;
	}

	@Override
	public Collection<R> getEdgeTypesBetween(E one, E two) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		if (!this.single.equals(two)) {
			throw new IllegalArgumentException(two + " vs " + single);
		}
		return Collections.emptySet();
	}

	@Override
	public Collection<R> getConnectingEdgeTypes(E one) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		return Collections.emptySet();
	}

	@Override
	public int edgeCount() {
		return 0;
	}

	@Override
	public Collection<E> getNodesImmutable() {
		return this.singleton;
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Collections.emptyIterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Collections.emptyIterator();
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Iterators.singletonIterator(single);
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Iterators.singletonIterator(single);
	}

	@Override
	public IRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		forEachNode.accept(startPoint);
		return new SingletonGraph<>(startPoint);
	}

	@Override
	public IRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<EdgeProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		forEachNode.accept(startPoint);
		return new SingletonGraph<>(startPoint);
	}

	@Override
	public IRelationGraph<E, R> subgraph(Collection<? extends E> nodes) {
		if (nodes.equals(this.singleton)) {
			return this;
		} else if (nodes.isEmpty()) {
			return EmptyGraph.instance();
		}
		throw new IllegalArgumentException(nodes + " not in graph: " + this.singleton);
	}

	@Override
	public SingletonGraph<E, R> copy() {
		return new SingletonGraph<>(this.single);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IRelationGraph g) {
			return g.size() == 1 && g.edgeCount() == 0 && single.equals(g.stream().findFirst().get());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return single.hashCode();
	}

	@Override
	public String toString() {
		return representation();
	}

	@Override
	public String representation() {
		return "SingletonGraph{" + this.single + "}";
	}

}
