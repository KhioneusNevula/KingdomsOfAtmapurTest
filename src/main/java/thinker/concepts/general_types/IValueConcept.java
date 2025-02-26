package thinker.concepts.general_types;

import thinker.concepts.IConcept;

/**
 * A concept representing a single integer number
 * 
 * @author borah
 *
 */
public interface IValueConcept extends IConcept {

	/**
	 * Return the quantity stored in this concept
	 * 
	 * @return
	 */
	public int getQuantity();
}
