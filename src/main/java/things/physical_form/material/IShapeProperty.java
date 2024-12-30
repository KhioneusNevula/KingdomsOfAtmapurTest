package things.physical_form.material;

/**
 * A property of a material
 * 
 * @author borah
 *
 * @param <E> the kind of value this property has
 */
public interface IShapeProperty<E> {

	/**
	 * The name of this property
	 * 
	 * @return
	 */
	public String name();

	/**
	 * The type of this property, e.g. a binary property should return boolean.class
	 * 
	 * @return
	 */
	public Class<E> getType();

	/**
	 * Return a default value for this property for a given material
	 * 
	 * @return
	 */
	public E getDefaultValue(IShape shap);

}
