package things.form.material.property;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import things.form.IPart;
import things.form.sensing.sensors.ISensor;
import things.form.shape.property.IShapeProperty;
import things.form.visage.ISensableProperty;
import things.form.visage.IVisage;

/** A sensable property derived directly from a shape property */
public class DirectShapeSensableProperty<E> implements ISensableProperty<E> {

	private IShapeProperty<E> from;
	private E defVal;
	private Set<ISensor> sensors;
	private boolean requiresFocus;

	public DirectShapeSensableProperty(IShapeProperty<E> from, E defVal, Collection<ISensor> sensors) {
		this.from = from;
		this.defVal = defVal;
		this.sensors = ImmutableSet.copyOf(sensors);
		this.sensors.forEach((a) -> a.registerSensableProperty(this));
	}

	@Override
	public String getPropertyName() {
		return "sensable_" + from;
	}

	public DirectShapeSensableProperty<E> setRequiresFocus(boolean requiresFocus) {
		this.requiresFocus = requiresFocus;
		return this;
	}

	@Override
	public boolean requiresFocus() {
		return requiresFocus;
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
		return fromPart.getShape().getProperty(from);
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
