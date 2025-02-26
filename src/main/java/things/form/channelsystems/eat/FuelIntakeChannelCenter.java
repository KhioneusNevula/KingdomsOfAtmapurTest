package things.form.channelsystems.eat;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IResource;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.interfaces.IThing;
import things.stains.IStain;
import utilities.couplets.Pair;

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
	public Pair<IResource<?>, ?> intake(ISoma body, IComponentPart part, IStain consumable, long ticks) {
		// TODO Food intake
		return IChannelCenter.super.intake(body, part, consumable, ticks);
	}

	@Override
	public Pair<IResource<?>, ?> intake(ISoma body, IComponentPart part, IThing consumable, long ticks) {
		// TODO food intake
		return IChannelCenter.super.intake(body, part, consumable, ticks);
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
