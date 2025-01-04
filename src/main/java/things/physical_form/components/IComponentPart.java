package things.physical_form.components;

import java.util.Collection;

import things.physical_form.IPart;
import things.physical_form.ISoma;
import things.physical_form.channelsystems.IChannelCenter;
import things.physical_form.channelsystems.IChannelCenter.ChannelRole;
import things.physical_form.material.IMaterial;
import things.spirit.ISpirit;

public interface IComponentPart extends IPart {

	/**
	 * Materials that spill out of this part when it is damaged. Depends on factors
	 * such as present channelsystems
	 * 
	 * @return
	 */
	public Collection<IMaterial> embeddedMaterials();

	/**
	 * The number indicating what planes this part can interact on
	 * 
	 * @return
	 */
	public int interactionPlanes();

	/**
	 * Get abilities (including ChannelCenters)
	 * 
	 * @return
	 */
	public Collection<IPartAbility> getAbilities();

	/**
	 * Add ability to part. Set callUpdate to true if you want to call an update on
	 * the parent soma
	 * 
	 * @param ability
	 */
	public void addAbility(IPartAbility ability, boolean callUpdate);

	/**
	 * Add embedded material to part. Set callUpdate to true if you want to call an
	 * update on the parent soma
	 * 
	 * @param mat
	 */
	public void addEmbeddedMaterials(Collection<? extends IMaterial> mat, boolean callUpdate);

	/**
	 * Get channelcenters in this part with the given role
	 * 
	 * @param role
	 * @return
	 */
	public Collection<IChannelCenter> getChannelCenters(ChannelRole role);

	/**
	 * Get channelcenters in this part that are automatic
	 * 
	 * @param role
	 * @return
	 */
	public Collection<IChannelCenter> getAutomaticChannelCenters();

	/**
	 * Whether this body part has any automatic channel centers
	 * 
	 * @return
	 */
	public boolean hasAutomaticChannelCenter();

	/**
	 * Return the channel roles of this part
	 * 
	 * @return
	 */
	public Collection<ChannelRole> getChannelRoles();

	/**
	 * Return all spirits tethered to this part
	 * 
	 * @return
	 */
	public Collection<ISpirit> getTetheredSpirits();

	/**
	 * Wehther this component part would allow the given spirit to attach to it.
	 * Usually, this returns false if the Part has no CONTROL channelcenter
	 * 
	 * @param spirit
	 * @return
	 */
	public boolean canAttachSpirit(ISpirit spirit);

	/**
	 * Attach the given spirit to this component. Only call this from the Soma!
	 * 
	 * @param spirit
	 */
	public void attachSpirit(ISpirit spirit);

	/**
	 * Remove this spirit. Only call this from a Soma!
	 * 
	 * @param toRemove
	 */
	public void removeSpirit(ISpirit toRemove);

	/**
	 * If this part has a channelcenter that performs control ticks
	 * 
	 * @return
	 */
	boolean hasControlCenter();

	/**
	 * G et a stat for an ability
	 * 
	 * @param <E>
	 * @param forStat
	 * @return
	 */
	<E> E getStat(IPartStat<E> forStat);

	/**
	 * Set a stat for abilities
	 * 
	 * @param <E>
	 * @param stat
	 * @param newValue
	 * @param callUpdate whether to call an update method on the owner soma
	 */
	public <E> void changeStat(IPartStat<E> stat, E newValue, boolean callUpdate);

	/**
	 * Get all stats assigned to this part
	 * 
	 * @return
	 */
	public Collection<? extends IPartStat<?>> getStats();

	/**
	 * Report details about this part;s systems and abilities and whatever
	 * 
	 * @return
	 */
	public String componentReport();

	/**
	 * Get the owner of this part, cast as a soma
	 * 
	 * @return
	 */
	default ISoma<?> getSomaOwner() {
		return (ISoma<?>) getOwner();
	}

	public void setOwner(ISoma<?> soma);

}
