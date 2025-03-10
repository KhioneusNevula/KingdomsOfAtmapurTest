package _utilities.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;

/**
 * A collection that muses two functions to map each element of the internal
 * type to an equivalent of the external type as needed.
 * 
 * @author borah
 *
 * @param <E>
 * @param <X>
 */
public class ImmutableMappedCollection<E, X> implements Collection<E> {

	private Collection<? extends X> internal;
	private Function<Object, X> exToIn;
	private Function<X, E> inToEx;
	private Class<? super E> clazz;

	public ImmutableMappedCollection(Collection<? extends X> internal, Function<Object, X> externalToInternal,
			Function<X, E> internalToExternal) {
		this.internal = internal;
		this.exToIn = externalToInternal;
		this.inToEx = internalToExternal;
		this.clazz = null;
	}

	public ImmutableMappedCollection(Collection<? extends X> internal, Function<E, X> externalToInternal,
			Function<X, E> internalToExternal, Class<? super E> externalClass) {
		this.internal = internal;
		this.exToIn = (o) -> externalToInternal.apply((E) o);
		this.inToEx = internalToExternal;
		this.clazz = externalClass;
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		if (clazz == null || clazz.isInstance(o)) {
			return internal.contains(exToIn.apply((E) o));
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return Iterators.unmodifiableIterator(Streams.stream(internal.iterator()).map(inToEx).iterator());
	}

	@Override
	public Object[] toArray() {
		return internal.stream().map(inToEx).toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return internal.stream().map(inToEx)
				.toArray((x) -> (T[]) Array.newInstance(a.getClass().getComponentType(), x));
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
		for (Object x : c) {
			if (!this.contains(x))
				return false;
		}
		return true;
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
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Collection) {
			if (internal instanceof List) {
				if (!(obj instanceof List))
					return false;
				List<X> list = (List<X>) this.internal;
				List other = (List) obj;
				if (list.size() != other.size())
					return false;
				for (int i = 0; i < list.size(); i++) {
					if (!inToEx.apply(list.get(i)).equals(other.get(i))) {
						return false;
					}
				}
				return true;
			}
			return this.size() == ((Collection<?>) obj).size() && this.containsAll((Collection<?>) obj);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) internal.stream().map(inToEx).collect(Collectors.summarizingInt(Object::hashCode)).getSum();
	}

	@Override
	public String toString() {
		return "ImmutableMappedView" + internal;
	}

}
