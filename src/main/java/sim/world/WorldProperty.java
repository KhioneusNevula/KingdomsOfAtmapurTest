package sim.world;

import java.util.function.Supplier;

public class WorldProperty<E> implements IWorldProperty<E> {

	public static <E> WorldProperty<E> prop(String name, Class<E> type, Supplier<E> defaultVal) {
		return new WorldProperty<>(name, type, defaultVal);
	}

	public static <E> WorldProperty<E> prop(String name, Class<E> type, E defaultVal) {
		return new WorldProperty<>(name, type, () -> defaultVal);
	}

	public static <E> WorldProperty<E> prop(String name, E defaultVal) {
		return new WorldProperty<>(name, (Class<E>) defaultVal.getClass(), () -> defaultVal);
	}

	/**
	 * Acceleration of gravity in a world
	 */
	public static final WorldProperty<Float> GRAVITY = prop("gravity", 10f);

	private String name;
	private Class<E> type;
	private Supplier<E> defaultValue;

	private WorldProperty(String name, Class<E> type, Supplier<E> defaultVal) {
		this.type = type;
		this.name = name;
		this.defaultValue = defaultVal;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public E defaultValue() {
		return defaultValue.get();
	}

	@Override
	public Class<E> getType() {
		return type;
	}

}
