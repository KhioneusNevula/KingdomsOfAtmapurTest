package things.status_effect;

import things.form.soma.component.IComponentPart;

public class PartStatusEffectInstance implements IPartStatusEffectInstance {

	private IPartStatusEffect effect;
	private int duration;
	private float intensity;

	public PartStatusEffectInstance(IPartStatusEffect effect, int duration, float intensity) {
		this.effect = effect;
		this.duration = duration;
		this.intensity = intensity;
	}

	@Override
	public IPartStatusEffect getEffect() {
		return effect;
	}

	@Override
	public int remainingDuration() {
		return duration;
	}

	@Override
	public float intensity() {
		return intensity;
	}

	@Override
	public int tick(IComponentPart part, long tick) {
		if (duration < 0)
			return -1;
		this.effect.effectTick(part, duration, tick);
		return --duration;
	}

	@Override
	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public PartStatusEffectInstance clone() {
		return new PartStatusEffectInstance(effect, duration, intensity);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof IPartStatusEffectInstance psei) {
			return this.effect.equals(psei.getEffect()) && this.duration == psei.remainingDuration()
					&& this.intensity == psei.intensity();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.effect.hashCode() + this.duration + Float.hashCode(this.intensity);
	}

	@Override
	public String toString() {
		return "{" + this.effect + "(" + this.duration + " ticks)" + (this.intensity != 1f ? "x" + this.intensity : "")
				+ "}";
	}

}
