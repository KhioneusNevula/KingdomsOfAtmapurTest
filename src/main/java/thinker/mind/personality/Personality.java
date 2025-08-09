package thinker.mind.personality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import thinker.mind.memory.IFeelingReason;
import thinker.mind.memory.IMindKnowledgeBase;

public class Personality implements IPersonality {

	private Multimap<IFeelingReason, IPersonalityTrait> reasonToTraits;
	private Map<ITendency, Float> traits;

	public Personality() {
		this.reasonToTraits = MultimapBuilder.hashKeys().hashSetValues().build();
		this.traits = new HashMap<>();
	}

	public static Personality fromTraits(ITendency... ts) {
		Personality p = new Personality();
		for (ITendency t : ts) {
			p.addTendency(t);
		}
		return p;
	}

	@Override
	public Collection<ITendency> getTendencies() {
		return traits.keySet();
	}

	@Override
	public float getValue(ITendency trait) {
		return traits.get(trait);
	}

	@Override
	public void setValue(ITendency trait, float value) {
		traits.put(trait, value);
		if (trait instanceof IPersonalityTrait tra) {
			reasonToTraits.put(tra.getReason(), tra);
		}
	}

	@Override
	public void removeTendency(ITendency trait) {
		traits.remove(trait);
		if (trait instanceof IPersonalityTrait tra) {
			reasonToTraits.remove(tra.getReason(), tra);
		}
	}

	@Override
	public void setDefaultAffects(IMindKnowledgeBase emotionBase) {
		for (IPersonalityTrait trait : reasonToTraits.get(null)) {
			emotionBase.setDefaultValueOfFeeling(trait.getRelevantAffect(),
					trait.getAffectDefault(this.getValue(trait)));
		}
	}

	@Override
	public void changeAffects(IMindKnowledgeBase memory, IFeelingReason reason1, float perceptorLevel, int time,
			boolean cascade) {
		if (reasonToTraits.get(reason1).isEmpty()) {
			return;
		}
		Set<IPersonalityTrait> visited = new HashSet<>();
		List<IFeelingReason> reasons = new ArrayList<>();
		reasons.add(reason1);
		while (!reasons.isEmpty()) {
			IFeelingReason reason = reasons.remove(reasons.size() - 1);

			for (IPersonalityTrait pers : reasonToTraits.get(reason)) {
				if (visited.contains(pers)) {
					throw new IllegalStateException(
							"Personality has a cyclic trait: " + pers + " for reason " + reason);
				}
				visited.add(pers);
				memory.putEffectOnFeeling(reason, pers.getRelevantAffect(),
						pers.getAffectFactor(traits.get(pers), perceptorLevel), time);
				reasons.add(pers.getRelevantAffect());
			}
		}
	}

	@Override
	public Iterable<Entry<String, ? extends Iterable>> getRenderables() {
		return this.reasonToTraits.asMap().entrySet().stream()
				.map((entry) -> Map.entry(entry.getKey() == null ? "[default]" : entry.getKey() + "",
						entry.getValue().stream().map((pt) -> traits.get(pt) < 0
								? Pair.of(pt.getOppositeName() + "(->" + pt.getRelevantAffect() + ")", -traits.get(pt))
								: Pair.of(pt.getPropertyName() + "(->" + pt.getRelevantAffect() + ")", traits.get(pt)))
								.collect(Collectors.toSet())))
				.collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + this.traits.entrySet().stream()
				.map((entry) -> entry.getValue() < 0
						? Triplet.of(entry.getKey(), entry.getKey().getOppositeName(), -entry.getValue())
						: Triplet.of(entry.getKey(), entry.getKey().getPropertyName(), entry.getValue()))
				.map((entry) -> Map.entry(entry.getColumnKey() + (entry.getRowKey() instanceof IPersonalityTrait pertra
						? "(" + pertra.getReason() + "->" + pertra.getRelevantAffect() + ")"
						: "()"), entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

}
