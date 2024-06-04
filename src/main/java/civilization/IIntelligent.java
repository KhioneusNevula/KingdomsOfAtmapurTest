package civilization;

import civilization.social.concepts.profile.Profile;
import sim.interfaces.IUniqueThing;

/**
 * An individual or group which makes decisions, such as a person, or a kingdom,
 * and has knowledge
 * 
 * @author borah
 *
 */
public interface IIntelligent extends IUniqueThing {

	/**
	 * Returns the profile representing the self of this entity
	 * 
	 * @return
	 */
	public Profile getSelfProfile();

	/**
	 * Update the agent at specific intervals. Groups usually tick slower than
	 * individuals
	 * 
	 * @param ticks
	 */
	public void tick(long ticks);

	/**
	 * Get the knowledge this entity contains
	 * 
	 * @return
	 */
	public IKnowledgeBase getKnowledge();

	/**
	 * Gets the {@link IAgent} representation of this entity
	 * 
	 * @return
	 */
	public IAgent getAgentRepresentation();

	/**
	 * If this entity has a parent group, get the parent group('s agent)
	 * 
	 * @return
	 */
	IAgent getParentAgent();

}
