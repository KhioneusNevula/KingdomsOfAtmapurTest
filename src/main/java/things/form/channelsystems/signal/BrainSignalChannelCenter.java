package things.form.channelsystems.signal;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.spirit.ISpirit;
import utilities.graph.IRelationGraph;

/**
 * A ChannelCenter representing the brain
 * 
 * @author borah
 *
 */
public class BrainSignalChannelCenter implements IChannelCenter {

	private String name;
	private IChannelSystem system;

	public BrainSignalChannelCenter(String name, IChannelSystem system) {
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
	 * TODO what stats do brains use? some kind of capacity stat? idk
	 */
	@Override
	public Collection<IPartStat<?>> getStats() {
		return Collections.emptySet();
	}

	@Override
	public ChannelRole getRole() {
		return ChannelRole.CONTROL;
	}

	@Override
	public boolean canTick(ISoma body, IComponentPart part, long ticks) {
		return true;
	}

	@Override
	public void controlTick(ISoma body, IComponentPart brain, long tick) {
		IRelationGraph<IComponentPart, IPartConnection> connections = body.getChanneledParts(brain, system);

		for (IComponentPart part : (Iterable<IComponentPart>) (Iterable) (() -> connections.stream()
				.filter((p) -> p.hasAutomaticChannelCenter()).iterator())) {

			for (IChannelCenter center : part.getAutomaticChannelCenters()) {
				if (center.canTick(body, part, tick)) {
					center.automaticTick(body, part, tick);
				}
			}
		}
		for (ISpirit spirit : brain.getTetheredSpirits()) {
			spirit.runTick(brain, connections, body, tick);
		}
	}

	@Override
	public boolean controllable() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IChannelCenter cc) {
			return this.name.equals(cc.name()) && this.system.equals(cc.getSystem());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "{|" + name + "(" + this.system + ") |}";
	}

}
