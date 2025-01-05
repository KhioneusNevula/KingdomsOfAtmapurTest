package things.form.kinds.multipart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import _main.WorldGraphics;
import _sim.RelativeSide;
import things.actor.IActor;
import things.form.IPart;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelResource;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.singlepart.SingleComponentSoma;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.IPartDestructionCondition;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.form.visage.IVisage;
import things.spirit.ISpirit;
import things.stains.IStain;
import utilities.MathUtils;
import utilities.collections.ImmutableCollection;
import utilities.couplets.Pair;
import utilities.graph.IModifiableRelationGraph;
import utilities.graph.IRelationGraph;
import utilities.graph.ImmutableGraphView;

public class MultipartSoma implements ISoma, IVisage<IComponentPart> {

	private IModifiableRelationGraph<IComponentPart, IPartConnection> partGraph;
	private IModifiableRelationGraph<IComponentPart, CoverageType> coverage;
	private Table<String, UUID, IComponentPart> partsByNameAndId;
	private IComponentPart centerPart;
	private Set<IComponentPart> contiguousParts;
	private Set<IComponentPart> allParts;
	private IActor owner;
	private float size;
	private int visplanes;
	private int intplanes;
	private boolean canrender;
	private Set<IChannelSystem> systems = new HashSet<>();
	private Table<IChannelSystem, IChannelCenter, IComponentPart> channeledParts = HashBasedTable.create();
	private Map<IPartStat<?>, Pair<Object, Integer>> aggregateStats;
	private Map<IChannelResource<?>, Object> aggregateResources;
	private float mass;
	private IPartDestructionCondition destructCondition;
	private boolean isAllHoles;
	private boolean isDestroyed;
	private List<ISoma> brokenParts;
	private Color color = Color.white;

	public MultipartSoma(IModifiableRelationGraph<IComponentPart, IPartConnection> parts,
			IModifiableRelationGraph<IComponentPart, CoverageType> coverage, float size, float mass,
			IComponentPart centerPart) {
		this.partsByNameAndId = HashBasedTable.create();
		this.allParts = new HashSet<>();
		this.partGraph = parts;
		this.size = size;
		this.mass = mass;
		this.coverage = coverage;
		this.aggregateStats = new HashMap<>();
		this.aggregateResources = new HashMap<>();
		this.brokenParts = new ArrayList<>();
		this.isAllHoles = true;
		visplanes = 1;
		intplanes = 1;
		this.centerPart = centerPart;
		float checkSize = 0;
		for (IComponentPart part : parts) {
			this.partsByNameAndId.put(part.getName(), part.getID(), part);
			part.setOwner((ISoma) this);
			checkSize += part.getRelativeSize();
			visplanes = MathUtils.primeUnion(visplanes, part.detectionPlanes());
			intplanes = MathUtils.primeUnion(intplanes, part.interactionPlanes());
			this.allParts.add(part);
			for (IPartStat stat : part.getStats()) {
				this.unionStat(stat, part.getStat(stat));
			}
			this.isAllHoles = this.isAllHoles || part.isHole();
		}
		if (checkSize != 1f) {
			for (IComponentPart part : parts) {
				part.changeSize(part.getRelativeSize() / checkSize, false);
			}
		}
		this.contiguousParts = Sets.newHashSet(partGraph.nodeTraversalIteratorBFS(centerPart,
				Set.of(PartConnection.JOINED, PartConnection.MERGED), (a, b) -> true));

		if (partGraph.isEmpty() || isAllHoles)
			canrender = false;
		else
			canrender = true;
	}

	protected Object unionStat(IPartStat<?> stat, Object newVal) {
		Pair<Object, Integer> val = this.aggregateStats.get(stat);
		if (val == null) {
			this.aggregateStats.put(stat, Pair.of(newVal, 1));
			return newVal;
		} else {
			Object newv = stat.aggregate((Set) Set.of(val, newVal));
			this.aggregateStats.put(stat, Pair.of(newv, val.getSecond() + 1));
			return newv;
		}
	}

	protected Object differenceStat(IPartStat<?> stat, Object removeVal) {
		Pair<Object, Integer> val = this.aggregateStats.get(stat);
		if (val == null) {
			throw new IllegalArgumentException("no value for " + stat);
		} else {
			Object newv = ((IPartStat) stat).extract(val.getFirst(), removeVal, val.getSecond());
			this.aggregateStats.put(stat, Pair.of(newv, val.getSecond() - 1));
			return newv;
		}
	}

	/**
	 * Add and apply a channel system
	 * 
	 * @param sys
	 * @param apply whether to apply it to the body
	 * @return
	 */
	public MultipartSoma addChannelSystem(IChannelSystem sys, boolean apply) {
		this.systems.add(sys);
		if (apply)
			sys.populateBody(this);
		for (IChannelResource<?> resource : sys.getChannelResources()) {
			for (IComponentPart part : this.partGraph) {
				this.aggregateResources.put(resource,
						((IChannelResource) resource).add(
								this.aggregateResources.getOrDefault(resource, resource.getEmptyValue()),
								part.getResourceAmount(resource)));
			}
		}
		return this;
	}

	/**
	 * Set condition for destroying the parts of this soma
	 * 
	 * @param condition
	 * @return
	 */
	public MultipartSoma setDestructionCondition(IPartDestructionCondition condition) {
		this.destructCondition = condition;
		return this;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public IRelationGraph<IComponentPart, IPartConnection> getRepresentationGraph() {
		return new ImmutableGraphView<>(partGraph);
	}

	@Override
	public Collection<IComponentPart> getPartsByName(String name) {
		return partsByNameAndId.row(name).values();
	}

	@Override
	public IComponentPart getPartById(UUID id) {
		return partsByNameAndId.column(id).values().stream().findFirst().orElse(null);
	}

	@Override
	public IRelationGraph<IComponentPart, CoverageType> getCoverageGraph() {
		return new ImmutableGraphView<>(coverage);
	}

	@Override
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {
		if (!partGraph.contains(toPart)) {
			throw new IllegalArgumentException(toPart + " is not part of " + this);
		}
		boolean newed = !partGraph.contains(newPart);
		if (newed) {
			newPart.setOwner(this);
			this.partGraph.add(newPart);
			this.coverage.add(newPart);
			this.allParts.add(newPart);

			for (IPartStat<?> stat : newPart.getStats()) {
				this.unionStat(stat, newPart.getStat(stat));
			}
			float totalSize = (float) partGraph.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();

			partGraph.forEach((p) -> p.changeSize(p.getRelativeSize() / totalSize, false));
		}
		boolean one = this.partGraph.addEdge(newPart, connectionType, toPart);
		if (connectionType.isAttachment()) {
			if (this.contiguousParts.contains(newPart)) {
				contiguousParts.add(toPart);
			} else if (this.contiguousParts.contains(toPart)) {
				contiguousParts.add(newPart);
			}
		}
		boolean two = true;
		for (RelativeSide covering : coveringsides) {
			two = two && this.coverage.addEdge(newPart, CoverageType.covers(covering), toPart);
		}
		for (IChannelSystem sys : this.systems) {
			sys.onBodyNew(this, newPart, connectionType, toPart, newed);
		}

		return one && two;
	}

	@Override
	public IActor getOwner() {
		return owner;
	}

	@Override
	public void runTick(long ticks) {
		for (IComponentPart p : this.partGraph) {
			if (p.hasControlCenter()) {
				p.getChannelCenters(ChannelRole.CONTROL).forEach((x) -> x.controlTick(this, p, ticks));
			}
			if (p.hasAutomaticChannelCenter()) {
				p.getAutomaticChannelCenters().forEach((x) -> x.automaticTick(this, p, ticks));
			}

			p.getStains().forEach((stain) -> stain.getSubstance().stainTick(p, stain, this, ticks));
		}

	}

	@Override
	public float size() {
		return size;
	}

	@Override
	public void setOwner(IActor owner) {
		this.owner = owner;
	}

	@Override
	public boolean canRender() {
		return canrender;
	}

	@Override
	public int visibilityPlanes() {
		return visplanes;
	}

	@Override
	public void draw(WorldGraphics g) {
		// TODO render multipart visage?
		g.stroke(color.darker().darker().getRGB());
		g.fill(color.getRGB());
		g.circle(0, 0, size * this.getOwner().getMap().getBlockRenderSize());
	}

	@Override
	public String visageReport() {
		return "{parts=" + this.partGraph.representation() + ",coverage=" + coverage.representation() + "}";
	}

	@Override
	public IComponentPart getCenterPart() {
		return centerPart;
	}

	@Override
	public void addChannel(IComponentPart one, IChannel channel, IComponentPart two, boolean callSys) {
		IComponentPart p1 = one;
		IComponentPart p2 = two;
		partGraph.addEdge(p1, channel, p2);
		if (callSys) {
			channel.getSystem().onBodyNew(this, p2, channel, p1, false);
		}
	}

	@Override
	public void severConnection(IComponentPart partOne, IComponentPart partTwo) {
		if (partGraph.removeAllConnections(partOne, partTwo)) {
			coverage.removeAllConnections(partOne, partTwo);
			IComponentPart one = partOne;
			IComponentPart two = partTwo;
			IModifiableRelationGraph<IComponentPart, IPartConnection> severedPart = partGraph.traverseBFS(one,
					PartConnection.valuesCollection(), (a) -> {
					}, (a, b) -> true);
			if (severedPart.size() == this.size) {
				return;
			}
			if (severedPart.contains(this.centerPart)) {
				severedPart = partGraph.traverseBFS(two, PartConnection.valuesCollection(), (a) -> {
				}, (a, b) -> true);
			}

			this.partGraph.removeAll(severedPart);
			this.coverage.removeAll(severedPart);
			float fraction = (float) severedPart.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();
			float sizePortion = fraction * size;
			float massPortion = fraction * mass;
			mass -= massPortion;
			size -= sizePortion;
			for (IComponentPart part : partGraph) {
				part.changeSize(part.getRelativeSize() / (1 - fraction), false);
			}
			this.contiguousParts.removeAll(severedPart);
			this.allParts.removeAll(severedPart);
			MultipartSoma soma2 = new MultipartSoma(severedPart, coverage.subgraph(severedPart).copy(), sizePortion,
					massPortion, two);
			severedPart.forEach((p) -> {
				this.systems.forEach((sys) -> sys.onBodyLoss(this, p));
				this.channeledParts.values().remove(p);
				p.getStats().forEach((stat) -> this.differenceStat(stat, p.getStat(stat)));
			});
			this.brokenParts.add(soma2);
			if (this.partGraph.stream().allMatch(IPart::isHole)) {
				this.isAllHoles = true;
				this.isDestroyed = true;
			}
		} else {
			throw new IllegalArgumentException("Nothing to sever between " + partOne + ", " + partTwo);
		}
	}

	@Override
	public Collection<IComponentPart> getConnectedParts(IComponentPart fromPart) {
		if (this.contiguousParts.contains(fromPart)) {
			return ImmutableCollection.from(this.contiguousParts);
		}
		if (this.partGraph.contains(fromPart)) {
			return partGraph.traverseBFS(fromPart, PartConnection.attachments(), (a) -> {
			}, (a, b) -> true);
		}
		throw new IllegalArgumentException("Uncontained part " + fromPart);
	}

	@Override
	public Collection<IComponentPart> getContiguousParts() {
		return ImmutableCollection.from(this.contiguousParts);
	}

	@Override
	public Collection<IComponentPart> getNonConnectedParts() {
		return Sets.difference(this.allParts, this.contiguousParts);
	}

	@Override
	public boolean growPart(IComponentPart newPart, IComponentPart fromPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {
		return this.attach(newPart, fromPart, connectionType, coveringsides);
	}

	@Override
	public Collection<IChannelSystem> getChannelSystems() {
		return ImmutableCollection.from(this.systems);
	}

	@Override
	public Collection<IComponentPart> getChannelCenters(IChannelCenter type) {
		return channeledParts.column(type).values();
	}

	@Override
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart) {

		return partGraph.getConnectingEdgeTypes(toPart).stream().filter((a) -> a instanceof IChannel)
				.map((a) -> (IChannel) a).collect(Collectors.toSet());
	}

	@Override
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart, IChannelSystem system) {

		return partGraph.getConnectingEdgeTypes(toPart).stream().filter((a) -> a instanceof IChannel)
				.map((a) -> (IChannel) a).filter((a) -> system.getChannelConnectionTypes().contains(a))
				.collect(Collectors.toSet());
	}

	@Override
	public IRelationGraph<IComponentPart, IPartConnection> getChanneledParts(IComponentPart toPart,
			IChannelSystem bySystem) {

		return partGraph.traverseBFS(toPart, bySystem.getChannelConnectionTypes(), (a) -> {
		}, (a, b) -> true);
	}

	@Override
	public IMaterial getMainMaterial() {
		return this.centerPart.getMaterial();
	}

	@Override
	public <E> E getAggregateStat(IPartStat<E> forStat) {
		return (E) this.aggregateStats.getOrDefault(forStat, Pair.of(forStat.getDefaultValue(this.centerPart), 1))
				.getFirst();
	}

	@Override
	public void onAttachSpirit(ISpirit spirit, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
	}

	@Override
	public void onRemoveSpirit(ISpirit spirit, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
	}

	@Override
	public float mass() {
		return this.mass;
	}

	@Override
	public String somaReport() {
		return "Soma{parts=" + this.partGraph + ",\ncoverage=" + this.coverage + "}";
	}

	@Override
	public void onPartEmbeddedMaterialsChanged(IComponentPart part, Collection<IMaterial> changedEmbeddeds) {

	}

	@Override
	public void onPartAbilitiesChange(IComponentPart part, Collection<IPartAbility> changedAbs) {
		for (IPartAbility aba : changedAbs) {
			if (aba instanceof IChannelCenter cc) {
				this.channeledParts.put(cc.getSystem(), cc, part);
			}
		}
	}

	@Override
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial) {
		if (!(part instanceof IComponentPart)) {
			return;
		}
		IComponentPart p = (IComponentPart) part;
		if (this.destructCondition.isDestroyed(this, p)) {
			if (this.centerPart.equals(part)) {
				this.isDestroyed = true;
			}
			this.systems.forEach((sys) -> sys.onBodyLoss(this, p));
			this.allParts.remove(p);
			this.partGraph.remove(p);
			this.coverage.remove(p);
			this.channeledParts.values().remove(p);
			p.getStats().forEach((stat) -> this.differenceStat(stat, p.getStat(stat)));
			SingleComponentSoma singlesoma = new SingleComponentSoma(p, size * p.getRelativeSize(),
					mass * p.getRelativeSize(), systems, Color.gray, destructCondition);
			brokenParts.add(singlesoma);
			IModifiableRelationGraph<IComponentPart, IPartConnection> centerGraph = this.partGraph; // nodes which are
																									// meant to
			// remain in this soma
			Iterator<IComponentPart> partIter = partGraph.iterator();
			while (partIter.hasNext()) {
				IComponentPart next = partIter.next();
				IModifiableRelationGraph<IComponentPart, IPartConnection> cetera = this.partGraph.traverseBFS(next,
						PartConnection.valuesCollection(), (a) -> {
						}, (a, b) -> true);
				if (cetera.contains(centerPart)) {
					centerGraph = cetera;
				}
				this.partGraph.removeAll(cetera);

				partIter = partGraph.iterator();
				float fraction = (float) cetera.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();
				float sizePortion = fraction * size;
				float massPortion = fraction * mass;
				mass -= massPortion;
				size -= sizePortion;
				for (IComponentPart parti : partGraph) {
					parti.changeSize(parti.getRelativeSize() / (1 - fraction), false);
				}
				MultipartSoma multisoma = new MultipartSoma(cetera, coverage.subgraph(cetera).copy(), sizePortion,
						massPortion, next);
				for (IComponentPart parti : cetera) {
					parti.changeSize(parti.getRelativeSize() / fraction, false);
				}

				this.brokenParts.add(multisoma);
			}
			this.partGraph = centerGraph;
			this.coverage.retainAll(partGraph);
			this.isAllHoles = (this.partGraph.stream().allMatch(IPart::isHole));
			if (this.isAllHoles)
				this.isDestroyed = true;
		}
	}

	@Override
	public void onPartStainChange(IPart part, Collection<IStain> stains) {

	}

	@Override
	public boolean hasBrokenOffParts() {
		return !this.brokenParts.isEmpty();
	}

	@Override
	public boolean isAllHoles() {
		return this.isAllHoles;
	}

	@Override
	public boolean isDestroyed() {
		return this.isDestroyed;
	}

	@Override
	public Iterable<ISoma> peekBrokenOffParts() {
		return (Iterable<ISoma>) (() -> this.brokenParts.iterator());
	}

	@Override
	public Iterable<ISoma> popBrokenOffParts() {
		List<ISoma> list = this.brokenParts;
		this.brokenParts = new ArrayList<>();
		return list;
	}

	@Override
	public void onPartShapeChange(IPart part, IShape formerShape) {

	}

	@Override
	public void onPartSizeChange(IPart part, float formerMaterial) {

	}

	@Override
	public void onPartStatChange(IComponentPart part, IPartStat<?> forStat, Object formerValue) {
		this.differenceStat(forStat, formerValue);
		this.unionStat(forStat, part.getStat(forStat));
	}

	@Override
	public void onChannelResourceChanged(IComponentPart part, IChannelResource<?> resource, Object formerValue) {
		Object val = this.aggregateResources.getOrDefault(resource, resource.getEmptyValue());
		val = ((IChannelResource) resource).subtract(val, formerValue);
		val = ((IChannelResource) resource).add(val, part.getResourceAmount(resource));
		this.aggregateResources.put(resource, val);
	}

	@Override
	public <E> E getResourceAggregate(IChannelResource<E> resource) {
		return (E) this.aggregateResources.getOrDefault(resource, resource.getEmptyValue());
	}

	@Override
	public MultipartSoma clone() {
		MultipartSoma copy;
		try {
			copy = (MultipartSoma) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		copy.aggregateStats = new HashMap<>(this.aggregateStats);
		copy.aggregateResources = new HashMap<>(this.aggregateResources);
		copy.partsByNameAndId = HashBasedTable.create();
		copy.allParts = new HashSet<>();
		for (Cell<String, UUID, IComponentPart> cell : this.partsByNameAndId.cellSet()) {
			IComponentPart newPart = cell.getValue().clone();
			copy.partsByNameAndId.put(cell.getRowKey(), cell.getColumnKey(), newPart);
			copy.allParts.add(newPart);
		}
		copy.partGraph = partGraph.deepCopy((part) -> copy.partsByNameAndId.get(part.getName(), part.getID()));
		copy.coverage = coverage.deepCopy((part) -> copy.partsByNameAndId.get(part.getName(), part.getID()));
		copy.brokenParts = new ArrayList<>();
		copy.centerPart = copy.partsByNameAndId.get(centerPart.getName(), centerPart.getID());
		copy.channeledParts = HashBasedTable.create();
		for (Cell<IChannelSystem, IChannelCenter, IComponentPart> cell : this.channeledParts.cellSet()) {
			copy.channeledParts.put(cell.getRowKey(), cell.getColumnKey(),
					copy.partsByNameAndId.get(cell.getValue().getName(), cell.getValue().getID()));
		}
		copy.systems = new HashSet<>(systems);
		copy.contiguousParts = new HashSet<>();
		for (IComponentPart part : this.contiguousParts) {
			copy.contiguousParts.add(copy.partsByNameAndId.get(part.getName(), part.getID()));
		}
		return copy;
	}

}
