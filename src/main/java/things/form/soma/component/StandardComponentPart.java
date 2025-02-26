package things.form.soma.component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import _sim.plane.Plane;
import things.form.IForm;
import things.form.IPart;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.stats.IPartStat;
import things.spirit.ISpirit;
import things.stains.IStain;
import things.status_effect.IPartStatusEffect;
import things.status_effect.IPartStatusEffectInstance;
import utilities.collections.ImmutableCollection;

public class StandardComponentPart implements IComponentPart {

	private String name;
	private UUID id;
	private IMaterial material;
	private int planes;
	private float size;
	private IShape shape;
	private Collection<IPartAbility> abilities;
	private Collection<IMaterial> embedded;
	private Map<IResource<?>, Comparable<?>> channelResources;
	private Set<IChannelCenter> autos;
	private Set<ChannelRole> roles;
	private Set<ISpirit> spirits;
	private Map<IPartStatusEffect, IPartStatusEffectInstance> effects;
	private Map<IPartStat<?>, Object> stats;
	private IForm<? super StandardComponentPart> owner;
	private Set<IStain> stains;

	public StandardComponentPart(String name, UUID id, IMaterial mat, IShape shape, float size, int planes,
			Collection<? extends IPartAbility> abilities, Map<? extends IPartStat<?>, ? extends Object> stats,
			Collection<? extends IMaterial> embeddedMaterials) {
		this.name = name;
		this.id = id;
		this.size = size;
		this.material = mat;
		this.shape = shape;
		this.planes = planes;
		this.abilities = new HashSet<>(abilities);
		this.embedded = new HashSet<>(embeddedMaterials);
		this.stains = new HashSet<>();
		this.channelResources = new HashMap<>();
		this.autos = abilities.stream()
				.filter((a) -> a instanceof IChannelCenter ? ((IChannelCenter) a).isAutomatic() : false)
				.map((a) -> (IChannelCenter) a).collect(Collectors.toSet());
		this.roles = this.abilities.stream().filter((a) -> a instanceof IChannelCenter)
				.map((a) -> ((IChannelCenter) a).getRole()).collect(Collectors.toSet());
		spirits = new HashSet<>();
		this.stats = new HashMap<>(stats);
		this.effects = new HashMap<>();
	}

	@Override
	public void addStain(IStain stain, boolean callUpdate) {
		if (this.stains.add(stain) && callUpdate && this.owner != null) {
			this.owner.onPartStainChange(this, Collections.singleton(stain));
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public Collection<IStain> getStains() {
		return ImmutableCollection.from(stains);
	}

	@Override
	public void removeAllStains(boolean callUpdate) {
		Set<IStain> stains = this.stains;
		this.stains = new HashSet<>();
		if (!stains.isEmpty() && callUpdate && this.owner != null) {
			this.owner.onPartStainChange(this, stains);
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public void removeStain(IStain stain, boolean callUpdate) {
		if (this.stains.remove(stain) && callUpdate && this.owner != null) {
			this.owner.onPartStainChange(this, Collections.singleton(stain));
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public UUID getID() {
		return id;
	}

	@Override
	public IPart setID(UUID id) {
		this.id = id;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public float getRelativeSize() {
		return size;
	}

	@Override
	public boolean isHole() {
		return false;
	}

	@Override
	public IShape getShape() {
		return shape;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public int detectionPlanes() {
		return planes;
	}

	@Override
	public Collection<IMaterial> embeddedMaterials() {
		return embedded;
	}

	@Override
	public int interactionPlanes() {
		return planes;
	}

	@Override
	public Collection<IPartAbility> getAbilities() {
		return abilities;
	}

	@Override
	public void addAbility(IPartAbility ability, boolean callUpdate) {
		if (abilities.add(ability)) {
			if (ability instanceof IChannelCenter cc) {
				if (cc.isAutomatic()) {
					this.autos = new HashSet<>(this.autos);
					autos.add(cc);
					autos = autos.stream().collect(Collectors.toUnmodifiableSet());
				}
				this.roles = new HashSet<>(this.roles);
				roles.add(cc.getRole());
				roles = roles.stream().collect(Collectors.toUnmodifiableSet());
			}
			if (callUpdate && this.owner instanceof ISoma soma) {
				soma.onPartAbilitiesChange(this, Collections.singleton(ability));
			}
			if (owner instanceof ISoma soma)
				this.spirits.forEach(this::spiritStateChange);
		}

	}

	/**
	 * Called on a spirit when the state of this part is changed in some way
	 * 
	 * @param forSpirit
	 */
	private void spiritStateChange(ISpirit forSpirit) {
		IComponentPart newPart = forSpirit.onHostStateChange(this, (ISoma) owner);
		if (newPart != null) {
			if (newPart.equals(this)) {
				return;
			}
			if (newPart.getOwner() != owner) {
				throw new IllegalArgumentException(
						forSpirit + " can't attach to " + newPart + " because it is not present in body");
			} else {
				owner.getOwner().getMap().queueAction(() -> {
					this.removeSpirit(forSpirit, true);
					newPart.attachSpirit(forSpirit, true);
					forSpirit.onAttachHost(newPart, (ISoma) this.owner);
				});
			}
		} else {
			owner.getOwner().getMap().queueAction(() -> {
				this.removeSpirit(forSpirit, true);
			});
		}
	}

	@Override
	public <E extends Comparable<?>> E getResourceAmount(IResource<E> resource) {
		return (E) this.channelResources.getOrDefault(resource, resource.getEmptyValue());
	}

	@Override
	public void changeResourceAmount(IResource<?> resource, Comparable<?> value, boolean callUpdate) {
		Comparable<?> returnV = this.channelResources.put(resource, value);
		if (!value.equals(returnV) && callUpdate && this.owner instanceof ISoma soma) {
			soma.onChannelResourceChanged(this, resource, returnV);
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public void addEmbeddedMaterials(Collection<? extends IMaterial> mat, boolean callUpdate) {

		if (embedded.addAll(mat) && callUpdate && this.owner instanceof ISoma soma) {
			soma.onPartEmbeddedMaterialsChanged(this, new HashSet<>(mat));
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public void changeMaterial(IMaterial material, boolean callUpdate) {
		IMaterial fmat = this.material;
		this.material = material;
		if (callUpdate && this.owner instanceof IForm<?>soma) {
			soma.onPartMaterialChange(this, fmat);
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public void changeSize(float size, boolean callUpdate) {
		float fmat = this.size;
		this.size = size;
		if (callUpdate && this.owner instanceof IForm<?>soma) {
			soma.onPartSizeChange(this, fmat);
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public void changeShape(IShape shape, boolean callUpdate) {
		IShape fmat = this.shape;
		this.shape = shape;
		if (callUpdate && this.owner instanceof IForm<?>soma) {
			soma.onPartShapeChange(this, fmat);
		}

		if (owner instanceof ISoma soma)
			this.spirits.forEach(this::spiritStateChange);
	}

	@Override
	public Collection<IChannelCenter> getChannelCenters(ChannelRole role) {
		return abilities.stream()
				.filter((a) -> a instanceof IChannelCenter ? ((IChannelCenter) a).getRole() == role : false)
				.map((a) -> (IChannelCenter) a).collect(Collectors.toSet());
	}

	@Override
	public Collection<IChannelCenter> getChannelCenters(IChannelSystem role) {
		return abilities.stream()
				.filter((a) -> a instanceof IChannelCenter ? ((IChannelCenter) a).getSystem().equals(role) : false)
				.map((a) -> (IChannelCenter) a).collect(Collectors.toSet());
	}

	@Override
	public Collection<IChannelCenter> getAutomaticChannelCenters() {
		return autos;
	}

	@Override
	public boolean hasControlCenter() {
		return this.roles.contains(ChannelRole.CONTROL);
	}

	@Override
	public boolean hasAutomaticChannelCenter() {
		return !autos.isEmpty();
	}

	@Override
	public Collection<ChannelRole> getChannelRoles() {
		return roles;
	}

	@Override
	public Collection<ISpirit> getTetheredSpirits() {
		return this.spirits;
	}

	@Override
	public boolean canAttachSpirit(ISpirit spirit) {
		return true;
	}

	@Override
	public void attachSpirit(ISpirit spirit, boolean callUpdate) {
		if (!(owner instanceof ISoma))
			throw new UnsupportedOperationException();
		boolean worked = this.spirits.add(spirit);
		if (callUpdate && worked) {
			spirit.onAttachHost(this, (ISoma) owner);
			((ISoma) owner).onAttachSpirit(spirit, this);
		}
	}

	@Override
	public void removeSpirit(ISpirit toRemove, boolean callUpdate) {
		if (!(owner instanceof ISoma))
			throw new UnsupportedOperationException();
		boolean worked = this.spirits.remove(toRemove);

		if (callUpdate && worked) {
			toRemove.onRemove(this, (ISoma) owner);
			((ISoma) owner).onRemoveSpirit(toRemove, this);
		}
	}

	@Override
	public void applyEffect(IPartStatusEffectInstance effect, boolean callUpdate) {
		Object ret = this.effects.put(effect.getEffect(), effect);
		if (callUpdate && ret == null) {
			((ISoma) owner).onApplyEffect(effect, this);
		}
	}

	@Override
	public IPartStatusEffectInstance removeEffect(IPartStatusEffect effect, boolean callUpdate) {
		IPartStatusEffectInstance ret = this.effects.remove(effect);
		if (callUpdate && ret != null) {
			((ISoma) owner).onRemoveEffect(ret, this);
		}
		return ret;
	}

	@Override
	public IPartStatusEffectInstance getEffectInstance(IPartStatusEffect effect) {
		return this.effects.get(effect);
	}

	@Override
	public Collection<IPartStatusEffectInstance> getEffectInstances() {
		return this.effects.values();
	}

	@Override
	public Collection<IPartStatusEffect> getEffectTypes() {
		return this.effects.keySet();
	}

	@Override
	public boolean hasEffect(IPartStatusEffect effect) {
		return this.effects.get(effect) != null;
	}

	@Override
	public <E> E getStat(IPartStat<E> forStat) {
		return (E) stats.getOrDefault(forStat, forStat.getDefaultValue(this));
	}

	@Override
	public <E> void changeStat(IPartStat<E> stat, E newValue, boolean callUpdate) {
		Object formerVal = stats.put(stat, newValue);
		if (callUpdate && this.owner instanceof ISoma soma) {
			soma.onPartStatChange(this, stat, formerVal);
		}
	}

	@Override
	public StandardComponentPart clone() {

		StandardComponentPart newPart;
		try {
			newPart = (StandardComponentPart) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		newPart.autos = this.autos.stream().collect(Collectors.toSet());
		newPart.roles = this.roles.stream().collect(Collectors.toSet());
		newPart.abilities = new HashSet<>(this.abilities);
		newPart.embedded = new HashSet<>(this.embedded);
		newPart.stats = new HashMap<>(this.stats);
		newPart.spirits = new HashSet<>(this.spirits);
		newPart.channelResources = new HashMap<>(this.channelResources);
		newPart.effects = new HashMap<>(Maps.asMap(this.effects.keySet(), (eff) -> this.effects.get(eff).clone()));
		return newPart;
	}

	@Override
	public Collection<? extends IPartStat<?>> getStats() {
		return this.stats.keySet();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IPart part) {
			return this.id.equals(part.getID());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public String toString() {
		return "{{\"" + this.name + "\", [" + this.material.name() + "] }}";
	}

	@Override
	public String componentReport() {
		return "{name=" + name + ",mat=" + this.material + ",shape=" + this.shape
				+ (planes != 1 ? ",planes=" + Plane.separate(planes) : "")
				+ (abilities.isEmpty() ? "" : ",abs=" + abilities) + (spirits.isEmpty() ? "" : ",spirits=" + spirits)
				+ (stats.isEmpty() ? "" : ",stats=" + stats) + (embedded.isEmpty() ? "" : ",embedded=" + embedded)
				+ (this.channelResources.isEmpty() ? "" : ",resources=" + this.channelResources)
				+ (effects.isEmpty() ? "" : ",effects=" + this.effects.values()) + "}";
	}

	@Override
	public IForm<?> getOwner() {
		return owner;
	}

	@Override
	public void setOwner(IForm<?> soma) {
		this.owner = (IForm<? super StandardComponentPart>) soma;
	}

	@Override
	public void setOwner(ISoma owner) {
		this.setOwner((IForm<? super StandardComponentPart>) owner);
	}

}
