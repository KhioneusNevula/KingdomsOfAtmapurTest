package things.form.channelsystems.eat;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannelCenter;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.stains.IStain;

/**
 * A ChannelCenter representing something that intakes fuel (to make energy)
 * 
 * @author borah
 *
 */
public class FuelIntakeChannelCenter implements IChannelCenter {

	private String name;
	private FuelChannelSystem system;
	private FuelChannelResource foodResource;

	/**
	 * 
	 * @param name
	 * @param system
	 * @param resource the type of food this system can intake
	 */
	public FuelIntakeChannelCenter(String name, FuelChannelSystem system, FuelChannelResource resource) {
		this.name = name;
		this.system = system;
		this.foodResource = resource;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public FuelChannelSystem getSystem() {
		return system;
	}

	public FuelChannelResource getResource() {
		return foodResource;
	}

	@Override
	public Collection<IPartStat<?>> getStats() {
		return Collections.emptySet();
	}

	@Override
	public ChannelRole getRole() {
		return ChannelRole.INTAKE;
	}

	@Override
	public boolean canTick(ISoma body, IComponentPart part, long ticks) {
		return true;
	}

	@Override
	public boolean canIntake(ISoma body, IComponentPart part, IStain consumable) {
		return this.foodResource.isAllowedFood(consumable.getSubstance());
	}

	@Override
	public boolean canIntake(ISoma body, IComponentPart part, IComponentPart consumable) {
		return this.foodResource.isAllowedFood(consumable.getMaterial());
	}

	@Override
	public boolean intake(ISoma body, IComponentPart part, IStain consumable, long ticks) {
		// TODO Food intake
		return IChannelCenter.super.intake(body, part, consumable, ticks);
	}

	@Override
	public boolean intake(ISoma body, IComponentPart part, IComponentPart consumable, long ticks) {
		// TODO food intake
		System.out.println("[" + body + "] Gulp.");
		part.changeResourceAmount(foodResource, part.getResourceAmount(foodResource) + 0.1f, true);
		return true;
	}

	@Override
	public boolean controllable() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FuelIntakeChannelCenter cc) {
			return this.name.equals(cc.name()) && this.foodResource.equals(cc.foodResource);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + this.foodResource.hashCode() + this.getClass().hashCode();
	}

	@Override
	public String toString() {
		return "{|" + name + "|}";
	}

}
