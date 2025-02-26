package utilities.collections;

import java.util.Set;
import java.util.function.Function;

/**
 * A collection that muses two functions to map each element of the internal
 * type to an equivalent of the external type as needed.
 * 
 * @author borah
 *
 * @param <E>
 * @param <X>
 */
public class MappedSet<E, X> extends MappedCollection<E, X> implements Set<E> {

	public MappedSet(Set<X> internal, Function<Object, X> externalToInternal, Function<X, E> internalToExternal) {
		super(internal, externalToInternal, internalToExternal);
	}

	public MappedSet(Set<X> internal, Function<E, X> externalToInternal, Function<X, E> internalToExternal,
			Class<? super E> externalClass) {
		super(internal, externalToInternal, internalToExternal, externalClass);
	}

	@Override
	public String toString() {
		return "MappedSetView" + internal;
	}

}
