package actor.construction.simple;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import actor.Actor;
import actor.IUniqueExistence;
import actor.construction.IBlueprintTemplate;
import actor.construction.IComponentPart;
import actor.construction.IComponentType;
import actor.construction.IPartAbility;
import actor.construction.IPhysicalActorObject;
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

	public SimpleActorPhysicalObject(Actor owner, IComponentPart mainPart, float mass) {
		this.owner = owner;
		this.part = mainPart;
		this.w = 10;
		this.mass = mass;
		this.hitbox = HitboxType.CIRCLE;
	}

	@Override
	public int visibilityMode() {
		return visi;
	}

	@Override
	public IUniqueExistence getOwner() {
		return owner;
	}

	@Override
	public IBlueprintTemplate getSpecies() {
		return owner.getSpecies();
	}

	@Override
	public Map<UUID, ? extends IComponentPart> getOutermostParts() {
		return Map.of(part.getId(), part);
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
		return "single-part" + (this.completelyDestroyed() ? "#" : "") + "(" + this.part.report() + ")";

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
		part.checkIfUsual();
	}

	@Override
	public boolean completelyDestroyed() {
		return this.part.isGone();
	}

	public void changePhysicality(int newPhysicality) {
		this.planes = newPhysicality;
	}

	public void changeVisibility(int newVisibility) {
		this.visi = newVisibility;
	}

}
