package things.form.shape;

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
		public String name() {
			return "_none";
		}
	};

	public static class ShapeBuilder {

		private Shape innerInstance;
		private boolean closed;

		private ShapeBuilder(String name) {
			innerInstance = new Shape(name);
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

	public static ShapeBuilder builder(String name) {
		return new ShapeBuilder(name);
	}

	/**
	 * Get a property of this material; return default value if the material has no
	 * value stored
	 * 
	 * @param <E>
	 * @param property
	 * @return
	 */
	public <E> E getProperty(IShapeProperty<E> property);

	/**
	 * Get the name of this material
	 * 
	 * @return
	 */
	public String name();
}
