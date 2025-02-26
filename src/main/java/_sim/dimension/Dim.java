package _sim.dimension;

public enum Dim implements IDimensionTag {
	EARTH, AFTERLIFE;

	@Override
	public String getId() {
		return "base_" + this.name().toLowerCase();
	}

}
