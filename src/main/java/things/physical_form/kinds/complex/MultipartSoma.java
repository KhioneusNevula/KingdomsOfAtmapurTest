package things.physical_form.kinds.complex;

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

import _main.WorldGraphics;
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
import things.physical_form.graph.PartConnection;
import things.physical_form.kinds.single.SingleComponentSoma;
import things.physical_form.material.IMaterial;
import things.physical_form.material.IShape;
import things.spirit.ISpirit;
import utilities.ImmutableCollection;
import utilities.MathUtils;
import utilities.Pair;
import utilities.graph.IModifiableRelationGraph;
import utilities.graph.IRelationGraph;

public class MultipartSoma<P extends IComponentVisagePart> implements ISoma<P>, IVisage<P> {

	private IModifiableRelationGraph<P, IPartConnection> partGraph;
	private IModifiableRelationGraph<P, CoverageType> coverage;
	private Table<String, UUID, P> partsByNameAndId;
	private P centerPart;
	private Set<P> contiguousParts;
	private Set<P> allParts;
	private IActor owner;
	private float size;
	private int visplanes;
	private int intplanes;
	private boolean canrender;
	private Set<IChannelSystem> systems = new HashSet<>();
	private Table<IChannelSystem, IChannelCenter, P> channeledParts = HashBasedTable.create();
	private Map<IPartStat<?>, Pair<Object, Integer>> aggregateStats;
	private float mass;
	private IPartDestructionCondition destructCondition;
	private boolean isAllHoles;
	private boolean isDestroyed;
	private List<ISoma<P>> brokenParts;

	public MultipartSoma(IModifiableRelationGraph<P, IPartConnection> parts,
			IModifiableRelationGraph<P, CoverageType> coverage, float size, float mass, P centerPart) {
		this.partsByNameAndId = HashBasedTable.create();
		this.allParts = new HashSet<>();
		this.partGraph = parts;
		this.size = size;
		this.mass = mass;
		this.coverage = coverage;
		this.aggregateStats = new HashMap<>();
		this.brokenParts = new ArrayList<>();
		this.isAllHoles = true;
		visplanes = 1;
		intplanes = 1;
		this.centerPart = centerPart;
		float checkSize = 0;
		for (P part : parts) {
			this.partsByNameAndId.put(part.getName(), part.getID(), part);
			part.setOwner((ISoma<?>) this);
			part.setOwner((IVisage<?>) this);
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
			for (P part : parts) {
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

	public MultipartSoma<P> addChannelSystem(IChannelSystem sys) {
		this.systems.add(sys);
		sys.populateBody(this);
		return this;
	}

	/**
	 * Set condition for destroying the parts of this soma
	 * 
	 * @param condition
	 * @return
	 */
	public MultipartSoma<P> setDestructionCondition(IPartDestructionCondition condition) {
		this.destructCondition = condition;
		return this;
	}

	@Override
	public IRelationGraph<P, IPartConnection> getRepresentationGraph() {
		return partGraph;
	}

	@Override
	public Collection<P> getPartsByName(String name) {
		return partsByNameAndId.row(name).values();
	}

	@Override
	public P getPartById(UUID id) {
		return partsByNameAndId.column(id).values().stream().findFirst().orElse(null);
	}

	@Override
	public IRelationGraph<P, CoverageType> getCoverageGraph() {
		return coverage;
	}

	@Override
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {
		if (!partGraph.contains(toPart)) {
			throw new IllegalArgumentException(toPart + " is not part of " + this);
		}
		boolean newed = !partGraph.contains(newPart);
		if (newed) {
			this.partGraph.add((P) newPart);
			this.coverage.add((P) newPart);
			this.allParts.add((P) newPart);
			for (IPartStat<?> stat : newPart.getStats()) {
				this.unionStat(stat, newPart.getStat(stat));
			}
			float totalSize = (float) partGraph.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();

			partGraph.forEach((p) -> p.changeSize(p.getRelativeSize() / totalSize, false));
		}
		boolean one = this.partGraph.addEdge((P) newPart, connectionType, (P) toPart);
		if (connectionType.isAttachment()) {
			if (this.contiguousParts.contains(newPart)) {
				contiguousParts.add((P) toPart);
			} else if (this.contiguousParts.contains(toPart)) {
				contiguousParts.add((P) newPart);
			}
		}
		boolean two = true;
		for (RelativeSide covering : coveringsides) {
			two = two && this.coverage.addEdge((P) newPart, CoverageType.covers(covering), (P) toPart);
		}
		for (IChannelSystem sys : this.systems) {
			sys.onBodyNew(this, (P) newPart, connectionType, (P) toPart, newed);
		}

		return one && two;
	}

	@Override
	public IActor getOwner() {
		return owner;
	}

	@Override
	public void runTick(long ticks) {
		for (P p : this.partGraph) {
			if (p.hasControlCenter()) {
				p.getChannelCenters(ChannelRole.CONTROL).forEach((x) -> x.controlTick(this, p, ticks));
			}
			if (p.hasAutomaticChannelCenter()) {
				p.getAutomaticChannelCenters().forEach((x) -> x.automaticTick(this, p, ticks));
			}
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
	}

	@Override
	public String visageReport() {
		return "{parts=" + this.partGraph.representation() + ",coverage=" + coverage.representation() + "}";
	}

	@Override
	public P getCenterPart() {
		return centerPart;
	}

	@Override
	public void addChannel(IComponentPart one, IChannel channel, IComponentPart two, boolean callSys) {
		P p1 = (P) one;
		P p2 = (P) two;
		partGraph.addEdge(p1, channel, p2);
		if (callSys) {
			channel.getSystem().onBodyNew(this, p2, channel, p1, false);
		}
	}

	@Override
	public void severConnection(IComponentPart partOne, IComponentPart partTwo) {
		if (partGraph.removeAllConnections(partOne, partTwo)) {
			coverage.removeAllConnections(partOne, partTwo);
			P one = (P) partOne;
			P two = (P) partTwo;
			IModifiableRelationGraph<P, IPartConnection> severedPart = partGraph.traverseBFS(one,
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
			for (P part : partGraph) {
				part.changeSize(part.getRelativeSize() / (1 - fraction), false);
			}
			this.contiguousParts.removeAll(severedPart);
			this.allParts.removeAll(severedPart);
			severedPart.forEach((p) -> {
				this.systems.forEach((sys) -> sys.onBodyLoss(this, p));
				this.channeledParts.values().remove(p);
				p.getStats().forEach((stat) -> this.differenceStat(stat, p.getStat(stat)));
			});
			this.brokenParts.add(new MultipartSoma<P>(severedPart, coverage.subgraph(severedPart).copy(), sizePortion,
					massPortion, two));
			if (this.partGraph.stream().allMatch(IPart::isHole)) {
				this.isAllHoles = true;
				this.isDestroyed = true;
			}
		} else {
			throw new IllegalArgumentException("Nothing to sever between " + partOne + ", " + partTwo);
		}
	}

	@Override
	public Collection<P> getConnectedParts(IComponentPart fromPart) {
		if (this.contiguousParts.contains(fromPart)) {
			return ImmutableCollection.from(this.contiguousParts);
		}
		if (this.partGraph.contains(fromPart)) {
			return partGraph.traverseBFS((P) fromPart, PartConnection.attachments(), (a) -> {
			}, (a, b) -> true);
		}
		throw new IllegalArgumentException("Uncontained part " + fromPart);
	}

	@Override
	public Collection<P> getContiguousParts() {
		return ImmutableCollection.from(this.contiguousParts);
	}

	@Override
	public Collection<P> getNonConnectedParts() {
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
	public Collection<P> getChannelCenters(IChannelCenter type) {
		return channeledParts.column(type).values();
	}

	@Override
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart) {

		return partGraph.getConnectingEdgeTypes((P) toPart).stream().filter((a) -> a instanceof IChannel)
				.map((a) -> (IChannel) a).collect(Collectors.toSet());
	}

	@Override
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart, IChannelSystem system) {

		return partGraph.getConnectingEdgeTypes((P) toPart).stream().filter((a) -> a instanceof IChannel)
				.map((a) -> (IChannel) a).filter((a) -> system.getChannelConnectionTypes().contains(a))
				.collect(Collectors.toSet());
	}

	@Override
	public IRelationGraph<P, IPartConnection> getChanneledParts(IComponentPart toPart, IChannelSystem bySystem) {

		return partGraph.traverseBFS((P) toPart, bySystem.getChannelConnectionTypes(), (a) -> {
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
	public void attachSpirit(ISpirit spirit, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		part.attachSpirit(spirit);
	}

	@Override
	public void removeSpirit(ISpirit spirit, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		part.removeSpirit(spirit);
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
				this.channeledParts.put(cc.getSystem(), cc, (P) part);
			}
		}
	}

	@Override
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial) {
		P p = (P) part;
		if (this.destructCondition.isDestroyed(this, p, part.getMaterial())) {
			if (this.centerPart.equals(part)) {
				this.isDestroyed = true;
			}
			this.systems.forEach((sys) -> sys.onBodyLoss(this, p));
			this.allParts.remove(p);
			this.partGraph.remove(p);
			this.coverage.remove(p);
			this.channeledParts.values().remove(p);
			p.getStats().forEach((stat) -> this.differenceStat(stat, p.getStat(stat)));
			brokenParts.add(new SingleComponentSoma<>(p, size * p.getRelativeSize(), mass * p.getRelativeSize(),
					systems, Color.gray, destructCondition));
			IModifiableRelationGraph<P, IPartConnection> centerGraph = this.partGraph; // nodes which are meant to
																						// remain in this soma
			Iterator<P> partIter = partGraph.iterator();
			while (partIter.hasNext()) {
				P next = partIter.next();
				IModifiableRelationGraph<P, IPartConnection> cetera = this.partGraph.traverseBFS(next,
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
				for (P parti : partGraph) {
					parti.changeSize(parti.getRelativeSize() / (1 - fraction), false);
				}
				for (P parti : cetera) {
					parti.changeSize(parti.getRelativeSize() / fraction, false);
				}
				this.brokenParts.add(
						new MultipartSoma<>(cetera, coverage.subgraph(cetera).copy(), sizePortion, massPortion, next));
			}
			this.partGraph = centerGraph;
			this.coverage.retainAll(partGraph);
			this.isAllHoles = (this.partGraph.stream().allMatch(IPart::isHole));
		}
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
	public Iterable<ISoma<P>> peekBrokenOffParts() {
		return (Iterable<ISoma<P>>) (() -> this.brokenParts.iterator());
	}

	@Override
	public Iterable<ISoma<P>> popBrokenOffParts() {
		List<ISoma<P>> list = this.brokenParts;
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

}
