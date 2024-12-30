package things.physical_form.components;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public enum PartAbility implements IPartAbility {
	/** indicates a part can heal */
	HEAL(false),
	/** indicates a part allows for walking */
	WALK(true, FloatPartStats.WALK_SPEED),
	/** indicates a part allows for grasping */
	GRASP(true, FloatPartStats.GRASP_STRENGTH);

	private Set<IPartStat<?>> stats;
	private boolean controllable;

	private PartAbility(boolean controllable, IPartStat<?>... stats) {
		this.stats = ImmutableSet.copyOf(stats);
		this.controllable = controllable;
	}

	@Override
	public Collection<IPartStat<?>> getStats() {
		return stats;
	}

	@Override
	public boolean controllable() {
		return controllable;
	}

	@Override
	public boolean sensor() {
		return false;
	}

}
