package thinker.mind.will;

import java.util.Collection;
import java.util.UUID;

import things.form.soma.component.IComponentPart;
import things.spirit.ISpirit;
import thinker.mind.will.IThought.ThoughtType;

public interface IWill {

	/**
	 * Returns the chance that a focused thought will get deleted randomly each
	 * tick. This chance increases with the number of focused thoughts, though it
	 * never reaches 1. It picks the focused thought that has been around for the
	 * shortest time.
	 */
	public float getMindStrainChance();

	/** Returns the number of thoughts currently being focused */
	public int getNumberOfFocusedThoughts();

	/**
	 * Return true if this thought is in focus
	 * 
	 * @param thought
	 * @return
	 */
	public boolean isFocused(IThought thought);

	/**
	 * Return all the thoughts being thunk
	 * 
	 * @return
	 */
	public Collection<IThought> getThoughts();

	/**
	 * Get all thoughts associated with the given process
	 * 
	 * @param id
	 * @return
	 */
	public Collection<IThought> getThoughtsByProcess(UUID id);

	/**
	 * Adds a thought to the mind. If "forceFocus" is true, this thought will be
	 * added to the focus group of thoughts.
	 * 
	 * @param thought
	 * @param forceFocus
	 */
	public void addThought(IThought thought, boolean forceFocus);

	/**
	 * Removes this thought from the mind
	 * 
	 * @param thought
	 * @return
	 */
	public boolean removeThought(IThought thought);

	/**
	 * Returns all thoughts of the given type
	 * 
	 * @param type
	 * @return
	 */
	public Collection<IThought> getThoughtsOfType(ThoughtType type);

	/**
	 * Removes all thoughts of the given type
	 * 
	 * @param ofType
	 * @return
	 */
	public boolean removeAllThoughts(ThoughtType ofType);

	/**
	 * Runs one will tick, i.e. tick of the mind
	 * 
	 * @param inSpirit
	 * @param ticks
	 */
	public void willTick(ISpirit inSpirit, IComponentPart onPart, Collection<? extends IComponentPart> access,
			long ticks);

}
