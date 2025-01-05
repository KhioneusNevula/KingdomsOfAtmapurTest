package things.form.sensing;

import java.util.Collection;
import java.util.Map;

import things.actor.IActor;
import things.form.IPart;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.form.visage.ISensableProperty;
import things.form.visage.IVisage;
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

		@Override
		public Integer extract(Integer val1, Integer subVal, int count) {
			return MathUtils.primeSetDifference(val1, subVal);
		}
	};

	/**
	 * Return the values of sensable properties for a visage part, taking relevant
	 * stats into account
	 */
	public Map<ISensableProperty<?>, Object> senseProperties(IPart part, Collection<IPartStat<?>> relevantStats);

	/**
	 * Return all parts of a visage which are sensable by this sensor; empty set if
	 * none are
	 * 
	 * @return
	 */
	public Collection<IPart> sensableParts(IVisage<?> sensing, IActor sensingActor);

	@Override
	default boolean sensor() {
		return true;
	}

}
