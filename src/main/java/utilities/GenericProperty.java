package utilities;

import java.util.function.Supplier;

/**
 * Implementation of a generic property
 * 
 * @author borah
 *
 * @param <E>
 */
public class GenericProperty<E> implements IGenericProperty<E> {

	private String name;
	private Class<E> type;
	private Supplier<E> defaultSupplier;

	GenericProperty(String name, Class<E> clazz, Supplier<E> defaultVal) {
		this.name = name;
		this.type = clazz;
		this.defaultSupplier = defaultVal;
	}

	public String name() {
		return name;
	}

	@Override
	public Class<E> getType() {
		return type;
	}

	@Override
	public E defaultValue() {
		return defaultSupplier.get();
	}

	public Supplier<E> getDefaultSupplier() {
		return defaultSupplier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IGenericProperty mat) {
			return this.name.equals(mat.name()) && this.type.equals(mat.getType());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + type.hashCode();
	}

	@Override
	public String toString() {
		return "<" + name + ">";
	}

}
