package thinker.concepts.general_types;

import java.util.UUID;

public abstract class PatternConcept implements IPatternConcept {

	private UUID id;

	PatternConcept(UUID id) {
		this.id = id;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.C_CONNECTOR;
	}

	@Override
	public String getUnderlyingName() {
		return "pattern_" + "(" + this.id + ")";
	}

	@Override
	public UUID getID() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IPatternConcept ilc) {
			return this.id.equals(ilc.getID());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	public String toString() {
		return "Pattern_" + this.id.toString().substring(0, 5);
	}

}
