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

	public static enum Length {
		SHORT, MEDIUM, LONG
	}

	public static enum Sharpness {
		ROUNDED, NOT_SHARP, SHARP, SPIKY
	}

	public static enum Thickness {
		THIN, THICK
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
	public static final ShapeProperty<Thickness> THICKNESS = make("thickness", Thickness.class, Thickness.THICK);
	/**
	 * Shape of the item in regard to whether or not it can roll
	 */
	public static final ShapeProperty<RollableShape> ROLL_SHAPE = make("roll_shape", RollableShape.class,
			RollableShape.NON_ROLLABLE);
	/** Tensile Strength — how strong it is; */
	public static final ShapeProperty<Float> TENSILE = make("tensile", float.class, 0.5f);

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
