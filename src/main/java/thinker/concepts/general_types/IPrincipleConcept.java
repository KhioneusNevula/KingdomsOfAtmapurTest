package thinker.concepts.general_types;

import thinker.concepts.IConcept;

/**
 * A general concept of a kind of event or idea
 * 
 * @author borah
 *
 */
public interface IPrincipleConcept extends IConcept {

	/**
	 * Creates a generic principle concept for a principle that is general or basic
	 */
	public static IPrincipleConcept createGenericFundamentalPrinciple(String name) {
		return new IPrincipleConcept() {

			@Override
			public String getUnderlyingName() {
				return name;
			}

			@Override
			public String toString() {
				return "||" + name + "||";
			}

			@Override
			public ConceptType getConceptType() {
				return ConceptType.PRINCIPLE;
			}

			@Override
			public boolean isEventType() {
				return false;
			}

		};
	}

	/**
	 * If this principle is an event type.
	 * 
	 * @return
	 */
	public boolean isEventType();

	/**
	 * Overriding {@link IConcept#doDecayChecks()}: Returns true.
	 */
	@Override
	default boolean doDecayChecks() {
		return true;
	}
}
