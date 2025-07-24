package thinker.mind.will;

import java.util.Collection;
import java.util.UUID;

import thinker.mind.util.IBeingAccess;
import thinker.mind.will.thoughts.IThought;
import thinker.mind.will.thoughts.IThought.ThoughtType;

/**
 * A will which can think with thoughts
 * 
 * @author borah
 *
 */
public interface IThinkerWill extends IWill {

	/**
	 * the max number of focused thoughts allowed before they start getting deleted
	 * randomly
	 */
	public int focusedThoughtsCap();

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
	public void removeThought(IThought thought);

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
	public void removeAllThoughts(ThoughtType ofType);

	/**
	 * Runs one will tick, i.e. tick of the mind.
	 * 
	 * @param onPart   MAY BE NULL if this is called while spirit is untethered
	 * @param inSpirit
	 * @param ticks
	 */
	public void willTick(IBeingAccess info);

}
