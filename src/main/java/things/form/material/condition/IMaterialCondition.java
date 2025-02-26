package things.form.material.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import things.form.material.IMaterial;
import things.form.material.property.IMaterialProperty;
import things.form.material.property.MaterialProperty;

/**
 * A condition for what kind of materials may satisfy a condition, which can
 * check equality of properties to determine whether something is permissible
 * 
 * @author borah
 *
 */
public interface IMaterialCondition {

	/** The default condition consisting of organic materials */
	public static final IMaterialCondition ORGANIC = IMaterialCondition.builder().prop(MaterialProperty.ORGANIC, true)
			.build();

	/** The default condition consisting of meat */
	public static final IMaterialCondition ONLY_MEAT = IMaterialCondition.builder().prop(MaterialProperty.MEAT, true)
			.build();

	/** The default condition consisting of only plants */
	public static final IMaterialCondition ONLY_PLANT = IMaterialCondition.builder().prop(MaterialProperty.PLANT, true)
			.build();

	/** Both plants and meat */
	public static final IMaterialCondition PLANT_AND_MEAT = ONLY_PLANT.conditionCopyBuilder().combineWith(ONLY_MEAT).build();

	/**
	 * Whether this eating condition allows the given material
	 * 
	 * @param mat
	 * @return
	 */
	public boolean acceptsMaterial(IMaterial mat);

	/**
	 * Returns all properties this condition checks
	 * 
	 * @return
	 */
	public Collection<IMaterialProperty<?>> getCheckedProperties();

	/**
	 * Returns the collection of allwoed values for each property
	 * 
	 * @param <E>
	 * @param forProp
	 * @return
	 */
	public <E> Collection<E> getAllowedValues(IMaterialProperty<E> forProp);

	/**
	 * Return a builder to make an eating condition
	 * 
	 * @return
	 */
	public static ConditionBuilder builder() {
		return new ConditionBuilder();
	}

	/**
	 * Return a builder to make an eating condition as a copy of this condition
	 * 
	 * @return
	 */
	public default ConditionBuilder conditionCopyBuilder() {
		return new ConditionBuilder(this);
	}

	/**
	 * Creates a condition builder prefilled with a given material's properties
	 * 
	 * @param material
	 * @return
	 */
	public default ConditionBuilder fromMaterial(IMaterial material) {
		ConditionBuilder builder = new ConditionBuilder();
		for (IMaterialProperty prop : material.getDistinctProperties()) {
			builder.prop(prop, Collections.singleton(material.getProperty(prop)));
		}
		return builder;
	}

	public static class ConditionBuilder {
		MaterialCondition internal;
		private boolean closed = false;

		public ConditionBuilder(IMaterialCondition con) {
			internal = new MaterialCondition();
			for (IMaterialProperty<?> pro : con.getCheckedProperties()) {
				internal.propertiesCollectioned.putAll(pro, con.getAllowedValues(pro));
			}
		}

		public ConditionBuilder() {
			internal = new MaterialCondition();
		}

		public <E> ConditionBuilder prop(IMaterialProperty<E> prop, Iterable<E> vals) {
			if (closed)
				throw new UnsupportedOperationException();
			internal.propertiesCollectioned.putAll(prop, vals);
			return this;
		}

		public <E> ConditionBuilder prop(IMaterialProperty<E> prop, E... vals) {
			return this.prop(prop, (Iterable<E>) () -> Arrays.stream(vals).iterator());
		}

		public ConditionBuilder combineWith(IMaterialCondition other) {
			if (closed)
				throw new UnsupportedOperationException();
			for (IMaterialProperty<?> pro : other.getCheckedProperties()) {
				internal.propertiesCollectioned.putAll(pro, other.getAllowedValues(pro));
			}
			return this;
		}

		public IMaterialCondition build() {
			closed = true;
			return internal;
		}
	}
}
