package thinker.concepts.general_types;

/**
 * A concept representing a single enum value
 * 
 * @author borah
 *
 */
public interface IEnumValueConcept extends IValueConcept {

	/**
	 * Return the quantity stored in this concept
	 * 
	 * @return
	 */
	@Override
	public Enum<?> getValue();

}
