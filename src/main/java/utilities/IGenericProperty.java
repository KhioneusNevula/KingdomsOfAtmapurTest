package utilities;

import java.util.function.Supplier;

/**
 * Some kind of property
 * 
 * @author borah
 *
 * @param <E>
 */
public interface IGenericProperty<E> {

	public static <E> GenericProperty<E> make(String name, Class<E> clazz, E defVal) {
		return new GenericProperty<>(name, clazz, () -> defVal);
	}

	public static <E> GenericProperty<E> make(String name, Class<E> clazz, Supplier<E> defVal) {
		return new GenericProperty<E>(name, clazz, defVal);
	}

	/**
	 * return the name of this property
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
	 * The type of this property
	 * 
	 * @return
	 */
	public Class<E> getType();

}
