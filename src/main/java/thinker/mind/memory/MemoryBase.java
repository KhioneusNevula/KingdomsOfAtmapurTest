package thinker.mind.memory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import _utilities.couplets.Triplet;
import things.status_effect.BasicStatusEffect;
import things.status_effect.IPartStatusEffectInstance;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.base.individual.IndividualKnowledgeBase;
import thinker.knowledge.node.IConceptNode;
import thinker.mind.emotions.IFeeling;
import thinker.mind.util.IBeingAccess;

public class MemoryBase extends IndividualKnowledgeBase implements IMindKnowledgeBase {

	private Map<IFeelingReason, Integer> reasonTimes;
	private Table<IFeeling, IFeelingReason, Float> feelingReasons;
	private Map<IFeeling, Float> feelings;
	private Map<IFeeling, Float> defVals;

	public MemoryBase(IConcept self) {
		super(self);
		this.feelingReasons = HashBasedTable.create();
		this.feelings = new HashMap<>();
		this.reasonTimes = new HashMap<>();
		this.defVals = new HashMap<>();
	}

	@Override
	public boolean forgetConcept(IConcept concept) {
		if (super.forgetConcept(concept)) {
			if (concept instanceof IFeelingReason fr) {
				this.removeEffects(fr);
			}
			return true;
		}
		return false;
	}

	@Override
	public void tickMemoriesAndFeelings(IBeingAccess info) {
		if (defVals.isEmpty()) {
			info.being().getPersonality().setDefaultAffects(this);
		}
		Set<IFeelingReason> toRemove = new HashSet<>();
		for (IFeelingReason rs : reasonTimes.keySet()) {
			if (reasonTimes.get(rs) <= 0) {
				toRemove.add(rs);
			} else {
				reasonTimes.put(rs, reasonTimes.get(rs) - 1);
			}
		}
		toRemove.forEach((f) -> this.removeEffects(f));
		IPartStatusEffectInstance effect = info.maybeTetherPart().filter((p) -> p.hasEffect(BasicStatusEffect.SLEEP))
				.map((p) -> p.getEffectInstance(BasicStatusEffect.SLEEP)).orElse(null);
		if (effect != null) { // if we are sleeping
			Set<Triplet<IConceptNode, IConceptRelationType, IConceptNode>> edgesToRemove = new HashSet<>();
			for (Triplet<IConceptNode, IConceptRelationType, IConceptNode> edge : this.conceptGraph.edgeCollection()) {
				if (this.getStorageTypeOfRelation(edge.getEdgeStart().getConcept(), edge.getEdgeType(),
						edge.getEdgeEnd().getConcept()) == StorageType.TEMPORARY) {
					edgesToRemove.add(edge);
				}
				if (info.gameMap().random() < 1 - effect.intensity())
					break;
			}
			edgesToRemove.forEach((edge) -> this.removeRelation(edge.getEdgeStart().getConcept(), edge.getEdgeType(),
					edge.getEdgeEnd().getConcept()));
		}
	}

	@Override
	public int getRemainingTime(IFeelingReason reason) {
		if (this.feelingReasons.containsColumn(reason)) {
			return this.reasonTimes.getOrDefault(reason, -1);
		}
		return 0;
	}

	@Override
	public void setRemainingTime(IFeelingReason reason, int time) {
		if (this.feelingReasons.containsColumn(reason)) {
			if (time > 0) {
				this.reasonTimes.put(reason, time);
			} else if (time < 0) {
				this.reasonTimes.remove(reason);
			} else {
				this.removeEffects(reason);
			}
		}
	}

	@Override
	public Collection<IFeeling> getFeelings() {
		return this.feelings.keySet();
	}

	@Override
	public float getEffectOnFeeling(IFeelingReason ofReason, IFeeling feeling) {
		return this.feelingReasons.column(ofReason).getOrDefault(feeling, 0f);
	}

	@Override
	public void setDefaultValueOfFeeling(IFeeling feeling, float amount) {
		this.defVals.put(feeling, amount);
	}

	private void changeFeelingsBy(IFeeling f, float d) {
		this.feelings.put(f, this.feelings.getOrDefault(f, this.defVals.getOrDefault(f, f.defaultValue())) + d);

	}

	@Override
	public void putEffectOnFeeling(IFeelingReason ofReason, IFeeling feeling, float amount, int timing) {
		if (timing == 0) {
			return;
		}
		Float formerVal = this.feelingReasons.put(feeling, ofReason, amount);

		if (formerVal != null)
			this.changeFeelingsBy(feeling, -formerVal);
		this.changeFeelingsBy(feeling, amount);

		if (timing >= 0) {
			this.reasonTimes.put(ofReason, timing);
		}
	}

	@Override
	public float removeEffectOnFeeling(IFeelingReason ofReason, IFeeling feeling) {
		Float output = feelingReasons.remove(feeling, ofReason);
		if (output == null) {
			return 0f;
		}
		this.changeFeelingsBy(feeling, -output);
		if (feelingReasons.column(ofReason).isEmpty()) {
			this.reasonTimes.remove(ofReason);
		}

		if (feelingReasons.row(feeling).isEmpty()) {
			this.feelings.remove(feeling);
		}
		return output;
	}

	@Override
	public void removeEffects(IFeelingReason ofReason) {
		if (!feelingReasons.containsColumn(ofReason))
			return;
		feelingReasons.column(ofReason).forEach((f, a) -> {
			this.changeFeelingsBy(f, -a);
		});
		feelingReasons.column(ofReason).clear();
		feelings.keySet().removeIf((f) -> feelingReasons.row(f).isEmpty());
		reasonTimes.remove(ofReason);
	}

	@Override
	public float removeFeeling(IFeeling feels) {
		feelingReasons.row(feels).forEach((f, a) -> reasonTimes.remove(f));
		feelingReasons.row(feels).clear();
		Float f = feelings.remove(feels);
		return f == null ? 0f : f;
	}

	@Override
	public float getFeeling(IFeeling feeling) {
		return Math.max(0,
				this.feelings.getOrDefault(feeling, this.defVals.getOrDefault(feeling, feeling.defaultValue())));
	}

	@Override
	public String toString() {
		String s = super.toString();
		return s.substring(0, s.length() - 1) + ",feelings[reasons]=" + this.feelingReasons.rowMap().entrySet().stream()
				.map((entry) -> Map.entry(entry.getKey() + "{level="
						+ (this.feelings.get(entry.getKey()) < 0 ? "0 (" + this.feelings.get(entry.getKey()) + ")"
								: this.feelings.get(entry.getKey()))
						+ "}",
						entry.getValue().entrySet().stream()
								.map((entry2) -> "{reason=\"" + entry2.getKey() + "\",amount="
										+ this.feelingReasons.get(entry.getKey(), entry2.getKey()) + ",time="
										+ this.reasonTimes.get(entry2.getKey()) + "}")
								.collect(Collectors.toSet())

				)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)) + "}";
	}

	@Override
	public Iterable<Entry<String, ? extends Iterable>> getRenderables() {
		return this.feelingReasons.rowMap().entrySet().stream().map((entry) -> Map.entry(
				entry.getKey() + "="
						+ (this.feelings.get(entry.getKey()) < 0 ? "0 (" + this.feelings.get(entry.getKey()) + ")"
								: this.feelings.get(entry.getKey()))
						+ "",
				entry.getValue().entrySet().stream()
						.map((entry2) -> "reason=\"" + entry2.getKey() + "\"\namount="
								+ this.feelingReasons.get(entry.getKey(), entry2.getKey()) + "\ntime="
								+ this.reasonTimes.get(entry2.getKey()) + "")
						.collect(Collectors.toSet())

		)).collect(Collectors.toSet());
	}

}
