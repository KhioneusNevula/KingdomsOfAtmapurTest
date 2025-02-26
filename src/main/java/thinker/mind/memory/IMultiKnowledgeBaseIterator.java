package thinker.mind.memory;

import java.util.Iterator;

import thinker.IKnowledgeBase;

/**
 * An iterator that iterates through multiple storages
 * 
 * @author borah
 *
 * @param <E>
 */
public interface IMultiKnowledgeBaseIterator<E> extends Iterator<E> {

	/**
	 * Returns the storage that the most recently returned node is from
	 * 
	 * @return
	 */
	public IKnowledgeBase justReturnedStorage();
}
