package sim.world;

public interface IWorldProperty<E> {

	/**
	 * Name of this world-property
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Return the default value of this property
	 * 
	 * @return
	 */
	public E defaultValue();

	/**
	 * The type of this property's value
	 * 
	 * @return
	 */
	public Class<E> getType();
}
