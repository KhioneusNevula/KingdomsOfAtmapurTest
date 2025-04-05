package _utilities.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.base.Functions;
import com.google.common.collect.Iterators;

import _utilities.couplets.Triplet;
import _utilities.property.IProperty;

public class SingletonGraph<E, R extends IInvertibleRelationType> implements IRelationGraph<E, R> {

	private E single;
	private Set<E> singleton;

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
	public <X> X getProperty(E one, R type, E two, IProperty<X> prop) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		if (!this.single.equals(two)) {
			throw new IllegalArgumentException(two + " vs " + single);
		}
		return null;
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		if (!this.single.equals(two)) {
			throw new IllegalArgumentException(two + " vs " + single);
		}
		throw new IllegalArgumentException(one + " is identical to " + two);
	}

	@Override
	public String edgeToString(E first, R second, E third, boolean includeEnds) {
		if (!this.single.equals(first)) {
			throw new IllegalArgumentException(first + " vs " + single);
		}
		if (!this.single.equals(third)) {
			throw new IllegalArgumentException(third + " vs " + single);
		}
		throw new IllegalArgumentException(first + " is identical to " + third);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
	}

	@Override
	public Set<E> getNeighbors(E node) {
		if (!this.single.equals(node)) {
			throw new IllegalArgumentException(node + " vs " + single);
		}
		return Collections.emptySet();
	}

	@Override
	public Set<E> getNeighbors(E one, R type) {
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
	public boolean containsEdge(Object one, Object type, Object two) {

		return false;
	}

	@Override
	public int degree(E one) {
		return 0;
	}

	@Override
	public int degree(E one, R type) {
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
	public E get(Object of) {
		if (of.equals(this.single)) {
			return this.single;
		}
		return null;
	}

	@Override
	public Set<R> getEdgeTypesBetween(E one, E two) {
		if (!this.single.equals(one)) {
			throw new IllegalArgumentException(one + " vs " + single);
		}
		if (!this.single.equals(two)) {
			throw new IllegalArgumentException(two + " vs " + single);
		}
		return Collections.emptySet();
	}

	@Override
	public Set<R> getOutgoingEdgeTypes(E one) {
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
	public Set<E> getNodeSetImmutable() {
		return this.singleton;
	}

	@Override
	public Iterable<E> getBareNodes() {
		return this.singleton;
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return Collections.emptyIterator();
	}

	@Override
	public Collection<R> getEdgeTypes() {
		return Collections.emptySet();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes) {
		return edgeIterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode) {
		if (!forNode.equals(this.single))
			throw new NodeNotFoundException(forNode);
		return Collections.emptyIterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode, R type) {
		return outgoingEdges(forNode);
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Collections.emptyIterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Collections.emptyIterator();
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Iterators.singletonIterator(single);
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		return Iterators.singletonIterator(single);
	}

	@Override
	public IRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		forEachNode.accept(startPoint);
		return new SingletonGraph<>(startPoint);
	}

	@Override
	public IRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		if (!startPoint.equals(this.single))
			throw new IllegalArgumentException(startPoint + " vs " + single);
		forEachNode.accept(startPoint);
		return new SingletonGraph<>(startPoint);
	}

	@Override
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes) {
		Iterator<? extends E> niter = nodes.iterator();
		if (niter.hasNext() && niter.next().equals(this.singleton) && !niter.hasNext()) {
			return this;
		} else if (!nodes.iterator().hasNext()) {
			return EmptyGraph.instance();
		}
		throw new IllegalArgumentException("Provided nodes not in graph: {" + this.singleton + "}");
	}

	@Override
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return this.subgraph(nodes);
	}

	@Override
	public IRelationGraph<E, R> subgraph(Predicate<? super E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		if (nodes.test(this.single)) {
			return this;
		}
		return EmptyGraph.instance();
	}

	@Override
	public SingletonGraph<E, R> copy() {
		return new SingletonGraph<>(this.single);
	}

	@Override
	public SingletonGraph<E, R> deepCopy(Function<E, E> cloner) {
		return mapCopy(cloner, Functions.identity());
	}

	@Override
	public <E2, R2 extends IInvertibleRelationType> SingletonGraph<E2, R2> mapCopy(Function<E, E2> nodeMapper,
			Function<R, R2> edgeMapper) {
		return new SingletonGraph<E2, R2>(nodeMapper.apply(single));
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
	public String representation(Function<E, String> converter, Function<R, String> edgeConverter) {
		return this.representation(converter);
	}

	@Override
	public String representation(Function<E, String> converter,
			BiFunction<Triplet<E, R, E>, Map<IProperty<?>, Object>, String> edgeConverter) {
		return representation(converter);
	}

	@Override
	public String representation(Function<E, String> converter) {
		return "SingletonGraph{" + converter.apply(this.single) + "}";
	}

	@Override
	public String representation() {
		return representation(Object::toString);
	}

}
