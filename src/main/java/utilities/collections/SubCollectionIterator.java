package utilities.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * An iterator which iterates through elements of a collection which are
 * collections themselves
 * 
 * @author borah
 *
 * @param <E>
 */
public class SubCollectionIterator<E> implements Iterator<E> {

	private Iterator<?> mapIter;
	private Iterator<E> valueIter;
	/**
	 * Specifically for the remove function; in case we want to go back to the
	 * previous iterator to remove from it
	 */
	private Iterator<E> prevIter;
	private boolean hasNext;
	private Function<Object, Iterator<E>> access;

	static <E> Iterator<E> fromCollection(Object o) {
		return ((Collection<E>) o).iterator();
	}

	static <E> Iterator<E> mapValues(Object o) {
		return ((Map<?, E>) o).values().iterator();
	}

	SubCollectionIterator(Collection<?> map, Function<Object, Iterator<E>> accessElements) {
		this.mapIter = map.iterator();
		this.access = accessElements;
		this.hasNext = false;
		if (mapIter.hasNext()) {
			valueIter = access.apply(mapIter.next());
			if (valueIter.hasNext()) {
				this.hasNext = true;
			}
		}
	}

	@Override
	public void remove() {
		try {
			valueIter.remove();
		} catch (IllegalStateException e) {
			if (prevIter == null) {
				throw e;
			}
			prevIter.remove();
		}
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public E next() {
		if (valueIter == null) {
			throw new NoSuchElementException();
		}
		if (!valueIter.hasNext()) {
			prevIter = valueIter;
			if (mapIter.hasNext()) {
				valueIter = access.apply(mapIter.next());
				if (valueIter.hasNext()) {
					return valueIter.next();
				} else {
					throw new NoSuchElementException();
				}
			} else {
				valueIter = null;
				throw new NoSuchElementException();
			}
		} else {
			return valueIter.next();
		}
	}

}
