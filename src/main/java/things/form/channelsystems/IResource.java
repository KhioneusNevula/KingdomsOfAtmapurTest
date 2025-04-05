package things.form.channelsystems;

import things.form.material.IMaterial;
import things.form.material.generator.IMaterialGeneratorResource;

/**
 * For things that can be present in an object; this can be materials like
 * blood, or things like a child, if related to pregnancy, or signals when
 * relating to nervous systems, or magic energy and similar things
 * 
 * @author borah
 * @param <E> indicates the way this is measured, e.g. materials in floats (by
 *            mass), or instances
 *
 */
public interface IResource<E extends Comparable<?>> {

	public static final IResource<?> NULL_RESOURCE = new IResource<Comparable<?>>() {
		@Override
		public Comparable<?> add(Comparable<?> instance, Comparable<?> instance2) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Comparable<?> divide(Comparable<?> g, int by) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Comparable<?> getEmptyValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Comparable<?> getMaxValue() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<Comparable<?>> getMeasureClass() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String name() {
			return "null_resource";
		}

		@Override
		public Comparable<?> subtract(Comparable<?> g, Comparable<?> l) {
			throw new UnsupportedOperationException();
		}

	};

	/**
	 * name of this resource
	 * 
	 * @return
	 */
	public String name();

	/**
	 * The value of this measure when empty; the minimum possible amounto f resource
	 * 
	 * @return
	 */
	public E getEmptyValue();

	/**
	 * The maximum "amount" of this resource possible
	 * 
	 * @return
	 */
	public E getMaxValue();

	/**
	 * The class that measurements of this resource are in
	 * 
	 * @return
	 */
	public Class<E> getMeasureClass();

	/**
	 * How to bring an instance of this resource together with anothr instance, e.g.
	 * blood combines through simple addition. Return null if this is not possible.
	 * 
	 * @param instance
	 * @param instance2
	 * @return
	 */
	public E add(E instance, E instance2);

	/**
	 * How to remove some instance of this resource from another; return null if not
	 * possible.
	 * 
	 * @param g
	 * @param l
	 * @return
	 */
	public E subtract(E g, E l);

	/**
	 * How to dive the quantity of this resource by an integral value; return null
	 * if not possible.
	 * 
	 * @param g
	 * @param l
	 * @return
	 */
	public E divide(E g, int by);

	/** Whether this resource is a {@link IMaterial} or not */
	public default boolean isMaterial() {
		return this instanceof IMaterial;
	}

	/** Whether this resource is a {@link IMaterialGeneratorResource} or not */
	public default boolean isMaterialGenerator() {
		return this instanceof IMaterialGeneratorResource;
	}

	/**
	 * If this resource has a material representation, return that material
	 * representation. Otherwise return {@link IMaterial#NONE}. For example, an
	 * {@link IMaterialGeneratorResource} has a base material it constructs other
	 * materials from. A general {@link IMaterial} material resource returns itself.
	 * 
	 * @return
	 */
	public default IMaterial getMaterialBase() {
		return IMaterial.NONE;
	}
}
