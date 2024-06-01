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

	private IComponentPart part;

	/**
	 * If this entity has a specific "part ability" or whatever
	 */
	private IPartAbility ability;

	public SimpleActorPhysicalObject(Actor owner, IComponentPart mainPart) {
		this.owner = owner;
		this.part = mainPart;
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

	public SimpleActorPhysicalObject setPhysicality(IInteractability... ints) {
		this.planes = IInteractability.combine(ints);
		return this;
	}

	public SimpleActorPhysicalObject setVisibility(IInteractability... ints) {
		this.visi = IInteractability.combine(ints);
		return this;
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
