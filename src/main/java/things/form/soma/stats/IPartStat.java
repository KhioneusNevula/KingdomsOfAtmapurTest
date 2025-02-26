package things.form.soma.stats;

import things.form.soma.component.IComponentPart;

/**
 * A stat relevant to a specific part ability. Should have some kind of ordering
 * permitting one stat to be higher than another
 * 
 * @author borah
 *
 * @param <E>
 */
public interface IPartStat<E> {
	/**
	 * The name of this stat
	 * 
	 * @return
	 */
	public String name();

	/**
	 * The default value of this stat (i.e. for a part that does not have this stat)
	 * 
	 * @param part
	 * @return
	 */
	public E getDefaultValue(IComponentPart part);

	/**
	 * The data type of this stat
	 * 
	 * @return
	 */
	public Class<? super E> getType();

	/**
	 * Compares two instances of a given stat
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public default int compare(E one, E two) {
		if (one instanceof Comparable com) {
			return ((Comparable) one).compareTo((Comparable) two);
		}
		throw new UnsupportedOperationException("Unimplemented comparator for stats");
	}

	/**
	 * Aggregate multiple values of this stat into one, which is useful for some
	 * kinds of uses. This may be summing, averaging, etc
	 * 
	 * @param values
	 * @return
	 */
	public E aggregate(Iterable<E> values);

	/**
	 * Return val1 with subVal "removed" from it, i.e. whatever aggregation is used,
	 * it is subtracted.
	 * 
	 * @param val1
	 * @param subVal
	 * @param count  how many values were aggregated together before the extracted
	 *               one was removed
	 * @return
	 */
	public E extract(E val1, E subVal, int count);
}
