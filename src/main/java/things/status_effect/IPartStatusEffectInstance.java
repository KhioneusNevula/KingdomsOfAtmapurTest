package things.status_effect;

public interface IPartStatusEffectInstance extends Cloneable {

	/**
	 * The actual status effect
	 * 
	 * @return
	 */
	public IPartStatusEffect getEffect();

	/**
	 * The remaining duration of the effect. If negative, the effect is eternal
	 * 
	 * @return
	 */
	public int remainingDuration();

	/**
	 * The level of intensity of this effect, if relevant; by default 1f
	 * 
	 * @return
	 */
	public float intensity();

	/**
	 * Decrease the duration by 1 and return the new tick it's at; return -1 if the
	 * duration is negative
	 * 
	 * @return
	 */
	public int tick();

	/**
	 * Set the duration of the effect. If negative, the effect is eternal
	 */
	public void setDuration(int duration);

	public IPartStatusEffectInstance clone();
}
