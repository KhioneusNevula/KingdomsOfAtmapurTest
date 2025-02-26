package thinker.concepts.application;

import com.google.common.base.Predicates;

import things.interfaces.IThing;

/**
 * A function to apply a concept to a world object
 * 
 * @author borah
 *
 */
@FunctionalInterface
public interface IConceptApplier {

	/**
	 * An applier for a predicate which never applies. Useful for concepts which are
	 * applied externally, eg. by a ritual
	 */
	public static final IConceptApplier NONE = Predicates.alwaysFalse()::apply;

	/**
	 * Determines whether this concept applies to the given Thing
	 */
	public boolean applies(IThing forThing);
}
