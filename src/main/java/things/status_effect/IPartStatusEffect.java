package things.status_effect;

import things.form.soma.component.IComponentPart;

/**
 * A status effect that can be applied to or removed from a part. Most status
 * effects do nothing on their own; rather, they are detected by other systems.
 * E.g. the SLEEP status effect makes the brain do sleep ticks. However, some
 * status effects do something when applied, and may do something independent
 * when ticked. It depends on the effect.
 * 
 * @author borah
 *
 */
public interface IPartStatusEffect {

	/**
	 * Name of this status effect
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Called to determine whether the effect should be removed; return false if the
	 * effect should just play out until its time is up
	 * 
	 * @param instance
	 * @param part
	 * @return
	 */
	public default boolean shouldRemove(IPartStatusEffectInstance instance, IComponentPart part, long ticks) {
		return false;
	}

	/**
	 * Called when the effect is applied to a part of something and what duration it
	 * is applied with
	 */
	public default void onEffectApplied(IComponentPart toPart, int duration) {

	}

	/**
	 * Called when the effect is removed from a part of something.
	 * 
	 * @param toPart
	 * @param worldTick
	 * @param external  whether the effect was removed by an external factor, or if
	 *                  it was removed due to the duration expiring
	 */
	public default void onEffectRemoved(IComponentPart toPart, boolean external) {

	}

	/**
	 * Called when an effect is ticking. Note that the given duration is how much
	 * time is LEFT, not how much time it has been on for.
	 */
	public default void effectTick(IComponentPart onPart, int duration, long worldTick) {

	}

	/** TODO color, icon, etc? */

}
