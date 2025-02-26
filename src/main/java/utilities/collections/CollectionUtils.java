package utilities.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class CollectionUtils {

	public static <T> ImmutableCollection<T> immutableCollection(Collection<T> th) {
		if (th instanceof ImmutableCollection) {
			return (ImmutableCollection<T>) th;
		}
		return new ImmutableCollection<>(th);
	}

	public static <E, X> ImmutableMappedCollection<E, X> immutableMappedCollection(Collection<? extends X> collection,
			Function<Object, X> outToIn, Function<X, E> inToOut) {
		return new ImmutableMappedCollection<>(collection, outToIn, inToOut);
	}

	public static <E, X> ImmutableMappedCollection<E, X> immutableMappedCollection(Collection<? extends X> collection,
			Class<? super E> outerClass, Function<E, X> outToIn, Function<X, E> inToOut) {
		return new ImmutableMappedCollection<>(collection, outToIn, inToOut, outerClass);
	}

	public static <E, X> ImmutableMappedCollection<E, X> immutableMappedCollection(Collection<? extends X> collection,
			Function<X, E> inToOut, Class<? super E> outerClass, Function<E, X> outToIn) {
		return new ImmutableMappedCollection<>(collection, outToIn, inToOut, outerClass);
	}

	public static <E, X> MappedCollection<E, X> mappedCollection(Collection<X> collection, Function<Object, X> outToIn,
			Function<X, E> inToOut) {
		return new MappedCollection<>(collection, outToIn, inToOut);
	}

	public static <E, X> MappedCollection<E, X> mappedCollection(Collection<X> collection, Function<X, E> inToOut,
			Class<? super E> outerClass, Function<E, X> outToIn) {
		return new MappedCollection<>(collection, outToIn, inToOut, outerClass);
	}

	public static <E, X> MappedCollection<E, X> mappedCollection(Collection<X> collection, Class<? super E> outerClass,
			Function<E, X> outToIn, Function<X, E> inToOut) {
		return new MappedCollection<>(collection, outToIn, inToOut, outerClass);
	}

	public static <X, E> SubCollectionIterator<E> of2(Collection<X> collection, Function<X, Collection<E>> access) {
		return new SubCollectionIterator<>(collection, (o) -> access.apply((X) o).iterator());
	}

	public static <X, E> SubCollectionIterator<E> of(Collection<X> collection, Function<X, Iterator<E>> access) {
		return new SubCollectionIterator<>(collection, (o) -> access.apply((X) o));
	}

	public static <E> SubCollectionIterator<E> ofMapOfMaps(Map<?, ? extends Map<?, E>> map) {
		return new SubCollectionIterator<E>(map.values(), SubCollectionIterator::fromCollection);
	}

	public static <E> SubCollectionIterator<E> ofMapCollection(Collection<? extends Map<?, E>> map) {
		return new SubCollectionIterator<>(map, SubCollectionIterator::mapValues);
	}

	public static <E, EI> MappedIterator<E, EI> mappedIterator(Iterator<? extends EI> ofIterator,
			Function<EI, E> mapper) {
		return new MappedIterator<>(ofIterator, mapper);
	}

	public static <E, EI> Iterable<E> mappedIterable(Iterable<? extends EI> of, Function<EI, E> mapper) {
		return () -> new MappedIterator<>(of.iterator(), mapper);
	}

}
