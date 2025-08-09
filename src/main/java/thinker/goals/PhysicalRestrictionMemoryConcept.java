package thinker.goals;

import things.form.condition.IFormCondition;

/** Implementation of {@link IPhysicalRestrictionMemoryConcept} */
public class PhysicalRestrictionMemoryConcept implements IPhysicalRestrictionMemoryConcept {

	private IFormCondition condition;
	private long time;

	public PhysicalRestrictionMemoryConcept(IFormCondition con, long time) {
		this.condition = con;
		this.time = time;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	@Override
	public String getUnderlyingName() {
		return "physical_restriction_memory_(" + this.getFormCondition().toString() + ")";
	}

	@Override
	public IFormCondition getFormCondition() {
		return this.condition;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof IPhysicalRestrictionMemoryConcept iprmc
				&& iprmc.getFormCondition().equals(this.getFormCondition()) && iprmc.getTime() == this.getTime();
	}

	@Override
	public int hashCode() {
		return condition.hashCode() + this.getClass().hashCode() + Long.hashCode(getTime());
	}

	public String toString() {
		return this.getClass().getSimpleName() + "{condition=" + this.condition + ",time=" + this.getTime() + "}";
	}

}
