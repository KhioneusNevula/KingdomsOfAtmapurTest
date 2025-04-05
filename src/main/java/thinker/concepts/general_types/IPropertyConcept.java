package thinker.concepts.general_types;

import _utilities.property.IProperty;

/**
 * A property-concept.
 * 
 * @author borah
 *
 */
public interface IPropertyConcept extends IDescriptiveConcept, IProperty<Boolean> {

	/**
	 * Whether this concept encodes an identity belonging to a group
	 * 
	 * @return
	 */
	public boolean isIdentity();

	/** Whether this concept can have enumerable or numeric values */
	public boolean isEnumerable();

}
