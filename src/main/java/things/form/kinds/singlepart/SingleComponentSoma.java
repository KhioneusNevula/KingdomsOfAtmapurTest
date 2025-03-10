package things.form.kinds.singlepart;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import _main.WorldGraphics;
import _sim.RelativeSide;
import _utilities.graph.EmptyGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.NodeNotFoundException;
import _utilities.graph.SingletonGraph;
import processing.core.PConstants;
import things.actor.IActor;
import things.form.IPart;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.IKind;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.soma.IPartDestructionCondition;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.form.visage.IVisage;
import things.stains.IStain;
import things.status_effect.IPartStatusEffectInstance;
import thinker.individual.IMindSpirit;

public class SingleComponentSoma implements ISoma, IVisage<IComponentPart> {

	private SingletonGraph<IComponentPart, IPartConnection> graph;
	private SingletonGraph<IComponentPart, CoverageType> coverage;
	private Map<String, IChannelSystem> systems;
	private IComponentPart part;
	private IActor owner;
	private float size;
	private float mass;
	private Color color;
	private IPartDestructionCondition descondition;
	private boolean isDestroyed;
	private IKind kind = IKind.MISCELLANEOUS;
	private UUID id = new UUID(0, 0);

	public SingleComponentSoma(IComponentPart part, float size, float mass,
			Collection<? extends IChannelSystem> systems, Color color, IPartDestructionCondition descondition) {
		if (part.isHole()) {
			throw new IllegalArgumentException("cannot have a single hole component");
		}
		this.part = part;
		part.setOwner((ISoma) this);
		this.graph = new SingletonGraph<>(part);
		this.coverage = new SingletonGraph<>(part);
		this.size = size;
		this.mass = mass;
		this.color = color;
		this.systems = new HashMap<>(systems.stream().map((sys) -> Map.entry(sys.name(), sys))
				.collect(Collectors.toMap((k) -> k.getKey(), (k) -> k.getValue())));
		this.descondition = descondition;
		for (IChannelSystem sys : this.systems.values()) {
			sys.populateBody(this);
		}
	}

	@Override
	public UUID getUUID() {
		return id;
	}

	public void setUUID(UUID id) {
		this.id = id;
	}

	@Override
	public void addChannel(IComponentPart one, IChannel channel, IComponentPart two, boolean callSystemUpdate) {

	}

	@Override
	public SingletonGraph<IComponentPart, IPartConnection> getRepresentationGraph() {
		return graph;
	}

	@Override
	public IChannelSystem getSystemByName(String name) {
		return this.systems.get(name);
	}

	@Override
	public Collection<IComponentPart> getPartsByName(String name) {
		return this.part.getName().equals(name) ? graph : Collections.emptySet();
	}

	@Override
	public IComponentPart getPartById(UUID id) {
		return this.part.getUUID().equals(id) ? part : null;
	}

	@Override
	public IRelationGraph<IComponentPart, CoverageType> getCoverageGraph() {
		return coverage;
	}

	@Override
	public float getConnectionIntegrity(IComponentPart one, IComponentPart two) {
		throw new IllegalArgumentException("No connections present between " + one + " and " + two);
	}

	@Override
	public void setConnectionIntegrity(IComponentPart one, IComponentPart two, float integrity) {
		throw new IllegalArgumentException("No connections present between " + one + " and " + two);
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

	public void setKind(IKind kind) {
		this.kind = kind;
	}

	@Override
	public IKind getKind() {
		return kind;
	}

	@Override
	public void runTick(long ticks) {
		if (this.part.hasControlCenter()) {
			this.part.getChannelCenters(ChannelRole.CONTROL).forEach((a) -> {
				if (a.canTick(this, part, ticks))
					a.controlTick(this, part, ticks);
			});
		}
		this.part.getStains().forEach((stain) -> stain.getSubstance().stainTick(part, stain, this, ticks));
		Iterator<IPartStatusEffectInstance> effs = this.part.getEffectInstances().iterator();
		if (effs.hasNext()) {
			for (IPartStatusEffectInstance effect = effs.next(); effs.hasNext(); effect = effs.next()) {
				if (effect.remainingDuration() == 0 || effect.getEffect().shouldRemove(effect, this.part)) {
					effs.remove();
				} else {
					effect.tick();
				}
			}
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
	public IComponentPart getCenterPart() {
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
	public Iterable<ISoma> peekBrokenOffParts() {
		return Collections.emptySet();
	}

	@Override
	public Iterable<ISoma> popBrokenOffParts() {
		return Collections.emptySet();
	}

	@Override
	public boolean hasBrokenOffParts() {
		return false;
	}

	@Override
	public Collection<IComponentPart> getConnectedParts(IComponentPart fromPart) {
		return Collections.emptySet();
	}

	@Override
	public Collection<IComponentPart> getContiguousParts() {
		return this.graph;
	}

	@Override
	public Collection<IComponentPart> getNonConnectedParts() {

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
		if (this.descondition.isDestroyed(this, (IComponentPart) part)) {
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
	public void onPartStainChange(IPart part, Collection<IStain> stains) {

	}

	@Override
	public Collection<IChannelSystem> getChannelSystems() {
		return this.getChannelSystems();
	}

	@Override
	public <E extends Comparable<?>> E getResourceAggregate(IResource<E> resource) {
		return part.getResourceAmount(resource);
	}

	@Override
	public Collection<IComponentPart> getChannelCenters(IChannelCenter type) {
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
	public IRelationGraph<IComponentPart, IPartConnection> getChanneledParts(IComponentPart toPart,
			IChannelSystem bySystem) {
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
	public void setCoveragePercentage(IComponentPart coverer, IComponentPart covered, RelativeSide side, float amount) {
		if (!part.equals(coverer))
			throw new NodeNotFoundException(coverer, 0);
		if (!part.equals(covered))
			throw new NodeNotFoundException(coverer, 1);
		throw new IllegalArgumentException("Argument 0, " + coverer + ", is equivalent to Argument 1");
	}

	@Override
	public void onPartStatChange(IComponentPart part, IPartStat<?> forStat, Object formerValue) {

	}

	@Override
	public void onChannelResourceChanged(IComponentPart part, IResource<?> resource, Comparable<?> formerValue) {

	}

	@Override
	public Collection<IMindSpirit> getAllTetheredSpirits() {
		return part.getTetheredSpirits();
	}

	@Override
	public IComponentPart getPartForSpirit(IMindSpirit spirit) {
		if (part.getTetheredSpirits().contains(spirit)) {
			return part;
		}
		return null;
	}

	@Override
	public void onAttachSpirit(IMindSpirit spirit, IComponentPart part) {
		if (!part.equals(this.part)) {
			throw new IllegalArgumentException(spirit + " " + part + " =/= " + this.part);
		}
	}

	@Override
	public void onRemoveSpirit(IMindSpirit spirit, IComponentPart part) {
		if (!part.equals(this.part))
			throw new IllegalArgumentException(spirit + " " + part + " =/= " + this.part);
	}

	@Override
	public void onApplyEffect(IPartStatusEffectInstance effect, IComponentPart part) {
		if (!part.equals(this.part))
			throw new IllegalArgumentException(effect + " " + part + " =/= " + this.part);
	}

	@Override
	public void onRemoveEffect(IPartStatusEffectInstance ret, IComponentPart part) {
		if (!part.equals(this.part))
			throw new IllegalArgumentException(ret + " " + part + " =/= " + this.part);
	}

	@Override
	public float mass() {
		return mass;
	}

	@Override
	public SingleComponentSoma clone() {
		SingleComponentSoma copy;
		try {
			copy = (SingleComponentSoma) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		copy.part = this.part.clone();
		copy.graph = new SingletonGraph<>(copy.part);
		copy.coverage = new SingletonGraph<>(copy.part);
		copy.systems = new HashMap<>(this.systems);
		return copy;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ISoma som) {
			return this.graph.equals(som.getRepresentationGraph()) && this.coverage.equals(som.getCoverageGraph())
					&& this.systems.values().equals(som.getChannelSystems()) && this.size == som.size()
					&& this.mass == som.mass();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.part.hashCode() + Float.hashCode(this.size + this.mass) + this.systems.values().hashCode();
	}

	@Override
	public String toString() {
		return "SingleComponentSoma{" + this.part + "}";
	}

	@Override
	public String somaReport() {
		return "SingleComponent(" + this.part.componentReport() + "){\n\tmass=" + mass + ",\n\tsize=" + size
				+ ",\n\tsystems=" + this.systems.values() + "}";
	}

	@Override
	public String visageReport() {
		return somaReport();
	}

}
