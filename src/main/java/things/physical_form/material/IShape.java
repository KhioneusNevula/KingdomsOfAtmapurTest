package things.physical_form.material;

public interface IShape {
	/**
	 * Get a property of this material; return default value if the material has no
	 * value stored
	 * 
	 * @param <E>
	 * @param property
	 * @return
	 */
	public <E> E getProperty(IShapeProperty<E> property);

	/**
	 * Get the name of this material
	 * 
	 * @return
	 */
	public String name();
}
