package thinker.concepts.application;

import thinker.concepts.IConcept;

/**
 * A pseudo-concept that encodes the associaion between a concept and a physical
 * phenomenon
 */
public interface IConceptAssociationInfo extends IConcept {

	/** Return the application predicate of this concept */
	public <T extends IConceptApplier> T getApplier();

}
