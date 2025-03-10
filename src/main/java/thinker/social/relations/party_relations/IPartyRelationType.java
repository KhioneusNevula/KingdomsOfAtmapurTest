package thinker.social.relations.party_relations;

import _utilities.graph.IInvertibleRelationType;

/**
 * The type of relation two parties can have
 * 
 * @author borah
 *
 */
public interface IPartyRelationType extends IInvertibleRelationType {

	@Override
	public IPartyRelationType invert();

	/**
	 * Whether this relation involves Start sending a resource to End
	 * 
	 * @return
	 */
	public boolean sendsResource();

	/** Whether this relation involves Start receiving a resource from End */
	public boolean receivesResource();

	/** Whether this relation involves Start controlling End */
	public boolean controls();

	/** Whether this relation involves Start being controlled by End */
	public boolean submits();

	/** whether this relation involves Start harming End */
	public boolean harms();

	/**
	 * Whether this relationship is one that Start knows about but End does not know
	 * about
	 */
	public boolean isSecret();

	/**
	 * Whether this relationship entails Start being harmed by End.
	 * 
	 * @return
	 */
	boolean isHarmedByOther();

	/** Whether this relationship is not known to Start (but known to End) */
	boolean isNotKnown();

	/**
	 * Whether this relationship involves Start being the parent knowledge base of
	 * End
	 */
	boolean isKnowledgeParent();

	/**
	 * Whether this relationship involves End being the parent knowledge base of
	 * Start
	 */
	boolean hasKnowledgeParent();

}
