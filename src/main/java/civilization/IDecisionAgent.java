package civilization;

import sim.interfaces.IUnique;

/**
 * An individual or group which makes decisions, such as a person, or a kingdom
 * 
 * @author borah
 *
 */
public interface IDecisionAgent extends IUnique {

	public IKnowledgeBase getKnowledge();

}
