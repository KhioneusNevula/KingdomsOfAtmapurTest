package things.physical_form.kinds;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import things.physical_form.IKindSettings;
import utilities.IGenericProperty;

public class KindSettings implements IKindSettings {

	public static class SettingsBuilder {
		private boolean closed;
		private KindSettings settings;

		private SettingsBuilder() {
			settings = new KindSettings();
			settings.properties = new HashMap<>();
		}

		public <E> SettingsBuilder prop(IGenericProperty<E> p, E o) {
			if (closed)
				throw new IllegalStateException();
			settings.properties.put(p, o);
			return this;
		}

		public SettingsBuilder prop(Map<IGenericProperty<?>, Object> map) {
			if (closed)
				throw new IllegalStateException();
			settings.properties.putAll(map);
			return this;
		}

		public KindSettings build() {
			this.closed = true;
			return this.settings;
		}

	}

	public static SettingsBuilder builder() {
		return new SettingsBuilder();
	}

	private Map<IGenericProperty<?>, Object> properties;

	private KindSettings() {
	}

	@Override
	public <E> E getSetting(IGenericProperty<E> property) {
		return (E) properties.getOrDefault(property, property.defaultValue());
	}

	@Override
	public Collection<? extends IGenericProperty<?>> getProperties() {
		return properties.keySet();
	}

}
