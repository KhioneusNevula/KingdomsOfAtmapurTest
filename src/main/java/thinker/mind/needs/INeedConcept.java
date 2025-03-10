package thinker.mind.needs;

import _utilities.property.IProperty;
import things.form.soma.component.IComponentPart;
import thinker.concepts.IConcept;
import thinker.goals.IGoalCondition;
import thinker.individual.IMindSpirit;

/**
 * A conceptual need
 * 
 * @author borah
 *
 */
public interface INeedConcept extends IProperty<Float>, IConcept {
	/**
	 * Whether this need is tied to the physical body, including things like hunger,
	 * but not including things like comfort
	 */
	public boolean isBiological();

	/**
	 * If this is a biological need, return the name of the associated system as a
	 * string.
	 */
	public String getSystemName();

	/**
	 * Return the level of need for the given spirit
	 * 
	 * @param forSpirit
	 * @return
	 */
	public float getNeedLevel(IMindSpirit forSpirit, IComponentPart bodyPart);

	/**
	 * Returns the graph of required states of affairs this need has and whether
	 * those requirements are obligatory or just preferred
	 * 
	 * @return
	 */
	public IGoalCondition getRequirements();

}
