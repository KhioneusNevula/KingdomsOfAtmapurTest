package civilization.mind;

import civilization.IDecisionAgent;

/**
 * For complex souls which have more than just standard AI, we need a mind
 * 
 * @author borah
 *
 */
public interface IMind extends IDecisionAgent {

	/**
	 * Update the mind each cycle
	 * 
	 * @param ticks
	 */
	public void tick(long ticks);

	/**
	 * Get memory of this agent
	 * 
	 * @return
	 */
	public IMemory getMemory();

}
