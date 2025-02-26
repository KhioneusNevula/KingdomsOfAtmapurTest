package thinker.concepts.general_types;

import java.util.UUID;

/**
 * Similar to a label concept, an Identity concept encodes the identity of a
 * group as held by its members
 * 
 * @author borah
 *
 */
public interface IIdentityConcept extends ILabelConcept {

	/**
	 * Get the ID of the group this concept encodes
	 * 
	 * @return
	 */
	public UUID getGroupID();
}
