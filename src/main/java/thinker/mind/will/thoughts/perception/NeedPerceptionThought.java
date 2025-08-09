package thinker.mind.will.thoughts.perception;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import things.form.channelsystems.IChannelNeed;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.EnumValueConcept;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.IGoalConcept;
import thinker.mind.emotions.Affect;
import thinker.mind.emotions.IFeeling;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.needs.INeedConcept;
import thinker.mind.needs.NeedConcept;
import thinker.mind.perception.IPerception;
import thinker.mind.perception.PerceptorLevel;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.IThinkerWill;

public class NeedPerceptionThought extends PerceptionThought {

	private boolean updatedNeed;
	private boolean knowsNeed;

	@Override
	public IChannelNeed getPerceptor() {
		return (IChannelNeed) perceptor;
	}

	public NeedPerceptionThought(UUID processID, IChannelNeed fromPerceptor) {
		super(processID, fromPerceptor);
	}

	@Override
	public boolean shouldDelete(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		return updatedNeed;
	}

	@Override
	protected void updateKnowledgeFromPerceptor(IThinkerWill owner, int ticksSinceCreation, boolean focused,
			IBeingAccess info) {
		IPerception perception = info.maybeSpirit()
				.orElseThrow(() -> new UnsupportedOperationException(
						"Cannot have thoughts about perceptions from something without perceptios..." + info))
				.getPerception();
		IMindKnowledgeBase knowledge = info.being().getKnowledge();
		if (!knowsNeed) {
			if (knowledge.knowsConcept(IConcept.NECESSITY)) {
				if (knowledge.knowsConcept(getPerceptor())) {
					knowsNeed = true;
				} else {
					NeedConcept needC = new NeedConcept(getPerceptor());
					knowledge.learnConcept(getPerceptor());
					knowledge.learnConcept(needC);
					knowledge.addConfidentRelation(getPerceptor(), KnowledgeRelationType.C_PERCEPTOR_OF, needC);
					knowledge.addConfidentRelation(needC, PropertyRelationType.OF_PRINCIPLE, IConcept.NECESSITY);
				}
			} else {
				knowledge.learnConcept(IConcept.NECESSITY);
			}
		} else {
			PerceptorLevel needLevel = PerceptorLevel.fromAmount(perception.getLevelOfNeed(getPerceptor()));
			updatedNeed = true;
			if (needLevel != PerceptorLevel.UNKNOWN) {
				Iterable<? extends IConcept> needCons = knowledge.getConnectedConcepts(getPerceptor(),
						KnowledgeRelationType.C_PERCEPTOR_OF);
				for (IConcept needCon : needCons) {
					if (needCon instanceof INeedConcept needConcept) {
						knowledge.removeAllRelations(needConcept, PropertyRelationType.HAS_VALUE);
						EnumValueConcept enumVal = new EnumValueConcept(needLevel);
						knowledge.learnConcept(enumVal);
						knowledge.addTemporaryRelation(needConcept, PropertyRelationType.HAS_VALUE, enumVal);
						if (needLevel.getThreshold() < 1f) {
							IGoalConcept gc = needConcept.getRequirements();
							knowledge.learnConcept(gc);
							knowledge.learnConcept(IConcept.INTENTIONALITY);
							knowledge.addConfidentRelation(gc, PropertyRelationType.OF_PRINCIPLE,
									IConcept.INTENTIONALITY);
						}
					}
				}

			}
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(id=" + this.getProcessID().toString().substring(0, 5) + "...){"
				+ this.perceptor + "}";
	}

}
