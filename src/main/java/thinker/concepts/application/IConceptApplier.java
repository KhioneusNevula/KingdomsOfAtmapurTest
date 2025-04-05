package thinker.concepts.application;

import things.interfaces.UniqueType;
import thinker.concepts.IConcept;

/**
 * An applicator for a cocnept to an associated physical thing or phenomenon
 * 
 * @author borah
 *
 */
public interface IConceptApplier {

	/**
	 * An applier for a predicate which never applies to anything. Not sure about
	 * use?
	 */
	public static final IConceptApplier NONE = new IConceptApplier() {

		@Override
		public UniqueType forType() {
			return UniqueType.ANY;
		}

		@Override
		public boolean applies(Object forThing, IConcept checker) {
			return false;
		}
	};

	/** An applier for a predicate that always applies to everything */
	public static final IConceptApplier ALL = new IConceptApplier() {

		@Override
		public UniqueType forType() {
			return UniqueType.ANY;
		}

		@Override
		public boolean applies(Object forThing, IConcept checker) {
			return true;
		}
	};

	/**
	 * Determines whether this concept applier can apply its concept to the given
	 * Thing
	 */
	public boolean applies(Object forThing, IConcept checker);

	/**
	 * If this profile has a UniqueType, return it ({@link UniqueType#N_A})
	 * otherwise)
	 */
	public UniqueType forType();

}
