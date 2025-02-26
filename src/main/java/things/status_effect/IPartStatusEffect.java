package things.status_effect;

import things.form.soma.component.IComponentPart;

/**
 * A status effect that can be applied to or removed from a part. Status effects
 * do nothing on their own; rather, they are detected by other systems. E.g. the
 * SLEEP status effect makes the brain do sleep ticks
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
	public default boolean shouldRemove(IPartStatusEffectInstance instance, IComponentPart part) {
		return false;
	}

	/** TODO color, icon, etc? */

}
