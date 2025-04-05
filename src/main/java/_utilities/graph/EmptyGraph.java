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

import _utilities.couplets.Triplet;
import _utilities.property.IProperty;

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
	public String edgeToString(E first, R second, E third, boolean includeEnds) {
		throw new IllegalArgumentException(first + ", " + third);
	}

	@Override
	public Set<E> getNodeSetImmutable() {
		return Collections.emptySet();
	}

	@Override
	public Iterable<E> getBareNodes() {
		return Collections.emptySet();
	}

	@Override
	public E get(Object of) {
		return null;
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
		return null;
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get) {
		throw new IllegalArgumentException(one + " " + two);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get) {
		throw new IllegalArgumentException(one + "");
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get) {
		throw new IllegalArgumentException(one + "");
	}

	@Override
	public Set<E> getNeighbors(E node) {
		throw new IllegalArgumentException(node + "");
	}

	@Override
	public Set<E> getNeighbors(E node, R type) {
		throw new IllegalArgumentException(node + "");
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
	public int degree(E node) {
		return 0;
	}

	@Override
	public int degree(E node, R type) {
		return 0;
	}

	@Override
	public boolean nodeHasConnections(Object one, R type) {
		return false;
	}

	@Override
	public Set<R> getEdgeTypesBetween(E one, E two) {
		throw new IllegalArgumentException(one + "" + two);
	}

	@Override
	public Set<R> getOutgoingEdgeTypes(E node) {
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
	public Collection<R> getEdgeTypes() {
		return Collections.emptySet();
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes) {
		return edgeIterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode) {
		throw new NodeNotFoundException(forNode);
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode, R type) {
		return outgoingEdges(forNode);
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public IRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public IRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		throw new IllegalArgumentException(startPoint + "");
	}

	@Override
	public IRelationGraph<E, R> subgraph(Predicate<? super E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return this;
	}

	@Override
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes) {
		if (!nodes.iterator().hasNext()) {
			return this;
		}
		throw new IllegalArgumentException(nodes + " not in graph: {}");
	}

	@Override
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return this.subgraph(nodes);
	}

	@Override
	public String representation() {
		return "EmptyGraph{}";
	}

	@Override
	public String representation(Function<E, String> converter) {
		return this.representation();
	}

	@Override
	public String representation(Function<E, String> converter, Function<R, String> edgeConverter) {
		return this.representation();
	}

	@Override
	public String representation(Function<E, String> converter,
			BiFunction<Triplet<E, R, E>, Map<IProperty<?>, Object>, String> edgeConverter) {
		return this.representation();
	}

	@Override
	public String toString() {
		return representation();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof IRelationGraph rg) {
			return rg.isEmpty();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Collections.emptySet().hashCode();
	}

	@Override
	public EmptyGraph<E, R> copy() {
		return instance();
	}

	@Override
	public EmptyGraph<E, R> deepCopy(Function<E, E> cloner) {
		return instance();
	}

	@Override
	public <E2, R2 extends IInvertibleRelationType> EmptyGraph<E2, R2> mapCopy(Function<E, E2> nodeMapper,
			Function<R, R2> edgeMapper) {
		return instance();
	}

}
