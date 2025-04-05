package things.form.sensing.sensors;

import java.util.Collection;
import java.util.Map;

import _utilities.MathUtils;
import _utilities.property.IProperty;
import things.actor.IActor;
import things.form.IPart;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.form.visage.ISensableProperty;
import things.form.visage.IVisage;
import thinker.knowledge.IKnowledgeMedium;
import thinker.mind.perception.IPerceptor;

public interface ISensor extends IPartAbility, IPerceptor {

	/**
	 * The stat representing what planes can be sensed
	 */
	public static final IPartStat<Integer> SENSOR_PLANES = new IPartStat<Integer>() {

		@Override
		public String name() {
			return "sensor_planes";
		}

		@Override
		public Class<Integer> getType() {
			return int.class;
		}

		@Override
		public Integer getDefaultValue(IComponentPart part) {
			return 1;
		}

		@Override
		public Integer aggregate(Iterable<Integer> values) {
			return MathUtils.primeUnion(values);
		}

		@Override
		public Integer extract(Integer val1, Integer subVal, int count) {
			return MathUtils.primeSetDifference(val1, subVal);
		}
	};

	public static final StandardSightSensor SIGHT_SENSOR = new StandardSightSensor("sight");

	@Override
	default PerceptorType getPerceptorType() {
		return PerceptorType.SENSOR;
	}

	/**
	 * set of possible properties this sense can sense
	 */
	public Collection<ISensableProperty<?>> getSensablePropertiesCollection();

	/**
	 * add a new possible properties this sense can sense and return self; used
	 * during initialization
	 */
	public ISensor registerSensableProperty(ISensableProperty<?> p);

	/**
	 * Return any linguistic utterance that is sensable directly from this component
	 * part, e.g. a sentence being spoken by a mouth or a text written on a book. If
	 * isFocused is false, this sensor might not return anything
	 */
	public Collection<IKnowledgeMedium> senseKnowledge(IPart part, IComponentPart sensorPart, boolean isFocused);

	/**
	 * Return the values of sensable properties for a visage part based on one's own
	 * sensing parts. Properties migth be omitted if isFocused is false
	 */
	public Map<ISensableProperty<?>, Object> senseProperties(IPart part, IComponentPart sensorPart, boolean isFocused);

	/**
	 * Return what perceptible properties (location, distance, direction) this
	 * sensor can sense
	 */
	public Collection<IProperty<?>> getPerceptibleProperties(IComponentPart sensor);

	/**
	 * Return all sensed perceptible properties (location, distance, direction) of
	 * the given Visage. Put a partial visage as the argument.
	 */
	public Map<IProperty<?>, Object> getPerceptibleProperties(IVisage<?> forVis, IComponentPart sensor);

	/**
	 * Return a partial visage representing all parts of the given visage which are
	 * sensable by this sensor.
	 * 
	 * @return
	 */
	public <T extends IPart> IVisage<T> sensableParts(IVisage<T> sensing, IActor sensingActor, IComponentPart sensor);

	/**
	 * Get the max distance this sensor can sense, or INFINITY if it can sense
	 * infinitely far.
	 */
	public float getMaxDistance(IComponentPart sensor);

	@Override
	default boolean sensor() {
		return true;
	}

}
