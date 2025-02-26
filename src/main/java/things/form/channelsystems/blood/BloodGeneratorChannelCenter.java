package things.form.channelsystems.blood;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IResource;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import utilities.couplets.Pair;

public class BloodGeneratorChannelCenter implements IChannelCenter {

	private String name;
	private BloodChannelSystem system;

	public BloodGeneratorChannelCenter(String name, BloodChannelSystem bloodChannelSystem) {
		this.name = name;
		this.system = bloodChannelSystem;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<IPartStat<?>> getStats() {

		return Collections.emptySet();
	}

	@Override
	public boolean controllable() {
		return false;
	}

	@Override
	public ChannelRole getRole() {
		return ChannelRole.GENERATION;
	}

	@Override
	public BloodChannelSystem getSystem() {
		return system;
	}

	@Override
	public boolean canTick(ISoma body, IComponentPart part, long ticks) {
		return true;
	}

	@Override
	public Pair<IResource<?>, ?> generate(ISoma body, IComponentPart part, long ticks) {
		// TODO generate blood
		return null;
	}

	@Override
	public String toString() {
		return "{|" + this.name + "|}";
	}

}
