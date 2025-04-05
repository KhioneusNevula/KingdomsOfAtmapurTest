package thinker.concepts.general_types;

import java.util.UUID;

import thinker.goals.IGoalConcept;

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IProcessConcept pc) {
			return this == obj || this.getUUID().equals(pc.getUUID());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.getUUID().hashCode() + IProcessConcept.class.hashCode();
	}

}
