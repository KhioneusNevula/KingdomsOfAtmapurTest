package _utilities.graph;

import java.lang.reflect.InvocationTargetException;
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
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;

import _utilities.collections.CollectionUtils;
import _utilities.collections.CombinedCollection;
import _utilities.collections.CombinedSet;
import _utilities.collections.ImmutableSetView;
import _utilities.couplets.Triplet;
import _utilities.property.IProperty;

/**
 * This graph view creates the illusion of a conglomeration of multiple graphs,
 * allowing the backing graphs to be used independently and this graph updating
 * to reflect them accordingly. The nature of this graph causes that edges and
 * nodes are often "duplicated" when iterating, due to appearing in both the
 * supergraph and subgraph.
 */
public class GraphCombinedView<E, R extends IInvertibleRelationType> implements IRelationGraph<E, R> {

	protected Set<IRelationGraph<E, R>> inners;

	public GraphCombinedView(Iterable<IRelationGraph<E, R>> gras) {
		inners = ImmutableSet.copyOf(gras);
	}

	@Override
	public int size() {
		return inners.parallelStream().mapToInt(Collection::size).sum();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return inners.parallelStream().anyMatch((i) -> i.contains(o));
	}

	@Override
	public Iterator<E> iterator() {
		return CollectionUtils.of(inners, Collection::iterator);
	}

	@Override
	public Object[] toArray() {
		return inners.parallelStream().flatMap((a) -> a.stream()).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return inners.parallelStream().flatMap((x) -> x.stream()).toArray((s) -> {
			try {
				return a.length >= s ? a : (T[]) a.getClass().getConstructor(int.class).newInstance(s);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new IllegalStateException();
			}
		});
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
		return c.parallelStream().allMatch(this::contains);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes) {
		return new GraphCombinedView<>(() -> inners.parallelStream().map((i) -> i.subgraph(nodes)).iterator());
	}

	@Override
	public IRelationGraph<E, R> subgraph(Iterable<? extends E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return new GraphCombinedView<>(
				() -> inners.parallelStream().map((i) -> i.subgraph(nodes, edgePred)).iterator());
	}

	@Override
	public IRelationGraph<E, R> subgraph(Predicate<? super E> nodes, Predicate<Triplet<E, R, E>> edgePred) {
		return new GraphCombinedView<>(
				() -> inners.parallelStream().map((i) -> i.subgraph(nodes, edgePred)).iterator());
	}

	@Override
	public <X> X getProperty(E one, R type, E two, IProperty<X> prop) {
		return inners.parallelStream()
				.filter((a) -> a.containsEdge(one, type, two) && a.getProperty(one, type, two, prop) != null)
				.map((x) -> x.getProperty(one, type, two, prop)).findFirst().orElse(null);
	}

	@Override
	public <X> void forEachEdgeProperty(E one, E two, IProperty<X> prop, Consumer<X> get) {
		inners.stream().forEach((a) -> a.forEachEdgeProperty(one, two, prop, get));

	}

	@Override
	public <X> void forEachEdgeProperty(E one, IProperty<X> prop, Consumer<X> get) {
		inners.stream().forEach((a) -> a.forEachEdgeProperty(one, prop, get));
	}

	@Override
	public <X> void forEachEdgeProperty(E one, R type, IProperty<X> prop, Consumer<X> get) {
		inners.stream().forEach((a) -> a.forEachEdgeProperty(one, type, prop, get));
	}

	@Override
	public Set<E> getNeighbors(E node) {
		return inners.parallelStream().flatMap((i) -> i.getNeighbors(node).stream()).collect(Collectors.toSet());
	}

	@Override
	public Set<E> getNeighbors(E node, R type) {
		return inners.parallelStream().flatMap((i) -> i.getNeighbors(node, type).stream()).collect(Collectors.toSet());
	}

	@Override
	public boolean containsEdge(Object one, Object two) {
		return inners.parallelStream().anyMatch((i) -> i.containsEdge(one, two));
	}

	@Override
	public boolean containsEdge(Object one, Object type, Object two) {
		return inners.parallelStream().anyMatch((i) -> i.containsEdge(one, type, two));
	}

	@Override
	public int degree(E node) {
		return inners.parallelStream().mapToInt((i) -> i.degree(node)).sum();
	}

	@Override
	public int degree(E node, R type) {
		return inners.parallelStream().mapToInt((i) -> i.degree(node, type)).sum();
	}

	@Override
	public boolean nodeHasConnections(Object one, R type) {
		return inners.parallelStream().anyMatch((a) -> a.nodeHasConnections(one, type));
	}

	@Override
	public Set<R> getEdgeTypesBetween(E one, E two) {
		return inners.parallelStream().flatMap((i) -> i.getEdgeTypesBetween(one, two).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public Set<R> getOutgoingEdgeTypes(E node) {
		return inners.parallelStream().flatMap((i) -> i.getOutgoingEdgeTypes(node).stream())
				.collect(Collectors.toSet());
	}

	@Override
	public int edgeCount() {
		return inners.parallelStream().mapToInt(IRelationGraph::edgeCount).sum();
	}

	@Override
	public Set<E> getNodeSetImmutable() {
		return ImmutableSetView.from(new CombinedSet<E>(
				(Iterable<Set<E>>) () -> inners.stream().map(IRelationGraph::getNodeSetImmutable).iterator()));
	}

	@Override
	public Iterable<E> getBareNodes() {
		return () -> inners.stream().flatMap((r) -> Streams.stream(r.getBareNodes())).iterator();
	}

	@Override
	public E get(Object of) {
		return inners.parallelStream().filter((i) -> i.contains(of)).findAny().orElseThrow().get(of);
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator() {
		return inners.parallelStream().flatMap((i) -> Streams.stream(i.edgeIterator())).iterator();
	}

	@Override
	public Collection<R> getEdgeTypes() {
		return new CombinedCollection<>(inners.stream().map((e) -> e.getEdgeTypes()).iterator());
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeIterator(Collection<? extends R> forTypes) {
		return inners.parallelStream().flatMap((i) -> Streams.stream(i.edgeIterator(forTypes))).iterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode) {
		return inners.parallelStream().flatMap((i) -> Streams.stream(i.outgoingEdges(forNode))).iterator();
	}

	@Override
	public Iterator<Triplet<E, R, E>> outgoingEdges(E forNode, R type) {
		return inners.parallelStream().flatMap((i) -> Streams.stream(i.outgoingEdges(forNode, type))).iterator();
	}

	@Override
	public String representation() {
		return "<" + this.getClass().getSimpleName() + ">"
				+ inners.stream().map(IRelationGraph::representation).collect(Collectors.toSet());
	}

	@Override
	public String representation(Function<E, String> converter) {
		return "<" + this.getClass().getSimpleName() + ">"
				+ inners.stream().map((i) -> i.representation(converter)).collect(Collectors.toSet());
	}

	@Override
	public String representation(Function<E, String> converter, Function<R, String> edgeConverter) {
		return "<" + this.getClass().getSimpleName() + ">"
				+ inners.stream().map((i) -> i.representation(converter, edgeConverter)).collect(Collectors.toSet());
	}

	@Override
	public String representation(Function<E, String> converter,
			BiFunction<Triplet<E, R, E>, Map<IProperty<?>, Object>, String> edgeConverter) {
		return "<" + this.getClass().getSimpleName() + ">"
				+ inners.stream().map((i) -> i.representation(converter, edgeConverter)).collect(Collectors.toSet());
	}

	@Override
	public IRelationGraph<E, R> traverseBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return new GraphCombinedView<>(() -> inners.stream().filter((a) -> a.contains(startPoint))
				.map((a) -> a.traverseBFS(startPoint, allowedEdgeTypes, forEachNode, applyAcrossObject)).iterator());
	}

	@Override
	public IRelationGraph<E, R> traverseDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			Consumer<E> forEachNode, BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return new GraphCombinedView<>(() -> inners.stream().filter((a) -> a.contains(startPoint))
				.map((a) -> a.traverseDFS(startPoint, allowedEdgeTypes, forEachNode, applyAcrossObject)).iterator());
	}

	@Override
	public Iterator<E> nodeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {
		return CollectionUtils.of(inners,
				(i) -> i.contains(startPoint)
						? i.nodeTraversalIteratorBFS(startPoint, allowedEdgeTypes, applyAcrossObject)
						: Collections.emptyIterator());
	}

	@Override
	public Iterator<E> nodeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObj) {

		return CollectionUtils.of(inners,
				(i) -> i.contains(startPoint) ? i.nodeTraversalIteratorDFS(startPoint, allowedEdgeTypes, applyAcrossObj)
						: Collections.emptyIterator());
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorBFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return CollectionUtils.of(inners,
				(i) -> i.contains(startPoint)
						? i.edgeTraversalIteratorBFS(startPoint, allowedEdgeTypes, applyAcrossObject)
						: Collections.emptyIterator());
	}

	@Override
	public Iterator<Triplet<E, R, E>> edgeTraversalIteratorDFS(E startPoint, Collection<? extends R> allowedEdgeTypes,
			BiPredicate<IProperty<?>, Object> applyAcrossObject) {

		return CollectionUtils.of(inners,
				(i) -> i.contains(startPoint)
						? i.edgeTraversalIteratorDFS(startPoint, allowedEdgeTypes, applyAcrossObject)
						: Collections.emptyIterator());
	}

	@Override
	public IRelationGraph<E, R> copy() {
		RelationGraph<E, R> ng = new RelationGraph<>();
		inners.stream().forEach((a) -> ng.addAll(a));
		return ng;
	}

	@Override
	public IRelationGraph<E, R> deepCopy(Function<E, E> cloner) {
		RelationGraph<E, R> ng = new RelationGraph<>();
		inners.stream().forEach((a) -> ng.addAll(a.deepCopy(cloner)));
		return ng;
	}

	@Override
	public <E2, R2 extends IInvertibleRelationType> IRelationGraph<E2, R2> mapCopy(Function<E, E2> nodeMapper,
			Function<R, R2> edgeMapper) {
		RelationGraph<E2, R2> ng = new RelationGraph<>();
		inners.stream().forEach((a) -> ng.addAll(a.mapCopy(nodeMapper, edgeMapper)));
		return ng;
	}

	@Override
	public String edgeToString(E first, R second, E third, boolean includeEnds) {
		return inners.stream().filter((a) -> a.containsEdge(first, second, third)).findFirst()
				.orElse(EmptyGraph.instance()).edgeToString(first, second, third, includeEnds);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + this.inners;
	}

}
