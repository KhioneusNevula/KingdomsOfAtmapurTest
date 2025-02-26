package thinker.social;

import things.interfaces.IUnique;
import thinker.IKnowledgeBase;
import thinker.concepts.general_types.IProfile;

/**
 * One entity which may have a relationship with another entity
 * 
 * @author borah
 *
 */
public interface IParty extends IUnique {

	/**
	 * Return this party's storage of knowledge
	 * 
	 * @return
	 */
	public IKnowledgeBase getKnowledge();

	/**
	 * Get the profile of this unique entity
	 * 
	 * @return
	 */
	public IProfile getProfile();

	/**
	 * Whether this Party is loaded into the world or is in an unloaded format
	 * 
	 * @return
	 */
	public boolean isLoaded();

	/**
	 * Return a number if there is a fixed number of individuals in this Party (e.g.
	 * an individual or singular role has a count of One, whereas a Group might have
	 * a count of its population) or null if there is an uncountable or changing
	 * number of individuals
	 * 
	 * @return
	 */
	public Integer getCount();

	/**
	 * If this party is non-individual, e.g. a Group, Role, or other
	 * multi-individual party
	 * 
	 * @return
	 */
	public default boolean isCollective() {
		return this instanceof ICollective;
	}

	/**
	 * Cast this to a Collective
	 * 
	 * @return
	 */
	public default ICollective asCollective() {
		return (ICollective) this;
	}

}
