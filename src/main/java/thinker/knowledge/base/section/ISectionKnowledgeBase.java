package thinker.knowledge.base.section;

import thinker.knowledge.base.IKnowledgeBase;
import thinker.knowledge.base.noosphere.INoosphereKnowledgeBase;

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
