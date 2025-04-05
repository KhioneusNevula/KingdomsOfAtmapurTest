package things.form.shape.property;

import things.form.shape.IShape;

/**
 * A property of a shape
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
	public Class<? super E> getType();

	/**
	 * Return a default value for this property for a given shape
	 * 
	 * @return
	 */
	public E getDefaultValue(IShape shap);

	/**
	 * Return a max value for this property for a given material. Return the default
	 * if this stat cannot be conceptualized as having a max value
	 * 
	 * @return
	 */
	public default E getMaxValue(IShape mat) {
		return this.getDefaultValue(mat);
	}

	/**
	 * Return a min value for this property for a given material. Return default if
	 * this stat cannot be conceptualized as having a min value
	 * 
	 * @return
	 */
	public default E getMinValue(IShape mat) {
		return this.getDefaultValue(mat);
	}

}
