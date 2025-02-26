package things.status_effect;

/**
 * Basic status effects
 * 
 * @author borah
 *
 */
public enum BasicStatusEffect implements IPartStatusEffect {
	/**
	 * Status effect of sleep, which is used to put a mind in a mode where it prunes
	 * memories and creates dreams and stuff like that
	 */
	SLEEP,
	/**
	 * Status effect of unconsciousness, used to put a mind in a mode where it does
	 * nothing, but also does not properly prune memories or generate dreams
	 * necessarily
	 */
	UNCONSCIOUS,
	/**
	 * Status effect of death, used to indicate a part should no longer be
	 * functional to have a spirit
	 */
	DEAD,
	/**
	 * Status effect of delusion, put in a brain-type part to indicate that the
	 * brain has an incorrect view of reality
	 */
	DELUDED
}
