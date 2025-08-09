package thinker.mind.personality;

import java.util.Collection;

import _graphics.IMindListRenderableInterface;
import things.form.channelsystems.IChannelNeed;
import things.form.channelsystems.eat.FuelChannelSystem;
import thinker.mind.emotions.Affect;
import thinker.mind.emotions.Emotion;
import thinker.mind.emotions.IAffect;
import thinker.mind.emotions.IEmotion;
import thinker.mind.memory.IFeelingReason;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.perception.sensation.ISensation;
import thinker.mind.perception.sensation.Sensation;

/**
 * Personality controls a series of factors in a being or other intelligent
 * agent, all linked to how different factors change its affects. These include:
 * <ul>
 * <li>What effects {@link ISensation}s have on {@link IAffect}s, e.g. one might
 * have a low level of PAIN_SENSITIVITY which means that {@link Sensation#PAIN}
 * has lower effects on their {@link Affect#SUFFERING}, or one might have higher
 * WIMPINESS which means that {@link Sensation#PAIN} has higher effects on their
 * {@link Affect#SELF_PRESERVATION}
 * <li>What effects some {@link IEmotion}s have on {@link IAffect}s, e.g. one
 * might have lower HOTHEADEDNESS, so {@link Emotion#FRUSTRATION} has lower
 * effects on {@link Affect#AGGRESSION}.
 * <li>What effects some {@link IAffect}s have on <strong>other</strong>
 * {@link IAffect}s, e.g. one might have higher WRATH, so
 * {@link Affect#SUFFERING} has higher effects on {@link Affect#AGGRESSION}.
 * <li>What effects some {@link IChannelNeed}s have on {@link IAffect}s, e.g.
 * one might have high HANGRINESS, meaning that their
 * {@link FuelChannelSystem}'s channel need has lower effects on
 * {@link Affect#AGGRESSION}
 * </ul>
 * 
 * @author borah
 *
 */
public interface IPersonality extends IMindListRenderableInterface, ITendencies {

	/**
	 * Sets the default value of relevant Affects.
	 * 
	 */
	public void setDefaultAffects(IMindKnowledgeBase emotionBase);

	/**
	 * Adds value to relevant Affects, based on the given Reason. E.g. for
	 * {@link Affect#SELF_PRESERVATION}, if a {@link Sensation#PAIN} perceptor is
	 * passed in with a 0.9 level, and the WIMPINESS trait is 0.9, then the result
	 * might be something like 1.7, which will be added onto
	 * {@link Affect#SELF_PRESERVATION}
	 * 
	 * @param how     much time to change it for
	 * @param cascade whether to also include affects that are changed BY other
	 *                affects
	 */
	public void changeAffects(IMindKnowledgeBase emotionBase, IFeelingReason reason, float perceptorLevel, int time,
			boolean cascade);
}
