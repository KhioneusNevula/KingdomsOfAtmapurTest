package things.physical_form.components;

import java.util.Collection;

public interface IPartAbility {

	/**
	 * Name of this ability
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Stats that are relevant to this ability
	 * 
	 * @return
	 */
	public Collection<IPartStat<?>> getStats();

	/**
	 * whether this ability governs something that is controllable, e.g. walking
	 * 
	 * @return
	 */
	public boolean controllable();

	/**
	 * Whether this ability is a kind of sensor
	 * 
	 * @return
	 */
	public default boolean sensor() {
		return false;
	}
}
