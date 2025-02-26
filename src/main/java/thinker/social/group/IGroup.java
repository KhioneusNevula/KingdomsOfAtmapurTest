package thinker.social.group;

import java.util.Collection;

import _sim.world.GameUniverse;
import thinker.mind.needs.INeedConcept;
import thinker.social.ICollective;
import thinker.social.systems.IRole;

public interface IGroup extends ICollective {

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
