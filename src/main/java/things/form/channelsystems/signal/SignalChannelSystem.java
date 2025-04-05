package things.form.channelsystems.signal;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import _sim.world.GameMap;
import _utilities.couplets.Triplet;
import metaphysics.spirit.ISpirit;
import party.collective.ICollective;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.settings.IKindSettings;
import things.form.material.generator.IMaterialGeneratorResource;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;

public class SignalChannelSystem implements IChannelSystem {

	private String name;
	private SignalChannelResource resource;
	private SignalChannel channel;
	private String controlCenterPart;
	private SignalControlCenter brainType;
	private BiFunction<ISoma, IComponentPart, ISpirit> spiritGen;

	/**
	 * 
	 * @param name                 the system name
	 * @param signalVectorMaterial the (base) material the nerves are made fromm
	 * @param controlCenterPart    the "brain" part
	 * @param spiritGen            the function that generates a Spirit for the
	 *                             "brain" part, or null if no such thing is needed
	 */
	public SignalChannelSystem(String name, IMaterialGeneratorResource signalVectorMaterial, String controlCenterPart,
			BiFunction<ISoma, IComponentPart, ISpirit> spiritGen) {
		this.name = name;
		this.resource = new SignalChannelResource(name + "_signal");
		this.channel = new SignalChannel(name + "_pathway", signalVectorMaterial, resource, this);
		this.controlCenterPart = controlCenterPart;
		this.brainType = new SignalControlCenter(name + "_controller", this);
		this.spiritGen = spiritGen;
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

	public SignalChannelResource getSignalResource() {
		return resource;
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
	public Collection<? extends IComponentPart> populateBody(ISoma body, IKindSettings set, GameMap world) {
		Collection<IComponentPart> brains = body.getPartsByName(controlCenterPart);
		for (IComponentPart brain : brains) {
			brain.addAbility(brainType, true);

			if (spiritGen != null) {
				ISpirit s1 = spiritGen.apply(body, brain);
				brain.attachSpirit(s1, true);
				ICollective collective = world.getUniverse().getKindCollective(body.getKind());
				if (collective != null) {
					s1.addToGroup(collective);
				}
			}
			for (Triplet<IComponentPart, IPartConnection, IComponentPart> edge : (Iterable<Triplet<IComponentPart, IPartConnection, IComponentPart>>) () -> body
					.getPartGraph()
					.edgeTraversalIteratorBFS(brain, Set.copyOf(PartConnection.attachments()), (a, b) -> true)) {
				body.addChannel(edge.getFirst(), channel, edge.getThird(), true);
				edge.getThird().addChannelMaterial(channel, channel.getVectorMaterials().stream()
						.map((mg) -> mg.generateMaterialFromSettings(set)).collect(Collectors.toSet()));
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
		body.addChannel(to, channel, gained, false);
	}

}
