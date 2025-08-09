package thinker.concepts.general_types;

import thinker.concepts.IConcept;

/** A concept representing something that happened in the past */
public interface IMemoryConcept extends IConcept {

	@Override
	default ConceptType getConceptType() {
		return ConceptType.MEMORY;
	}

	/** Return the time when the memory occurred, or -1 if it is not reelvant? */
	public long getTime();
}
