package thinker.concepts.application;

import things.interfaces.IThing;

/**
 * TODO Used to apply an enumerated trait for a trait concept
 * 
 * @author borah
 *
 */
public interface ITraitApplier<T> extends IConceptApplier {
	/**
	 * Returns the trait to be applied to some given thing
	 * 
	 * @param forThing
	 * @return
	 */
	public T getTraitFor(IThing forThing);
}
