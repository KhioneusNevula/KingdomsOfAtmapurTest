package _utilities.property;

import java.util.function.Supplier;

/**
 * Implementation of a generic property
 * 
 * @author borah
 *
 * @param <E>
 */
public class PropertyImpl<E> implements IProperty<E> {

	private String name;
	private Class<? super E> type;
	private Supplier<E> defaultSupplier;

	PropertyImpl(String name, Class<? super E> clazz, Supplier<E> defaultVal) {
		this.name = name;
		this.type = clazz;
		this.defaultSupplier = defaultVal;
	}

	public String getPropertyName() {
		return name;
	}

	@Override
	public Class<? super E> getType() {
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
		if (obj instanceof IProperty mat) {
			return this.name.equals(mat.getPropertyName()) && this.type.equals(mat.getType());
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
