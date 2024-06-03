package civilization.mind;

import civilization.IKnowledgeBase;
import civilization.group.ICulture;

/**
 * Memory of an individual person. Mainly based on the dominant culture of the
 * being
 * 
 * @author borah
 *
 */
public interface IMemory extends IKnowledgeBase {

	/**
	 * get the main culture that this mind uses knowledge from
	 * 
	 * @return
	 */
	public ICulture getCulture();
}
