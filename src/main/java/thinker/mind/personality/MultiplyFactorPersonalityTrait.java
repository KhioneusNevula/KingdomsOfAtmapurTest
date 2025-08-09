package thinker.mind.personality;

import java.util.Arrays;

import thinker.mind.emotions.IAffect;
import thinker.mind.memory.IFeelingReason;

/**
 * A personality trait which outputs a value that is directly a multiplication
 * of this personality trait, which is then multiplied by the perceptor level.
 * can also be a default-value trait, if reason=null
 * 
 * @author borah
 *
 */
public class MultiplyFactorPersonalityTrait implements IPersonalityTrait {

	private IFeelingReason reason;
	private IAffect affect;
	private float defaultVal = 0f;
	private float minVal = 0f;
	private float maxVal = 1f;
	private float factor;
	private String name;
	private String oppositename;

	/**
	 * 
	 * @param name
	 * @param reason if this is null, it is a default value trait
	 * @param affect
	 * @param factor
	 * @param ovals  in order, these are: defaultVal, minVal, maxVal
	 */
	public MultiplyFactorPersonalityTrait(String name, IFeelingReason reason, IAffect affect, float factor,
			float... ovals) {
		this.name = name;
		this.oppositename = name + "(opposite)";
		this.reason = reason;
		this.affect = affect;
		this.factor = factor;
		if (ovals.length > 0) {
			this.defaultVal = ovals[0];
		}
		if (ovals.length > 1) {
			this.minVal = ovals[1];
		}
		if (ovals.length > 2) {
			this.maxVal = ovals[2];
		}
		if (ovals.length > 3)
			throw new IllegalArgumentException(Arrays.toString(ovals));
	}

	public MultiplyFactorPersonalityTrait setOppositeName(String oppositename) {
		this.oppositename = oppositename;
		return this;
	}

	@Override
	public float getMax() {
		return maxVal;
	}

	@Override
	public float getMin() {
		return minVal;
	}

	@Override
	public String getPropertyName() {
		return name;
	}

	@Override
	public String getOppositeName() {
		return this.oppositename;
	}

	@Override
	public Float defaultValue() {
		return this.defaultVal;
	}

	@Override
	public Class<? super Float> getType() {
		return float.class;
	}

	@Override
	public IFeelingReason getReason() {
		return this.reason;
	}

	@Override
	public IAffect getRelevantAffect() {
		return this.affect;
	}

	@Override
	public float getAffectFactor(float traitLevel, float perceptorLevel) {

		return this.factor * traitLevel * perceptorLevel;
	}

	@Override
	public float getAffectDefault(float traitLevel) {
		if (this.isDefault()) {
			return this.factor * traitLevel;
		}
		return IPersonalityTrait.super.getAffectDefault(traitLevel);
	}

	@Override
	public String toString() {
		return this.name;
	}

}
