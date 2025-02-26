package thinker.mind.needs;

import things.form.soma.component.IComponentPart;
import things.spirit.ISpirit;
import thinker.concepts.IConcept;
import thinker.goals.IGoalCondition;
import utilities.property.IProperty;

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
	 * Return the level of need for the given spirit
	 * 
	 * @param forSpirit
	 * @return
	 */
	public float getNeedLevel(ISpirit forSpirit, IComponentPart bodyPart);

	/**
	 * Returns the graph of required states of affairs this need has and whether
	 * those requirements are obligatory or just preferred
	 * 
	 * @return
	 */
	public IGoalCondition getRequirements();

}
