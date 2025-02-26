package things.form.channelsystems;

import things.biology.genes.IGenome;
import things.form.material.IMaterial;
import things.form.material.condition.IMaterialCondition;
import things.form.material.property.MaterialProperty;

/**
 * A resource considered as a genetic material
 * 
 * @author borah
 *
 */
public class GeneticMaterialResource implements IGeneticMaterialResource {

	private String name;
	private IMaterial base;

	public GeneticMaterialResource(String name, IMaterial baseMaterial) {
		this.name = name;
		this.base = baseMaterial;
	}

	/**
	 * The base type for this genetic material
	 * 
	 * @return
	 */
	public IMaterialCondition getMaterialBase() {
		return base;
	}

	@Override
	public IMaterial createMaterialFromGenome(IGenome genome) {
		return IMaterial.copyBuilder(base).prop(MaterialProperty.GENETICS, genome).build();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Float getEmptyValue() {
		return IMaterial.getEmptyValueStatic();
	}

	@Override
	public Float getMaxValue() {
		return IMaterial.getMaxValueStatic();
	}

	@Override
	public Float add(Float instance, Float instance2) {
		return IMaterial.addStatic(instance, instance2);
	}

	@Override
	public Float subtract(Float g, Float l) {
		return IMaterial.subtractStatic(g, l);
	}

	@Override
	public Class<Float> getMeasureClass() {
		return IMaterial.getMeasureClassStatic();
	}

	@Override
	public int hashCode() {
		return name.hashCode() + base.hashCode() + this.getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeneticMaterialResource rs) {
			return this.name.equals(rs.name()) && this.base.equals(rs.base);
		}
		return super.equals(obj);
	}

}
