package things.physical_form.channelsystems.signal;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import things.physical_form.ISoma;
import things.physical_form.channelsystems.IChannel;
import things.physical_form.channelsystems.IChannelCenter;
import things.physical_form.channelsystems.IChannelCenter.ChannelRole;
import things.physical_form.channelsystems.IChannelSystem;
import things.physical_form.components.IComponentPart;
import things.physical_form.graph.IPartConnection;
import things.physical_form.graph.PartConnection;
import things.physical_form.material.IMaterial;
import utilities.Triplet;

public class SignalChannelSystem implements IChannelSystem {

	private String name;
	private SignalChannelResource resource;
	private SignalChannel channel;
	private String controlCenterPart;
	private BrainSignalChannelCenter brainType;

	public SignalChannelSystem(String name, IMaterial signalVectorMaterial, String controlCenterPart) {
		this.name = name;
		this.resource = new SignalChannelResource("signal");
		this.channel = new SignalChannel("nerve", signalVectorMaterial, resource, this);
		this.controlCenterPart = controlCenterPart;
		this.brainType = new BrainSignalChannelCenter("brain", this);
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
		if (obj instanceof IChannelSystem ics) {
			return this.name.equals(ics.name()) && ics.getType() == ChannelType.SIGNAL
					&& Collections.singleton(this.brainType).equals(ics.getCenterTypes())
					&& Collections.singleton(this.channel).equals(ics.getChannelConnectionTypes());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + brainType.hashCode() + channel.hashCode();
	}

	@Override
	public <E extends IComponentPart> void populateBody(ISoma<E> body) {
		Collection<E> brains = body.getPartsByName(controlCenterPart);
		for (E brain : brains) {
			brain.addAbility(brainType, true);
			for (Triplet<E, IPartConnection, E> edge : (Iterable<Triplet<E, IPartConnection, E>>) () -> body
					.getRepresentationGraph()
					.edgeTraversalIteratorBFS(brain, Set.copyOf(PartConnection.attachments()), (a, b) -> true)) {
				body.addChannel(edge.getFirst(), channel, edge.getThird(), true);
				edge.getThird().addEmbeddedMaterials(channel.getVectorMaterials(), true);
			}

		}
	}

	@Override
	public <E extends IComponentPart> void onBodyUpdate(ISoma<E> body, E updated) {
		// TODO update channel system

	}

	@Override
	public <E extends IComponentPart> void onBodyLoss(ISoma<E> body, E lost) {

	}

	@Override
	public <E extends IComponentPart> void onBodyNew(ISoma<E> body, E gained, IPartConnection connection, E to,
			boolean isNew) {
		if (to.embeddedMaterials().containsAll(channel.getVectorMaterials())) {
			body.addChannel(to, channel, gained, false);
		}
	}

}
