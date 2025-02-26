package _sim.plane;

/**
 * A plane of existence; used to see or touch and determine what is interactable
 * 
 * @author borah
 *
 */
public interface IPlane {

	/**
	 * The unique prime number representing this plane
	 * 
	 * @return
	 */
	public int getPrime();

	/**
	 * The string name representing this plane
	 * 
	 * @return
	 */
	public String getName();
}
