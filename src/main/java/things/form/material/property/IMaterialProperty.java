package things.form.material.property;

import things.form.material.IMaterial;

/**
 * A property of a material
 * 
 * @author borah
 *
 * @param <E> the kind of value this property has
 */
public interface IMaterialProperty<E> {

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
	 * Return a default value for this property for a given material
	 * 
	 * @return
	 */
	public E getDefaultValue(IMaterial mat);

	/**
	 * Return a max value for this property for a given material. Return the default
	 * if this stat cannot be conceptualized as having a max value
	 * 
	 * @return
	 */
	public default E getMaxValue(IMaterial mat) {
		return this.getDefaultValue(mat);
	}

	/**
	 * Return a min value for this property for a given material. Return the default
	 * if this stat cannot be conceptualized as having a min value
	 * 
	 * @return
	 */
	public default E getMinValue(IMaterial mat) {
		return this.getDefaultValue(mat);
	}

}
