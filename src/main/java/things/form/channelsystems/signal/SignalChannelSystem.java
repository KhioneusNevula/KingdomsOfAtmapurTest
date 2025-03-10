package things.form.channelsystems.signal;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import _utilities.couplets.Triplet;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.material.IMaterial;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;

public class SignalChannelSystem implements IChannelSystem {

	private String name;
	private SignalChannelResource resource;
	private SignalChannel channel;
	private String controlCenterPart;
	private SignalControlCenter brainType;

	public SignalChannelSystem(String name, IMaterial signalVectorMaterial, String controlCenterPart) {
		this.name = name;
		this.resource = new SignalChannelResource(name + "_signal");
		this.channel = new SignalChannel(name + "_pathway", signalVectorMaterial, resource, this);
		this.controlCenterPart = controlCenterPart;
		this.brainType = new SignalControlCenter(name + "_controller", this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public ChannelType getType() {
		return ChannelType.SIGNAL;
	}

	@Override
	public Collection<SignalChannelResource> getChannelResources() {
		return Collections.singleton(this.resource);
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes() {
		return Collections.singleton(brainType);
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes(ChannelRole role) {
		if (role == ChannelRole.CONTROL) {
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
		if (obj instanceof SignalChannelSystem ics) {
			return this.name.equals(ics.name()) && ics.getType() == ChannelType.SIGNAL
					&& this.brainType.equals(ics.brainType) && this.channel.equals(ics.channel)
					&& this.controlCenterPart.equals(ics.controlCenterPart);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + brainType.hashCode() + channel.hashCode() + controlCenterPart.hashCode();
	}

	@Override
	public Collection<? extends IComponentPart> populateBody(ISoma body) {
		Collection<IComponentPart> brains = body.getPartsByName(controlCenterPart);
		for (IComponentPart brain : brains) {
			brain.addAbility(brainType, true);
			for (Triplet<IComponentPart, IPartConnection, IComponentPart> edge : (Iterable<Triplet<IComponentPart, IPartConnection, IComponentPart>>) () -> body
					.getRepresentationGraph()
					.edgeTraversalIteratorBFS(brain, Set.copyOf(PartConnection.attachments()), (a, b) -> true)) {
				body.addChannel(edge.getFirst(), channel, edge.getThird(), true);
				edge.getThird().addEmbeddedMaterials(channel.getVectorMaterials(), true);
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
