package things.form.soma;

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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;

import _sim.RelativeSide;
import _sim.world.GameMap;
import _utilities.MathUtils;
import _utilities.collections.ImmutableCollection;
import _utilities.collections.ImmutableSetView;
import _utilities.couplets.Pair;
import _utilities.graph.EmptyGraph;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import metaphysics.magic.ITether;
import metaphysics.spirit.ISpirit;
import things.actor.IActor;
import things.form.IPart;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.graph.connections.CoverageType;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.settings.IKindSettings;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.shape.Shape;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.component.StandardComponentPart;
import things.form.soma.component.StandardHolePart;
import things.form.soma.stats.IPartStat;
import things.form.visage.MultipartVisage;
import things.stains.IStain;
import things.status_effect.IPartStatusEffectInstance;

/**
 * A MultipartSoma is an implementation of {@link MultipartVisage} but with
 * physical properties, such that it may be used for physical objects
 */
public class MultipartSoma extends MultipartVisage<IComponentPart> implements ISoma {

	private int intplanes;

	private Map<String, IChannelSystem> systems = new HashMap<>();

	private Multimap<IChannelCenter, IComponentPart> channelSystemCenters = MultimapBuilder.hashKeys().hashSetValues()
			.build();

	private Multimap<IChannelSystem, IComponentPart> channelSystemParts = MultimapBuilder.hashKeys().hashSetValues()
			.build();

	private Map<IPartStat<?>, Pair<Object, Integer>> aggregateStats;

	private Map<IResource<? extends Comparable<?>>, Comparable<?>> aggregateResources;

	private float mass;

	private IPartDestroyedCondition destructCondition;

	private boolean isDestroyed;

	private List<ISoma> brokenParts;

	private Map<ISpirit, IComponentPart> spirits = new HashMap<>();

	private IKindSettings settings = IKindSettings.NONE;

	public MultipartSoma(IModifiableRelationGraph<IComponentPart, IPartConnection> parts,
			IModifiableRelationGraph<IComponentPart, CoverageType> coverage, float size, float mass,
			IComponentPart centerPart) {
		super(parts, coverage, size, centerPart);
		this.mass = mass;
		this.aggregateStats = new HashMap<>();
		this.aggregateResources = new HashMap<>();
		this.brokenParts = new ArrayList<>();
		intplanes = 1;
		for (IComponentPart part : parts) {
			intplanes = MathUtils.primeUnion(intplanes, part.interactionPlanes());
			for (IPartStat stat : part.getStats()) {
				this.unionStat(stat, part.getStat(stat));
			}
		}
	}

	protected Object unionStat(IPartStat<?> stat, Object newVal) {
		Pair<Object, Integer> val = this.aggregateStats.get(stat);
		if (val == null) {
			this.aggregateStats.put(stat, Pair.of(newVal, 1));
			return newVal;
		} else {
			Object newv = stat.aggregate((List) List.of(val.getFirst(), newVal));
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

	@SuppressWarnings("unchecked")
	@Override
	public void populateChannelSystems(IActor act, GameMap currentMap, long ticks) {
		for (IChannelSystem sys : this.systems.values()) {
			sys.populateBody(this, this.settings, currentMap);

			for (IResource<?> resource : sys.getChannelResources()) {
				for (IComponentPart part : this.partGraph) {
					this.aggregateResources.put(resource,
							((IResource) resource).add(
									this.aggregateResources.getOrDefault(resource, resource.getEmptyValue()),
									part.getResourceAmount(resource)));
				}
			}
		}
	}

	@Override
	public MultipartSoma addChannelSystem(IChannelSystem sys, IKindSettings withSettings) {
		this.systems.put(sys.name(), sys);

		return this;
	}

	/**
	 * Set condition for destroying the parts of this soma
	 * 
	 * @param condition
	 * @return
	 */
	public MultipartSoma setDestructionCondition(IPartDestroyedCondition condition) {
		this.destructCondition = condition;
		return this;
	}

	protected void onAddPart(IComponentPart newPart) {
		super.onAddPart(newPart);

		for (IPartStat<?> stat : newPart.getStats()) {
			this.unionStat(stat, newPart.getStat(stat));
		}
		for (IChannelSystem sys : this.getChannelSystems()) {
			if (!newPart.getChannelCenters(sys).isEmpty()) {
				newPart.getChannelCenters(sys).forEach((cen) -> {
					this.channelSystemCenters.put(cen, newPart);
					this.channelSystemParts.put(sys, newPart);

				});
			}
		}

	}

	@Override
	public boolean attach(IComponentPart newPart, IComponentPart toPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides, boolean changeOwner) {
		boolean x = super.attach(newPart, toPart, connectionType, coveringsides, changeOwner);
		boolean newed = !partGraph.contains(newPart);
		for (IChannelSystem sys : this.systems.values()) {
			sys.onBodyNew(this, newPart, connectionType, toPart, newed);
		}
		return x;
	}

	@Override
	public void runTick(long ticks) {
		super.runTick(ticks);
		for (IComponentPart p : this.partGraph) {
			if (p.getTrueOwner() != this)
				continue;

			Collection<IStain> stains = p.generateStains();
			for (IStain sta : stains) {
				p.addStain(sta, true);
			}

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
					if (effect.remainingDuration() == 0 || effect.getEffect().shouldRemove(effect, p, ticks)) {
						effs.remove();
						effect.getEffect().onEffectRemoved(p, false);
						for (ISpirit spir : new HashSet<>(p.getTetheredSpirits())) {
							maybeRetetherSpirit(spir, spir.onHostEffectRemoved(p, this, effect),
									getPartForSpirit(spir));
						}
						onAnyPartStateChange(p, p.getTetheredSpirits());
					} else {
						effect.tick(p, ticks);
					}
				}
			}
		}

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

		if (centerPart == null) {
			System.out.print(""); // breakpoint
		}

		if (!removed.equals(centerPart) && partGraph.contains(centerPart)) {
			this.contiguousParts.clear();
			this.contiguousParts.addAll(partGraph.traverseBFS(centerPart, PartConnection.attachments(), (a) -> {
			}, (a, b) -> true));
		}

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
			if (severedPart.size() == partGraph.size()) {
				return;
			}
			if (severedPart.contains(this.centerPart)) {
				severedPart = partGraph.traverseBFS(two, PartConnection.valuesCollection(), (a) -> {
				}, (a, b) -> true);
			}

			float fraction = (float) severedPart.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();
			float sizePortion = fraction * size;
			float massPortion = fraction * mass;
			mass -= massPortion;
			size -= sizePortion;
			for (IComponentPart part : partGraph) {
				part.changeSize(part.getRelativeSize() / (1 - fraction), false);
			}

			MultipartSoma soma2 = new MultipartSoma(severedPart, coverage.subgraph(severedPart).copy(), sizePortion,
					massPortion, two).setDestructionCondition(this.destructCondition);

			severedPart.forEach((a) -> this.onRemovePartWithoutRemovingMass(a));

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
		return this.attach(newPart, fromPart, connectionType, coveringsides, true);
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

		return partGraph.getOutgoingEdgeTypes(toPart).stream().filter((a) -> a instanceof IChannel)
				.map((a) -> (IChannel) a).collect(Collectors.toSet());
	}

	@Override
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart, IChannelSystem system) {

		return partGraph.getOutgoingEdgeTypes(toPart).stream().filter((a) -> a instanceof IChannel)
				.map((a) -> (IChannel) a).filter((a) -> system.getChannelConnectionTypes().contains(a))
				.collect(Collectors.toSet());
	}

	@Override
	public IRelationGraph<IComponentPart, IPartConnection> getChanneledParts(IComponentPart toPart,
			IChannelSystem bySystem) {
		if (toPart.getChannelCenters(bySystem).isEmpty()) {
			return EmptyGraph.instance();
		}

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

	public Collection<ISpirit> getAllTetheredSpirits() {
		return ImmutableSetView.from(this.spirits.keySet());
	}

	public IComponentPart getPartForSpirit(ISpirit spirit) {
		return this.spirits.get(spirit);
	}

	@Override
	public void onAttachSpirit(ISpirit spirit, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		this.spirits.put(spirit, part);
	}

	@Override
	public void onRemoveSpirit(ISpirit spirit, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		this.spirits.remove(spirit, part);
	}

	@Override
	public float mass() {
		return this.mass;
	}

	@Override
	public String somaReport() {
		return "Soma{systems=" + this.systems.values() + ",\nparts="
				+ this.partGraph.representation(IComponentPart::componentReport,
						(e, p) -> this.partGraph.edgeToString(e, true))
				+ ",\ncoverage=" + this.coverage.representation() + "}";
	}

	/** Removes spirit and spits it out onto map */
	private void ejectSpirit(ISpirit spirit, IComponentPart original) {
		System.out.println("!!!" + this.uuid + " was forced to eject spirit " + spirit);
		if (this.owner != null && this.owner.getMap() != null) {
			this.owner.getMap().queueAction(() -> this.owner.getMap().addUntetheredBeingToWorld(spirit));
		}
		original.removeSpirit(spirit, true);
	}

	/**
	 * Handles whether or not to retether the spirit, or even whether to eject the
	 * spirit into the map
	 */
	private void maybeRetetherSpirit(ISpirit spirit, IComponentPart result, IComponentPart original) {
		if (original == null)
			throw new IllegalArgumentException("Null original part " + original + " for spirit retethering " + spirit);
		if (result == null) {
			this.spirits.remove(spirit);
			ejectSpirit(spirit, original);
		} else {
			if (result == original && result.getOwner() == this) {
			} else {
				System.out.println("Retethering spirit " + spirit + " in " + original + " to " + result);
				original.removeSpirit(spirit, true);

				result.attachSpirit(spirit, true);
			}
		}

	}

	/**
	 * Kills a spirit on this soma and ejects it into the world, or whatever the
	 * soma is designed to do
	 */
	@Override
	public void killSpirit(ISpirit spirit) {
		this.maybeRetetherSpirit(spirit, null, this.getPartForSpirit(spirit));
	}

	private void onAnyPartStateChange(IComponentPart part, Collection<ISpirit> alreadyUpdated) {
		for (ISpirit spir : new HashSet<>(this.getAllTetheredSpirits())) {
			if (alreadyUpdated.contains(spir)) {
				continue;
			}
			maybeRetetherSpirit(spir, spir.onAnyPartStateChange(this.getPartForSpirit(spir), this, part),
					this.getPartForSpirit(spir));

		}
	}

	public void onApplyEffect(IPartStatusEffectInstance effect, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		effect.getEffect().onEffectApplied(part, effect.remainingDuration());
		for (ISpirit spir : new HashSet<>(part.getTetheredSpirits())) {
			maybeRetetherSpirit(spir, spir.onHostEffectApplied(part, this, effect), getPartForSpirit(spir));
		}
		onAnyPartStateChange(part, part.getTetheredSpirits());
	}

	public void onRemoveEffect(IPartStatusEffectInstance effect, IComponentPart part) {
		if (!this.partGraph.contains(part)) {
			throw new IllegalArgumentException(part + "");
		}
		effect.getEffect().onEffectRemoved(part, true);
		for (ISpirit spir : new HashSet<>(part.getTetheredSpirits())) {
			maybeRetetherSpirit(spir, spir.onHostEffectRemoved(part, this, effect), getPartForSpirit(spir));
		}
		onAnyPartStateChange(part, part.getTetheredSpirits());
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
		this.onAnyPartStateChange(part, Collections.emptySet());
	}

	protected void onDestroyablePartChange(IComponentPart p) {

		Set<ISpirit> partOSpirits = Sets.newHashSet(p.getTetheredSpirits());
		if (this.destructCondition == null) {
			throw new IllegalStateException("Destruction condition is null for " + this + ", " + this.owner);
		}
		if (this.destructCondition.isDestroyed(this, p)) { // if we get severance
			if (this.centerPart.equals(p)) {
				this.isDestroyed = true;
			}
			this.onRemovePartWithoutRemovingMass(p);
			Set<ISoma> genSomas = new HashSet<>(); // for spirits
			RelationGraph<IComponentPart, IPartConnection> singleGraph = new RelationGraph<>();
			singleGraph.add(p);
			RelationGraph<IComponentPart, CoverageType> cover = new RelationGraph<>();
			cover.add(p);
			MultipartSoma singlesoma = new MultipartSoma(singleGraph, cover, size * p.getRelativeSize(),
					mass * p.getRelativeSize(), p).setDestructionCondition(destructCondition);
			genSomas.add(singlesoma);

			singlesoma.setUUID(UUID.randomUUID());
			brokenParts.add(singlesoma);
			IRelationGraph<IComponentPart, IPartConnection> centerGraph = EmptyGraph.instance(); // nodes
																									// which are
			// meant to
			// remain in this soma
			Iterator<IComponentPart> partIter = partGraph.iterator(); // repeatedly update this to be the iterator for
																		// the modified partgraph
			while (partIter.hasNext()) {
				IComponentPart next = partIter.next();
				if (centerGraph.contains(next))
					continue;
				IModifiableRelationGraph<IComponentPart, IPartConnection> cetera = this.partGraph.traverseBFS(next,
						PartConnection.valuesCollection(), (a) -> {
						}, (a, b) -> true);
				if (cetera.contains(centerPart)) {
					centerGraph = cetera;
					continue;
				}

				float fraction = (float) cetera.stream().mapToDouble((a) -> (double) a.getRelativeSize()).sum();
				float sizePortion = fraction * size;
				float massPortion = fraction * mass;
				mass -= massPortion;
				size -= sizePortion;

				for (IComponentPart parti : cetera) {
					parti.changeSize(parti.getRelativeSize() / fraction, false);
				}

				MultipartSoma multisoma = new MultipartSoma(cetera, coverage.subgraph(cetera).copy(), sizePortion,
						massPortion, next).setDestructionCondition(this.destructCondition);
				this.brokenParts.add(multisoma);
				genSomas.add(multisoma);

				cetera.forEach((a) -> this.onRemovePartWithoutRemovingMass(a)); // remove all this stuff

				partIter = partGraph.iterator(); // update this to match modified graph

				for (IComponentPart parti : partGraph) {
					parti.changeSize(parti.getRelativeSize() / (1 - fraction), false);
				}

			}
			this.partGraph = centerGraph.editableOrCopy();
			if (this.partGraph.isEmpty()) {
				centerPart = new StandardHolePart("empty", UUID.randomUUID(), Shape.AMORPHOUS, 0, intplanes,
						Collections.emptySet(), Collections.emptyMap());
				partGraph.add(centerPart);
				this.contiguousParts.clear();
			} else {
				this.contiguousParts = partGraph.traverseBFS(centerPart, PartConnection.attachments(), (x) -> {
				}, (a, b) -> true);
			}
			this.coverage.addAll(partGraph);
			this.coverage.retainAll(partGraph);
			this.isAllHoles = (this.partGraph.stream().allMatch(IPart::isHole));
			if (this.isAllHoles) {
				this.isDestroyed = true;
			}
			Set<ISpirit> spirisev = new HashSet<>(this.spirits.keySet());
			for (ISpirit spir : spirisev) {
				IComponentPart curPart = this.getPartForSpirit(spir);
				maybeRetetherSpirit(spir,
						spir.onAnySeverances(curPart, this, genSomas.toArray(new ISoma[genSomas.size()])), curPart);
			}
		} else {
			for (ISpirit spir : partOSpirits) {
				maybeRetetherSpirit(spir, spir.onHostStateChange(p, this), p);
			}
			onAnyPartStateChange(p, this.spirits.keySet());
		}
	}

	@Override
	public void onPartMaterialChange(IPart part, IMaterial formerMaterial) {
		super.onPartMaterialChange(part, formerMaterial);
		this.onDestroyablePartChange((IComponentPart) part);
	}

	@Override
	public void onPartStainChange(IPart part, Collection<IStain> stains) {
		super.onPartStainChange(part, stains);

	}

	@Override
	public void onAddTether(ITether tether, StandardComponentPart standardComponentPart) {
	}

	@Override
	public void onRemoveTether(ITether tether, StandardComponentPart standardComponentPart) {
	}

	@Override
	public void onRemoveTethers(Set<ITether> removed, StandardComponentPart standardComponentPart) {
	}

	@Override
	public boolean hasBrokenOffParts() {
		return !this.brokenParts.isEmpty();
	}

	@Override
	public boolean isDestroyed() {
		return this.isDestroyed;
	}

	@Override
	public boolean isDestroyed(IComponentPart part) {
		return this.destructCondition.isDestroyed(this, part);
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
	public IKindSettings getCreationSettings() {
		return settings;
	}

	@Override
	public void setCreationSettings(IKindSettings settings) {
		this.settings = settings;
	}

	@Override
	public void onPartShapeChange(IPart part, IShape formerShape) {
		super.onPartShapeChange(part, formerShape);
		this.onDestroyablePartChange((IComponentPart) part);
	}

	@Override
	public void onPartSizeChange(IPart part, float formerMaterial) {
		super.onPartSizeChange(part, formerMaterial);
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
		MultipartSoma copy = (MultipartSoma) super.clone();

		copy.channelSystemCenters = MultimapBuilder.hashKeys().hashSetValues().build();
		copy.channelSystemParts = MultimapBuilder.hashKeys().hashSetValues().build();
		for (Map.Entry<IChannelCenter, IComponentPart> cell : this.channelSystemCenters.entries()) {
			copy.channelSystemCenters.put(cell.getKey(),
					copy.partsByNameAndId.get(cell.getValue().getName(), cell.getValue().getUUID()));
		}

		for (Map.Entry<IChannelSystem, IComponentPart> cell : this.channelSystemParts.entries()) {
			copy.channelSystemParts.put(cell.getKey(),
					copy.partsByNameAndId.get(cell.getValue().getName(), cell.getValue().getUUID()));
		}

		copy.systems = new HashMap<>(systems);
		copy.aggregateStats = new HashMap<>(this.aggregateStats);
		copy.aggregateResources = new HashMap<>(this.aggregateResources);
		copy.brokenParts = new ArrayList<>();
		for (ISoma x : this.brokenParts) {
			copy.brokenParts.add(x.clone());
		}

		return copy;
	}

}
