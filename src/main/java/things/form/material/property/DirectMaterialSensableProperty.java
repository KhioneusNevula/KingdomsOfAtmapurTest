package things.form.material.property;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import things.form.IPart;
import things.form.sensing.sensors.ISensor;
import things.form.visage.ISensableProperty;
import things.form.visage.IVisage;

/** A sensable property derived directly from a material property */
public class DirectMaterialSensableProperty<E> implements ISensableProperty<E> {

	// public static final DirectMaterialSensableProperty<Color> COLOR = new
	// DirectMaterialSensableProperty<>(MaterialProperty.COLOR, null, null);

	private IMaterialProperty<E> from;
	private E defVal;
	private Set<ISensor> sensors;
	private boolean requiresFocus;

	public DirectMaterialSensableProperty(IMaterialProperty<E> from, E defVal, Collection<ISensor> sensors) {
		this.from = from;
		this.defVal = defVal;
		this.sensors = ImmutableSet.copyOf(sensors);
		this.sensors.forEach((a) -> a.registerSensableProperty(this));
	}

	public DirectMaterialSensableProperty<E> setRequiresFocus(boolean requiresFocus) {
		this.requiresFocus = requiresFocus;
		return this;
	}

	@Override
	public boolean requiresFocus() {
		return requiresFocus;
	}

	@Override
	public String getPropertyName() {
		return "sensable_" + from;
	}

	@Override
	public E defaultValue() {
		return defVal;
	}

	@Override
	public Class<? super E> getType() {
		return from.getType();
	}

	@Override
	public E getPropertyFromPart(IPart fromPart) {
		return fromPart.getMaterial().getProperty(from);
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public int[] getComplexScore(IPart part, IVisage<?> visage) {
		throw new UnsupportedOperationException();
	}

}
