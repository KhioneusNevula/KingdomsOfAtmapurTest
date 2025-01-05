package things.form.material;

import things.form.channelsystems.IChannelResource;
import things.form.material.property.IMaterialProperty;
import things.form.material.property.MaterialProperty;
import things.form.material.property.Phase;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.stains.IStain;

public interface IMaterial extends IChannelResource<Float> {

	/**
	 * Material representing the lack of a material, e.g. for a hole
	 */
	public static final IMaterial NONE = new IMaterial() {

		@Override
		public String name() {
			return "_none";
		}

		@Override
		public <E> E getProperty(IMaterialProperty<E> property) {
			if (property == MaterialProperty.PHASE) {
				return (E) Phase.OTHER;
			}
			return property.getDefaultValue(this);
		}

		@Override
		public void stainTick(IComponentPart onPart, IStain stainInstance, ISoma parentForm, long ticks) {

		}
	};

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
	 * Called every tick this material exists as a stain
	 * 
	 * @param onPart
	 * @param form
	 * @param ticks
	 */
	public void stainTick(IComponentPart onPart, IStain stainInstance, ISoma parentForm, long ticks);

	/**
	 * If this is the "None" material
	 * 
	 * @return
	 */
	public default boolean isNothing() {
		return this == NONE;
	}

	@Override
	default Float getEmptyValue() {
		return 0f;
	}

	@Override
	default Class<Float> getMeasureClass() {
		return float.class;
	}

	@Override
	default Float add(Float instance, Float instance2) {
		return instance + instance2;
	}

	@Override
	default Float subtract(Float g, Float l) {
		return g - l;
	}
}
