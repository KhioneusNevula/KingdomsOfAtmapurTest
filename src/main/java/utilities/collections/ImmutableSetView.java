package utilities.collections;

import java.util.Set;

/**
 * Immutable collection view specifically interpreted as a set
 * 
 * @author borah
 *
 * @param <T>
 */
public class ImmutableSetView<T> extends ImmutableCollection<T> implements Set<T> {

	ImmutableSetView(Set<T> inner) {
		super(inner);

	}

	public static <T> ImmutableSetView<T> from(Set<T> th) {
		if (th instanceof ImmutableSetView) {
			return (ImmutableSetView<T>) th;
		}
		return new ImmutableSetView<>(th);
	}

}
