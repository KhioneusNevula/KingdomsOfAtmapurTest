package thinker.concepts.general_types;

public class PrincipleConcept implements IPrincipleConcept {

	private String principleName;
	private boolean isEvent;

	public PrincipleConcept(String principleName, boolean isEvent, boolean creates, boolean destroys,
			boolean transforms) {
		this.principleName = principleName;
		this.isEvent = isEvent;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.PRINCIPLE;
	}

	@Override
	public String getUnderlyingName() {
		return "principle_" + this.principleName;
	}

	@Override
	public String toString() {
		return "[*" + this.principleName + "*]";
	}

	@Override
	public boolean isEventType() {
		return this.isEvent;
	}

}
