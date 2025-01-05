package things.form.shape;

import java.util.HashMap;
import java.util.Map;

import things.form.shape.property.IShapeProperty;

/**
 * implementation of material
 * 
 * @author borah
 *
 */
public class Shape implements IShape {

	String name;
	Map<IShapeProperty<?>, Object> properties;

	Shape(String name) {
		this.name = name;
		this.properties = new HashMap<>();
	}

	@Override
	public <E> E getProperty(IShapeProperty<E> property) {
		return (E) properties.getOrDefault(property, property.getDefaultValue(this));
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof IShape mat) {
			if (!this.name.equals(mat.name()))
				return false;
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
		return this.name.hashCode() + this.properties.hashCode();
	}

	@Override
	public String toString() {
		return "|:" + name + ":|";
	}

}
