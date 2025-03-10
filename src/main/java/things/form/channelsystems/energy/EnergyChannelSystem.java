package things.form.channelsystems.energy;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import _utilities.couplets.Triplet;
import things.form.channelsystems.ChannelNeed;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import thinker.individual.IMindSpirit;

public class EnergyChannelSystem implements IChannelSystem {

	private String name;
	private EnergyChannelResource resource;
	private EnergyChannel channel;
	private String controlCenterPart;
	private EnergyGeneratorChannelCenter generatorType;
	private ChannelNeed energyChannelNeed;

	public EnergyChannelSystem(String name, String controlCenterPart, float maxStorable) {
		this.name = name;
		this.resource = new EnergyChannelResource(this.name + "_energy", maxStorable);
		this.channel = new EnergyChannel(this.name + "_pathway", resource, this);
		this.controlCenterPart = controlCenterPart;
		this.generatorType = new EnergyGeneratorChannelCenter(this.name + "_generator", this);
		this.energyChannelNeed = new ChannelNeed("energy", this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public ChannelType getType() {
		return ChannelType.ENERGY;
	}

	@Override
	public Collection<ChannelNeed> getChannelSystemNeeds() {
		return Collections.singleton(energyChannelNeed);
	}

	@Override
	public float getNeedLevel(IMindSpirit spirit, IComponentPart body, ChannelNeed forNeed) {
		// TODO energy channel need
		return IChannelSystem.super.getNeedLevel(spirit, body, forNeed);
	}

	@Override
	public Collection<EnergyChannelResource> getChannelResources() {
		return Collections.singleton(this.resource);
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes() {
		return Collections.singleton(generatorType);
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes(ChannelRole role) {
		if (role == ChannelRole.GENERATION) {
			return getCenterTypes();
		}
		return Collections.emptySet();
	}

	@Override
	public Collection<IChannel> getChannelConnectionTypes() {

		return Collections.singleton(channel);
	}

	@Override
	public String toString() {
		return "sys{~" + this.name + "~}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnergyChannelSystem ics) {
			return this.name.equals(ics.name()) && ics.getType() == this.getType()
					&& this.generatorType.equals(ics.generatorType) && this.channel.equals(ics.channel)
					&& this.resource.equals(ics.resource) && this.controlCenterPart.equals(ics.controlCenterPart);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + generatorType.hashCode() + channel.hashCode() + this.resource.hashCode()
				+ this.controlCenterPart.hashCode();
	}

	@Override
	public Collection<? extends IComponentPart> populateBody(ISoma body) {
		Collection<IComponentPart> brains = body.getPartsByName(controlCenterPart);
		for (IComponentPart brain : brains) {
			brain.addAbility(generatorType, true);
			for (Triplet<IComponentPart, IPartConnection, IComponentPart> edge : (Iterable<Triplet<IComponentPart, IPartConnection, IComponentPart>>) () -> body
					.getRepresentationGraph()
					.edgeTraversalIteratorBFS(brain, Set.copyOf(PartConnection.attachments()), (a, b) -> true)) {
				body.addChannel(edge.getFirst(), channel, edge.getThird(), true);
			}

		}
		return brains;
	}

	@Override
	public void onBodyUpdate(ISoma body, IComponentPart updated) {
		// TODO update channel system

	}

	@Override
	public boolean onBodyLoss(ISoma body, IComponentPart lost) {
		// TODO how to destroy energy system
		return true;
	}

	@Override
	public void onBodyNew(ISoma body, IComponentPart gained, IPartConnection connection, IComponentPart to,
			boolean isNew) {
		if (to.embeddedMaterials().containsAll(channel.getVectorMaterials())) {
			body.addChannel(to, channel, gained, false);
		}
	}

}
