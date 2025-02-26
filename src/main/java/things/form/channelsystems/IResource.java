package things.form.channelsystems;

/**
 * For things that can be present in an object; this can be materials like
 * blood, or things like a child, if related to pregnancy, or signals when
 * relating to nervous systems, or magic energy and similar things
 * 
 * @author borah
 * @param <E> indicates the way this is measured, e.g. materials in floats (by
 *            mass), or instances
 *
 */
public interface IResource<E extends Comparable<?>> {

	/**
	 * name of this resource
	 * 
	 * @return
	 */
	public String name();

	/**
	 * The value of this measure when empty; the minimum possible amounto f resource
	 * 
	 * @return
	 */
	public E getEmptyValue();

	/**
	 * The maximum "amount" of this resource possible
	 * 
	 * @return
	 */
	public E getMaxValue();

	/**
	 * The class that measurements of this resource are in
	 * 
	 * @return
	 */
	public Class<E> getMeasureClass();

	/**
	 * How to bring an instance of this resource together with anothr instance, e.g.
	 * blood combines through simple addition. Return null if this is not possible.
	 * 
	 * @param instance
	 * @param instance2
	 * @return
	 */
	public E add(E instance, E instance2);

	/**
	 * How to remove some instance of this resource from another; return null if not
	 * possible.
	 * 
	 * @param g
	 * @param l
	 * @return
	 */
	public E subtract(E g, E l);
}
