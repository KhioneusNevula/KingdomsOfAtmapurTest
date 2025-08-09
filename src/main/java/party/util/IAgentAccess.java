package party.util;

import java.util.Optional;

import _sim.world.GameMap;
import party.IParty;
import party.agent.IAgent;
import thinker.concepts.profile.IProfile;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IBeingAccess;

/**
 * A wrapper class containing info about what an agent-acting apparatus has
 * access to i.e. an {@link IAgent} and knowledge, a game map, etc
 * 
 * @author borah
 *
 */
public interface IAgentAccess {

	/** REturns the map that this being is on */
	public GameMap gameMap();

	/** The party that is acting */
	public IAgent agent();

	/** Get the knowledge of the being */
	public default IKnowledgeBase knowledge() {
		return agent().getKnowledge();
	}

	/** Get the profile of the party */
	public default IProfile profile() {
		return agent().getProfile();
	}

	/** The current number of ticks */
	public long ticks();

	/**
	 * If this is an instanceof {@link IBeingAccess} return an optional containing
	 * itself; else return mpty
	 * 
	 * @return
	 */
	public default Optional<IBeingAccess> maybeCastBeing() {
		if (this instanceof IBeingAccess) {
			return Optional.of((IBeingAccess) this);
		}
		return Optional.empty();
	}

	/**
	 * If this is an instanceof {@link IGroupAccess} return an optional
	 * containing itself; ese return empty
	 * 
	 * @return
	 */
	public default Optional<IGroupAccess> maybeCastGroup() {
		if (this instanceof IGroupAccess) {
			return Optional.of((IGroupAccess) this);
		}
		return Optional.empty();
	}

}
