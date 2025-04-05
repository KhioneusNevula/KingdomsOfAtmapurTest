package party.relations.social_bonds;

public enum SocialBondTrait implements ISocialBondTrait {
	/**
	 * how likely it is this individual/group would delegate a task to the
	 * individual on the other end; how likely it is this individual would go to the
	 * individual on the other end for a resource; Can increase by various things,
	 * including the other individual’s acts that complete goals of this individual
	 */
	RELIANCE(0, 0, 1),
	/**
	 * how likely this individual/group is to believe something the other individual
	 * says as true, or believe their promises. Can increase by various things
	 * including things that increase Reliance and Safety
	 */
	TRUST(0.5f, 0, 1),
	/**
	 * How much this individual’s anxiety decreases around the other; alternatively,
	 * if negative, how much anxiety increases around them
	 */
	PEACE(0, -1, 1),
	/**
	 * how likely this individual is to go to the other for safety; alternatively,
	 * how likely this individual is to avoid the other individual
	 */
	SAFETY(0, -1, 1),
	/**
	 * how likely it is that this individual or group would protect the other
	 * individual; Can increase by things that increase Trust, Attraction, Reliance,
	 * Safety, and Submission
	 */
	CARING(0, 0, 1),
	/**
	 * how likely it is that this individual would hurt or harm the other
	 * individual; can increase by things that decrease trust,
	 */
	AGGRESSION(0, 0, 1),
	/**
	 * how likely this individual/group is to do something the other end individual
	 * commands; Can increase by various things, including gestures that increase
	 * fear, or reliance; Negatively, how likely this individual or group is to
	 * assume the other end will do things for them when commanded; Can increase
	 * along with contempt, or more nuancedly
	 */
	SUBMISSION(0, -1, 1),
	/**
	 * how attracted this individual is to the other individual; how likely they are
	 * to feel feelings of attraction toward them; Can increase for various reasons
	 */
	ATTRACTION(0, 0, 1),
	/**
	 * how disgusted they are by the other individual; how likely they are to feel
	 * disgust toward them; Can increase for various reasons
	 */
	DISGUST(0, 0, 1),
	/**
	 * ranging from how happy this individual is in the other’s presence; Also
	 * controls whether the individual thinks positive thoughts about the other
	 */
	LIKE(0, 0, 1),
	/**
	 * how unhappy they become in the other’s presence; or how likely they are to
	 * have negative thoughts
	 */
	DISLIKE(0, 0, 1),
	/**
	 * how likely this individual is to take nice actions toward the other end; Can
	 * increase by things that increase like, submission, trust, and reliance
	 */
	RESPECT(0, 0, 1),
	/**
	 * how likely this individual is to be rude and mean to the other end; Can
	 * increase by things that increase disgust, dislike, prejudice, or more nuanced
	 */
	CONTEMPT(0, 0, 1);

	private float def;
	private float min;
	private float max;

	private SocialBondTrait(float defaultVal, float min, float max) {
		this.def = defaultVal;
		this.min = min;
		this.max = max;
	}

	@Override
	public Float defaultValue() {
		return def;
	}

	@Override
	public Class<? super Float> getType() {
		return float.class;
	}

	@Override
	public float getMax() {
		return max;
	}

	@Override
	public float getMin() {
		return min;
	}

	@Override
	public String getPropertyName() {
		return "sbt_" + name();
	}

}
