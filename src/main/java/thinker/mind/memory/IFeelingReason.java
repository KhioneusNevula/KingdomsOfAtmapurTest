package thinker.mind.memory;

/**
 * A superinterface to delineate anything that can be used as a reason for a
 * specific feeling
 */
public interface IFeelingReason {

	/** Returns the type of reason for a feeling this is */
	public FeelingReasonType getReasonType();

	public static enum FeelingReasonType {
		/** This indicates that the reason for this feeling is a sensation */
		SENSATION,
		/** This indicates the reason for this feeling is some need */
		NEED,
		/** This indicates the reason for this feeling is something that is known */
		KNOWLEDGE,
		/**
		 * This indicates the reason for this feeling is a goal (fulfilled or
		 * unfulfilled)
		 */
		GOAL,
		/** This indicates the reason for this feeling is another feeling */
		FEELING,
		/**
		 * This indicates the reason for this feeling is unidentifiable (e.g. a spell)
		 */
		OTHER
	}
}
