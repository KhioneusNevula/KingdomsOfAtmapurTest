package thinker.mind.will.thoughts.perception;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import thinker.mind.perception.IPerceptor;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.IThinkerWill;
import thinker.mind.will.thoughts.IThought;

/**
 * A kind of thought which updates the standard mind with info from the
 * Perception. This thought typically remains subconscious and vanishes after
 * being created.
 */
public abstract class PerceptionThought implements IThought {

	private UUID pID;
	protected IPerceptor perceptor;

	public PerceptionThought(UUID processID, IPerceptor fromPerceptor) {
		this.pID = processID;
		this.perceptor = fromPerceptor;
	}

	@Override
	public UUID getProcessID() {
		return pID;
	}

	public IPerceptor getPerceptor() {
		return perceptor;
	}

	@Override
	public boolean forceSubconscious(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		return true;
	}

	/**
	 * Called every tick; used to update the knowledge of this mind based on what
	 * the perceptor sees.
	 */
	protected abstract void updateKnowledgeFromPerceptor(IThinkerWill owner, int ticksSinceCreation, boolean focused,
			IBeingAccess info);

	@Override
	public void tickThoughtActively(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		this.updateKnowledgeFromPerceptor(owner, ticksSinceCreation, true, info);
	}

	@Override
	public void tickThoughtPassively(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		this.updateKnowledgeFromPerceptor(owner, ticksSinceCreation, false, info);
	}

	@Override
	public boolean shouldDelete(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {

		return true;
	}

	@Override
	public void aboutToDelete(IThinkerWill owner, int ticksSinceCreation, boolean interrupted, IBeingAccess info) {

	}

	@Override
	public boolean hasChildThoughts() {

		return false;
	}

	@Override
	public Collection<Map.Entry<IThought, Boolean>> popChildThoughts() {
		return Collections.emptySet();
	}

	@Override
	public ThoughtType getThoughtType() {
		return ThoughtType.PERCEPTION;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PerceptionThought pt) {
			return this.getClass().equals(pt.getClass()) && pID.equals(pt.pID) && perceptor.equals(pt.perceptor);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode() + pID.hashCode() + perceptor.hashCode();
	}

}
