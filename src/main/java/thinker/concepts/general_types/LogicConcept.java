package thinker.concepts.general_types;

import java.util.UUID;

public class LogicConcept implements ILogicConcept {

	private UUID id;
	private LogicType type;

	LogicConcept(LogicType type, UUID id) {
		this.id = id;
		this.type = type;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.LOGIC_CONNECTOR;
	}

	@Override
	public LogicType getLogicType() {
		return type;
	}

	@Override
	public String getUnderlyingName() {
		return "gate_" + this.type.toString().toLowerCase() + "(" + this.id + ")";
	}

	@Override
	public UUID getID() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ILogicConcept ilc) {
			return this.type == ilc.getLogicType() && this.id.equals(ilc.getID());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode() * this.type.hashCode();
	}

	public String toString() {
		return this.type.toString();
	}

}
