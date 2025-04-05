package things.form.material;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import things.form.channelsystems.IResource;
import things.form.kinds.settings.IKindSettings;
import things.form.material.condition.IMaterialCondition;
import things.form.material.generator.IMaterialGeneratorResource;
import things.form.material.generator.PropertyMapperMaterialGeneratorResource.MaterialGeneratorBuilder;
import things.form.material.property.IMaterialProperty;
import things.form.material.property.MaterialProperty;
import things.form.material.property.Phase;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.stains.IStain;

public interface IMaterial extends IResource<Float>, IMaterialCondition, IMaterialGeneratorResource {

	/**
	 * Material representing the lack of a material, e.g. for a hole
	 */
	public static final IMaterial NONE = new IMaterial() {

		@Override
		public String name() {
			return "no-material";
		}

		@Override
		public <E> E getProperty(IMaterialProperty<E> property) {
			if (property == MaterialProperty.PHASE) {
				return (E) Phase.OTHER;
			}
			return property.getDefaultValue(this);
		}

		@Override
		public Collection<IMaterialProperty<?>> getDistinctProperties() {
			return Collections.emptySet();
		}

		@Override
		public void stainTick(IComponentPart onPart, IStain stainInstance, ISoma parentForm, long ticks) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IMaterial generateMaterialFromSettings(IKindSettings genome) {
			return this;
		}

		@Override
		public boolean isGenerator() {
			return false;
		}

		@Override
		public boolean isBasisOf(IMaterial other) {
			return other == this;
		}
	};

	/**
	 * Returns all properties this material has set for itself
	 * 
	 * @return
	 */
	public Collection<IMaterialProperty<?>> getDistinctProperties();

	/**
	 * Get a property of this material; return default value if the material has no
	 * value stored
	 * 
	 * @param <E>
	 * @param property
	 * @return
	 */
	public <E> E getProperty(IMaterialProperty<E> property);

	/**
	 * Get the name of this material
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Called every tick this material exists as a stain. Queue a removal if the
	 * stain should be removed.
	 * 
	 * @param onPart
	 * @param form
	 * @param ticks
	 */
	public void stainTick(IComponentPart onPart, IStain stainInstance, ISoma parentForm, long ticks);

	@Override
	default IMaterial getMaterialBase() {
		return this;
	}

	/**
	 * If this is the "None" material
	 * 
	 * @return
	 */
	public default boolean isNothing() {
		return this == NONE;
	}

	/**
	 * Static version of {@link IResource#getEmptyValue()}
	 * 
	 * @return
	 */
	public static Float getEmptyValueStatic() {
		return 0f;
	}

	@Override
	default Float getEmptyValue() {
		return getEmptyValueStatic();
	}

	/**
	 * Static version of {@link IResource#getMaxValue()}
	 * 
	 * @return
	 */
	public static Float getMaxValueStatic() {
		return 1f;
	}

	@Override
	default Float getMaxValue() {
		return getMaxValueStatic();
	}

	/**
	 * Static version of {@link IResource#getMeasureClass())}
	 * 
	 * @return
	 */
	public static Class<Float> getMeasureClassStatic() {
		return float.class;
	}

	@Override
	default Class<Float> getMeasureClass() {
		return getMeasureClassStatic();
	}

	/**
	 * Static version of {@link IResource#add(Comparable, Comparable)}
	 * 
	 * @return
	 */
	public static Float addStatic(Float instance, Float instance2) {
		return instance + instance2;
	}

	@Override
	default Float add(Float instance, Float instance2) {
		return addStatic(instance, instance2);
	}

	/**
	 * Static version of {@link IResource#subtract(Comparable, Comparable)}
	 * 
	 * @return
	 */
	public static Float subtractStatic(Float instance, Float instance2) {
		return instance - instance2;
	}

	/**
	 * Static version of {@link IResource#divide(Comparable, int)}
	 * 
	 * @return
	 */
	public static Float divideStatic(Float instance, int instance2) {
		return instance / instance2;
	}

	@Override
	default Float subtract(Float g, Float l) {
		return subtractStatic(g, l);
	}

	@Override
	default Float divide(Float g, int by) {
		return divideStatic(g, by);
	}

	@Override
	default boolean acceptsMaterial(IMaterial mat) {
		return this.equals(mat);
	}

	@Override
	default <E> Collection<E> getAllowedValues(IMaterialProperty<E> forProp) {
		return Collections.singleton(this.getProperty(forProp));
	}

	@Override
	default Collection<IMaterialProperty<?>> getCheckedProperties() {
		return this.getDistinctProperties();
	}

	public static class MaterialBuilder {

		private Material innerInstance;

		private MaterialBuilder(String name) {
			innerInstance = new Material(name);
		}

		private MaterialBuilder(IMaterial copyOf) {
			innerInstance = new Material(copyOf.name());
			for (IMaterialProperty<?> pro : copyOf.getDistinctProperties()) {
				innerInstance.properties.put(pro, copyOf.getProperty(pro));
			}
		}

		public <E> MaterialBuilder prop(IMaterialProperty<E> prop, E val) {
			innerInstance.properties.put(prop, val);
			return this;
		}

		/** Deletes the property indicated */
		public MaterialBuilder delProp(IMaterialProperty<?> prop) {
			innerInstance.properties.remove(prop);
			return this;
		}

		public MaterialBuilder prop(Map<? extends IMaterialProperty<?>, ?> props) {
			innerInstance.properties.putAll(props);
			return this;
		}

		public MaterialBuilder prop(Map.Entry<? extends IMaterialProperty<?>, ?>... pairs) {
			for (Map.Entry<? extends IMaterialProperty<?>, ?> entry : pairs) {
				innerInstance.properties.put(entry.getKey(), entry.getValue());
			}
			return this;
		}

		public Material build() {
			return innerInstance;
		}

		/**
		 * Returns a builder to turn this material into a generator rather than a pure
		 * material
		 */
		public MaterialGeneratorBuilder generator() {
			return IMaterialGeneratorResource.builder(innerInstance, "generator_" + innerInstance.name);
		}

		/**
		 * Returns a builder to turn this material into a (genetic-based,
		 * i.e.{@link IMaterialGeneratorResource#geneticEncodedMaterial}) generator
		 * rather than a pure material
		 */
		public MaterialGeneratorBuilder geneticGenerator() {
			return IMaterialGeneratorResource.geneticEncodedMaterial(innerInstance, "generator_" + innerInstance.name);
		}
	}

	/**
	 * a builder for a material
	 * 
	 * @param name
	 * @return
	 */
	public static MaterialBuilder builder(String name) {
		return new MaterialBuilder(name);
	}

	/**
	 * A builder for a material based on a copy of the given material
	 * 
	 * @param from
	 * @return
	 */
	public static MaterialBuilder copyBuilder(IMaterial from) {
		return new MaterialBuilder(from);
	}

}
