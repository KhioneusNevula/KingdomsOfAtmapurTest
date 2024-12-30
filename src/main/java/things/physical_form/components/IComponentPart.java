package things.physical_form.components;

import java.util.Collection;

import things.physical_form.IPart;
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
	 * Add ability to part
	 * 
	 * @param ability
	 */
	public void addAbility(IPartAbility ability);

	/**
	 * Add embedded material to part
	 * 
	 * @param mat
	 */
	public void addEmbeddedMaterials(Collection<? extends IMaterial> mat);

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
	 * Attach the given spirit to this component
	 * 
	 * @param spirit
	 */
	public void attachSpirit(ISpirit spirit);

	/**
	 * Remove this spirit
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
	 * G@Override et a stat for an ability
	 * 
	 * @param <E>
	 * @param forStat
	 * @return
	 */
	<E> E getStat(IPartStat<E> forStat);

}
