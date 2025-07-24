package metaphysics.soul;

import metaphysics.spirit.ISpirit;
import party.agent.IAgent;
import thinker.mind.clock.IClock;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.perception.IPerception;
import thinker.mind.personality.IPersonality;
import thinker.mind.will.IThinkerWill;

/**
 * An intangible element of a sentient being which governs their body and how
 * they operate it
 * 
 * @author borah
 *
 */
public interface ISoul extends ISpirit {

	/**
	 * The will is the location of the thoughts this being thinks, as well as having
	 * the properties of what is relevant to {@link IAgent#getWill()}
	 * 
	 * @return
	 */
	@Override
	public IThinkerWill getWill();
}
