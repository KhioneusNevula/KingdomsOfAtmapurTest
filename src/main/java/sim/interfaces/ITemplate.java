package sim.interfaces;

public interface ITemplate {

	public String name();

	/**
	 * For treemap sorting
	 * 
	 * @return
	 */
	public String getUniqueName();

	/**
	 * The average unusualness of any distinctive example of this template
	 * 
	 * @return
	 */
	public float averageUniqueness();

}
