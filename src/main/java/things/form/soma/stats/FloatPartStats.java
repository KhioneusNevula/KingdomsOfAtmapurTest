package things.form.soma.stats;

import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import things.form.soma.component.IComponentPart;
import utilities.function.TriFunction;

public enum FloatPartStats implements IPartStat<Float> {
	WALK_SPEED(0f, false), GRASP_STRENGTH(0f, true);

	private Function<IComponentPart, Float> defaVal;
	private Function<Iterable<Float>, Float> aggregator;
	private TriFunction<Float, Float, Integer, Float> extractor;

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
						},
				!avg ? (a, b, count) -> a - b : (a, b, count) -> {
					float sum = a * count;
					sum -= b;
					return sum / (count - 1f);
				});
	}

	private FloatPartStats(float dVal, Function<Iterable<Float>, Float> aggregator,
			TriFunction<Float, Float, Integer, Float> extractor) {
		this((a) -> dVal, aggregator, extractor);
	}

	private FloatPartStats(Function<IComponentPart, Float> defaVal, Function<Iterable<Float>, Float> aggregator,
			TriFunction<Float, Float, Integer, Float> extractor) {
		this.defaVal = defaVal;
		this.aggregator = aggregator;
		this.extractor = extractor;
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

	@Override
	public Float extract(Float val1, Float subVal, int count) {
		return extractor.apply(val1, subVal, count);
	}

}
