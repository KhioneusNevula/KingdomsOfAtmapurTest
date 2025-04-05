package thinker.concepts.general_types;

public class IntegerValueConcept implements IIntegerValueConcept {

	private int value;

	public IntegerValueConcept(int value) {
		this.value = value;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.VALUE;
	}

	@Override
	public String getUnderlyingName() {
		return "int_" + value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IIntegerValueConcept vc) {
			return this.value == vc.getValue().intValue();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return IIntegerValueConcept.class.hashCode() + Integer.hashCode(value);
	}

	@Override
	public String toString() {
		return "int(" + this.value + ")";
	}

}
