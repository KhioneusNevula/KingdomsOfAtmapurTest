package thinker.knowledge.base.individual;

import java.util.Iterator;

import thinker.knowledge.base.IKnowledgeBase;

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
