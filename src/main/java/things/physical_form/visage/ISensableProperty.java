package things.physical_form.visage;

import java.util.Collection;

import things.physical_form.IVisage;
import things.physical_form.components.ISensor;

public interface ISensableProperty<E> {

	/**
	 * The type of data of this property
	 * 
	 * @return
	 */
	public Class<E> getType();

	/**
	 * The name of this property
	 * 
	 * @return
	 */
	public String name();

	/**
	 * What sensors can sense this property
	 * 
	 * @return
	 */
	public Collection<ISensor> getSensors();

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
	public int[] getComplexScore(IVisagePart part, IVisage<?> visage);

}
