package utilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class IteratorUtils {

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

}
