package biology.systems.types;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import actor.Actor;
import actor.construction.INutritionType;
import actor.construction.ISystemHolder;
import actor.construction.physical.IComponentPart;
import biology.systems.ESystem;
import biology.systems.EnergySystem;
import biology.systems.SystemType;
import energy.IEnergyUnit.EnergyUnit;
import sim.physicality.PhysicalState;

public class HungerSystem extends EnergySystem {

	private double hungerChance;
	private boolean canEat = true;
	private boolean canNourish = true;
	private Set<INutritionType> edibleTypes = Set.of();

	public static enum HungerLevel {
		FULL, WELL_FED, STABLE, HUNGRY, MALNOURISHED, STARVING;

	}

	/**
	 * chance out of 20
	 * 
	 * @param owner
	 * @param max
	 * @param chance
	 * @param edibleTypes types of nutrition this being can eat
	 */
	public HungerSystem(ISystemHolder owner, double max, double chance, Iterable<INutritionType> edibleTypes) {

		super(SystemType.HUNGER, owner, EnergyUnit.LIFE, max, max);
		this.hungerChance = chance;
		this.edibleTypes = ImmutableSet.<INutritionType>builder().addAll(edibleTypes).build();
	}

	@Override
	public boolean canUpdate() {
		return canNourish;
	}

	/**
	 * if this entity is capable of eating TODO make eating more general
	 * 
	 * @return
	 */
	public boolean canEat() {
		return canEat;
	}

	/**
	 * if this entity can supply its life system with energy
	 * 
	 * @return
	 */
	public boolean canNourish() {
		return canNourish;
	}

	public void disableEating() {
		this.canEat = false;

	}

	public void enableEating() {
		this.canEat = true;
	}

	public void disableNourishing() {
		this.canNourish = false;
	}

	public void enableNourishing() {
		this.canNourish = true;
	}

	@Override
	protected void update(SystemType<?> type, ESystem other) {

		if (other instanceof LifeSystem life) {
			if (canNourish) {
				this.supplyEnergy(life, life.getPowerUse() * hungerChance / 20.0 + 1);
			}

		}
	}

	/**
	 * TODO needs from system
	 * 
	 * @Override protected void update(long ticks) { super.update(ticks); double
	 *           percent = this.getPercent(); if (percent < 0.2) { if
	 *           (!this.getNeeds().get(NeedType.SUSTENANCE).stream().anyMatch((a) ->
	 *           a.getDegree() == Degree.SEVERE)) {
	 *           this.clearNeeds(NeedType.SUSTENANCE); this.postNeed(new
	 *           SustenanceNeed(Degree.SEVERE, null)); } } else if (percent < 0.5) {
	 *           if (!this.getNeeds().get(NeedType.SUSTENANCE).stream().anyMatch((a)
	 *           -> a.getDegree() == Degree.MODERATE)) {
	 *           this.clearNeeds(NeedType.SUSTENANCE); this.postNeed(new
	 *           SustenanceNeed(Degree.MODERATE, null)); } } else if (percent < 0.7)
	 *           { if
	 *           (!this.getNeeds().get(NeedType.SUSTENANCE).stream().anyMatch((a) ->
	 *           a.getDegree() == Degree.MILD)) {
	 *           this.clearNeeds(NeedType.SUSTENANCE); this.postNeed(new
	 *           SustenanceNeed(Degree.MILD, null)); } } }
	 */
	public double getHunger() {
		return this.getEnergy();
	}

	public double getHungerChance() {
		return hungerChance;
	}

	/**
	 * eat the given part of the food, return whether it can be eaten; -2 means this
	 * entity is incapable of eating, -1 means it's too big, 0 means it provides no
	 * nourishment, 1 means it can be eaten TODO figure this out
	 */
	public int eat(Actor edible, IComponentPart part) {
		if (!this.canEat) {
			return -2;
		}
		boolean canEat = false;
		for (INutritionType nuttype : this.edibleTypes) {
			if (part.nutritionTypes() % nuttype.primeFactor() == 0) {
				canEat = true;
				break;
			}
		}
		if (canEat) {
			this.supplyEnergy(part.getNutrition());
			// this.supplyEnergy(1);
			part.getMaterials().forEach((type, mat) -> mat.changeState(PhysicalState.GONE));
			edible.getPhysical().updatePart(part);
			return 1;
		}
		return 0;
		// }
		// return 0;
	}

}
