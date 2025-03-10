package thinker.concepts.application;

/**
 * TODO Used to apply an enumerated trait for a trait concept
 * 
 * @author borah
 *
 */
public interface ITraitApplier<T> extends IConceptApplier {
	/**
	 * Returns the trait to be applied to some given thing, or null if none can be
	 * applied
	 * 
	 * @param forThing
	 * @return
	 */
	public T getTraitFor(Object forThing);
}
