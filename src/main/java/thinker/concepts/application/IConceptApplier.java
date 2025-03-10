package thinker.concepts.application;

import thinker.concepts.IConcept;

/**
 * An applicator for a cocnept to an associated physical thing or phenomenon
 * 
 * @author borah
 *
 */
@FunctionalInterface
public interface IConceptApplier {

	/**
	 * An applier for a predicate which never applies to anything. Not sure about
	 * use?
	 */
	public static final IConceptApplier NONE = (a, b) -> false;

	/** An applier for a predicate that always applies to everything */
	public static final IConceptApplier ALL = (a, b) -> true;

	/**
	 * Determines whether this concept applier can apply its concept to the given
	 * Thing
	 */
	public boolean applies(Object forThing, IConcept checker);

}
