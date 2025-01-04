package things.physical_form.kinds.single;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import com.google.common.collect.Sets;

import _main.WorldGraphics;
import processing.core.PConstants;
import sim.RelativeSide;
import things.interfaces.IActor;
import things.physical_form.IPart;
import things.physical_form.IPart.IComponentVisagePart;
import things.physical_form.ISoma;
import things.physical_form.IVisage;
import things.physical_form.channelsystems.IChannel;
import things.physical_form.channelsystems.IChannelCenter;
import things.physical_form.channelsystems.IChannelCenter.ChannelRole;
import things.physical_form.channelsystems.IChannelSystem;
import things.physical_form.components.IComponentPart;
import things.physical_form.components.IPartAbility;
import things.physical_form.components.IPartDestructionCondition;
import things.physical_form.components.IPartStat;
import things.physical_form.graph.CoverageType;
import things.physical_form.graph.IPartConnection;
import things.physical_form.kinds.StandardComponentPart;
import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;
import things.physical_form.material.ShapeProperty;
import things.spirit.ISpirit;
import utilities.graph.EmptyGraph;
import utilities.graph.IRelationGraph;
import utilities.graph.SingletonGraph;

public class SingleComponentSoma<P extends IComponentVisagePart> implements ISoma<P>, IVisage<P> {

	private SingletonGraph<P, IPartConnection> graph;
	private SingletonGraph<P, CoverageType> coverage;
	private Collection<IChannelSystem> systems;
	private P part;
	private IActor owner;
	private float size;
	private float mass;
	private Color color;
	private IPartDestructionCondition descondition;
	private boolean isDestroyed;

	public SingleComponentSoma(P part, float size, float mass, Collection<? extends IChannelSystem> systems,
			Color color, IPartDestructionCondition descondition) {
		if (part.isHole()) {
			throw new IllegalArgumentException("cannot have a single hole component");
		}
		this.part = part;
		part.setOwner((ISoma<?>) this);
		this.graph = new SingletonGraph<>(part);
		this.coverage = new SingletonGraph<>(part);
		this.size = size;
		this.mass = mass;
		this.color = color;
		this.systems = Sets.newHashSet(systems);
		this.descondition = descondition;
		for (IChannelSystem sys : this.systems) {
			sys.populateBody(this);
		}
	}

	@Override
	public void addChannel(IComponentPart one, IChannel channel, IComponentPart two, boolean callSystemUpdate) {

	}

	@Override
	public SingletonGraph<P, IPartConnection> getRepresentationGraph() {
		return graph;
	}

	@Override
	public Collection<P> getPartsByName(String name) {
		return this.part.getName().equals(name) ? graph : Collections.emptySet();
	}

	@Override
	public P getPartById(UUID id) {
		return this.part.getID().equals(id) ? part : null;
	}

	@Override
	public IRelationGraph<P, CoverageType> getCoverageGraph() {
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
		// TODO more fun drawing
		g.stroke(color.darker().darker().getRGB());
		/*
		 * if (g.random(0, 1) < 0.1) g.fill(color.darker().getRGB()); else if
		 * (g.random(0, 1) > 0.9) g.fill(color.brighter().getRGB()); else
		 */
		g.fill(color.getRGB());
		if (this.part.getShape().getProperty(ShapeProperty.ROLL_SHAPE) != ShapeProperty.RollableShape.ROLLABLE_OVOID) {
			float w = this.owner.getMap().getBlockRenderSize() * this.size;
			g.rectMode(PConstants.CENTER);
			g.rect(0, 0, w, w);
		} else {
			float radius = this.owner.getMap().getBlockRenderSize() * this.size;
			g.ellipseMode(PConstants.CENTER);
			g.ellipse(0, 0, radius, radius);
		}
	}

	@Override
	public P getCenterPart() {
		return part;
	}

	@Override
	public void severConnection(IComponentPart partOne, IComponentPart partTwo) {
	}

	@Override
	public boolean isAllHoles() {
		return false;
	}

	@Override
	public boolean isDestroyed() {
		return this.isDestroyed;
	}

	@Override
	public Iterable<ISoma<?>> peekBrokenOffParts() {
		return Collections.emptySet();
	}

	@Override
	public Iterable<ISoma<?>> popBrokenOffParts() {
		return Collections.emptySet();
	}

	@Override
	public boolean hasBrokenOffParts() {
		return false;
	}

	@Override
	public Collection<P> getConnectedParts(IComponentPart fromPart) {
		return Collections.emptySet();
	}

	@Override
	public Collection<P> getContiguousParts() {
		return this.graph;
	}

	@Override
	public Collection<P> getNonConnectedParts() {

		return Collections.emptySet();
	}

	@Override
	public boolean growPart(IComponentPart newPart, IComponentPart fromPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {

		return false;
	}

	@Override
	public void onPartAbilitiesChange(IComponentPart part, Collection<IPartAbility> changedEmbeddeds) {

	}

	@Override
	public void onPartEmbeddedMaterialsChanged(IComponentPart part, Collection<IMaterial> changedEmbeddeds) {

	}

	@Override
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial) {
		if (this.descondition.isDestroyed(this, (StandardComponentPart) part, part.getMaterial())) {
			this.isDestroyed = true;
		}
	}

	@Override
	public void onPartShapeChange(IPart part, IShape formerShape) {

	}

	@Override
	public void onPartSizeChange(IPart part, float formerMaterial) {

	}

	@Override
	public Collection<IChannelSystem> getChannelSystems() {
		return this.getChannelSystems();
	}

	@Override
	public Collection<P> getChannelCenters(IChannelCenter type) {
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
	public IRelationGraph<P, IPartConnection> getChanneledParts(IComponentPart toPart, IChannelSystem bySystem) {
		return EmptyGraph.instance();
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
	public void onPartStatChange(IComponentPart part, IPartStat<?> forStat, Object formerValue) {

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ISoma som) {
			return this.graph.equals(som.getRepresentationGraph()) && this.coverage.equals(som.getCoverageGraph())
					&& this.systems.equals(som.getChannelSystems()) && this.size == som.size()
					&& this.mass == som.mass();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.part.hashCode() + Float.hashCode(this.size + this.mass) + this.systems.hashCode();
	}

	@Override
	public String toString() {
		return "SingleComponentSoma{" + this.part + "}";
	}

	@Override
	public String somaReport() {
		return "SingleComponent(" + this.part.componentReport() + "){\n\tmass=" + mass + ",\n\tsize=" + size
				+ ",\n\tsystems=" + this.systems + "}";
	}

	@Override
	public String visageReport() {
		return somaReport();
	}

}
