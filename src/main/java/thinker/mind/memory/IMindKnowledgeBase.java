package thinker.mind.memory;

import java.util.Collection;
import java.util.Map.Entry;

import _graphics.IMindListRenderableInterface;
import thinker.knowledge.base.individual.IIndividualKnowledgeBase;
import thinker.mind.emotions.IFeeling;
import thinker.mind.emotions.IHasFeelings;
import thinker.mind.util.IBeingAccess;

/**
 * The storage of an individual's mind as opposed to something like a group
 * 
 * @author borah
 *
 */
public interface IMindKnowledgeBase extends IIndividualKnowledgeBase, IMindListRenderableInterface {

	/**
	 * For this class, {@link IMindListRenderableInterface#getRenderables()} returns
	 * a renderable for feelings, NOT for memories. Show memories in a graph.
	 */
	@Override
	public Iterable<Entry<String, ? extends Iterable>> getRenderables();

	/**
	 * runs ticks
	 */
	public void tickMemoriesAndFeelings(IBeingAccess info);

	/**
	 * How much time is left for this reason's effect (or 0 if the reason was never
	 * present, or -1 if it is endless)
	 */
	public int getRemainingTime(IFeelingReason reason);

	/** Returns all feelings in this that are being affected by something */
	public Collection<IFeeling> getFeelings();

	/** Returns what effect this object has on this thing's feelings */
	public float getEffectOnFeeling(IFeelingReason ofReason, IFeeling feeling);

	/**
	 * Changes the level of this thing's feelings by the given amount using the
	 * given reason, and give a timing to indicate how long it will last (or a
	 * negative value if this reason should last forever; a value of 0 causes
	 * nothing to be done). If the value is 0, the effect is still added. If this
	 * reason was already present, then replace it with the new amount and timing
	 */
	public void putEffectOnFeeling(IFeelingReason ofReason, IFeeling feeling, float amount, int time);

	/**
	 * Sets the default value of a feeling in this memory base
	 */
	public void setDefaultValueOfFeeling(IFeeling feeling, float amount);

	/**
	 * Removes the effect of this reason on the given @Override feeling and returns
	 * the effect it had, or 0 if no effect.
	 */
	public float removeEffectOnFeeling(IFeelingReason ofReason, IFeeling feeling);

	/**
	 * Removes the effects of this reason on all feelings
	 */
	public void removeEffects(IFeelingReason ofReason);

	/**
	 * Removes this feeling entirely, returning its value, or the default value if
	 * none
	 */
	public float removeFeeling(IFeeling feels);

	/**
	 * Returns the level of this thing's feeling or the default value; this value is
	 * never negative
	 */
	public float getFeeling(IFeeling feeling);

	/**
	 * Sets the remaining time for this feeling. If the feeling is not present, this
	 * does nothing. if the time given is negative, set to infinite time. If the
	 * time is 0, remove that feeling.
	 */
	public void setRemainingTime(IFeelingReason reason, int time);

	/** TODO memories e.g. seeing, feeling something, etc, to make emotions */
	/** public Collection<? extends IMemoryItem> getRecentMemories(); */
	/** public IMemoryItem getRecentMemory(UUID fromID); */
	/** public void addMemory(IMemoryItem memory); */
	/** public void forgetRecentMemory(IMemoryItem memory); */
	/** public IMemoryItem forgetRecentMemory(UUID memoryID); */
}
