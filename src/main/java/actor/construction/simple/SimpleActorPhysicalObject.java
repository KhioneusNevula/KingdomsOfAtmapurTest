package actor.construction.simple;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import actor.Actor;
import actor.IUniqueEntity;
import actor.construction.physical.IActorType;
import actor.construction.physical.IComponentPart;
import actor.construction.physical.IComponentType;
import actor.construction.physical.IPartAbility;
import actor.construction.physical.IPhysicalActorObject;
import actor.construction.properties.ISensableTrait;
import actor.construction.properties.SenseProperty;
import biology.sensing.ISense;
import metaphysical.ISpiritObject;
import metaphysical.ISpiritObject.SpiritType;
import sim.physicality.ExistencePlane;
import sim.physicality.IInteractability;

public class SimpleActorPhysicalObject implements IPhysicalActorObject {

	private Actor owner;

	private int visi = ExistencePlane.ALL_PLANES.primeFactor();
	private int planes = ExistencePlane.PHYSICAL.primeFactor();

	private int w, h;
	private HitboxType hitbox;

	private IComponentPart part;

	private float mass;

	/**
	 * If this entity has a specific "part ability" or whatever
	 */
	private IPartAbility ability;

	private Multimap<Boolean, ISpiritObject> tetheredSpirits = MultimapBuilder.treeKeys().hashSetValues().build(); // true
																													// =
																													// tethered
																													// to
																													// the
																													// main
																													// part,
																													// false
																													// =
	// to whole

	public SimpleActorPhysicalObject(Actor owner, IComponentPart mainPart, float mass) {
		this.owner = owner;
		this.part = mainPart;
		this.w = 10;
		this.mass = mass;
		this.hitbox = HitboxType.CIRCLE;
	}

	@Override
	public int sensabilityMode(ISense toSense) {
		return visi;
	}

	@Override
	public IUniqueEntity getOwner() {
		return owner;
	}

	@Override
	public IActorType getObjectType() {
		return owner.getObjectType();
	}

	@Override
	public Collection<IComponentPart> getOutermostParts() {
		return Collections.singleton(part);
	}

	@Override
	public boolean isBuilt() {
		return true;
	}

	@Override
	public Map<UUID, ? extends IComponentPart> getPartsWithoutParent() {
		return Map.of(part.getId(), part);
	}

	@Override
	public Collection<? extends IComponentPart> getPartsWithAbility(IPartAbility ability) {
		return this.ability != null && this.ability.equals(ability) ? Collections.singleton(part)
				: Collections.emptySet();
	}

	@Override
	public Map<String, ? extends IComponentType> getPartTypes() {

		return Map.of(part.getType().toString(), part.getType());
	}

	@Override
	public String report() {
		return "single-part" + (this.completelyDestroyed() ? "#" : "") + "(" + this.part.report() + ")"
				+ (this.tetheredSpirits.isEmpty() ? "" : "(spirits:" + this.tetheredSpirits.values() + ")");

	}

	@Override
	public Collection<? extends IComponentPart> getParts() {
		return Collections.singleton(part);
	}

	@Override
	public boolean hasSinglePart() {
		return true;
	}

	@Override
	public IComponentPart mainComponent() {
		return part;
	}

	@Override
	public int physicalityMode() {
		return this.planes;
	}

	public float getMass() {
		return mass;
	}

	public SimpleActorPhysicalObject setPhysicality(IInteractability... ints) {
		this.planes = IInteractability.combine(ints);
		return this;
	}

	public SimpleActorPhysicalObject setVisibility(IInteractability... ints) {
		this.visi = IInteractability.combine(ints);
		return this;
	}

	public SimpleActorPhysicalObject makeCircle(int radius) {
		this.hitbox = HitboxType.CIRCLE;
		this.w = radius;
		return this;
	}

	public SimpleActorPhysicalObject makeRectangle(int width, int height) {
		this.hitbox = HitboxType.RECTANGLE;
		this.w = width;
		this.h = height;
		return this;
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

	@Override
	public void updatePart(IComponentPart part) {
		if (part != this.part) {
			throw new IllegalArgumentException();
		}
		this.tetheredSpirits.get(true).forEach((s) -> s.onPartUpdate(part));
		part.checkIfUsual();
	}

	@Override
	public boolean completelyDestroyed() {
		return this.part.isGone();
	}

	public void changePhysicality(int newPhysicality) {
		this.planes = newPhysicality;
	}

	public void changeSensability(int newVisibility) {
		this.visi = newVisibility;
	}

	@Override
	public boolean isDead() {
		return true;
	}

	@Override
	public boolean containsSpirit(ISpiritObject spirit) {
		return this.tetheredSpirits.values().contains(spirit);
	}

	@Override
	public Collection<? extends ISpiritObject> getContainedSpirits(IComponentPart part) {
		if (part != this.part)
			throw new IllegalArgumentException();
		return this.tetheredSpirits.get(true);
	}

	@Override
	public Collection<? extends ISpiritObject> getContainedSpirits(SpiritType type) {

		return this.tetheredSpirits.values().stream().filter((a) -> a.getSpiritType() == type)
				.collect(Collectors.toSet());
	}

	@Override
	public void removeSpirit(ISpiritObject spirit) {
		this.tetheredSpirits.remove(true, spirit);
		this.tetheredSpirits.remove(false, spirit);
	}

	@Override
	public void tetherSpirit(ISpiritObject spirit, Collection<IComponentPart> tethers) {
		if (tethers == null) {
			this.tetheredSpirits.put(false, spirit);
		} else {
			if (tethers.size() != 1 || !tethers.contains(part)) {
				throw new IllegalArgumentException();
			}
			this.tetheredSpirits.put(true, spirit);
		}
	}

	@Override
	public boolean containsSpirit(ISpiritObject spir, IComponentPart part) {

		return part == this.part && this.tetheredSpirits.containsEntry(true, spir);
	}

	@Override
	public <A extends ISensableTrait> A getGeneralProperty(SenseProperty<A> property, boolean ignoreType) {
		return null;
	}

	@Override
	public Collection<SenseProperty<?>> getGeneralSensableProperties() {
		return Collections.emptySet();
	}

}
