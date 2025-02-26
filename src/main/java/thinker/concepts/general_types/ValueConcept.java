package thinker.concepts.general_types;

public class ValueConcept implements IValueConcept {

	private int value;

	public ValueConcept(int value) {
		this.value = value;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.VALUE;
	}

	@Override
	public String getUnderlyingName() {
		return "value_" + value;
	}

	@Override
	public int getQuantity() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IValueConcept vc) {
			return this.value == vc.getQuantity();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public String toString() {
		return "value(" + this.value + ")";
	}

}
