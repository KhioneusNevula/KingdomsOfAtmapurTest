package _utilities.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A filtered view of a collection that is read-only and only accesses elements
 * fitting the filter. Technically implements Set<>
 */
public class FilteredCollectionView<E> implements Set<E> {

	private Collection<E> inner;
	private Predicate<Object> filter;

	public FilteredCollectionView(Collection<E> iner, Predicate<Object> filterBy) {
		inner = iner;
		filter = filterBy;
	}

	@Override
	public int size() {
		return (int) inner.stream().filter(filter).count();
	}

	@Override
	public boolean isEmpty() {
		return inner.stream().noneMatch(filter);
	}

	@Override
	public boolean contains(Object o) {
		return filter.test(o) && inner.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return inner.stream().filter(filter).iterator();
	}

	@Override
	public Object[] toArray() {
		return inner.stream().filter(filter).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return inner.stream().filter(filter).toArray((i) -> i <= a.length ? a : (T[]) new Object[i]);
	}

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException("Read-only");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Read-only");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return c.stream().allMatch((a) -> filter.test(a) && inner.contains(a));
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("Read-only");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Read-only");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Read-only");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Read-only");
	}

}
