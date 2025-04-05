package thinker.mind.will.thoughts;

import java.util.Collection;
import java.util.UUID;

import _utilities.couplets.Pair;
import metaphysics.soul.ISoul;
import things.form.soma.component.IComponentPart;
import thinker.mind.emotions.IFeeling;
import thinker.mind.memory.IFeelingReason;
import thinker.mind.util.IMindAccess;
import thinker.mind.will.IWill;

/**
 * An individual entity in the mind
 * 
 * @author borah
 *
 */
public interface IThought {

	/** A collection of what feelings this thought is likely to increase */
	public Collection<? extends IFeeling> getPossibleIncreasedFeelings();

	/** A collection of what feelings this thought is likely to decrease */
	public Collection<? extends IFeeling> getPossibleDecreasedFeelings();

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
	public void tickThoughtActively(IWill owner, int ticksSinceCreation, IMindAccess info);

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
	public void tickThoughtPassively(IWill owner, int ticksSinceCreation, IMindAccess info);

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
	public boolean shouldDelete(IWill owner, int ticksSinceCreation, IMindAccess info);

	/**
	 * Run if either {@link #shouldDelete(IWill, int, ISoul, IComponentPart, long)}
	 * returns true, or if the thought is interrupted (e.g. by losing focus) and
	 * must be deleted. This can be where memories, feelings, etc are created, for
	 * example.
	 * 
	 * @param owner
	 * @param ticksSinceCreation
	 * @param inSpirit
	 * @param onPart
	 * @param gameTicks
	 */
	public void aboutToDelete(IWill owner, int ticksSinceCreation, boolean interrupted, IMindAccess info);

	/**
	 * Called after every tick and before deleting the thought to check if the
	 * thought has any child thoughts to produce
	 */
	public boolean hasChildThoughts();

	/**
	 * Whether this thought should force itself to be focused in the current
	 * tick.Checked before the first tick.
	 */
	public default boolean forceFocus(IWill owner, int ticksSinceCreation, IMindAccess info) {
		return false;
	}

	/**
	 * Whether this thought should force itself to be removed from focus in the
	 * current tick. Checked before the first tick.
	 */
	public default boolean forceSubconscious(IWill owner, int ticksSinceCreation, IMindAccess info) {
		return false;
	}

	/**
	 * Called after every tick and before deleting the thought to get child
	 * thoughts. Each should be paired with a boolean indicating whether or not it
	 * is focused.
	 */
	public Collection<Pair<? extends IThought, Boolean>> popChildThoughts();

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
		/** A thought that brings perceived information into the mind */
		PERCEPTION,
		/** A thought bringing up a past occurrence */
		REMEMBER,
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
