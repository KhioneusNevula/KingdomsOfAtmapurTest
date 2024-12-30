package things.physical_form;

import java.util.Collection;

import things.physical_form.channelsystems.IChannelSystem;
import utilities.IGenericProperty;

/**
 * An interface that functions as settings for generating an object based on a
 * kind. This includes things like genetics (e.g. hair color), as well as simply
 * settings such as making a blue rock
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
	public <E> E getSetting(IGenericProperty<E> property);

	/**
	 * Get all properties
	 * 
	 * @return
	 */
	public Collection<? extends IGenericProperty<?>> getProperties();

	/**
	 * Get the channel systems included in thi Kind
	 * 
	 * @return
	 */
	public Collection<? extends IChannelSystem> getChannelSystems();
}
