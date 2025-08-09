package thinker.mind.perception.sensation;

import java.util.Map;

import com.google.common.collect.Streams;

import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import thinker.concepts.IConcept;
import thinker.concepts.application.IConceptApplier;
import thinker.concepts.application.IConceptAssociationInfo;
import thinker.concepts.application.ISensationApplier;
import thinker.mind.emotions.IAffect;
import thinker.mind.memory.IFeelingReason;
import thinker.mind.perception.IPerceptor;

/**
 * An {@link ISensation} is something experienced physiologically. It is, of
 * course, a {@link IPerceptor}.
 */
public interface ISensation extends IPerceptor, IConceptAssociationInfo, IFeelingReason {

	@Override
	default FeelingReasonType getReasonType() {
		return FeelingReasonType.SENSATION;
	}

	@Override
	default ConceptType getConceptType() {
		return ConceptType.C_ASSOCIATION_INFO;
	}

	@Override
	default String getUnderlyingName() {
		return "sensation_applier_" + this.getName();
	}

	/**
	 * A SensitivtyStat is a stat that indicates how sensitive to pain something is.
	 * 1f is the standard sensitivity, while anything abvoe that is extra
	 * sensitivity and anything below that is deficient sensitivity
	 * 
	 * @author borah
	 *
	 */
	public static class SensitivityStat implements IPartStat<Float> {

		private ISensation fs;

		protected SensitivityStat(ISensation forSensation) {
			this.fs = forSensation;
		}

		public ISensation getAssociatedSensation() {
			return fs;
		}

		@Override
		public String name() {
			return fs.getName() + "_SENSITIVITY";
		}

		@Override
		public Float getDefaultValue(IComponentPart part) {
			return 1f;
		}

		@Override
		public Class<? super Float> getType() {
			return float.class;
		}

		@Override
		public Float aggregate(Iterable<Float> values) {
			return (float) Streams.stream(values).mapToDouble(Float::doubleValue).average().getAsDouble();
		}

		@Override
		public Float extract(Float val1, Float subVal, int count) {
			float sumval = val1 * count;
			float newval = sumval - subVal;
			newval /= (count - 1);
			return newval;
		}

	}

	/** If this sensation is liked/preferred */
	public boolean isPreferred();

	/** If this sensation is disliked/disfavored */
	public boolean isDisfavored();

	/** Name of this sensation */
	public String getName();

	/**
	 * The sensitivity stat ({@link SensitivityStat}) associated with this sensation
	 * (i.e. how sensitive somethign is to this sensation)
	 */
	public SensitivityStat getSensitivityStat();

	@Override
	default PerceptorType getPerceptorType() {
		return PerceptorType.SENSATION;
	}
}
