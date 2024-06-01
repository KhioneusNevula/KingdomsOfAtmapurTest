package sim.interfaces;

import java.util.UUID;

public interface IUnique extends Comparable<IUnique> {

	public UUID getUUID();

	@Override
	default int compareTo(IUnique o) {
		return getUUID().compareTo(o.getUUID());
	}

	default String getUnitString() {
		return "unit";
	}

	default String getSimpleName() {
		return this.toString();
	}

}
