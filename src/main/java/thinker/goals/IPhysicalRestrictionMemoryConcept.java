package thinker.goals;

import things.form.condition.IFormCondition;
import thinker.concepts.general_types.IMemoryConcept;

/**
 * A memory of failing to complete an action due to a lack of certain form
 * conditions
 */
public interface IPhysicalRestrictionMemoryConcept extends IMemoryConcept {

	/** Return what form condition was failed */
	public IFormCondition getFormCondition();

}
