package thinker.concepts.general_types;

import java.util.UUID;

public class ProcessConcept implements IProcessConcept {

	private UUID id;

	public ProcessConcept(UUID processID) {
		this.id = processID;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.PROCESS;
	}

	@Override
	public String getUnderlyingName() {
		return "process-" + this.id;
	}

	@Override
	public String toString() {
		return "process(" + this.id + ")";
	}

	@Override
	public UUID getUUID() {
		return this.id;
	}

}
