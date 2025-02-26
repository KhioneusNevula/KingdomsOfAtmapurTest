package things.form.sensing;

import java.util.Collection;
import java.util.Set;

import things.form.soma.stats.IPartStat;

public abstract class AbstractSensor implements ISensor {

	private String name;
	private Set<IPartStat<?>> stats;

	public AbstractSensor(String name, IPartStat<?>... requiredStats) {
		this.name = name;
		this.stats = Set.of(requiredStats);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<IPartStat<?>> getStats() {
		return stats;
	}

	@Override
	public boolean controllable() {
		return true;
	}

}
