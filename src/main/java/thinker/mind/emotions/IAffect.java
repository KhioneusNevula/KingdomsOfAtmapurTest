package thinker.mind.emotions;

/**
 * One of the fundamental motivations
 * 
 * @author borah
 *
 */
public interface IAffect extends IFeeling {

	@Override
	default boolean isAffect() {
		return true;
	}

	@Override
	default boolean isEmotion() {
		return false;
	}
}
