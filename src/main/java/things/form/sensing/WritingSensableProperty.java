package things.form.sensing;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import things.form.IPart;
import things.form.sensing.sensors.ISensor;
import things.form.visage.ISensableProperty;
import things.form.visage.IVisage;

/**
 * A sensable property which can convey something sensable, but more
 * importantly, is used to convey text
 */
public class WritingSensableProperty implements ISensableProperty<Boolean> {

	private String name;
	private Set<ISensor> sensors;
	private boolean requiresFocus;

	public WritingSensableProperty(String name, Set<ISensor> sensors, boolean requiresFocus) {
		this.name = name;
		this.sensors = ImmutableSet.copyOf(sensors);
		this.sensors.forEach((a) -> a.registerSensableProperty(this));
		this.requiresFocus = requiresFocus;
	}

	@Override
	public boolean requiresFocus() {
		return requiresFocus;
	}

	@Override
	public String getPropertyName() {
		return name;
	}

	@Override
	public Boolean defaultValue() {
		return false;
	}

	@Override
	public Class<? super Boolean> getType() {
		return boolean.class;
	}

	@Override
	public Boolean getPropertyFromPart(IPart fromPart) {
		return fromPart.readKnowledge(this) != null;
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public int[] getComplexScore(IPart part, IVisage<?> visage) {
		return null;
	}

}
