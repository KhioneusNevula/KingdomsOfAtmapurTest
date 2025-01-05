package things.form.soma.stats;

import things.form.soma.component.IComponentPart;

/**
 * A stat relevant to a specific part ability
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
