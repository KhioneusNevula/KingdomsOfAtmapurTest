package things.form.material.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import _utilities.property.IProperty;
import things.biology.kinds.OrganicKindProperties;
import things.form.kinds.settings.IKindSettings;
import things.form.material.IMaterial;
import things.form.material.IMaterial.MaterialBuilder;
import things.form.material.property.IMaterialProperty;
import things.form.material.property.MaterialProperty;

/**
 * A material generator that simply directly maps specific properties of
 * {@link IKindSettings} to specific Material properties, generating off of a
 * base material
 */
public class PropertyMapperMaterialGeneratorResource implements IMaterialGeneratorResource {

	public static class MaterialGeneratorBuilder {

		private PropertyMapperMaterialGeneratorResource pmmgr;

		MaterialGeneratorBuilder(PropertyMapperMaterialGeneratorResource imgr) {
			this.pmmgr = new PropertyMapperMaterialGeneratorResource(imgr.base, imgr.name);
			this.pmmgr.mapper.putAll(imgr.mapper);
		}

		MaterialGeneratorBuilder(IMaterial baseMaterial, String name) {
			this.pmmgr = new PropertyMapperMaterialGeneratorResource(baseMaterial, name);
		}

		/** Associate a material property with a settings property */
		public <T> MaterialGeneratorBuilder addMapping(IMaterialProperty<T> materialProp, IProperty<T> settingsProp) {
			this.pmmgr.mapper.put(materialProp, settingsProp);
			return this;
		}

		public PropertyMapperMaterialGeneratorResource buildGenerator() {
			return pmmgr;
		}

	}

	protected Map<IMaterialProperty<?>, IProperty<?>> mapper = new HashMap<>();
	private IMaterial base;
	private String name;

	private PropertyMapperMaterialGeneratorResource(IMaterial baseMaterial, String name) {
		this.base = baseMaterial;
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Float getEmptyValue() {
		return base.getEmptyValue();
	}

	@Override
	public Float getMaxValue() {
		return base.getMaxValue();
	}

	@Override
	public Class<Float> getMeasureClass() {
		return base.getMeasureClass();
	}

	@Override
	public Float add(Float instance, Float instance2) {
		return base.add(instance, instance2);
	}

	@Override
	public Float subtract(Float g, Float l) {
		return base.subtract(g, l);
	}

	@Override
	public Float divide(Float g, int by) {
		return base.divide(g, by);
	}

	@Override
	public IMaterial getMaterialBase() {
		return base;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IMaterial generateMaterialFromSettings(IKindSettings settings) {
		MaterialBuilder builder = IMaterial.copyBuilder(base);
		for (Entry<IMaterialProperty<?>, IProperty<?>> entry : mapper.entrySet()) {
			builder.prop((IMaterialProperty) entry.getKey(), settings.getSetting(entry.getValue()));
		}
		return builder.build();
	}

	@Override
	public boolean isGenerator() {
		return true;
	}

	@Override
	public boolean isBasisOf(IMaterial other) {
		if (base.equals(other))
			return true;
		MaterialBuilder cb = IMaterial.copyBuilder(other);
		for (IMaterialProperty<?> mp : mapper.keySet()) {
			cb = cb.delProp(mp);
		}
		return base.equals(cb.build());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof PropertyMapperMaterialGeneratorResource pmmgr) {
			return this.name.equals(pmmgr.name) && this.base.equals(pmmgr.base) && this.mapper.equals(pmmgr.mapper);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.base.hashCode() + this.mapper.hashCode();
	}

	@Override
	public String toString() {
		return "[:{" + this.name + "}:]";
	}

}
