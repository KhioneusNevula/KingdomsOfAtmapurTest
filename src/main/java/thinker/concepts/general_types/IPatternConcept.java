package thinker.concepts.general_types;

import java.util.UUID;

import thinker.concepts.IConcept;

public interface IPatternConcept extends IConcept {

	@Override
	default ConceptType getConceptType() {
		return ConceptType.C_PATTERN;
	}

	public UUID getID();

	/**
	 * Whether this pattern is for an action
	 * 
	 * @return
	 */
	public boolean isAction();

	/**
	 * Whether this pattern is for a type
	 * 
	 * @return
	 */
	public boolean isType();
}
