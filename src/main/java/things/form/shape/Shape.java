package things.form.shape;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import things.form.shape.property.IShapeProperty;
import utilities.collections.ImmutableCollection;

/**
 * implementation of Shape concept
 * 
 * @author borah
 *
 */
public class Shape implements IShape {

	Map<IShapeProperty<?>, Object> properties;

	Shape() {
		this.properties = new HashMap<>();
	}

	@Override
	public <E> E getProperty(IShapeProperty<E> property) {
		return (E) properties.getOrDefault(property, property.getDefaultValue(this));
	}

	@Override
	public Collection<IShapeProperty<?>> properties() {
		return ImmutableCollection.from(properties.keySet());
	}

	@Override
	public <E> IShape withProperty(IShapeProperty<E> property, E value) {
		Shape ret = new Shape();
		ret.properties = new HashMap<>(this.properties);
		ret.properties.put(property, value);
		return ret;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof IShape mat) {
			for (Map.Entry<IShapeProperty<?>, Object> entry : this.properties.entrySet()) {
				if (!mat.getProperty(entry.getKey()).equals(entry.getValue())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.properties.hashCode();
	}

	@Override
	public String toString() {
		return "|:" + this.properties + ":|";
	}

}
