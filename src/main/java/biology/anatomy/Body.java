package biology.anatomy;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Table;

import actor.Actor;
import actor.construction.physical.IComponentPart;
import actor.construction.physical.IPartAbility;
import actor.construction.physical.IPhysicalActorObject;
import actor.construction.properties.ISensableTrait;
import actor.construction.properties.SenseProperty;
import biology.sensing.ISense;
import metaphysical.ISpiritObject;
import metaphysical.ISpiritObject.SpiritType;
import metaphysical.soul.AbstractSoul;
import metaphysical.soul.ISoulGenerator;
import sim.physicality.ExistencePlane;

/**
 * Add colors/appearances to body parts; add traits
 * 
 * @author borah
 *
 */
public class Body implements IPhysicalActorObject {

	private Table<String, UUID, BodyPart> bodyParts;
	private Map<UUID, BodyPart> noParentParts;
	private Map<UUID, BodyPart> outermostParts;
	private Map<String, ITissueLayerType> tissueTypes = Map.of();
	private Map<String, IBodyPartType> partTypes = Map.of();
	private Map<String, ITissueLayerType> bloodTypes = Map.of();
	private Multimap<IPartAbility, BodyPart> partsByAbility = MultimapBuilder.hashKeys().treeSetValues().build();
	private UUID rootID;
	private BodyPart rootPart;
	private Actor owner;
	private boolean built;
	private float lifePercent = 1f;
	private ISpecies species;
	private int existencePlane = ExistencePlane.PHYSICAL.primeFactor();
	private int visibilityPlane = ExistencePlane.ALL_PLANES.primeFactor();
	private BodyPart onlyPart;
	private int intactParts;
	private int w, h;
	private HitboxType hitbox;
	private float mass;
	private Multimap<SpiritType, ISpiritObject> allSpirits = MultimapBuilder.enumKeys(SpiritType.class).hashSetValues()
			.build();
	private AbstractSoul soulReference;

	public Body(Actor owner, int radius, float mass) {
		this.owner = owner;
		this.hitbox = HitboxType.CIRCLE;
		this.w = radius;
		this.mass = mass;
	}

	public Body(Actor owner, ISpecies template) {
		this.owner = owner;
		this.species = template;
		this.hitbox = HitboxType.CIRCLE;
		this.w = 10; // TODO species specific radius
		this.mass = 70; // TODO species specific mass
	}

	@Override
	public int getHitboxHeight() {
		return h;
	}

	@Override
	public int getHitboxRadius() {
		return w;
	}

	@Override
	public HitboxType getHitboxType() {
		return this.hitbox;
	}

	@Override
	public int getHitboxWidth() {
		return w;
	}

	public float getMass() {
		return mass;
	}

	/**
	 * Set the amount of blood/life essence in this creature
	 * 
	 * @param lifePercent
	 */
	public void setLifePercent(float lifePercent) {
		this.lifePercent = lifePercent;
	}

	/**
	 * lose an amount of blood of the given percentage
	 */
	public void bleed(float byPercent) {
		this.lifePercent *= (1 - byPercent);
	}

	public ISpecies getObjectType() {
		return species;
	}

	@Override
	public boolean isBuilt() {
		return built;
	}

	@Override
	public int sensabilityMode(ISense toSense) {
		return this.visibilityPlane;
	}

	/**
	 * What physicality this body has
	 * 
	 * @return
	 */
	@Override
	public int physicalityMode() {
		return existencePlane;
	}

	@Override
	public void changePhysicality(int newPhysicality) {
		this.existencePlane = newPhysicality;
	}

	@Override
	public void changeSensability(int newVisibility) {
		this.visibilityPlane = newVisibility;
	}

	public void buildBody() {
		if (species != null) {
			System.out.println("Creating body from template of species:" + species.name());
			this.addTissueLayers(species.tissueTypes().values());
			this.addBodyPartTypes(species.partTypes().values());

		}
		System.out.print("Starting to build body for " + owner + ".");
		initializeBody();
		System.out.print(".");
		initializeBodyConnections();
		System.out.println(". Body built for " + owner);
		built = true;
	}

	@Override
	public Map<UUID, BodyPart> getPartsWithoutParent() {
		return noParentParts == null ? Map.of() : noParentParts;
	}

	@Override
	public Collection<BodyPart> getOutermostParts() {
		return outermostParts == null ? Set.of() : outermostParts.values();
	}

	@Override
	public <A extends ISensableTrait> A getGeneralProperty(SenseProperty<A> property, boolean ignoreType) {
		return null;
	}

	@Override
	public Collection<SenseProperty<?>> getGeneralSensableProperties() {
		return Collections.emptySet();
	}

	private void initializeBody() {
		for (Map.Entry<String, IBodyPartType> entry : partTypes.entrySet()) {
			if (bodyParts == null)
				bodyParts = HashBasedTable.<String, UUID, BodyPart>create();
			String key = entry.getKey();
			IBodyPartType partType = entry.getValue();
			int count = partType.count();
			String parentPartStr = partType.getParent();
			if (parentPartStr != null) {
				while (parentPartStr != null) {
					IBodyPartType parentPart = partTypes.get(parentPartStr);
					if (parentPart == null)
						throw new IllegalStateException(
								"no part in body called " + parentPartStr + "; referenced as parent by " + key);
					count *= parentPart.count();
					parentPartStr = parentPart.getParent();
				}
			}
			for (int i = 0; i < count; i++) {
				BodyPart part = new BodyPart(partType, partTypes, tissueTypes);
				intactParts++;
				bodyParts.put(partType.getName(), part.id, part);
				for (IPartAbility ab : partType.abilities()) {
					this.partsByAbility.put(ab, part);
				}
				if (partType.isRoot()) {
					if (rootID != null)
						throw new RuntimeException("Existing root " + rootPart.type.getName() + " " + rootID
								+ "; trying to insert new root " + part.type.getName() + " " + part.id);

				}
				if (partType.getParent() == null) {
					if (noParentParts == null)
						noParentParts = new TreeMap<>();
					noParentParts.put(part.id, part);
				}
			}
		}
		if (this.bodyParts.size() == 1) {
			this.onlyPart = this.bodyParts.values().iterator().next();
		}
	}

	private void initializeBodyConnections() {
		if (bodyParts == null)
			throw new RuntimeException("first step incomplete");
		for (String type : bodyParts.rowKeySet()) {
			Collection<BodyPart> parts = bodyParts.row(type).values();
			IBodyPartType partType = partTypes.get(type);
			String parentPart = partType.getParent();
			String surrounding = partType.getSurroundingPart();
			if (parentPart != null) {
				Collection<BodyPart> parentParts = bodyParts.row(parentPart).values();
				Iterator<BodyPart> parentIter = parentParts.iterator();
				BodyPart parent = parentIter.next();
				int count = 0;
				for (BodyPart part : parts) {
					count++;
					if (count <= partType.count()) {
						parent.addChild(part);
						part.setParent(parent);
					} else {
						count = 0;
						if (parentIter.hasNext()) {
							parent = parentIter.next();
						} else {
							break;
						}
					}
				}
			}
			if (surrounding != null) {/*
										 * System.out.println("surrounded " + type + " by " + surrounding);
										 */
				Collection<BodyPart> surroundParts = bodyParts.row(surrounding).values();
				IBodyPartType surroundingType = partTypes.get(surrounding);
				for (BodyPart part : parts) {
					BodyPart surrounder = null;
					BodyPart currentChecked = part.parent;
					while (currentChecked != null) {
						if (currentChecked.type.equals(surroundingType)) {
							surrounder = currentChecked;
							break;
						} else {
							for (BodyPart child : currentChecked.getSurroundeds().get(surrounding)) {
								surrounder = child;
								break;
							}
						}
						currentChecked = currentChecked.parent;
					}

					if (surrounder == null) {
						surrounder = surroundParts.iterator().next();
					}
					part.setSurrounding(surrounder);
					surrounder.addSurrounded(part);
				}
			}
		}

		for (String type : bodyParts.rowKeySet()) {
			Collection<BodyPart> parts = bodyParts.row(type).values();
			IBodyPartType partType = partTypes.get(type);
			String parentPart = partType.getParent();
			String surrounding = partType.getSurroundingPart();
			if (surrounding == null) {
				if (parentPart != null) {
					for (BodyPart part : parts) {
						BodyPart parent = part.parent;
						boolean surrounded = false;
						while (parent != null) {
							if (parent.type.getSurroundingPart() != null) {
								surrounded = true;/*
													 * System.out.println("surrounded " + type + " at " +
													 * parent.type.getName() + " by " +
													 * parent.type.getSurroundingPart());
													 */
								break;
							}
							parent = parent.parent;
						}
						if (!surrounded) {
							(outermostParts == null ? outermostParts = new TreeMap<>() : outermostParts).put(part.id,
									part);/*
											 * System.out.println("unsurrounded " + type);
											 */
						}

					}
				} else {
					for (BodyPart part : parts) {
						(outermostParts == null ? outermostParts = new TreeMap<>() : outermostParts).put(part.id, part);
						/* System.out.println("unsurrounded " + type); */

					}
				}
			}

		}

	}

	protected Body addBodyPartTypes(Iterable<IBodyPartType> parts) {
		Map<String, IBodyPartType> types = new TreeMap<>();
		for (IBodyPartType part : parts) {
			types.put(part.getName(), part);
		}
		if (partTypes.isEmpty())
			partTypes = ImmutableMap.copyOf(types);
		else {
			Map<String, IBodyPartType> mapa = new TreeMap<>(partTypes);
			mapa.putAll(types);
			partTypes = ImmutableMap.<String, IBodyPartType>builder().putAll(mapa).build();
		}
		return this;
	}

	protected Body addBodyPartTypes(IBodyPartType... types) {
		return this.addBodyPartTypes(Set.of(types));
	}

	/**
	 * Run these before adding body parts, because the body needs to recognize
	 * tissue layers before it recognizes parts
	 * 
	 * @param layers
	 * @return
	 */
	protected Body addTissueLayers(Iterable<ITissueLayerType> layers) {
		Map<String, ITissueLayerType> types = new TreeMap<>();
		Map<String, ITissueLayerType> bloodTypes = new TreeMap<>();
		for (ITissueLayerType layer : layers) {
			types.put(layer.getName(), layer);
			if (layer.isLifeEssence())
				bloodTypes.put(layer.getName(), layer);
		}
		if (tissueTypes.isEmpty())
			tissueTypes = ImmutableMap.copyOf(types);
		else {
			Map<String, ITissueLayerType> mapa = new TreeMap<>(tissueTypes);
			mapa.putAll(types);
			tissueTypes = ImmutableMap.<String, ITissueLayerType>builder().putAll(mapa).build();
		}
		if (!bloodTypes.isEmpty()) {
			if (this.bloodTypes.isEmpty()) {
				this.bloodTypes = ImmutableMap.copyOf(bloodTypes);
			} else {
				Map<String, ITissueLayerType> mapa = new TreeMap<>(this.bloodTypes);
				mapa.putAll(bloodTypes);
				this.bloodTypes = ImmutableMap.<String, ITissueLayerType>builder().putAll(mapa).build();
			}
		}
		return this;
	}

	/**
	 * Run these before adding body parts, because the body needs to recognize
	 * tissue layers before it recognizes parts
	 * 
	 * @param layers
	 * @return
	 */
	protected Body addTissueLayers(ITissueLayerType... layers) {
		return addTissueLayers(Set.of(layers));
	}

	/**
	 * Gets the types of life essence in this entity -- what bleeds from tissue
	 * which hasLifeEssence, and if enough of this is gone, the entity dies
	 */
	public Map<String, ITissueLayerType> getLifeEssenceTypes() {
		return bloodTypes;
	}

	/**
	 * Amount of blood/life substance in this creature
	 * 
	 * @return
	 */
	public float getLifePercent() {
		return lifePercent;
	}

	@Override
	public Collection<BodyPart> getPartsWithAbility(IPartAbility ability) {
		return partsByAbility.get(ability);
	}

	@Override
	public Map<String, IBodyPartType> getPartTypes() {
		return partTypes;
	}

	@Override
	public Collection<BodyPart> getParts() {
		return this.bodyParts.values();
	}

	@Override
	public boolean hasSinglePart() {
		return this.bodyParts.size() == 1;
	}

	@Override
	public void updatePart(IComponentPart part) {
		if (!this.bodyParts.containsValue(part)) {
			throw new IllegalArgumentException();
		}
		BodyPart bpart = (BodyPart) part;
		bpart.getSpirits().forEach((s) -> s.onPartUpdate(bpart));
		bpart.checkIfUsual();
		boolean gone = bpart.isGone();
		if (gone) {
			intactParts--;
		}
	}

	@Override
	public boolean completelyDestroyed() {
		return this.intactParts == 0;
	}

	@Override
	public boolean isDead() {
		return this.soulReference == null;
	}

	@Override
	public IComponentPart mainComponent() {
		if (!hasSinglePart())
			throw new UnsupportedOperationException();
		return this.onlyPart;
	}

	/**
	 * Gets the soul of this being, or null if it lacks a soul
	 * 
	 * @return
	 */
	public AbstractSoul getSoulReference() {
		return soulReference;
	}

	@Override
	public void onGiveFirstSoul(AbstractSoul soul, ISoulGenerator soulgen) {
		if (this.soulReference == null)
			this.soulReference = soul;
	}

	@Override
	public boolean containsSpirit(ISpiritObject spir, IComponentPart part) {
		if (part instanceof BodyPart bpart) {
			return bpart.containsSpirit(spir);
		}
		throw new IllegalArgumentException();
	}

	@Override
	public boolean containsSpirit(ISpiritObject spirit) {
		if (spirit == soulReference)
			return true;
		return this.allSpirits.containsEntry(spirit.getSpiritType(), spirit);
	}

	@Override
	public Collection<? extends ISpiritObject> getContainedSpirits(IComponentPart part) {
		if (part instanceof BodyPart bpart) {
			return bpart.getSpirits();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public Collection<? extends ISpiritObject> getContainedSpirits(SpiritType type) {
		return this.allSpirits.get(type);
	}

	@Override
	public void removeSpirit(ISpiritObject spirit) {
		if (!spirit.isTetheredToWhole()) {
			for (IComponentPart pa : spirit.getTethers()) {
				if (pa instanceof BodyPart part) {
					part.removeSpirit(spirit);
				} else {
					throw new IllegalArgumentException();
				}
			}
		}
		this.allSpirits.remove(spirit.getSpiritType(), spirit);
		if (soulReference == spirit)
			soulReference = null;
	}

	@Override
	public void tetherSpirit(ISpiritObject spirit, Collection<IComponentPart> tethers) {
		if (tethers != null) {
			for (IComponentPart pa : tethers) {
				if (pa instanceof BodyPart part) {
					part.addSpirit(spirit);
				} else {
					throw new IllegalArgumentException();
				}
			}
		}
		this.allSpirits.put(spirit.getSpiritType(), spirit);
	}

	@Override
	public Actor getOwner() {
		return this.owner;
	}

	@Override
	public String report() {
		StringBuilder builder = new StringBuilder();
		builder.append("Body " + (this.completelyDestroyed() ? "#" : "") + " of " + this.species + "(" + this.owner
				+ "):\n\t parts:"
				+ (bodyParts == null ? null
						: this.bodyParts.values().stream().map((a) -> a.report()).collect(Collectors.toSet()))
				+ "\n\t tissuetypes:" + this.tissueTypes);

		return builder.toString();
	}

}
