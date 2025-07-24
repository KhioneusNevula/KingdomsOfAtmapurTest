package party.collective.group;

import java.util.Collection;

import _sim.GameUniverse;
import party.agent.IAgent;
import party.collective.ICollective;
import party.systems.IRole;
import thinker.mind.needs.INeedConcept;

/**
 * A kind of party that typically, among other things, acts as a self-governing
 * agent
 */
public interface IGroup extends ICollective, IAgent {

	/**
	 * Return all roles embedded in this group
	 * 
	 * @return
	 */
	public Collection<? extends IRole> getRoles();

	/**
	 * Allows a member of a group to introduce a need that is not being fulfilled to
	 * the group at large
	 * 
	 * @param need
	 */
	public void postNeed(INeedConcept need);

	/**
	 * Get the needs posted by members of this group
	 * 
	 * @return
	 */
	public Collection<INeedConcept> getPostedNeeds();

	/**
	 * Tick this group
	 * 
	 * @param universe
	 * @param ticks
	 */
	public void runTick(GameUniverse universe, long ticks);

}
