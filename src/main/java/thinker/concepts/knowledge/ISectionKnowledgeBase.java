package thinker.concepts.knowledge;

import thinker.IKnowledgeBase;

/**
 * A knowledge base storing no information on its own and instead representing a
 * section of the noosphere
 * 
 * @author borah
 *
 */
public interface ISectionKnowledgeBase extends IKnowledgeBase {

	/**
	 * Return the noosphere of knowledge of this sectional knowledge base
	 * 
	 * @return
	 */
	public INoosphereKnowledgeBase getNoosphere();
}
