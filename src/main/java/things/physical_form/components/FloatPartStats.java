package things.physical_form.components;

import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

public enum FloatPartStats implements IPartStat<Float> {
	WALK_SPEED(0f, false), GRASP_STRENGTH(0f, true);

	private Function<IComponentPart, Float> defaVal;
	private Function<Iterable<Float>, Float> aggregator;

	/**
	 * default summing or averaging (based on boolean)
	 * 
	 * @param dval
	 */
	private FloatPartStats(float dval, boolean avg) {
		this((a) -> dval,
				!avg ? (a) -> Streams.stream(a).collect(Collectors.summingDouble((x) -> x.doubleValue())).floatValue()
						: (a) -> {
							float su = 0;
							int c = 0;
							for (float f : a) {
								su += f;
								c++;
							}
							return su / c;
						});
	}

	private FloatPartStats(float dVal, Function<Iterable<Float>, Float> aggregator) {
		this((a) -> dVal, aggregator);
	}

	private FloatPartStats(Function<IComponentPart, Float> defaVal, Function<Iterable<Float>, Float> aggregator) {
		this.defaVal = defaVal;
		this.aggregator = aggregator;
	}

	@Override
	public Float getDefaultValue(IComponentPart part) {
		return defaVal.apply(part);
	}

	@Override
	public Class<Float> getType() {
		return float.class;
	}

	@Override
	public Float aggregate(Iterable<Float> values) {
		return aggregator.apply(values);
	}

}
