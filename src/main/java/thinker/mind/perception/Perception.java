package thinker.mind.perception;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import _utilities.property.IProperty;
import things.actor.IActor;
import things.form.IPart;
import things.form.channelsystems.IChannelNeed;
import things.form.channelsystems.IChannelSystem;
import things.form.sensing.sensors.ISensor;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.visage.IVisage;
import things.form.visage.PartialVisage;
import thinker.knowledge.IKnowledgeMedium;
import thinker.mind.perception.sensation.ISensation;
import thinker.mind.perception.sensation.ISensationReceptor;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.thoughts.perception.NeedPerceptionThought;

public class Perception implements IPerception {

	private Table<UUID, IProperty<?>, Object> sensed;

	private Multimap<IVisage<?>, ISensor> bySensor;
	private SetMultimap<IComponentPart, ISensor> avSensors;
	private Table<ISensor, UUID, Multimap<Boolean, IKnowledgeMedium>> knowledgeSensed;
	private Map<IChannelNeed, Float> needs;
	private Map<ISensation, Float> sensations;
	private Map<ISensation, ISensationReceptor> sensationReceptors;
	private Set<IPerceptor> blockages;
	private Map<UUID, IVisage<?>> visagesByUUID;

	public Perception() {
		sensed = Tables.synchronizedTable(HashBasedTable.create());
		visagesByUUID = Collections.synchronizedMap(new HashMap<>());
		knowledgeSensed = Tables.synchronizedTable(HashBasedTable.create());
		avSensors = Multimaps.synchronizedSetMultimap(MultimapBuilder.hashKeys().hashSetValues().build());
		bySensor = Multimaps.synchronizedSetMultimap(MultimapBuilder.hashKeys().hashSetValues().build());
		blockages = Collections.synchronizedSet(new HashSet<>());
		needs = Collections.synchronizedMap(new HashMap<>());
		sensations = Collections.synchronizedMap(new HashMap<>());
		sensationReceptors = new HashMap<>();
	}

	public Perception addSensationReceptor(ISensation sensation, ISensationReceptor recep) {
		this.sensationReceptors.put(sensation, recep);
		return this;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{x" + (!sensed.isEmpty() ? ", sensed=" + visagesByUUID : "")
				+ (!avSensors.isEmpty() ? ", availableSensors=" + avSensors : "")
				+ (!needs.isEmpty() ? ", needs=" + needs : "")
				+ (!sensations.isEmpty() ? ", sensations=" + sensations : "")
				+ (!blockages.isEmpty() ? ", blockages=" + blockages : "") + "}";
	}

	@Override
	public Collection<IProperty<?>> getPerceptibleProperties(UUID e, IBeingAccess info) {
		if (sensed.row(e).keySet().isEmpty()) {
			IVisage<?> visage = this.getVisageFor(e, info);
			for (IComponentPart part : this.avSensors.keySet()) {
				for (ISensor sensor : this.avSensors.get(part)) {
					sensor.getPerceptibleProperties(visage, part).forEach((prop, val) -> sensed.put(e, prop, val));
				}
			}
		}
		return sensed.row(e).keySet();
	}

	@Override
	public <T> T getPerceptibleProperty(UUID e, IProperty<T> property, IBeingAccess info) {
		if (!sensed.contains(e, property)) {
			this.getPerceptibleProperties(e, info);
		}
		return (T) sensed.get(e, property);
	}

	@Override
	public float getSensation(ISensation forAffect) {
		return sensations.getOrDefault(forAffect, 0f);
	}

	@Override
	public Collection<ISensation> getKnownSensations() {
		return sensations.keySet();
	}

	@Override
	public IVisage<?> getVisageFor(UUID entity, IBeingAccess info) {
		if (!visagesByUUID.containsKey(entity)) {
			IActor actor = info.gameMap().getActorByUUID(entity);
			PartialVisage pv = new PartialVisage(actor.visage());
			for (IComponentPart part : this.avSensors.keySet()) {
				for (ISensor sensor : this.avSensors.get(part)) {
					pv.addSensableParts(sensor.sensableParts(actor.visage(),
							info.maybeActor().orElseThrow(() -> new UnsupportedOperationException(
									"Cannot sense parts of " + actor + " from nonexistent perceiver... " + info)),
							part).getPartGraph());
				}
			}
			visagesByUUID.put(entity, pv);
		}
		return this.visagesByUUID.get(entity);
	}

	@Override
	public void addBlockage(IPerceptor blockage) {
		this.blockages.add(blockage);
	}

	@Override
	public Collection<IPerceptor> getBlockages() {
		return this.blockages;
	}

	@Override
	public void removeBlockage(IPerceptor blockage) {
		this.blockages.remove(blockage);
	}

	@Override
	public Collection<IKnowledgeMedium> getSensedKnowledge(UUID fromEntity, ISensor forSensor, boolean focused,
			IBeingAccess info) {
		if (!knowledgeSensed.contains(forSensor, fromEntity)
				|| knowledgeSensed.get(forSensor, fromEntity).get(focused).isEmpty()) {
			Multimap<Boolean, IKnowledgeMedium> knows = MultimapBuilder.hashKeys().hashSetValues().build();
			knowledgeSensed.put(forSensor, fromEntity, knows);
			IVisage<?> visage = this.getVisageFor(fromEntity, info);
			for (IComponentPart sensorpart : this.avSensors.keySet()) {
				for (ISensor sensor : this.avSensors.get(sensorpart)) {
					for (IPart visagepart : visage.getPartGraph()) {
						knows.get(focused).addAll(sensor.senseKnowledge(visagepart, sensorpart, focused));
					}
				}
			}
		}
		return knowledgeSensed.get(forSensor, fromEntity).get(focused);
	}

	@Override
	public Collection<ISensor> getAvailableSensors() {
		return avSensors.values();
	}

	@Override
	public Collection<IComponentPart> getAvailableSensorParts() {
		return avSensors.keySet();
	}

	@Override
	public Collection<IChannelNeed> getKnownNeeds() {
		return needs.keySet();
	}

	@Override
	public float getLevelOfNeed(IChannelNeed need) {
		return needs.getOrDefault(need, 1f);
	}

	private void maybeCreateThoughtsForNeed(IChannelNeed need, float curVal, Float oldVal, IBeingAccess info) {
		PerceptorLevel oldLevel = oldVal == null ? PerceptorLevel.UNKNOWN : PerceptorLevel.fromAmount(oldVal);
		PerceptorLevel curLevel = PerceptorLevel.fromAmount(curVal);
		if (oldLevel != curLevel) { // if a need changed notably, or was detected the first time make a thought
			boolean foc = curLevel.getThreshold() <= PerceptorLevel.LOW.getThreshold();
			info.maybeSoul().get().getWill().addThought(new NeedPerceptionThought(UUID.randomUUID(), need), foc);
		}
	}

	@Override
	public void update(IBeingAccess info) {

		avSensors.clear();
		visagesByUUID.clear();
		sensed.clear();
		knowledgeSensed.clear();
		bySensor.clear();

		if (info.isCorporeal()) {
			ISoma body = info.maybeSoma().orElseThrow(
					() -> new UnsupportedOperationException("Part " + info.maybeTetherPart().get() + " lacks soma?"));
			if (body != null) {

				for (ISensation sensation : this.sensationReceptors.keySet()) {
					if (!this.blockages.contains(sensation)) {
						float slevel = this.sensationReceptors.get(sensation).getSensationLevel(sensation, info);

						Float prior = this.sensations.put(sensation, slevel);
						info.being().getPersonality().changeAffects(info.being().getKnowledge(), sensation, slevel, 10,
								true);
						/*
						 * PerceptorLevel plev = prior == null ? PerceptorLevel.UNKNOWN :
						 * PerceptorLevel.fromAmount(prior); PerceptorLevel clev =
						 * PerceptorLevel.fromAmount(slevel); if (clev.getThreshold() >
						 * plev.getThreshold()) { inSpirit.getWill().addThought(new
						 * SensationPerceptionThought(UUID.randomUUID(), sensation, (int) (10 * slevel +
						 * 1), true), true); } else { // TODO relief
						 * 
						 * // inSpirit.getWill().addThought(new ReliefThought(UUID.randomUUID(),
						 * sensation, // slevel), true);
						 * 
						 * }
						 */
					} else {
						this.sensations.remove(sensation);
					}

				}

				for (IChannelSystem system : body.getChannelSystems()) {
					for (IChannelNeed need : system.getChannelSystemNeeds()) {
						if (blockages.contains(need)) { // if a need is blocked, remove it!!
							this.needs.remove(need);
						} else {
							float nl = system.getNeedLevel(need, info);
							if (nl >= 0) {
								Float priorVal = this.needs.put(need, nl);
								info.being().getPersonality().changeAffects(info.being().getKnowledge(), need, nl, 10,
										true);
								this.maybeCreateThoughtsForNeed(need, nl, priorVal, info);
							}
						}
					}
				}

				IActor owner = body.getOwner();

				if (owner != null && owner.getMap() != null) {

					for (IComponentPart sensorPart : (Iterable<IComponentPart>) () -> (Iterator) info.partAccess().stream()
							.filter((part) -> part.hasSensor()).iterator()) {
						for (ISensor sensor : sensorPart.getSensors()) {
							if (blockages.contains(sensor)) {
								continue;
							}
							if (!avSensors.get(sensorPart).contains(sensor)) {
								avSensors.put(sensorPart, sensor);
							}

						}
					}
				}
			}
		} else {
			this.sensations.entrySet().forEach((e) -> e.setValue(0f));
			this.needs.entrySet().forEach((e) -> e.setValue(0f));
		}

	}

}
