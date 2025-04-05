package thinker.concepts.general_types;

import thinker.concepts.IConcept;

/**
 * A concept representing an enumerable value (a number or enum)
 * 
 * @author borah
 *
 */
public interface IValueConcept extends IConcept {

	/**
	 * Return the value stored in this concept
	 * 
	 * @return
	 */
	public Comparable<?> getValue();
}
