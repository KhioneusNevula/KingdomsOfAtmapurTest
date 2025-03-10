package thinker.mind.will;

import java.util.Collection;
import java.util.UUID;

import things.form.soma.component.IComponentPart;
import thinker.individual.IMindSpirit;

/**
 * An individual entity in the mind
 * 
 * @author borah
 *
 */
public interface IThought {

	/**
	 * Return the UUID of the process this thought represents. Every thought can
	 * start an independent process; however, parents can also make child thoughts
	 * with the same ProcessID as them
	 * 
	 * @return
	 */
	public UUID getProcessID();

	/**
	 * Runs one tick on a given thought if it is focused and active (as opposed to
	 * if it is in the background)
	 * 
	 * @param owner
	 * @param ticksSinceCreation
	 * @param inSpirit
	 * @param onPart
	 * @param gameTicks
	 */
	public void tickThoughtActively(IWill owner, int ticksSinceCreation, IMindSpirit inSpirit, IComponentPart onPart,
			long gameTicks);

	/**
	 * Runs one tick on a given thought if it is in the background, rather than
	 * focused and active
	 * 
	 * @param owner
	 * @param ticksSinceCreation
	 * @param inSpirit
	 * @param onPart
	 * @param gameTicks
	 */
	public default void tickThoughtPassively(IWill owner, int ticksSinceCreation, IMindSpirit inSpirit,
			IComponentPart onPart, long gameTicks) {

	}

	/**
	 * Return true if this thought is completed and ought to be deleted
	 * 
	 * @param owner
	 * @param ticksSinceCreation
	 * @param inSpirit
	 * @param onPart
	 * @param gameTicks
	 * @return
	 */
	public boolean shouldDelete(IWill owner, int ticksSinceCreation, IMindSpirit inSpirit, IComponentPart onPart,
			long gameTicks);

	/**
	 * Run if either
	 * {@link #shouldDelete(IWill, int, IMindSpirit, IComponentPart, long)} returns
	 * true, or if the thought is interrupted and must be deleted. This can be where
	 * memories, feelings, etc are created, for example.
	 * 
	 * @param owner
	 * @param ticksSinceCreation
	 * @param inSpirit
	 * @param onPart
	 * @param gameTicks
	 */
	public void aboutToDelete(IWill owner, int ticksSinceCreation, IMindSpirit inSpirit, IComponentPart onPart,
			long gameTicks);

	/**
	 * Called after every tick and before deleting the thought to check if the
	 * thought has any child thoughts to produce
	 */
	public boolean hasChildThoughts();

	/**
	 * Called after every tick and before deleting the thought to get child thoughts
	 */
	public Collection<? extends IThought> popChildThoughts();

	/**
	 * Returns what kind of thought this is
	 * 
	 * @return
	 */
	public ThoughtType getThoughtType();

	public static enum ThoughtType {
		/** A thought that is an action being taken */
		ACTION,
		/** A thought that is deciding actions to be taken */
		PONDER,
		/** A thought that brings sensory information into the mind */
		SENSING,
		/** A thought that changes the structure of memories */
		SYNTHESIZE,
		/**
		 * A thought recognizing the occurrence of an event (and associated feelings, or
		 * whatever)
		 */
		EVENT,
		/**
		 * A thought about a circumstance that is wanted, so a goal
		 */
		WANT, OTHER
	}
}
