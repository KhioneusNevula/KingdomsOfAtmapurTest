package things.form.kinds.settings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import _utilities.property.IProperty;

public class KindSettings implements IKindSettings {

	public static class SettingsBuilder {
		private boolean closed;
		private KindSettings settings;

		private SettingsBuilder(IKindSettings dup) {
			settings = new KindSettings();
			settings.properties = new HashMap<>();
			dup.getProperties().forEach((pr) -> settings.properties.put(pr, dup.getSetting(pr)));
		}

		private SettingsBuilder() {
			settings = new KindSettings();
			settings.properties = new HashMap<>();
		}

		public <E> SettingsBuilder prop(IProperty<E> p, E o) {
			if (closed)
				throw new IllegalStateException();
			settings.properties.put(p, o);
			return this;
		}

		public SettingsBuilder prop(Map<IProperty<?>, Object> map) {
			if (closed)
				throw new IllegalStateException();
			settings.properties.putAll(map);
			return this;
		}

		public KindSettings build() {
			this.closed = true;
			settings.properties = ImmutableMap.copyOf(settings.properties);
			return this.settings;
		}

	}

	public static SettingsBuilder copyBuilder(IKindSettings from) {
		return new SettingsBuilder(from);
	}

	public static SettingsBuilder builder() {
		return new SettingsBuilder();
	}

	private Map<IProperty<?>, Object> properties;

	private KindSettings() {
	}

	@Override
	public <E> E getSetting(IProperty<E> property) {
		return (E) properties.getOrDefault(property, property.defaultValue());
	}

	@Override
	public Collection<? extends IProperty<?>> getProperties() {
		return properties.keySet();
	}
}
