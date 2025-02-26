package things.form.channelsystems.energy;

import things.form.channelsystems.IResource;

/**
 * A Signal resource is just a boolean value indicating presence or absence of
 * signal.
 * 
 * @author borah
 *
 */
public class EnergyChannelResource implements IResource<Float> {

	private String name;
	private float max;

	public EnergyChannelResource(String name, float max) {
		this.name = name;
		this.max = max;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Float getEmptyValue() {
		return 0f;
	}

	@Override
	public Float getMaxValue() {
		return max;
	}

	@Override
	public Class<Float> getMeasureClass() {
		return float.class;
	}

	@Override
	public Float add(Float instance, Float instance2) {
		return instance + instance2;
	}

	@Override
	public Float subtract(Float g, Float l) {
		return g - l;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnergyChannelResource rs) {
			return this.name.equals(rs.name());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + this.getClass().hashCode();
	}

}
