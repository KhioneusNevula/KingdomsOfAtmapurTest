package things.physical_form.kinds;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import _main.WorldGraphics;
import sim.RelativeSide;
import things.interfaces.IActor;
import things.physical_form.ISoma;
import things.physical_form.IVisage;
import things.physical_form.channelsystems.IChannel;
import things.physical_form.channelsystems.IChannelCenter;
import things.physical_form.channelsystems.IChannelCenter.ChannelRole;
import things.physical_form.channelsystems.IChannelSystem;
import things.physical_form.components.IComponentPart;
import things.physical_form.components.IPartStat;
import things.physical_form.graph.CoverageType;
import things.physical_form.graph.IPartConnection;
import things.physical_form.material.IMaterial;
import things.spirit.ISpirit;
import utilities.graph.EmptyGraph;
import utilities.graph.IRelationGraph;
import utilities.graph.SingletonGraph;

public class SingleComponentSoma implements ISoma<SingleComponent>, IVisage<SingleComponent> {

	private SingletonGraph<SingleComponent, IPartConnection> graph;
	private SingletonGraph<SingleComponent, CoverageType> coverage;
	private Collection<IChannelSystem> systems;
	private SingleComponent part;
	private IActor owner;
	private float size;
	private float mass;

	public SingleComponentSoma(SingleComponent part, float size, float mass, IChannelSystem... systems) {
		this.part = part;
		this.graph = new SingletonGraph<>(part);
		this.coverage = new SingletonGraph<>(part);
		this.size = size;
		this.mass = mass;
		this.systems = Sets.newHashSet(systems);
		for (IChannelSystem sys : this.systems) {
			sys.populateBody(this);
		}
	}

	@Override
	public void addChannel(IComponentPart one, IChannel channel, IComponentPart two) {

	}

	@Override
	public SingletonGraph<SingleComponent, IPartConnection> getRepresentationGraph() {
		return graph;
	}

	@Override
	public Collection<SingleComponent> getPartsByName(String name) {
		return this.part.getName().equals(name) ? graph : Collections.emptySet();
	}

	@Override
	public SingleComponent getPartById(UUID id) {
		return this.part.getID().equals(id) ? part : null;
	}

	@Override
	public IRelationGraph<SingleComponent, CoverageType> getCoverageGraph() {
		return coverage;
	}

	@Override
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {
		return false;
	}

	@Override
	public void setOwner(IActor owner) {
		this.owner = owner;
	}

	@Override
	public IActor getOwner() {
		return owner;
	}

	@Override
	public void runTick(long ticks) {
		if (this.part.hasControlCenter()) {
			this.part.getChannelCenters(ChannelRole.CONTROL).forEach((a) -> {
				if (a.canTick(this, part, ticks))
					a.controlTick(this, part, ticks);
			});
		}
	}

	@Override
	public float size() {
		return size;
	}

	@Override
	public boolean canRender() {
		return true;
	}

	@Override
	public int visibilityPlanes() {
		return part.detectionPlanes();
	}

	@Override
	public void draw(WorldGraphics g) {
		// TODO draw this?

	}

	@Override
	public SingleComponent getCenterPart() {
		return part;
	}

	@Override
	public ISoma<SingleComponent> severConnection(IComponentPart partOne, IComponentPart partTwo) {
		return null;
	}

	@Override
	public Collection<SingleComponent> getConnectedParts(IComponentPart fromPart) {
		return Collections.emptySet();
	}

	@Override
	public Collection<SingleComponent> getContiguousParts() {
		return this.graph;
	}

	@Override
	public Collection<SingleComponent> getNonConnectedParts() {

		return Collections.emptySet();
	}

	@Override
	public boolean growPart(IComponentPart newPart, IComponentPart fromPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {

		return false;
	}

	@Override
	public Collection<IChannelSystem> getChannelSystems() {
		return this.getChannelSystems();
	}

	@Override
	public Collection<SingleComponent> getChannelCenters(IChannelCenter type) {
		return part.getChannelCenters(type.getRole()).contains(type) ? graph : Collections.emptySet();
	}

	@Override
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart) {
		return Collections.emptySet();
	}

	@Override
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart, IChannelSystem system) {
		return Collections.emptySet();
	}

	@Override
	public IRelationGraph<SingleComponent, IPartConnection> getChanneledParts(IComponentPart toPart,
			IChannelSystem bySystem) {
		return EmptyGraph.instance();
	}

	@Override
	public Collection<IMaterial> getAllBodilyMaterials() {
		return Sets.union(ImmutableSet.copyOf(part.embeddedMaterials()), Set.of(part.getMaterial()));
	}

	@Override
	public IMaterial getMainMaterial() {
		return part.getMaterial();
	}

	@Override
	public <E> E getAggregateStat(IPartStat<E> forStat) {
		return part.getStat(forStat);
	}

	@Override
	public void attachSpirit(ISpirit spirit, IComponentPart part) {
		part.attachSpirit(spirit);
		spirit.onAttachHost(part, this);
	}

	@Override
	public void removeSpirit(ISpirit spirit, IComponentPart part) {
		part.removeSpirit(spirit);
		spirit.onRemove(part, this);
	}

	@Override
	public float mass() {
		return mass;
	}

}
