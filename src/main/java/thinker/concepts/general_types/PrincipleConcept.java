package thinker.concepts.general_types;

public class PrincipleConcept implements IPrincipleConcept {

	private String principleName;
	private boolean isEvent;
	private boolean creates;
	private boolean destroys;
	private boolean transforms;

	public PrincipleConcept(String principleName, boolean isEvent, boolean creates, boolean destroys,
			boolean transforms) {
		this.principleName = principleName;
		this.isEvent = isEvent;
		this.creates = creates;
		this.destroys = destroys;
		this.transforms = transforms;
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

	@Override
	public boolean destructive() {
		return this.destroys;
	}

	@Override
	public boolean creative() {
		return this.creates;
	}

	@Override
	public boolean transformative() {
		return this.transforms;
	}

}
