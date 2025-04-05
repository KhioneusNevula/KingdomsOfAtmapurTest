package party;

import party.collective.ICollective;
import things.interfaces.IUnique;
import thinker.concepts.profile.IProfile;
import thinker.knowledge.IKnowledgeBearer;

/**
 * One entity which may have a relationship with another entity
 * 
 * @author borah
 *
 */
public interface IParty extends IUnique, IKnowledgeBearer {

	/** Displays relevant details about this {@link IParty} */
	public String report();

	/**
	 * Get the profile of this unique entity
	 * 
	 * @return
	 */
	public IProfile getProfile();

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
