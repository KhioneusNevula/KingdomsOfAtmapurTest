package civilization.mind;

import civilization.IKnowledgeBase;
import civilization.group.ICultureKnowledge;

/**
 * Memory of an individual person. Mainly based on the dominant culture of the
 * being
 * 
 * @author borah
 *
 */
public interface IMemoryKnowledge extends IKnowledgeBase {

	@Override
	public ICultureKnowledge getParent();

	/**
	 * get the main culture that this mind uses knowledge from. Equivalent to
	 * {@link #getParent()}
	 * 
	 * @return
	 */
	default ICultureKnowledge getCulture() {
		return getParent();
	}
}
