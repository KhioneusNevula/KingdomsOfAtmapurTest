package things.physical_form.components;

import java.util.Collection;
import java.util.Map;

import things.interfaces.IActor;
import things.physical_form.IVisage;
import things.physical_form.visage.ISensableProperty;
import things.physical_form.visage.IVisagePart;
import utilities.MathUtils;

public interface ISensor extends IPartAbility {

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
	};

	/**
	 * Return the values of sensable properties for a visage part, taking relevant
	 * stats into account
	 */
	public Map<ISensableProperty<?>, Object> senseProperties(IVisagePart part, Collection<IPartStat<?>> relevantStats);

	/**
	 * Return all parts of a visage which are sensable by this sensor; empty set if
	 * none are
	 * 
	 * @return
	 */
	public Collection<IVisagePart> sensableParts(IVisage sensing, IActor sensingActor);

	@Override
	default boolean sensor() {
		return true;
	}

}
