package utilities;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

public class ImmutableCollection<T> extends AbstractCollection<T> {

	private Collection<T> inner;

	public ImmutableCollection(Collection<T> inner) {
		this.inner = inner;
	}

	@Override
	public Iterator<T> iterator() {
		return new ImIterator();
	}

	@Override
	public int size() {
		return inner.size();
	}

	@Override
	public boolean contains(Object o) {
		return inner.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return inner.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return inner.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		return inner.equals(obj);
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		inner.forEach(action);
	}

	private class ImIterator implements Iterator<T> {
		private Iterator<T> other;

		private ImIterator() {
			this.other = inner.iterator();
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

}
