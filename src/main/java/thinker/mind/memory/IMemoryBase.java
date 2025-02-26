package thinker.mind.memory;

import thinker.IIndividualKnowledgeBase;
import thinker.mind.emotions.IFeelings;

/**
 * The storage of an individual's mind as opposed to something like a group
 * 
 * @author borah
 *
 */
public interface IMemoryBase extends IIndividualKnowledgeBase {

	/**
	 * Get the container of the feelings of this mind
	 * 
	 * @return
	 */
	public IFeelings getFeelings();

	/** TODO memories e.g. seeing, feeling something, etc, to make emotions */
	/** public Collection<? extends IMemoryItem> getRecentMemories(); */
	/** public IMemoryItem getRecentMemory(UUID fromID); */
	/** public void addMemory(IMemoryItem memory); */
	/** public void forgetRecentMemory(IMemoryItem memory); */
	/** public IMemoryItem forgetRecentMemory(UUID memoryID); */
}
