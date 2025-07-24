package thinker.mind.will.thoughts;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import metaphysics.soul.ISoul;
import things.form.soma.component.IComponentPart;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IMemoryConcept;
import thinker.concepts.general_types.IProcessConcept;
import thinker.concepts.general_types.ProcessConcept;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.memory.StorageType;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.IThinkerWill;

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
	 * Creates a process concept in memory to store info pertaining to this action,
	 * and links it to Focus
	 * 
	 * @param mem
	 * @return
	 */
	public default IProcessConcept createProcessInMemory(IKnowledgeBase mem) {
		ProcessConcept process = new ProcessConcept(getProcessID());

		mem.learnConcept(process, StorageType.TEMPORARY);
		mem.addTemporaryRelation(process, KnowledgeRelationType.QUICKLY_ACCESSIBLE_TO, IConcept.FOCUS);

		return process;
	}

	/**
	 * Puts this memory in the knowledge base and also links it to the appropriate
	 * memory concept; sets storage type to Temporary.
	 * 
	 * @param memory
	 * @param inBase
	 */
	public default void createMemory(IMemoryConcept memory, IKnowledgeBase inBase) {
		inBase.learnConcept(memory, StorageType.TEMPORARY);
		inBase.addTemporaryRelation(memory, PropertyRelationType.OF_PRINCIPLE, IConcept.RECOLLECTION);
	}

	/**
	 * Removes a process from memory
	 * 
	 * @param mem
	 */
	public default void forgetProcessFromMemory(IKnowledgeBase mem) {
		ProcessConcept process = new ProcessConcept(getProcessID());
		mem.forgetConcept(process);
	}

	/**
	 * Creates a dummy process concept ot access the process n memory
	 * 
	 * @return
	 */
	public default IProcessConcept dummyProcessInMemory() {
		return new ProcessConcept(getProcessID());
	}

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
	public void tickThoughtActively(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info);

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
	public void tickThoughtPassively(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info);

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
	public boolean shouldDelete(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info);

	/**
	 * Run if either {@link #shouldDelete(IThinkerWill, int, ISoul, IComponentPart, long)}
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
	public void aboutToDelete(IThinkerWill owner, int ticksSinceCreation, boolean interrupted, IBeingAccess info);

	/**
	 * Called after every tick and before deleting the thought to check if the
	 * thought has any child thoughts to produce
	 */
	public boolean hasChildThoughts();

	/**
	 * Whether this thought should force itself to be focused in the current
	 * tick.Checked before the first tick.
	 */
	public default boolean forceFocus(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		return false;
	}

	/**
	 * Whether this thought should force itself to be removed from focus in the
	 * current tick. Checked before the first tick.
	 */
	public default boolean forceSubconscious(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		return false;
	}

	/**
	 * Called after every tick and before deleting the thought to get child
	 * thoughts. Each should be paired with a boolean indicating whether or not it
	 * is focused.
	 */
	public Collection<Map.Entry<IThought, Boolean>> popChildThoughts();

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
		/** A thought that is evaluating conditions and goals */
		CHECK,
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
