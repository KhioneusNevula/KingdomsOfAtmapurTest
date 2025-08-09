package thinker.concepts.application;

import things.form.IForm;
import things.form.condition.IFormCondition;
import things.interfaces.UniqueType;
import thinker.concepts.IConcept;

/**
 * Labels something using a simple Form Condition. The Condition is of course
 * contractually bound to be sensable, not based on physical properties.
 */
public class FormConditionLabelApplier implements IConceptApplier {

	private IFormCondition condition;

	/**
	 * 
	 * @param condition
	 */
	public FormConditionLabelApplier(IFormCondition condition) {
		this.condition = condition;
	}

	@Override
	public UniqueType forType() {
		return UniqueType.FORM;
	}

	@Override
	public boolean applies(Object forThing, IConcept c) {
		if (forThing instanceof IForm forma) {
			if (condition.isBodyRequirement()) {
				return true;
			}
			return condition.test(forma);
		}
		throw new UnsupportedOperationException("Only can be applied to forms!");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof FormConditionLabelApplier la) {
			return condition.equals(la.condition);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode() + condition.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" + this.condition + "}";
	}

}
