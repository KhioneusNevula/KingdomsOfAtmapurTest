package things.form.kinds.multipart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import _main.WorldGraphics;
import _sim.RelativeSide;
import _sim.vectors.IVector;
import processing.core.PConstants;
import things.actor.IActor;
import things.form.IPart;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.CoverageType.CoverageDirection;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.IKind;
import things.form.kinds.singlepart.SingleComponentSoma;
import things.form.material.IMaterial;
import things.form.material.property.MaterialProperty;
import things.form.shape.IShape;
import things.form.shape.property.ShapeProperty;
import things.form.soma.IPartDestructionCondition;
import things.form.soma.ISoma;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.component.StandardComponentPart;
import things.form.soma.stats.IPartStat;
import things.form.visage.IVisage;
import things.spirit.ISpirit;
import things.stains.IStain;
import things.status_effect.IPartStatusEffectInstance;
import utilities.MathUtils;
import utilities.collections.ImmutableCollection;
import utilities.couplets.Pair;
import utilities.couplets.Triplet;
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
	private Map<String, IChannelSystem> systems = new HashMap<>();
	private Multimap<IChannelCenter, IComponentPart> channelSystemCenters = MultimapBuilder.hashKeys().hashSetValues()
			.build();
	private Multimap<IChannelSystem, IComponentPart> channelSystemParts = MultimapBuilder.hashKeys().hashSetValues()
			.build();
	private Map<IPartStat<?>, Pair<Object, Integer>> aggregateStats;
	private Map<IResource<? extends Comparable<?>>, Comparable<?>> aggregateResources;
	private float mass;
	private IPartDestructionCondition destructCondition;
	private boolean isAllHoles;
	private boolean isDestroyed;
	private List<ISoma> brokenParts;
	private IKind kind = IKind.MISCELLANEOUS;

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
			this.isAllHoles = this.isAllHoles && part.isHole();
		}
		if (checkSize != 1f) {
			for (IComponentPart part : parts) {
				part.changeSize(part.getRelativeSize() / checkSize, false);
			}
		}
		this.contiguousParts = Sets.newHashSet(partGraph.nodeTraversalIteratorBFS(centerPart,
				Set.of(PartConnection.JOINED, PartConnection.MERGED), (a, b) -> true));

		if (partGraph.isEmpty() || isAllHoles) {
			canrender = false;
		} else
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
	@SuppressWarnings("unchecked")
	public MultipartSoma addChannelSystem(IChannelSystem sys, boolean apply) {
		this.systems.put(sys.name(), sys);
		Collection<? extends IComponentPart> centers = Collections.emptySet();
		if (apply) {
			centers = sys.populateBody(this);

		}
		for (IResource<?> resource : sys.getChannelResources()) {
			for (IComponentPart part : this.partGraph) {
				this.aggregateResources.put(resource,
						((IResource) resource).add(
								this.aggregateResources.getOrDefault(resource, resource.getEmptyValue()),
								part.getResourceAmount(resource)));
			}
		}

		return this;
	}

	@Override
	public IKind getKind() {
		return kind;
	}

	/**
	 * Set this body's kind
	 * 
	 * @param kind
	 */
	public void setKind(IKind kind) {
		this.kind = kind;
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

	@Override
	public IRelationGraph<IComponentPart, IPartConnection> getRepresentationGraph() {
		return ImmutableGraphView.of(partGraph);
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
		return ImmutableGraphView.of(coverage);
	}

	protected void onAddPart(IComponentPart newPart) {
		newPart.setOwner(this);
		this.partGraph.add(newPart);
		this.coverage.add(newPart);
		this.allParts.add(newPart);

		for (IPartStat<?> stat : newPart.getStats()) {
			this.unionStat(stat, newPart.getStat(stat));
		}
		float totalSize = (float) partGraph.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();

		partGraph.forEach((p) -> p.changeSize(p.getRelativeSize() / totalSize, false));

		for (IChannelSystem sys : this.getChannelSystems()) {
			if (!newPart.getChannelCenters(sys).isEmpty()) {
				newPart.getChannelCenters(sys).forEach((cen) -> {
					this.channelSystemCenters.put(cen, newPart);
					this.channelSystemParts.put(sys, newPart);

				});
			}
		}
		if (!canrender && !newPart.isHole()) {
			canrender = true;
		}
	}

	@Override
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides) {
		if (!partGraph.contains(toPart)) {
			throw new IllegalArgumentException(toPart + " is not part of " + this);
		}
		boolean newed = !partGraph.contains(newPart);
		if (newed) {
			this.onAddPart(newPart);
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
		for (IChannelSystem sys : this.systems.values()) {
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
			Iterator<IPartStatusEffectInstance> effs = p.getEffectInstances().iterator();
			if (effs.hasNext()) {
				for (IPartStatusEffectInstance effect = effs.next(); effs.hasNext(); effect = effs.next()) {
					if (effect.remainingDuration() == 0 || effect.getEffect().shouldRemove(effect, p)) {
						effs.remove();
					} else {
						effect.tick();
					}
				}
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

	protected void drawPart(IComponentPart part, WorldGraphics g, IVector loc) {
		Color color = part.getMaterial().getProperty(MaterialProperty.COLOR);

		g.stroke(color.darker().darker().getRGB());
		g.fill(color.getRGB());
		IShape shape = part.getShape();
		float h = shape.getProperty(ShapeProperty.LENGTH).factor / 2 * part.getRelativeSize() * this.size
				* g.getWorld().getMainMap().getBlockRenderSize();
		float w = shape.getProperty(ShapeProperty.THICKNESS).factor / 2 * part.getRelativeSize() * this.size
				* g.getWorld().getMainMap().getBlockRenderSize();
		switch (shape.getProperty(ShapeProperty.ROLL_SHAPE)) {
		case NON_ROLLABLE:
		case ROLLABLE_OVOID:
			g.ellipseMode(PConstants.CENTER);
			g.ellipse((float) loc.getUnadjustedX(), (float) loc.getUnadjustedY(), w, h);
			return;
		case ROLLABLE_CYLINDER:
			g.rectMode(PConstants.CENTER);
			g.rect((float) loc.getUnadjustedX(), (float) loc.getUnadjustedY(), w, h);
			return;
		}
	}

	@Override
	public void draw(WorldGraphics g) {
		// TODO render multipart visage?
		Iterator<Triplet<IComponentPart, CoverageType, IComponentPart>> iterator = this.coverage
				.edgeTraversalIteratorBFS(centerPart, CoverageType.getCoverageTypes(CoverageDirection.COVERED_ON),
						(a, b) -> true);
		float step = this.size / this.partGraph.size() * g.getWorld().getMainMap().getBlockRenderSize();
		Map<IComponentPart, IVector> positions = new HashMap<>();
		Set<IComponentPart> drawn = new HashSet<>();
		drawPart(centerPart, g, IVector.ZERO);
		drawn.add(centerPart);
		for (Triplet<IComponentPart, CoverageType, IComponentPart> edge : (Iterable<Triplet<IComponentPart, CoverageType, IComponentPart>>) () -> iterator) {
			if (drawn.contains(edge.getThird()))
				continue;
			IVector firstpos = positions.getOrDefault(edge.getFirst(), IVector.ZERO);
			IVector dir = edge.getSecond().getSide().getChangeVector().scaleMagnitudeBy(step);
			IVector newPos = firstpos.add(dir);
			if (newPos != IVector.ZERO) {
				positions.put(edge.getThird(), newPos);
			}
			drawPart(edge.getThird(), g, newPos);
			drawn.add(edge.getThird());
		}

	}

	@Override
	public String visageReport() {
		return "{parts=" + this.partGraph.representation() + ",coverage=" + coverage.representation() + "}";
	}

	@Override
	public String toString() {
		return "MultipartSoma{parts=" + this.partGraph + ",coverage=" + coverage + "}";
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

	/**
	 * Remove part without adjsuting mass of this soma
	 * 
	 * @param removed
	 */
	protected void onRemovePartWithoutRemovingMass(IComponentPart removed) {

		this.partGraph.remove(removed);
		this.coverage.remove(removed);

		this.contiguousParts.clear();
		this.contiguousParts.addAll(partGraph.traverseBFS(centerPart, PartConnection.attachments(), (a) -> {
		}, (a, b) -> true));

		this.allParts.remove(removed);
		this.partsByNameAndId.values().remove(removed);

		Collection<String> toRemoveChanSys = new HashSet<>();
		this.systems.values().forEach((sys) -> {
			if (!sys.onBodyLoss(this, removed)) {
				toRemoveChanSys.add(sys.name());
			}

		});
		this.systems.keySet().removeAll(toRemoveChanSys);
		this.channelSystemCenters.values().remove(removed);
		this.channelSystemParts.values().remove(removed);
		removed.getStats().forEach((stat) -> this.differenceStat(stat, removed.getStat(stat)));

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

			severedPart.forEach((a) -> this.onRemovePartWithoutRemovingMass(a));
			float fraction = (float) severedPart.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();
			float sizePortion = fraction * size;
			float massPortion = fraction * mass;
			mass -= massPortion;
			size -= sizePortion;
			for (IComponentPart part : partGraph) {
				part.changeSize(part.getRelativeSize() / (1 - fraction), false);
			}

			MultipartSoma soma2 = new MultipartSoma(severedPart, coverage.subgraph(severedPart).copy(), sizePortion,
					massPortion, two);

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
	public float getConnectionIntegrity(IComponentPart one, IComponentPart two) {
		Float out = this.partGraph.getProperty(one, PartConnection.JOINED, two, IPartConnection.CONNECTION_INTEGRITY);
		if (out == null)
			out = 1f;
		return out;
	}

	@Override
	public void setConnectionIntegrity(IComponentPart one, IComponentPart two, float integrity) {
		if (integrity < 0 || integrity > 1)
			throw new IllegalArgumentException(integrity + "");
		this.partGraph.setProperty(one, PartConnection.JOINED, two, IPartConnection.CONNECTION_INTEGRITY, integrity);

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
	public void setCoveragePercentage(IComponentPart coverer, IComponentPart covered, RelativeSide side, float amount) {
		this.coverage.setProperty(coverer, CoverageType.covers(side), covered, CoverageType.COVERAGE_PERCENT, amount);
	}

	@Override
	public Collection<IChannelSystem> getChannelSystems() {
		return ImmutableCollection.from(this.systems.values());
	}

	@Override
	public IChannelSystem getSystemByName(String name) {
		return this.systems.get(name);
	}

	@Override
	public Collection<IComponentPart> getChannelCenters(IChannelCenter type) {
		return channelSystemCenters.get(type);
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
		toPart.getChannelCenters(bySystem).stream().findAny().orElseThrow(
				() -> new IllegalArgumentException("No ChannelCenter found for " + toPart + " in system " + bySystem));

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

	public void onApplyEffect(IPartStatusEffectInstance effect, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		for (ISpirit spir : part.getTetheredSpirits()) {
			spir.onHostEffectApplied(part, this, effect);
		}
	}

	public void onRemoveEffect(IPartStatusEffectInstance effect, StandardComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		for (ISpirit spir : part.getTetheredSpirits()) {
			spir.onHostEffectRemoved(part, this, effect);
		}
	}

	@Override
	public float mass() {
		return this.mass;
	}

	@Override
	public String somaReport() {
		return "Soma{systems=" + this.systems.values() + ",\nparts="
				+ this.partGraph.representation(IComponentPart::componentReport) + ",\ncoverage="
				+ this.coverage.representation() + "}";
	}

	@Override
	public void onPartEmbeddedMaterialsChanged(IComponentPart part, Collection<IMaterial> changedEmbeddeds) {

	}

	@Override
	public void onPartAbilitiesChange(IComponentPart part, Collection<IPartAbility> changedAbs) {
		boolean added = part.getAbilities().containsAll(changedAbs);
		for (IPartAbility aba : changedAbs) {
			if (aba instanceof IChannelCenter cc) {
				if (added) {
					this.channelSystemCenters.put(cc, part);
					this.channelSystemParts.put(cc.getSystem(), part);
				} else {

					this.channelSystemCenters.remove(cc, part);
				}
			}
		}
		for (IChannelSystem sys : this.systems.values()) {
			if (part.getChannelCenters(sys).isEmpty())
				this.channelSystemParts.remove(sys, part);
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
			this.onRemovePartWithoutRemovingMass(p);
			SingleComponentSoma singlesoma = new SingleComponentSoma(p, size * p.getRelativeSize(),
					mass * p.getRelativeSize(), systems.values(), Color.gray, destructCondition);
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
	public void onChannelResourceChanged(IComponentPart part, IResource<?> resource, Comparable<?> formerValue) {
		Comparable<?> val = this.aggregateResources.getOrDefault(resource, resource.getEmptyValue());
		val = ((IResource) resource).subtract(val, formerValue);
		val = ((IResource) resource).add(val, part.getResourceAmount(resource));
		this.aggregateResources.put(resource, val);
	}

	@Override
	public <E extends Comparable<?>> E getResourceAggregate(IResource<E> resource) {
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
		copy.channelSystemCenters = MultimapBuilder.hashKeys().hashSetValues().build();
		copy.channelSystemParts = MultimapBuilder.hashKeys().hashSetValues().build();
		for (Map.Entry<IChannelCenter, IComponentPart> cell : this.channelSystemCenters.entries()) {
			copy.channelSystemCenters.put(cell.getKey(),
					copy.partsByNameAndId.get(cell.getValue().getName(), cell.getValue().getID()));
		}
		for (Map.Entry<IChannelSystem, IComponentPart> cell : this.channelSystemParts.entries()) {
			copy.channelSystemParts.put(cell.getKey(),
					copy.partsByNameAndId.get(cell.getValue().getName(), cell.getValue().getID()));
		}
		copy.systems = new HashMap<>(systems);
		copy.contiguousParts = new HashSet<>();
		for (IComponentPart part : this.contiguousParts) {
			copy.contiguousParts.add(copy.partsByNameAndId.get(part.getName(), part.getID()));
		}
		return copy;
	}

}
