package things.form.soma.abilities;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import things.form.soma.stats.FloatPartStats;
import things.form.soma.stats.IPartStat;

public enum PartAbility implements IPartAbility {
	/** indicates a part can heal */
	HEAL(false),
	/** indicates a part allows for motion */
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
