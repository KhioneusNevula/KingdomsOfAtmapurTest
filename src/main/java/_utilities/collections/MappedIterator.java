package _utilities.collections;

import java.util.Iterator;
import java.util.function.Function;

public class MappedIterator<E, EI> implements Iterator<E> {

	private Iterator<? extends EI> inner;
	private Function<EI, E> inToEx;

	MappedIterator(Iterator<? extends EI> inner, Function<EI, E> inToEx) {
		this.inner = inner;
		this.inToEx = inToEx;
	}

	@Override
	public boolean hasNext() {
		return inner.hasNext();
	}

	@Override
	public E next() {
		return inToEx.apply(inner.next());
	}

	@Override
	public void remove() {
		inner.remove();
	}

	/**
	 * Returns the function that converts inner values
	 * 
	 * @return
	 */
	public Function<EI, E> getConverter() {
		return inToEx;
	}

}
