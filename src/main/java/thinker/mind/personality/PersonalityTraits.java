package thinker.mind.personality;

import java.util.function.Function;

import things.form.channelsystems.IChannelNeed;
import thinker.mind.emotions.Affect;
import thinker.mind.emotions.Emotion;
import thinker.mind.perception.sensation.Sensation;

/** Typical basic personality traits */
public class PersonalityTraits {

	/** A trait representing the default satisfaction level of an individual */
	public static final MultiplyFactorPersonalityTrait PEACE = new MultiplyFactorPersonalityTrait("peace", null,
			Affect.SATISFACTION, 1f, Affect.SATISFACTION.defaultValue());

	/** A trait representing the default wonder level of an individual */
	public static final MultiplyFactorPersonalityTrait INQUISITIVITY = new MultiplyFactorPersonalityTrait(
			"inquisitivity", null, Affect.WONDER, 1f, Affect.WONDER.defaultValue());

	/** A trait representing the default agression level of an individual */
	public static final MultiplyFactorPersonalityTrait INNER_RAGE = new MultiplyFactorPersonalityTrait("inner_rage",
			null, Affect.AGGRESSION, 1f, Affect.AGGRESSION.defaultValue());

	/** A trait representing the default focus level of an individual */
	public static final MultiplyFactorPersonalityTrait FOCUSEDNESS = new MultiplyFactorPersonalityTrait("focusedness",
			null, Affect.FOCUS, 1f, Affect.FOCUS.defaultValue());

	/** A trait representing the default self-preservation level of an individual */
	public static final MultiplyFactorPersonalityTrait PARANOIA = new MultiplyFactorPersonalityTrait("paranoia", null,
			Affect.SELF_PRESERVATION, 1f, Affect.SELF_PRESERVATION.defaultValue());

	/** A trait representing the default vigor level of an individual */
	public static final MultiplyFactorPersonalityTrait ENERGY = new MultiplyFactorPersonalityTrait("energy", null,
			Affect.VIGOR, 1f, Affect.VIGOR.defaultValue());

	/**
	 * How much pain decreases focus. Can be a negative value, i.e. pain INCREASES_VALUE
	 * focus.
	 */
	public static final MultiplyFactorPersonalityTrait PAIN_DISTRACTION = new MultiplyFactorPersonalityTrait(
			"pain_distraction", Sensation.PAIN, Affect.FOCUS, -0.5f, 0.5f, -1f, 1f).setOppositeName("pain_focus");

	/** Pain causing suffering */
	public static final MultiplyFactorPersonalityTrait PAIN_SENSITIVITY = new MultiplyFactorPersonalityTrait(
			"pain_sensitivity", Sensation.PAIN, Affect.SUFFERING, 1f, 0.5f);

	/** Heat causing satisfaction */
	public static final MultiplyFactorPersonalityTrait HEAT_PREFERENCE = new MultiplyFactorPersonalityTrait(
			"heat_preference", Sensation.HEAT, Affect.SATISFACTION, 0.5f, 0.1f, -1f, 1f)
					.setOppositeName("heat_aversion");

	/** Suffering causing aggression */
	public static final MultiplyFactorPersonalityTrait RETALIATION = new MultiplyFactorPersonalityTrait("retaliation",
			Affect.SUFFERING, Affect.AGGRESSION, 1f, 0.1f, -1f, 1f).setOppositeName("placation");

	/**
	 * Suffering causing a lowering of vigor. On the other end, suffering can
	 * increase vigor since this trait can be negative
	 */
	public static final MultiplyFactorPersonalityTrait DEMORALIZATION = new MultiplyFactorPersonalityTrait(
			"demoralization", Affect.SUFFERING, Affect.VIGOR, 2f, 0.5f, -1f, 1f).setOppositeName("moralization");

	/** Pain causing an increase in survival instinct */
	public static final MultiplyFactorPersonalityTrait WIMPINESS = new MultiplyFactorPersonalityTrait("wimpiness",
			Sensation.PAIN, Affect.SELF_PRESERVATION, 1.5f, 0.5f);

	/** Grief causing an increase in aggression */
	public static final MultiplyFactorPersonalityTrait GRIEF_ANGER = new MultiplyFactorPersonalityTrait("grief_anger",
			Emotion.GRIEF, Affect.AGGRESSION, 1f, 0.1f);

	/** Frustration causing an increase in aggression */
	public static final MultiplyFactorPersonalityTrait IRRITATION = new MultiplyFactorPersonalityTrait("irritation",
			Emotion.GRIEF, Affect.AGGRESSION, 1f, 0.1f);

	/** Annoyance causing an increase in aggression */
	public static final MultiplyFactorPersonalityTrait IRRITABILITY = new MultiplyFactorPersonalityTrait("irritability",
			Emotion.GRIEF, Affect.AGGRESSION, 1f, 0.1f);

	/** Embarrassment causing an increase in suffering */
	public static final MultiplyFactorPersonalityTrait SHAME = new MultiplyFactorPersonalityTrait("shame",
			Emotion.EMBARRASSMENT, Affect.SUFFERING, 1f, 0.1f);

	/**
	 * A generator to make a trait that determines how much hunger causes an
	 * increase in aggression
	 */
	public static final Function<IChannelNeed, MultiplyFactorPersonalityTrait> HANGRINESS_GEN = (
			need) -> new MultiplyFactorPersonalityTrait("hangriness", need, Affect.AGGRESSION, 2f, 0.1f);

}
