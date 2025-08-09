package thinker.mind.personality;

import java.util.Collection;

/**
 * A container for the traits of a willful agent which govern how it acts
 */
public interface ITendencies {

	/** Return all traits with defined values */
	public Collection<ITendency> getTendencies();

	/** Return the numeric value for the given personality tendency */
	public float getValue(ITendency trait);

	/**
	 * Set the numeric value for the given personality trait (and add it if it isn't
	 * there)
	 */
	public void setValue(ITendency trait, float value);

	/** Adds a trait with a default value */
	public default void addTendency(ITendency trait) {
		this.setValue(trait, trait.defaultValue());
	}

	/** Remove a tendency */
	public void removeTendency(ITendency trait);
}
