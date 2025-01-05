package things.form.soma.component;

import java.util.Collection;

import things.form.IPart;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelResource;
import things.form.material.IMaterial;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.stats.IPartStat;
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
	 * Get the amount of a specific resource.
	 * 
	 * @param <E>
	 * @param resource
	 * @return
	 */
	public <E> E getResourceAmount(IChannelResource<E> resource);

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
	 * the parent soma. Will automatically call an update on contained spirits
	 * 
	 * @param ability
	 */
	public void addAbility(IPartAbility ability, boolean callUpdate);

	/**
	 * Add embedded material to part. Set callUpdate to true if you want to call an
	 * update on the parent soma. Will automatically call an update on contained
	 * spirits
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
	public void attachSpirit(ISpirit spirit, boolean callUpdate);

	/**
	 * Remove this spirit. Only call this from a Soma!
	 * 
	 * @param toRemove
	 */
	public void removeSpirit(ISpirit toRemove, boolean callUpdate);

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
	 * Set a stat for abilities. Will automatically call an update on contained
	 * spirits
	 * 
	 * @param <E>
	 * @param stat
	 * @param newValue
	 * @param callUpdate whether to call an update method on the owner soma and
	 *                   contained spirits
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
	default ISoma getSomaOwner() {
		return (ISoma) getOwner();
	}

	public void setOwner(ISoma soma);

	@Override
	public IComponentPart clone();

	/**
	 * Change the amount of a channel resource. Will automatically call an update on
	 * contained spirits
	 * 
	 * @param resource
	 * @param value
	 * @param callUpdate whether to call update on parent soma and contained spirits
	 */
	void changeResourceAmount(IChannelResource<?> resource, Object value, boolean callUpdate);

	/** TODO temperature */
	/** public float getTemperature(); */

}
