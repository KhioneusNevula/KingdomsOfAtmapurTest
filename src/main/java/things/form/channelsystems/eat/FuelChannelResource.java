package things.form.channelsystems.eat;

import things.form.channelsystems.IResource;
import things.form.material.IMaterial;
import things.form.material.condition.IMaterialCondition;

/**
 * A Food resource is just nutrition interpreted as a resource
 * 
 * @author borah
 *
 */
public class FuelChannelResource implements IResource<Float> {

	private String name;
	private float max;
	private IMaterialCondition allowedFood;

	public FuelChannelResource(String name, float max, IMaterialCondition allowedFood) {
		this.name = name;
		this.max = max;
		this.allowedFood = allowedFood;
	}

	public boolean isAllowedFood(IMaterial material) {
		return allowedFood.acceptsMaterial(material);
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
	public Float divide(Float g, int by) {
		return g / by;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FuelChannelResource rs) {
			return this.name.equals(rs.name()) && this.allowedFood.equals(rs.allowedFood);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + this.allowedFood.hashCode() + this.getClass().hashCode();
	}

}
