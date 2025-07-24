package things.form.soma.component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import _sim.plane.Plane;
import _utilities.collections.ImmutableCollection;
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
import things.form.material.property.MaterialProperty;
import things.form.sensing.sensors.ISensor;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Shapedness;
import things.form.soma.IPartHealth;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.stats.IPartStat;
import things.form.visage.ISensableProperty;
import things.stains.IStain;
import things.stains.Stain;
import things.status_effect.IPartStatusEffect;
import things.status_effect.IPartStatusEffectInstance;
import thinker.concepts.profile.IProfile;
import thinker.knowledge.IKnowledgeMedium;
import thinker.mind.perception.sensation.DamageSensationReceptor;
import thinker.mind.perception.sensation.Sensation;
import thinker.mind.util.IBeingAccess;

public class StandardComponentPart implements IComponentPart {

	private String name;
	private UUID id;
	private IMaterial material;
	private int planes;
	private float size;
	private IShape shape;
	private Collection<IPartAbility> abilities;
	private Multimap<Object, IMaterial> embedded;
	private Map<IResource<?>, Comparable<?>> channelResources;
	private Set<IChannelCenter> autos;
	private Set<ChannelRole> roles;
	private Set<ISpirit> spirits;
	private Map<IPartStatusEffect, IPartStatusEffectInstance> effects;
	private Map<IPartStat<?>, Object> stats;
	private IForm<? super StandardComponentPart> owner;
	private IForm<?> trueOwner;
	private Map<IMaterial, IStain> stains;
	private Map<ISensableProperty<?>, IKnowledgeMedium> writings;
	private Set<ISensor> sensors;
	private Table<TetherType, IProfile, ITether> immaterialTethers;

	public StandardComponentPart(String name, UUID id, IMaterial mat, IShape shape, float size, int planes,
			Collection<? extends IPartAbility> abilities, Map<? extends IPartStat<?>, ? extends Object> stats) {
		this.name = name;
		this.id = id;
		this.size = size;
		this.material = mat;
		this.shape = shape;
		this.planes = planes;
		this.abilities = new HashSet<>();
		this.autos = Collections.emptySet();
		this.roles = Collections.emptySet();
		this.sensors = Collections.emptySet();
		for (IPartAbility ab : abilities) {
			this.addAbility(ab, false);
		}
		this.embedded = MultimapBuilder.hashKeys().hashSetValues().build();
		this.stains = new HashMap<>();
		this.channelResources = new HashMap<>();
		this.immaterialTethers = HashBasedTable.create();
		spirits = new HashSet<>();
		this.stats = new HashMap<>(stats);
		this.effects = new HashMap<>();
		this.writings = new HashMap<>();
	}

	@Override
	public boolean hasSensor() {
		return !sensors.isEmpty();
	}

	@Override
	public Set<ISensor> getSensors() {
		return sensors;
	}

	@Override
	public void addStain(IStain stain, boolean callUpdate) {
		IStain res = this.stains.put(stain.getSubstance(),
				new Stain(stain.getSubstance(),
						this.stains.getOrDefault(stain.getSubstance(), new Stain(stain.getSubstance(), 0)).getAmount()
								+ stain.getAmount()));
		if ((res == null || !res.equals(stain)) && callUpdate && this.owner != null) {
			this.owner.onPartStainChange(this, Collections.singleton(stain));
		}

	}

	@Override
	public Collection<IStain> getStains() {
		return ImmutableCollection.from(stains.values());
	}

	@Override
	public void removeAllStains(boolean callUpdate) {
		Collection<IStain> stains = this.stains.values();
		this.stains = new HashMap<>();
		if (!stains.isEmpty() && callUpdate && this.owner != null) {
			this.owner.onPartStainChange(this, stains);
		}

	}

	@Override
	public void removeStain(IMaterial stain, boolean callUpdate) {
		IStain sten = this.stains.remove(stain);
		if (sten != null && callUpdate && this.owner != null) {
			this.owner.onPartStainChange(this, Collections.singleton(sten));
		}

	}

	@Override
	public UUID getUUID() {
		return id;
	}

	@Override
	public IPart setUUID(UUID id) {
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
	public <T> T getSensableProperty(ISensableProperty<T> prop) {
		return prop.getPropertyFromPart(this);
	}

	@Override
	public IKnowledgeMedium readKnowledge(ISensableProperty<?> property) {
		return writings.get(property);
	}

	@Override
	public void writeKnowledge(IKnowledgeMedium utterance, ISensableProperty<?> property) {
		writings.put(property, utterance);
	}

	@Override
	public Collection<ISensableProperty<?>> getAllPropertiesWithSensableLanguage() {
		return writings.keySet();
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
		return embedded.values();
	}

	@Override
	public IMaterial getEmbeddedMaterialFor(IResource<?> resource) {
		return embedded.get(resource).stream().findFirst().orElse(IMaterial.NONE);
	}

	@Override
	public Collection<IStain> generateStains() {
		Map<IMaterial, IStain> stains = new HashMap<>();
		for (Object resource : embedded.keySet()) {
			Collection<IMaterial> materials = embedded.get(resource);
			if (resource instanceof IResource res) {
				for (IMaterial mat : materials) {
					Comparable amt = res.getEmptyValue();
					IStain newstain = null;
					if (mat.getProperty(MaterialProperty.PHASE).isFluid()
							&& material.getProperty(MaterialProperty.PHASE).isFluid()) {
						newstain = IStain.fromResourceMaterial(res, mat,
								amt = res.divide(this.getResourceAmount(res), 5));

					} else if (shape.getProperty(ShapeProperty.SHAPEDNESS) == Shapedness.AMORPHIC) {

						newstain = IStain.fromResourceMaterial(res, mat, amt = this.getResourceAmount(res));
					} else if (shape.getProperty(ShapeProperty.INTEGRITY) < 1.0f) {
						newstain = IStain.fromResourceMaterial(res, mat,
								Collections.min(Set.of(getResourceAmount(res), amt = res.divide(res.getMaxValue(),
										(int) (1 / (1 - shape.getProperty(ShapeProperty.INTEGRITY)))))));
					}
					if (newstain != null
							&& stains.getOrDefault(mat, IStain.EMPTY_STAIN).getAmount() < newstain.getAmount()) {
						stains.put(mat, newstain);
						changeMaterialResourceAmount(res, mat, res.subtract(getResourceAmount(res), amt), true);
					}
				}
			}
		}
		return stains.values();
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
			if (ability instanceof ISensor sensor) {
				this.sensors = new HashSet<>(this.sensors);
				sensors.add(sensor);
				sensors = sensors.stream().collect(Collectors.toUnmodifiableSet());
			}
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
		}

	}

	@Override
	public <E extends Comparable<?>> E getResourceAmount(IResource<E> resource) {
		return (E) this.channelResources.getOrDefault(resource, resource.getEmptyValue());
	}

	@Override
	public void changeMaterialResourceAmount(IResource<?> resource, IMaterial geneticMaterial, Comparable<?> value,
			boolean callUpdate) {
		if (!geneticMaterial.isNothing()) {
			if (((Comparable) value).compareTo(resource.getEmptyValue()) == 0) {
				embedded.removeAll(resource);
			} else {
				embedded.put(resource, geneticMaterial);

			}
		}

		Comparable<?> returnV = this.channelResources.put(resource, value);
		if (returnV == null)
			returnV = resource.getEmptyValue();
		if (!value.equals(returnV) && callUpdate && this.owner instanceof ISoma soma) {
			soma.onChannelResourceChanged(this, resource, returnV);
		}
	}

	@Override
	public void changeResourceAmount(IResource<?> resource, Comparable<?> value, boolean callUpdate) {
		changeMaterialResourceAmount(resource, resource.getMaterialBase(), value, callUpdate);

	}

	@Override
	public void addChannelMaterial(IChannel vectorMaterials, Collection<IMaterial> geneticMs) {
		embedded.get(vectorMaterials).addAll(geneticMs);
	}

	@Override
	public void changeMaterial(IMaterial material, boolean callUpdate) {
		IMaterial fmat = this.material;
		this.material = material;
		if (callUpdate && this.owner instanceof IForm<?>soma) {
			soma.onPartMaterialChange(this, fmat);
		}

	}

	@Override
	public void changeSize(float size, boolean callUpdate) {
		float fmat = this.size;
		this.size = size;
		if (callUpdate && this.owner instanceof IForm<?>soma) {
			soma.onPartSizeChange(this, fmat);
		}

	}

	@Override
	public void changeShape(IShape shape, boolean callUpdate) {
		IShape fmat = this.shape;
		this.shape = shape;
		if (this.owner instanceof ISoma body && shape.getProperty(ShapeProperty.INTEGRITY) < 1f) {
			System.out.println("Part " + this + " in " + this.owner + " reduced integrity to "
					+ this.shape.getProperty(ShapeProperty.INTEGRITY) + " causing part health to become "
					+ IPartHealth.Standard.INTEGRITY.health(body, this)
					+ ". A damage receptor now SHOULD detect integrity as "
					+ new DamageSensationReceptor(IPartHealth.Standard.INTEGRITY).getSensationLevel(Sensation.PAIN,
							IBeingAccess.create(null, this, body.getPartGraph(), 0)));
		}
		if (callUpdate && this.owner instanceof IForm<?>soma) {
			soma.onPartShapeChange(this, fmat);
		}

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
	public void addTether(ITether tether, boolean callUpdate) {
		if (!(owner instanceof ISoma))
			throw new UnsupportedOperationException();
		if (this.immaterialTethers.put(tether.getTetherType(), tether.getReferentProfile(), tether) == null
				&& callUpdate) {
			((ISoma) owner).onAddTether(tether, this);
		}

	}

	@Override
	public Collection<ITether> getTethers() {
		return immaterialTethers.values();
	}

	@Override
	public Collection<ITether> getTethers(IProfile tether) {
		return immaterialTethers.column(tether).values();
	}

	@Override
	public Collection<ITether> getTethers(TetherType type) {
		return immaterialTethers.row(type).values();
	}

	@Override
	public boolean hasTether(IProfile tether) {
		return !immaterialTethers.column(tether).isEmpty();
	}

	@Override
	public boolean hasTether(ITether tether) {
		return immaterialTethers.get(tether.getTetherType(), tether.getReferentProfile()).equals(tether);
	}

	@Override
	public boolean hasTether(TetherType type, IProfile tether) {
		return immaterialTethers.contains(type, tether);
	}

	@Override
	public boolean removeTethers(IProfile tether, boolean callUpdate) {
		if (!(owner instanceof ISoma))
			throw new UnsupportedOperationException();
		Set<ITether> removed = Sets.newHashSet(immaterialTethers.column(tether).values());
		immaterialTethers.column(tether).clear();
		if (!removed.isEmpty()) {
			if (callUpdate)
				((ISoma) owner).onRemoveTethers(removed, this);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeTether(ITether tether, boolean callUpdate) {
		if (!(owner instanceof ISoma))
			throw new UnsupportedOperationException();
		if (immaterialTethers.remove(tether.getTetherType(), tether.getReferentProfile()) != null) {
			if (callUpdate)
				((ISoma) owner).onRemoveTether(tether, this);
			return true;
		}
		return false;
	}

	@Override
	public ITether getTether(TetherType type, IProfile prof) {
		return immaterialTethers.get(type, prof);
	}

	@Override
	public boolean removeTether(TetherType type, IProfile tether, boolean callUpdate) {
		if (!(owner instanceof ISoma))
			throw new UnsupportedOperationException();
		ITether teth = null;
		if ((teth = immaterialTethers.remove(type, tether)) != null) {
			if (callUpdate)
				((ISoma) owner).onRemoveTether(teth, this);
			return true;
		}
		return false;
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
		newPart.embedded = MultimapBuilder.hashKeys().hashSetValues().build(this.embedded);
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
		if (obj == this) {
			return true;
		}
		if (obj instanceof IPart part) {
			return this.id.equals(part.getUUID());
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
				+ (immaterialTethers.isEmpty() ? "" : ",tethers=" + immaterialTethers.values())
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

	@Override
	public ISoma getTrueOwner() {
		return (ISoma) this.trueOwner;
	}

	@Override
	public void setTrueOwner(IForm<?> so) {
		this.trueOwner = so;
	}

}
