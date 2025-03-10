package _utilities.collections;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class ImmutableCollection<T> extends AbstractCollection<T> {

	protected Collection<T> internal;

	ImmutableCollection(Collection<T> inner) {
		this.internal = inner;

	}

	@Override
	public Iterator<T> iterator() {
		return new ImIterator();
	}

	@Override
	public int size() {
		return internal.size();
	}

	@Override
	public boolean contains(Object o) {
		return internal.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return internal.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof ImmutableCollection) {
			return this.internal.equals(((ImmutableCollection) obj).internal);
		}
		return internal.equals(obj);
	}

	@Override
	public int hashCode() {
		return internal.hashCode();
	}

	@Override
	public String toString() {
		return "ImmutableView" + this.internal;
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		internal.forEach(action);
	}

	private class ImIterator implements Iterator<T> {
		private Iterator<T> other;

		private ImIterator() {
			this.other = internal.iterator();
		}

		@Override
		public boolean hasNext() {
			return other.hasNext();
		}

		@Override
		public T next() {
			return other.next();
		}
	}

	public static <T> ImmutableCollection<T> from(Collection<T> th) {
		if (th instanceof ImmutableCollection) {
			return (ImmutableCollection<T>) th;
		}
		return new ImmutableCollection<>(th);
	}

}
