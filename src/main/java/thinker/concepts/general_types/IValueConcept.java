package thinker.concepts.general_types;

import thinker.concepts.IConcept;

/**
 * A concept representing an enumerable value (a number or enum)
 * 
 * @author borah
 *
 */
public interface IValueConcept extends IConcept {

	/**
	 * A ValueConcept indicating some value is ABSENT
	 */
	public static final IValueConcept ABSENT = new IValueConcept() {

		@Override
		public String getUnderlyingName() {
			return "Value_ABSENT";
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.VALUE;
		}

		@Override
		public Comparable<?> getValue() {
			return Boolean.FALSE;
		}
	};

	/**
	 * A ValueConcept indicating some value is PRESENT
	 */
	public static final IValueConcept PRESENT = new IValueConcept() {

		@Override
		public String getUnderlyingName() {
			return "Value_PRESENT";
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.VALUE;
		}

		@Override
		public Comparable<?> getValue() {
			return Boolean.TRUE;
		}
	};

	/**
	 * Return the value stored in this concept
	 * 
	 * @return
	 */
	public Comparable<?> getValue();
}
