package things.form.kinds.settings;

import java.util.Collection;
import java.util.Collections;

import utilities.property.IProperty;

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
	 * A KindSettings with no properties
	 */
	public static final IKindSettings NONE = new IKindSettings() {

		@Override
		public <E> E getSetting(IProperty<E> property) {
			return property.defaultValue();
		}

		@Override
		public Collection<? extends IProperty<?>> getProperties() {
			return Collections.emptySet();
		}
	};

	/**
	 * Get a specific property
	 * 
	 * @param <E>
	 * @param property
	 * @return
	 */
	public <E> E getSetting(IProperty<E> property);

	/**
	 * Return the value of this property, or the default value if this prpoperty is
	 * not set
	 * 
	 * @param <E>
	 * @param property
	 * @param defaultVal
	 * @return
	 */
	public default <E> E getOrDefault(IProperty<E> property, E defaultVal) {
		if (!getProperties().contains(property)) {
			return defaultVal;
		}
		return this.getSetting(property);
	}

	/**
	 * Get all properties
	 * 
	 * @return
	 */
	public Collection<? extends IProperty<?>> getProperties();

}
