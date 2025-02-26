package thinker.mind.emotions;

/**
 * The container of an individual's feelings
 * 
 * @author borah
 *
 */
public interface IFeelings {

	/**
	 * Return the value of some affect
	 * 
	 * @param affect
	 * @return
	 */
	public float affectValue(IAffect affect);

	/**
	 * Return the value of some emotion
	 * 
	 * @param emotion
	 * @return
	 */
	public float emotionValue(IEmotion emotion);

	/**
	 * Change the value of this affect, clamping it at min or max
	 * 
	 * @param affect
	 * @param delta
	 */
	public void changeAffectValue(IAffect affect, float delta);

	/**
	 * Change the value of this emotion, clamping it at min or max
	 * 
	 * @param affect
	 * @param delta
	 */
	public void changeEmotionValue(IEmotion affect, float delta);

	/**
	 * Set the value of this affect
	 * 
	 * @param affect
	 * @param val
	 */
	public void setAffectValue(IAffect affect, float val);

	/**
	 * Set the value of this emotion
	 * 
	 * @param affect
	 * @param val
	 */
	public void setEmotionValue(IEmotion affect, float val);
}
