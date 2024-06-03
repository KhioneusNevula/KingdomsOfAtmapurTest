package sim.interfaces;

import civilization.social.concepts.IConcept;

/**
 * A "type" of actor or phenomenon
 * 
 * @author borah
 *
 */
public interface IObjectType extends IConcept {

	public String name();

	/**
	 * The average unusualness of any distinctive example of this template
	 * 
	 * @return
	 */
	public float averageUniqueness();

}
