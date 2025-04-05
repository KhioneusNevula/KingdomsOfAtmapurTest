package thinker.concepts.general_types;

import java.util.UUID;

public class ConnectorConcept implements IConnectorConcept {

	private UUID id;
	private ConnectorType type;

	ConnectorConcept(ConnectorType type, UUID id) {
		this.id = id;
		this.type = type;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.CONNECTOR;
	}

	@Override
	public ConnectorType getConnectorType() {
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
		if (obj instanceof IConnectorConcept ilc) {
			return this.type == ilc.getConnectorType() && this.id.equals(ilc.getID());
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
