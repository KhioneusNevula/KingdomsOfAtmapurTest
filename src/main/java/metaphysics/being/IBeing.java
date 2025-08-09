package metaphysics.being;

import java.util.Collection;
import java.util.Optional;

import _sim.world.GameMap;
import party.agent.IAgent;
import party.collective.ICollective;
import things.form.soma.component.IComponentPart;
import thinker.mind.clock.IClock;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.personality.IPersonality;

/**
 * An individual being which can take actions in some fashion. Typically either
 * a mortal being, god, ghost, or something similar
 * 
 * @author borah
 *
 */
public interface IBeing extends IFigure, IAgent {

	/** The personality is the location of the being's own tendencies when acting */
	public IPersonality getPersonality();

	public Collection<ICollective> getParentGroups();

	/**
	 * Get the internal clock of this being
	 * 
	 * @return
	 */
	public IClock getClock();

	/**
	 * This is used when a being is unloaded to create a persistent figure to
	 * represent this being while it is not represetning itself.
	 */
	public IFigure createPersistentFigure(Optional<IComponentPart> onPart);

	@Override
	public IMindKnowledgeBase getKnowledge();

	/** By default, things which implement IBeing should refer to themselves */
	@Override
	default IBeing getReferent() {
		return this;
	}

	/** By default, thigns which implement IBeing should be existing agents */
	@Override
	default boolean exists() {
		return true;
	}

	/**
	 * Run a tick on this being when it is untethered from any body (this is the
	 * norm for some beings)
	 */
	public void runUntetheredTick(GameMap world, long ticks);

	/**
	 * return true if this being, while untethered, should be deleted. This
	 * condition is ONLY checked while untethered.
	 */
	public boolean readyForDeletion(GameMap world, long ticks);

	/** Called after a being is just untethered and added to the map */
	public void onUntethering(GameMap gameMap, long ticks);

	/** Called when this being is removed from the map while it is untethered. */
	public void onRemoveFromMap(GameMap gameMap, long ticks);

	/** If this ebing is no longer extant in the world. */
	public boolean isRemoved();

	/**
	 * Marks this being as being removed from the world. Does not necessarily affect
	 * its behavior, but is useful fr things that want to check the being's status
	 */
	public void setRemoved(boolean removed);

}
