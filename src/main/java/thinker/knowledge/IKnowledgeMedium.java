package thinker.knowledge;

import java.util.Collection;
import java.util.Collections;

import things.form.sensing.sensors.ISensor;
import things.form.soma.component.IComponentPart;
import things.status_effect.IPartStatusEffect;
import things.status_effect.IPartStatusEffectInstance;
import thinker.mind.memory.IFeelingReason;

/**
 * Anything which transfers knowledge in some fashion. This may be a text that
 * contains knowledge, or it may simply be an utterance spoken, or even a
 * picture carved in a wall.
 * 
 * IMedia contain a graph of knowledge transferred by them. Some kinds of
 * knowledge can also cause special effects when acquired, which is applied to
 * the part that sensed the knowledge.
 * 
 * 
 * @author borah
 *
 */
public interface IKnowledgeMedium extends IFeelingReason {

	@Override
	default FeelingReasonType getReasonType() {
		return FeelingReasonType.KNOWLEDGE;
	}

	/** Return a representation of the knowledge this is supposed to contain */
	public IKnowledgeRepresentation getKnowledge();

	// TODO public Set<ISkill> getChangedSkills()
	// public float getSkillChangePercent(ISkill skill)

	/**
	 * If this knowledge has mystical effects when it is learned, return the
	 * possible ones here.
	 */
	public default Collection<IPartStatusEffect> getEffectsOfUnderstanding() {
		return Collections.emptySet();
	}

	/**
	 * If this knowledge has mystical effects when it is learned, create them here.
	 */
	public default Collection<IPartStatusEffectInstance> generateEffectsOfUnderstanding(IComponentPart sensor,
			ISensor medium) {
		return Collections.emptySet();
	}

}
