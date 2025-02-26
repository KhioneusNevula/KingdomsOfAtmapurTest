package thinker.concepts.general_types;

import thinker.concepts.IConcept;

/**
 * A general concept of a kind of event or idea
 * 
 * @author borah
 *
 */
public interface IPrincipleConcept extends IConcept {

	/**
	 * If this principle is an event type.
	 * 
	 * @return
	 */
	public boolean isEventType();

	/**
	 * Whether this principle involves something being destroyed
	 * 
	 * @return
	 */
	public boolean destructive();

	/**
	 * whether this principle involves something being created
	 * 
	 * @return
	 */
	public boolean creative();

	/**
	 * Whether this principle involves something undergoing a transformation
	 * 
	 * @return
	 */
	public boolean transformative();
}
