package thinker.mind.needs;

import java.util.function.BiFunction;

import things.form.soma.component.IComponentPart;
import things.spirit.ISpirit;
import thinker.goals.IGoalCondition;

public enum Need implements INeedConcept {
	HEALTH((spirit, part) -> {
		// TODO health need
		return 0f;
	});

	private BiFunction<ISpirit, IComponentPart, Float> checker;
	private NeedGoalCondition cond;

	private Need(BiFunction<ISpirit, IComponentPart, Float> checker) {
		this.checker = checker;
		this.cond = new NeedGoalCondition(this);
	}

	@Override
	public IGoalCondition getRequirements() {
		return cond;
	}

	@Override
	public String getPropertyName() {
		return "need_" + name().toLowerCase();
	}

	@Override
	public Float defaultValue() {
		return 1f;
	}

	@Override
	public Class<? super Float> getType() {
		return float.class;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.NEED;
	}

	@Override
	public String getUnderlyingName() {
		return getPropertyName();
	}

	@Override
	public boolean isBiological() {
		return false;
	}

	@Override
	public float getNeedLevel(ISpirit forSpirit, IComponentPart body) {
		return checker.apply(forSpirit, body);
	}

	@Override
	public String toString() {
		return "<:" + this.getPropertyName() + ":>";
	}

}
