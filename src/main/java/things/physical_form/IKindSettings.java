package things.physical_form;

import java.util.Collection;

import utilities.IProperty;

/**
 * An interface that functions as settings for generating an object based on a
 * kind. This includes things like genetics (e.g. hair color), as well as simply
 * settings such as making a rock that is blue. For example, a RockKind may
 * accept KindSettings which designate the color, or something.
 * 
 * @author borah
 *
 */
public interface IKindSettings {

	/**
	 * Get a specific property
	 * 
	 * @param <E>
	 * @param property
	 * @return
	 */
	public <E> E getSetting(IProperty<E> property);

	/**
	 * Get all properties
	 * 
	 * @return
	 */
	public Collection<? extends IProperty<?>> getProperties();

}
