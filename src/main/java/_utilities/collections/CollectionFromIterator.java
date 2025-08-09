package _utilities.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.Streams;

public class CollectionFromIterator<E> implements Set<E> {

	private Supplier<Iterator<E>> supplier;

	public CollectionFromIterator(Supplier<Iterator<E>> iter) {
		this.supplier = iter;
	}

	@Override
	public int size() {
		return (int) Streams.stream(supplier.get()).count();
	}

	@Override
	public boolean isEmpty() {
		return !supplier.get().hasNext();
	}

	@Override
	public boolean contains(Object o) {
		return Streams.stream(supplier.get()).anyMatch((e) -> e.equals(o));
	}

	@Override
	public Iterator<E> iterator() {
		return supplier.get();
	}

	@Override
	public Object[] toArray() {
		return Streams.stream(supplier.get()).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return Streams.stream(supplier.get()).toArray((x) -> a.length >= x ? a : (T[]) new Object[x]);
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
		return Streams.stream(supplier.get()).filter((x) -> c.contains(x)).count() == c.size();
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

}
