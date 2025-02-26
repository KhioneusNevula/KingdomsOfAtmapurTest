package things.form.channelsystems.energy;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import utilities.couplets.Pair;

/**
 * A ChannelCenter representing something that generates some energy
 * 
 * @author borah
 *
 */
public class EnergyGeneratorChannelCenter implements IChannelCenter {

	private String name;
	private IChannelSystem system;

	public EnergyGeneratorChannelCenter(String name, IChannelSystem system) {
		this.name = name;
		this.system = system;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public IChannelSystem getSystem() {
		return system;
	}

	/**
	 * TODO magic regen rate stat(?)
	 */
	@Override
	public Collection<IPartStat<?>> getStats() {
		return Collections.emptySet();
	}

	@Override
	public ChannelRole getRole() {
		return ChannelRole.GENERATION;
	}

	@Override
	public boolean canTick(ISoma body, IComponentPart part, long ticks) {
		return true;
	}

	@Override
	public Pair<IResource<?>, ?> generate(ISoma body, IComponentPart part, long ticks) {
		// TODO create energy
		return IChannelCenter.super.generate(body, part, ticks);
	}

	@Override
	public boolean controllable() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnergyGeneratorChannelCenter cc) {
			return this.name.equals(cc.name());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.getClass().hashCode();
	}

	@Override
	public String toString() {
		return "{|" + name + "|}";
	}

}
