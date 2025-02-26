package things.form.channelsystems.blood;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannelCenter;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;

public class HeartChannelCenter implements IChannelCenter {

	private String name;
	private BloodChannelSystem system;

	public HeartChannelCenter(String name, BloodChannelSystem bloodChannelSystem) {
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
		return ChannelRole.DISTRIBUTION;
	}

	@Override
	public BloodChannelSystem getSystem() {
		return system;
	}

	@Override
	public boolean canTick(ISoma body, IComponentPart part, long ticks) {
		return true; // TODO when would a heart stop beating...
	}

	@Override
	public void automaticTick(ISoma body, IComponentPart part, long ticks) {
		IChannelCenter.super.automaticTick(body, part, ticks);
		// TODO hearts beat, also do not generate blood
		body.getConnectedParts(part).forEach((a) -> {
			float blood = a.getResourceAmount(this.system.getBloodMaterial());
			float newblood = blood + 0.05f;
			if (newblood <= system.getBloodMaterial().getMaxValue()) {
				a.changeResourceAmount(this.system.getBloodMaterial(), newblood, true);
			}
		});
	}

	@Override
	public String toString() {
		return "{|" + this.name + "|}";
	}

}
