package things.form.shape.property;

import java.util.function.Function;
import java.util.function.Supplier;

import things.form.shape.IShape;

public class ShapeProperty<E> implements IShapeProperty<E> {

	public static <E> ShapeProperty<E> make(String name, Class<E> clazz, E defVal) {
		return new ShapeProperty<E>(name, clazz, (a) -> defVal);
	}

	public static <E> ShapeProperty<E> make(String name, Class<E> clazz, Supplier<E> defVal) {
		return new ShapeProperty<E>(name, clazz, (a) -> defVal.get());
	}

	public static <E> ShapeProperty<E> make(String name, Class<E> clazz, Function<IShape, E> defVal) {
		return new ShapeProperty<E>(name, clazz, defVal);
	}

	public static enum Shapedness {
		AMORPHIC, SHAPED
	}

	/**
	 * Usually determines things like drawing height
	 * 
	 * @author borah
	 *
	 */
	public static enum Length {
		/** A short length, with a factor of 0.5 */
		SHORT(0.5f),
		/** A normal length, with a factor of 1 */
		MEDIUM(1f),
		/** A long length, with a factor of 2 */
		LONG(2f);

		/** the multiplying factor for a length */
		public final float factor;

		private Length(float factor) {
			this.factor = factor;
		}
	}

	public static enum Sharpness {
		ROUNDED, NOT_SHARP, SHARP, SPIKY
	}

	/**
	 * Usually determines things like drawing width
	 * 
	 * @author borah
	 *
	 */
	public static enum Thickness {
		THIN(0.5f), MEDIUM(1f), THICK(2f);

		/** the multiplying factor for a thickness */
		public final float factor;

		private Thickness(float factor) {
			this.factor = factor;
		}
	}

	public static enum RollableShape {
		ROLLABLE_OVOID, ROLLABLE_CYLINDER, NON_ROLLABLE
	}

	/**
	 * Amorphic/Morphic — whether it has a cohesive shape (amorphic is default for
	 * things like piles of dust)
	 */
	public static final ShapeProperty<Shapedness> SHAPEDNESS = make("shapedness", Shapedness.class, Shapedness.SHAPED);
	/**
	 * Long/Medium/Short — if it is long (in the way of a stick) or not (impacts how
	 * it breaks and whether it can stab)
	 */
	public static final ShapeProperty<Length> LENGTH = make("length", Length.class, Length.MEDIUM);
	/**
	 * Rounded/Sharp/Spiky — if it is round, or sharp, or spiky (impacts whether it
	 * would do blunt damage or sharp damage or multiple-cut damage)
	 */
	public static final ShapeProperty<Sharpness> SHARPNESS = make("sharpness", Sharpness.class, Sharpness.NOT_SHARP);
	/**
	 * Thin/Thick — if it is thin (in the way of a sheet) or not (impacts how it
	 * breaks and whether it can slice)
	 */
	public static final ShapeProperty<Thickness> THICKNESS = make("thickness", Thickness.class, Thickness.MEDIUM);
	/**
	 * Shape of the item in regard to whether or not it can roll
	 */
	public static final ShapeProperty<RollableShape> ROLL_SHAPE = make("roll_shape", RollableShape.class,
			RollableShape.NON_ROLLABLE);
	/**
	 * Integrity — how much it withstands damage; integrity decreases when
	 * undergoing damaging forces. Acts as a multiplier to resistance of the
	 * material
	 */
	public static final ShapeProperty<Float> INTEGRITY = make("integrity", float.class, 1f);

	/**
	 * Is Shard -- whether this shape is a broken shard
	 */
	public static final ShapeProperty<Boolean> IS_SHARD = make("is_shard", boolean.class, false);

	/**
	 * Is Half -- whether this shape is half of something that was bisected
	 */
	public static final ShapeProperty<Boolean> IS_HALF = make("is_half", boolean.class, false);

	/**
	 * 
	 * 
	 * 
	 * 
	 */

	private String name;
	private Class<E> type;
	private Function<IShape, E> defaultSupplier;

	private ShapeProperty(String name, Class<E> clazz, Function<IShape, E> defaultVal) {
		this.name = name;
		this.type = clazz;
		this.defaultSupplier = defaultVal;
	}

	public String name() {
		return name;
	}

	@Override
	public Class<? super E> getType() {
		return type;
	}

	@Override
	public E getDefaultValue(IShape mat) {
		return defaultSupplier.apply(mat);
	}

	public Function<IShape, E> getDefaultSupplier() {
		return defaultSupplier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IShapeProperty mat) {
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
