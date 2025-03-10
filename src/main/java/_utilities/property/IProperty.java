package _utilities.property;

import java.util.function.Supplier;

/**
 * Some kind of property
 * 
 * @author borah
 *
 * @param <E>
 */
public interface IProperty<E> {

	public static <E> PropertyImpl<E> make(String name, Class<? super E> clazz, E defVal) {
		return new PropertyImpl<>(name, clazz, () -> defVal);
	}

	public static <E> PropertyImpl<E> make(String name, Class<? super E> clazz, Supplier<E> defVal) {
		return new PropertyImpl<E>(name, clazz, defVal);
	}

	/**
	 * return the name of this property
	 * 
	 * @return
	 */
	public String getPropertyName();

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
	public Class<? super E> getType();

}
