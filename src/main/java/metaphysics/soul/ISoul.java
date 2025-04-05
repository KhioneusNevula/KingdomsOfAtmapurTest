package metaphysics.soul;

import metaphysics.spirit.ISpirit;
import thinker.mind.clock.IClock;
import thinker.mind.memory.IMemoryBase;
import thinker.mind.perception.IPerception;
import thinker.mind.personality.IPersonality;
import thinker.mind.will.IWill;

/**
 * An intangible element of a sentient being which governs their body and how
 * they operate it
 * 
 * @author borah
 *
 */
public interface ISoul extends ISpirit {

	/**
	 * The will is the location of the thoughts this being thinks
	 * 
	 * @return
	 */
	public IWill getWill();
}
