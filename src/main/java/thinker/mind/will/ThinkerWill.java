package thinker.mind.will;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;

import metaphysics.soul.ISoul;
import thinker.actions.searching.ActionFinder;
import thinker.actions.searching.ActionPicker;
import thinker.actions.searching.IActionFinder;
import thinker.actions.searching.IActionPicker;
import thinker.actions.searching.IProfileFinder;
import thinker.actions.searching.ProfileFinder;
import thinker.actions.searching.RelationMutability;
import thinker.mind.emotions.Affect;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.thoughts.IThought;
import thinker.mind.will.thoughts.IThought.ThoughtType;

public class ThinkerWill implements IThinkerWill {

	private float baseMindStrainChance;
	private int focusedCap;
	private Multimap<UUID, IThought> thoughts;
	private List<Consumer<ISoul>> queuedMindActions;
	private Map<IThought, Integer> thoughtTimes;
	private Multimap<ThoughtType, IThought> thoughtsByType;
	private Set<IThought> focused;
	private IActionFinder afinder;
	private IActionPicker apicker;
	private IProfileFinder pfinder;

	public ThinkerWill(float baseMindStrainChance, int focusedCap, List<RelationMutability> relmut) {
		this.focusedCap = focusedCap;
		this.baseMindStrainChance = baseMindStrainChance;
		this.thoughts = Multimaps.synchronizedMultimap(MultimapBuilder.hashKeys().hashSetValues().build());
		this.thoughtTimes = Collections.synchronizedMap(new HashMap<>());
		this.focused = Collections.synchronizedSet(new HashSet<>());
		this.thoughtsByType = Multimaps.synchronizedMultimap(MultimapBuilder.hashKeys().hashSetValues().build());
		this.queuedMindActions = Collections.synchronizedList(new ArrayList<>());
		this.afinder = new ActionFinder();
		this.apicker = new ActionPicker();
		this.pfinder = new ProfileFinder(relmut);
	}

	@Override
	public int focusedThoughtsCap() {
		return focusedCap;
	}

	@Override
	public float getMindStrainChance() {
		int overflow = Math.max(0, focused.size() - focusedCap);

		return (float) (1 - (1 - baseMindStrainChance) * Math.pow(0.7, overflow));
	}

	@Override
	public int getNumberOfFocusedThoughts() {
		return focused.size();
	}

	@Override
	public boolean isFocused(IThought thought) {
		return focused.contains(thought);
	}

	@Override
	public IActionFinder getActionFinder() {
		return afinder;
	}

	@Override
	public IActionPicker getActionPicker() {
		return apicker;
	}

	@Override
	public Collection<IThought> getThoughts() {
		return thoughts.values();
	}

	@Override
	public Collection<IThought> getThoughtsByProcess(UUID id) {
		return thoughts.get(id);
	}

	protected void queueAction(Runnable action) {
		this.queuedMindActions.add((f) -> action.run());
	}

	protected void queueAction(Consumer<ISoul> action) {
		this.queuedMindActions.add(action);
	}

	/**
	 * To clarify, this is equivalent to
	 * {@link IThinkerWill#addThought(IThought, boolean)}, but it queues the adding
	 * action
	 */
	@Override
	public void addThought(IThought thought, boolean forceFocus) {
		this.queueAction((soul) -> {
			thoughts.put(thought.getProcessID(), thought);
			thoughtTimes.put(thought, 0);
			thoughtsByType.put(thought.getThoughtType(), thought);
			if (forceFocus)
				focused.add(thought);
			else
				focused.remove(thought);

		});
	}

	@Override
	public void removeThought(IThought thought) {
		this.queueAction(() -> {
			thoughtTimes.remove(thought);
			focused.remove(thought);
			thoughtsByType.remove(thought.getThoughtType(), thought);
			thoughts.remove(thought.getProcessID(), thought);
		});
	}

	@Override
	public Collection<IThought> getThoughtsOfType(ThoughtType type) {
		return thoughtsByType.get(type);
	}

	@Override
	public void removeAllThoughts(ThoughtType ofType) {
		this.queueAction(() -> {
			Collection<IThought> col = thoughtsByType.removeAll(ofType);
			thoughts.values().removeAll(col);
			thoughtTimes.keySet().removeAll(col);
			focused.removeAll(col);
		});
	}

	@Override
	public void willTick(IBeingAccess info) {
		ISoul inSpirit = info.maybeSoul().orElseThrow(() -> new UnsupportedOperationException(
				"Cannot call willTick in " + this + " from a non-soul " + info.being()));

		List<Consumer<ISoul>> queued = null;
		synchronized (queuedMindActions) {
			queued = new ArrayList<>(queuedMindActions);
			queuedMindActions.clear();
		}
		for (Consumer<ISoul> run : queued) {
			run.accept(inSpirit);
		}

		float focus = inSpirit.getKnowledge().getFeeling(Affect.FOCUS); // amount of focus (as per the relevant affect)
		float mindStrainChance = this.getMindStrainChance();
		mindStrainChance = 0.99f - (1 - mindStrainChance) * focus; // focus affects the mind strain chance

		for (IThought tat : thoughts.values()) {
			if (thoughtTimes.get(tat) == null) {
				throw new IllegalStateException("Unclear exception");
			}
			if (tat.forceFocus(this, thoughtTimes.get(tat), info)) {
				this.focused.add(tat);
			}
			if (tat.forceSubconscious(this, thoughtTimes.get(tat), info)) {
				this.focused.remove(tat);
			}
		}

		Set<IThought> shouldDelete = new HashSet<>();
		Set<IThought> shouldDeleteI = new HashSet<>();
		for (IThought tat : focused) { // tick focused thoughts
			tat.tickThoughtActively(this, thoughtTimes.get(tat), info);
			if (info.gameMap().random() < mindStrainChance) {
				shouldDeleteI.add(tat);
			} else if (tat.shouldDelete(this, thoughtTimes.get(tat), info)) {
				shouldDelete.add(tat);
			}
		}
		for (IThought tat : thoughts.values()) { // tick subconscious thoughts
			if (focused.contains(tat))
				continue;
			tat.tickThoughtPassively(this, thoughtTimes.get(tat), info);
			if (tat.shouldDelete(this, thoughtTimes.get(tat), info)) {
				shouldDelete.add(tat);
			}
		}

		for (IThought tat : thoughts.values()) { // tick thought times up, get children of thoughts
			thoughtTimes.put(tat, thoughtTimes.getOrDefault(tat, 0) + 1);
			if (tat.hasChildThoughts()) {
				Collection<Entry<IThought, Boolean>> children = tat.popChildThoughts();
				children.forEach((pair) -> this.addThought(pair.getKey(), pair.getValue()));
			}
		}

		for (IThought tat : shouldDeleteI) { // interrupted thoughts (focus lost)
			tat.aboutToDelete(this, thoughtTimes.get(tat), true, info);
		}
		for (IThought tat : shouldDelete) { // regular thoughts (naturally ended)
			tat.aboutToDelete(this, thoughtTimes.get(tat), false, info);
		}
		for (IThought tat : Iterables.concat(shouldDelete, shouldDeleteI)) {
			this.removeThought(tat);
		}

	}

	@Override
	public IProfileFinder getProfileFinder() {
		return this.pfinder;
	}

	@Override
	public String toString() {
		return "ThinkerWill{fcap=" + this.focusedCap + ",strainChance=" + this.getMindStrainChance() + ",thoughts={"
				+ this.thoughtTimes.keySet().stream().filter((x) -> !focused.contains(x)).reduce("",
						(s, t) -> s + "," + t, (x, x2) -> x + x2)
				+ "},focused=" + this.focused + "}";
	}

}
