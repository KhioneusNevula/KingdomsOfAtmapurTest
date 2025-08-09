package thinker.mind.will.thoughts.perception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import _utilities.couplets.Pair;
import thinker.mind.perception.IPerception;
import thinker.mind.perception.PerceptorLevel;
import thinker.mind.perception.sensation.ISensation;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.IThinkerWill;
import thinker.mind.will.thoughts.IThought;

public class SensationPerceptionThought extends PerceptionThought {

	private int lifetime;
	private int initLVal; // value of lifetime on initialization
	private boolean duplicate;
	private boolean ischild;
	private List<SensationPerceptionThought> childs = Collections.emptyList();

	@Override
	public ISensation getPerceptor() {
		return (ISensation) perceptor;
	}

	/**
	 * 
	 * @param processID
	 * @param fromPerceptor
	 * @param lifetime      how long this thought should last
	 * @param duplicate     whether the thought should make duplicates of itself
	 */
	public SensationPerceptionThought(UUID processID, ISensation fromPerceptor, int lifetime, boolean duplicate) {
		super(processID, fromPerceptor);
		this.lifetime = lifetime;
		this.initLVal = lifetime;
		this.duplicate = duplicate;
		this.ischild = !duplicate;
	}

	@Override
	public boolean shouldDelete(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		return ticksSinceCreation >= lifetime;
	}

	@Override
	public void aboutToDelete(IThinkerWill owner, int ticksSinceCreation, boolean interrupted, IBeingAccess info) {
		super.aboutToDelete(owner, ticksSinceCreation, interrupted, info);

	}

	@Override
	protected void updateKnowledgeFromPerceptor(IThinkerWill owner, int ticksSinceCreation, boolean focused,
			IBeingAccess info) {
		IPerception perception = info.maybeSpirit()
				.orElseThrow(() -> new UnsupportedOperationException(
						"Cannot have thoughts about perceptions from something without perceptios..." + info))
				.getPerception();
		float sensationAmount = perception.getSensation(getPerceptor());
		PerceptorLevel needLevel = PerceptorLevel.fromAmount(sensationAmount);
		if (duplicate) {

			this.childs = new ArrayList<>();
			for (int i = 0; i < needLevel.getThreshold() * owner.focusedThoughtsCap(); i++) {
				childs.add(new SensationPerceptionThought(getProcessID(), getPerceptor(), this.lifetime + i, false));
			}
			this.duplicate = false;

		}
		if (!ischild) {

			info.being().getPersonality().changeAffects(info.being().getKnowledge(), getPerceptor(), sensationAmount,
					this.initLVal, true);
		}

	}

	@Override
	public boolean hasChildThoughts() {
		return !childs.isEmpty();
	}

	@Override
	public Collection<Map.Entry<IThought, Boolean>> popChildThoughts() {
		List<Map.Entry<IThought, Boolean>> childs2 = childs.stream()
				.<Pair<IThought, Boolean>>map((c) -> Pair.of(c, true)).collect(Collectors.toList());
		childs = Collections.emptyList();
		return childs2;
	}

	@Override
	public boolean forceFocus(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(id=" + this.getProcessID().toString().substring(0, 5) + "..."
				+ (ischild ? "type=child" : "type=parent") + "){" + this.perceptor + ",lifetime=" + this.lifetime + "}";
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode() + this.perceptor.hashCode() + initLVal + (ischild ? 3 : 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof SensationPerceptionThought spt) {
			return this.initLVal == spt.initLVal && this.ischild == spt.ischild && this.perceptor.equals(spt.perceptor);
		}
		return false;
	}

}
