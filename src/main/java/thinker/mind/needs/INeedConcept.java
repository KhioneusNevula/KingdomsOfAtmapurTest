package thinker.mind.needs;

import _utilities.property.IProperty;
import metaphysics.spirit.ISpirit;
import things.form.soma.component.IComponentPart;
import thinker.concepts.IConcept;
import thinker.goals.IGoalConcept;

/**
 * A conceptual need
 * 
 * @author borah
 *
 */
public interface INeedConcept extends IConcept {

	/**
	 * Whether this need is tied to the physical body, including things like hunger,
	 * but not including things like comfort
	 */
	public boolean isBiological();

	/**
	 * Returns the graph of required states of affairs this need has and whether
	 * those requirements are obligatory or just preferred
	 * 
	 * @return
	 */
	public IGoalConcept getRequirements();

	/** Name of this need in simple terms */
	public String getSimpleName();

}
