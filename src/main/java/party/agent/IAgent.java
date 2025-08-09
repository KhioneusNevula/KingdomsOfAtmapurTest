package party.agent;

import party.IParty;
import thinker.mind.emotions.IHasFeelings;
import thinker.mind.personality.IPersonality;
import thinker.mind.personality.ITendencies;
import thinker.mind.will.IWill;

/**
 * Something which can take actions in the world. This has {@link IHasFeelings}
 * because "feelings" represent its tendencies of action
 * 
 * @author borah
 *
 */
public interface IAgent extends IParty, IHasFeelings {

	/**
	 * Return the personality of this agent, i.e. its {@link ITendencies tendencies}
	 * when acting
	 * 
	 * @return
	 */
	public ITendencies getPersonality();

	/**
	 * This is the major center of action used by this agent.
	 * 
	 * @return
	 */
	public IWill getWill();
}
