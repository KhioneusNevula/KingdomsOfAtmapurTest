package sim.physicality;

/**
 * Interactibility modes are used to both indicate what class of things
 * something is interactable with/visible to, and also for seers or interactors
 * to indicate whether they can interact with something. Basically, if the
 * Interactability number of a visage can be divided by any of the
 * interactability numbers of a seer, it is visible to it.
 * 
 * @author borah
 *
 */
public interface IInteractability {

	/**
	 * The prime number used to index the visibility/interactibility of this mode,
	 * so it can be multiplied with others
	 * 
	 * @return
	 */
	int primeFactor();

	String getName();

	/**
	 * Return a number combining these physicalities. literally just multiplication.
	 * 
	 * @param ins
	 * @return
	 */
	public static int combine(IInteractability... ins) {
		int prod = 1;
		for (IInteractability in : ins)
			prod *= in.primeFactor();
		return prod;
	}

	/**
	 * If the interactor's given interactabilities can interact with the int factor
	 * given
	 * 
	 * @param factor
	 * @param interactabilities
	 * @return
	 */
	public static boolean interactibleWith(int interactedTo, IInteractability... interactabilities) {
		for (IInteractability in : interactabilities) {
			if (interactedTo % in.primeFactor() == 0) {
				return true;
			}
		}
		return false;
	}

}