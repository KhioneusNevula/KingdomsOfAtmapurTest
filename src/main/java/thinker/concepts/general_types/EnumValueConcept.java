package thinker.concepts.general_types;

/** The value of a property concept */
public class EnumValueConcept implements IEnumValueConcept {

	private Enum<?> value;

	public EnumValueConcept(Enum<?> val) {
		this.value = val;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.VALUE;
	}

	@Override
	public String getUnderlyingName() {
		return "enum_" + value.getClass().getSimpleName() + "_" + value.name();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + value.getClass().getSimpleName() + "." + this.value.name() + ")";
	}

	@Override
	public Enum<?> getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof IEnumValueConcept evc) {
			return this.value.equals(evc.getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return IEnumValueConcept.class.hashCode() + this.value.hashCode();
	}

}
