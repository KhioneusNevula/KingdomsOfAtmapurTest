package _utilities.collections;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class CombinedSet<E> implements Set<E> {

	private Set<Collection<? extends E>> sets;

	public CombinedSet(Collection<? extends E>... iter) {
		sets = ImmutableSet.copyOf(iter);
	}

	public CombinedSet(Iterable<? extends Collection<? extends E>> iter) {
		sets = ImmutableSet.copyOf(iter);
	}

	@Override
	public int size() {
		return sets.parallelStream().mapToInt(Collection::size).sum();
	}

	@Override
	public boolean isEmpty() {
		return sets.parallelStream().allMatch(Collection::isEmpty);
	}

	@Override
	public boolean contains(Object o) {
		return sets.parallelStream().anyMatch((a) -> a.contains(o));
	}

	@Override
	public Iterator<E> iterator() {
		return sets.parallelStream().flatMap(Collection::stream).map((a) -> (E) a).distinct().iterator();
	}

	@Override
	public Object[] toArray() {
		return sets.parallelStream().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {

		return (T[]) sets.parallelStream().toArray((x) -> {
			try {
				return a.length <= x ? a : a.getClass().getConstructor(int.class).newInstance(x);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new IllegalArgumentException();
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

}
