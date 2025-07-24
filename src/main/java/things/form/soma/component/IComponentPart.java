package things.form.soma.component;

import java.util.Collection;
import java.util.UUID;

import metaphysics.magic.ITether;
import metaphysics.magic.ITether.TetherType;
import metaphysics.spirit.ISpirit;
import things.form.IForm;
import things.form.IPart;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.material.IMaterial;
import things.form.sensing.sensors.ISensor;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.stats.IPartStat;
import things.stains.IStain;
import things.status_effect.IPartStatusEffect;
import things.status_effect.IPartStatusEffectInstance;
import thinker.concepts.profile.IProfile;

/**
 * Note: a component part can be linked to one in another entity, representing
 * the idea of 'holding' something
 * 
 * @author borah
 *
 */
public interface IComponentPart extends IPart {

	/**
	 * returns the Form that this part Actually belongs to, i.e. when it is being
	 * held
	 */
	public ISoma getTrueOwner();

	/**
	 * Creates a "dummy part" (cast as an {@link IComponentPart}, as opposed to the
	 * version of the method {@link IPart#dummy(UUID)} which just returns an IPart),
	 * i.e. a single-use part whose sole purpose is to be used to find a component
	 * part in a hash map. If any method other than {@link #equals},
	 * {@link #hashCode}, {@link #toString()}, or {@link #getName()} is called on
	 * this, throw an exception
	 * 
	 * @param id
	 * @return
	 */
	public static IComponentPart dummy(UUID id) {
		return new DummyComponentPart(id);
	}

	/**
	 * Get the amount of a specific resource.
	 * 
	 * @param <E>
	 * @param resource
	 * @return
	 */
	public <E extends Comparable<?>> E getResourceAmount(IResource<E> resource);

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
	 * the parent soma. ThinkerWill automatically call an update on contained spirits
	 * 
	 * @param ability
	 */
	public void addAbility(IPartAbility ability, boolean callUpdate);

	/**
	 * Get channelcenters in this part with the given role
	 * 
	 * @param role
	 * @return
	 */
	public Collection<IChannelCenter> getChannelCenters(ChannelRole role);

	/**
	 * Get channelcenters in this part which come from this system
	 * 
	 * @param role
	 * @return
	 */
	public Collection<IChannelCenter> getChannelCenters(IChannelSystem role);

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
	 * Return all effect instances on this part
	 * 
	 * @return
	 */
	public Collection<IPartStatusEffectInstance> getEffectInstances();

	/**
	 * Return all effect types on this part
	 * 
	 * @return
	 */
	public Collection<IPartStatusEffect> getEffectTypes();

	/**
	 * Wehther this component part has the given effect
	 * 
	 * @param spirit
	 * @return
	 */
	public boolean hasEffect(IPartStatusEffect effect);

	/**
	 * Return the status effect instance for a given part
	 * 
	 * @param effect
	 * @return
	 */
	public IPartStatusEffectInstance getEffectInstance(IPartStatusEffect effect);

	/**
	 * Materials that are considered "embedded" in this one. Such materials are
	 * typically derived by checking channel systems and resource amounts
	 */
	public Collection<IMaterial> embeddedMaterials();

	/**
	 * Check if this material is damaged and generate the appropriate stains with
	 * the appropriate amounts from its embedded substances; return empty if there
	 * is no spillage
	 */
	public Collection<IStain> generateStains();

	/**
	 * Apply the given status effect to this component; override any previous status
	 * effect of the same kind
	 * 
	 * @param spirit
	 */
	public void applyEffect(IPartStatusEffectInstance effect, boolean callUpdate);

	/**
	 * Remove this status effect and return the instance if this status effect
	 * existed
	 * 
	 * @param toRemove
	 */
	public IPartStatusEffectInstance removeEffect(IPartStatusEffect toRemove, boolean callUpdate);

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
	 * Set a stat for abilities. ThinkerWill automatically call an update on contained
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
	 * Report de@Override tails about this part;s systems and abilities and whatever
	 * 
	 * @return
	 */
	public String componentReport();

	/**
	 * Get the owner of this part, cast as a soma, or null if it doesn't exist or is
	 * not a soma
	 * 
	 * @return
	 */
	default ISoma getSomaOwner() {
		if (getOwner() instanceof ISoma) {
			return (ISoma) getOwner();
		}
		return null;
	}

	public void setOwner(ISoma soma);

	/** If this part has a sensing ability */
	public boolean hasSensor();

	/** If this part has a sensing ability */
	public Collection<ISensor> getSensors();

	@Override
	public IComponentPart clone();

	/**
	 * Change the amount of a channel resource. ThinkerWill automatically call an update on
	 * contained spirits
	 * 
	 * @param resource
	 * @param value
	 * @param callUpdate whether to call update on parent soma and contained spirits
	 */
	void changeResourceAmount(IResource<?> resource, Comparable<?> value, boolean callUpdate);

	/**
	 * Alike to {@link #changeResourceAmount(IResource, Comparable, boolean)}, but
	 * for resources that require an argument specifying the material
	 * 
	 * @param resource
	 * @param geneticMaterial
	 * @param value
	 * @param callUpdate
	 */
	void changeMaterialResourceAmount(IResource<?> resource, IMaterial geneticMaterial, Comparable<?> value,
			boolean callUpdate);

	/**
	 * Adds a channel's embedded material to this Part. Pass in the
	 * genetic-generated versions of the materials.
	 */
	public void addChannelMaterial(IChannel vectorMaterials, Collection<IMaterial> materialsGen);

	/** Getst the embedded material in this part based on the given resource */
	IMaterial getEmbeddedMaterialFor(IResource<?> resource);

	/** Return all tethers to this componentPart */
	public Collection<ITether> getTethers();

	/** Add a tethers to this componentPart of the given type */
	public void addTether(ITether tether, boolean callUpdate);

	/**
	 * Remove a tether from this componentPart of the given type and return true if
	 * successful
	 */
	public boolean removeTether(ITether tether, boolean callUpdate);

	/**
	 * Remove a tether from this componentPart of the given type and return true if
	 * successful
	 */
	public boolean removeTether(TetherType type, IProfile tether, boolean callUpdate);

	/**
	 * Remove all tethers from this componentPart for something with the given
	 * profile
	 */
	public boolean removeTethers(IProfile tether, boolean callUpdate);

	/**
	 * Return all spiritual tethers this componentPart has for something with the
	 * given profile
	 */
	public Collection<ITether> getTethers(IProfile tether);

	/**
	 * Return if this componentPart has a tether for something with the given
	 * profile
	 */
	public boolean hasTether(IProfile tether);

	/**
	 * Return if this componentPart has a tether for something with the given
	 * profile and type
	 */
	public boolean hasTether(TetherType type, IProfile tether);

	/**
	 * return if this particular tether exists
	 */
	public boolean hasTether(ITether tether);

	/** Return all tethers to this componentPart of the given type */
	public Collection<ITether> getTethers(TetherType type);

	/**
	 * Return the tethers to this componentPart of the given profile and type or
	 * null if not present
	 */
	public ITether getTether(TetherType type, IProfile prof);

	/** TODO Shatters a part into shards */
	/** public void shatterPart(float force, ForceType type, boolean update); */

	/** TODO temperature */
	/** public float getTemperature(); */

	/**
	 * The equals method should just compare UUID's to one another
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * The hash code should solely be based on UUID
	 * 
	 * @return
	 */
	@Override
	int hashCode();

	public class DummyComponentPart extends DummyPart implements IComponentPart {

		public DummyComponentPart(UUID id) {
			super(id);
		}

		@Override
		public <E extends Comparable<?>> E getResourceAmount(IResource<E> resource) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasSensor() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<ISensor> getSensors() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int interactionPlanes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasTether(TetherType type, IProfile tether) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addTether(ITether tether, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ITether getTether(TetherType type, IProfile prof) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<ITether> getTethers() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<ITether> getTethers(TetherType type) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasTether(ITether tether) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeTether(ITether tether, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeTether(TetherType type, IProfile tether, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<ITether> getTethers(IProfile tether) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasTether(IProfile tether) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeTethers(IProfile tether, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IPartAbility> getAbilities() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addAbility(IPartAbility ability, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IMaterial getEmbeddedMaterialFor(IResource<?> resource) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IStain> generateStains() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void applyEffect(IPartStatusEffectInstance effect, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IPartStatusEffectInstance getEffectInstance(IPartStatusEffect effect) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IPartStatusEffectInstance> getEffectInstances() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IMaterial> embeddedMaterials() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IPartStatusEffect> getEffectTypes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasEffect(IPartStatusEffect effect) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IPartStatusEffectInstance removeEffect(IPartStatusEffect toRemove, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IChannelCenter> getChannelCenters(ChannelRole role) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IChannelCenter> getChannelCenters(IChannelSystem role) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<IChannelCenter> getAutomaticChannelCenters() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasAutomaticChannelCenter() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<ChannelRole> getChannelRoles() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<ISpirit> getTetheredSpirits() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean canAttachSpirit(ISpirit spirit) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void attachSpirit(ISpirit spirit, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeSpirit(ISpirit toRemove, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasControlCenter() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <E> E getStat(IPartStat<E> forStat) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <E> void changeStat(IPartStat<E> stat, E newValue, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<? extends IPartStat<?>> getStats() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String componentReport() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setOwner(ISoma soma) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ISoma getTrueOwner() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setTrueOwner(IForm<?> so) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void changeResourceAmount(IResource<?> resource, Comparable<?> value, boolean callUpdate) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void changeMaterialResourceAmount(IResource<?> resource, IMaterial geneticMaterial, Comparable<?> value,
				boolean callUpdate) {
			this.changeResourceAmount(resource, value, callUpdate);
		}

		@Override
		public void addChannelMaterial(IChannel vectorMaterials, Collection<IMaterial> geneticM) {
			throw new UnsupportedOperationException();
		}

	}

}
