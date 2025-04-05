package things.form.visage;

import _utilities.property.IProperty;
import things.form.IPart;

/**
 * Property of an object that can be sensed; only accepts discrete values
 * 
 * @author borah
 *
 * @param <E>
 */
public interface ISensableProperty<E> extends IProperty<E> {

	/**
	 * Return the proper value of this property using the given part.
	 */
	public E getPropertyFromPart(IPart fromPart);

	/**
	 * Whether this is a complex sensable trait
	 * 
	 * @return
	 */
	public boolean isComplex();

	/**
	 * If this is a complex sensable trait, return the ID-vector of the trait based
	 * on the given part and maybe surrounding parts
	 * 
	 * @return
	 */
	public int[] getComplexScore(IPart part, IVisage<?> visage);

	/** If this property can only be sensed when focused */
	public boolean requiresFocus();

}
