package thinker.concepts.general_types;

import thinker.concepts.profile.IProfile;

/**
 * A type variant concept; linked to a type profile, and able to be the argument
 * of an action or something similar
 * 
 * @author borah
 *
 */
public interface ITypePatternConcept extends IPatternConcept, IProfile {

	@Override
	default ConceptType getConceptType() {
		return IPatternConcept.super.getConceptType();
	}
}
