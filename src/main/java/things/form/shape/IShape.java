package things.form.shape;

import static things.form.shape.IShape.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import things.form.shape.property.IShapeProperty;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Shapedness;

public interface IShape {
	public static final IShape AMORPHOUS = new IShape() {
		@Override
		public <E> E getProperty(IShapeProperty<E> property) {

			return property == ShapeProperty.SHAPEDNESS ? (E) Shapedness.AMORPHIC : property.getDefaultValue(this);
		}

		@Override
		public <E> IShape withProperty(IShapeProperty<E> property, E value) {
			return builder().addProperty(property, value).build();
		}

		@Override
		public Collection<IShapeProperty<?>> properties() {
			return Collections.singleton(ShapeProperty.SHAPEDNESS);
		}

	};

	public static class ShapeBuilder {

		private Shape innerInstance;
		private boolean closed;

		private ShapeBuilder(IShape other) {
			innerInstance = new Shape();
			innerInstance.properties = new HashMap<>();
			for (IShapeProperty<?> prop : other.properties()) {
				innerInstance.properties.put(prop, other.getProperty(prop));
			}

		}

		private ShapeBuilder() {
			innerInstance = new Shape();
		}

		public <E> ShapeBuilder addProperty(IShapeProperty<E> prop, E val) {
			if (closed) {
				throw new IllegalStateException();
			}
			innerInstance.properties.put(prop, val);
			return this;
		}

		public Shape build() {
			closed = true;
			return innerInstance;
		}
	}

	public static ShapeBuilder builder() {
		return new ShapeBuilder();
	}

	/**
	 * Returns a shape builder that copies from this shape
	 * 
	 * @return
	 */
	public default ShapeBuilder copyBuilder() {
		return new ShapeBuilder(this);
	}

	/**
	 * Get a property of this shape; return default value if the shape has no value
	 * stored
	 * 
	 * @param <E>
	 * @param property
	 * @return
	 */
	public <E> E getProperty(IShapeProperty<E> property);

	/**
	 * Return all properties set in this shape
	 * 
	 * @return
	 */
	public Collection<IShapeProperty<?>> properties();

	/**
	 * Return a variant of this shape with the given property changed
	 * 
	 * @param <E>
	 * @param property
	 * @param value
	 * @return
	 */
	public <E> IShape withProperty(IShapeProperty<E> property, E value);

}
