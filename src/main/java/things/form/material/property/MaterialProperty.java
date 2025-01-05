package things.form.material.property;

import java.util.function.Function;
import java.util.function.Supplier;

import things.form.material.IMaterial;
import things.form.material.Material;

public class MaterialProperty<E> implements IMaterialProperty<E> {

	public static <E> MaterialProperty<E> make(String name, Class<E> clazz, E defVal) {
		return new MaterialProperty<E>(name, clazz, (a) -> defVal);
	}

	public static <E> MaterialProperty<E> make(String name, Class<E> clazz, Supplier<E> defVal) {
		return new MaterialProperty<E>(name, clazz, (a) -> defVal.get());
	}

	public static <E> MaterialProperty<E> make(String name, Class<E> clazz, Function<IMaterial, E> defVal) {
		return new MaterialProperty<E>(name, clazz, defVal);
	}

	/** TODO genetic material */

	/**
	 * Organic: Whether this is biological material or not
	 */
	public static final MaterialProperty<Boolean> ORGANIC = make("organic", boolean.class, false);

	/** Phase: what phase a material is in */
	public static final MaterialProperty<Phase> PHASE = make("phase", Phase.class, Phase.SOLID);

	/** Softness: how much it deforms when struck, if solid **/
	public static final MaterialProperty<Float> SOFTNESS = make("softness", float.class, 0f);

	/** Elasticity: how much it regains shape after it deforms, if solid **/
	public static final MaterialProperty<Float> ELASTICITY = make("elasticity", float.class, 0f);

	/** Bounciness: how much it bounces, if solid **/
	public static final MaterialProperty<Float> BOUNCINESS = make("bounciness", float.class, 0f);

	/** How likely this is to clean a Stain off something, if a Stain or Fluid */
	public static final MaterialProperty<Float> WASHING = make("washing", float.class, 0f);

	/**
	 * How likely this material is to move along with something that touches it
	 * while moving
	 */
	public static final MaterialProperty<Float> STICKINESS = make("stickiness", float.class, 0f);

	/**
	 * The quantity of drag force this material exerts on whatever moves through it,
	 * if it is a fluid or gas
	 */
	public static final MaterialProperty<Float> VISCOSITY = make("viscosity", float.class, 0.001f);

	/**
	 * Used to calculate the coefficient of dynamic friction by averaging the
	 * roughness of two objects.
	 */
	public static final MaterialProperty<Float> ROUGHNESS = make("roughness", float.class, 0.5f);

	/**
	 * Used to calculate the coefficient of static friction by averaging the
	 * unevenness of two objects. By default equivalent to {@link #ROUGHNESS}
	 */
	public static final MaterialProperty<Float> UNEVENNESS = make("unevenness", float.class,
			(IMaterial a) -> a.getProperty(ROUGHNESS));

	/**
	 * How likely this material is to leave a stain, and also how unlikely the stain
	 * is to come off when washed
	 */
	public static final MaterialProperty<Float> STAINING = make("staining", float.class, 0f);

	/** How much this material as a fluid or stain corrodes a material in contact */
	public static final MaterialProperty<Float> CORROSIVENESS = make("corrosiveness", float.class, 0f);

	/**
	 * How much this material corrodes when exposed to a corrosive substance.
	 * Multiplied by Corrosiveness to determine corrosion amounts
	 */
	public static final MaterialProperty<Float> CORRODIBILITY = make("corrodibility", float.class, 0.5f);

	/**
	 * Flexibility: how much it flops and wiggles (idk we’ll figure it out), if
	 * solid
	 **/
	public static final MaterialProperty<Float> FLEXIBILITY = make("flexibility", float.class, 0f);

	/**
	 * Flow: Probability that this flows a block outward in a given tick, if liquid,
	 * granular, or gaseous
	 **/
	public static final MaterialProperty<Float> FLUIDITY = make("fluidity", float.class, 1f);

	/**
	 * Density: How dense this is in g/cm^3 (or in thousands of kg/m^3). Impacts how
	 * heavy pieces of it are(?). Impacts whether a fluid flows up or down when it
	 * meets another fluid
	 **/
	public static final MaterialProperty<Float> DENSITY = make("density", float.class, 2.4f);

	/** Opacity: how much it can (not) be seen through, in any state **/
	public static final MaterialProperty<Float> OPACITY = make("opacity", float.class, 1f);

	/** Resistance: how much force is needed to break it, if solid **/
	public static final MaterialProperty<Float> RESISTANCE = make("resistance", float.class, 1000f);

	/**
	 * Fineness: how fluid-like it is, if granular; impacts the probability that an
	 * Actor would sink into/pass through this as a fluid, versus to walk
	 * over/impact with it like a block
	 **/
	public static final MaterialProperty<Float> FINENESS = make("fineness", float.class, 0.5f);

	/** Crystalline: true/false, whether it breaks into shards or not, if solid **/
	public static final MaterialProperty<Boolean> CRYSTALLINE = make("crystalline", boolean.class, false);

	/** Crumble: true/false, whether it can be crushed into a Granular **/
	public static final MaterialProperty<Boolean> CRUMBLES = make("crumbles", boolean.class, false);

	/** Crumble Material: what Material it becomes when crushed into powder **/
	public static final MaterialProperty<IMaterial> CRUMBLE_MATERIAL = make("crumble_material", IMaterial.class,
			Function.identity());

	/** Coarse: how much Scratch it causes when touched, if solid or granular **/
	public static final MaterialProperty<Float> COARSENESS = make("coarseness", float.class, 0.1f);

	/**
	 * H-Conductivity: How well it conducts heat; how many celsiusu of temperature
	 * changes through it per tick
	 */
	public static final MaterialProperty<Float> HEAT_CONDUCTIVITY = make("heat_conductivity", float.class, 0.1f);

	/** H-Storage: How well it stores heat */
	public static final MaterialProperty<Float> HEAT_STORAGE = make("heat_storage", float.class, 0.5f);

	/** E-Conductivity: How well it conducts electricity, in siemens/meter */
	public static final MaterialProperty<Float> ELECTRIC_CONDUCTIVITY = make("electric_conductivity", float.class,
			0.001f);

	/** E-Storage: How well it stores charge */
	public static final MaterialProperty<Float> ELECTRIC_STORAGE = make("electric_storage", float.class, 0.1f);

	/** Flammable: true/false, whether it can catch fire */
	public static final MaterialProperty<Boolean> FLAMMABLE = make("flammable", boolean.class, false);

	/**
	 * Heat Transition Point: How much heat it needs to receive/lose to go to the
	 * next state
	 */
	public static final MaterialProperty<Float> HEAT_TRANSITION_ENERGY = make("heat_transition_energy", float.class,
			1000f);

	/**
	 * Heat Transition Material: What material it turns into when it heats high
	 * enough. For solid/granular this is usually a liquid; for liquid this is
	 * usually gas. For gas, this is usually plasma
	 */
	public static final MaterialProperty<IMaterial> HEAT_TRANSITION_MATERIAL = make("heat_transition_material",
			IMaterial.class, (IMaterial m) -> (IMaterial) (Material.PLASMA));

	/**
	 * Cold Transition Point: How much heat it needs to lose to go to the next state
	 */
	public static final MaterialProperty<Float> COLD_TRANSITION_ENERGY = make("cold_transition_energy", float.class,
			0f);

	/**
	 * Cold Transition Material: What material it turns into when losing enough
	 * energy. For gases this is usually liquid; for liquid this is usually
	 * solid/granular.
	 */
	public static final MaterialProperty<IMaterial> COLD_TRANSITION_MATERIAL = make("cold_transition_material",
			IMaterial.class, Function.identity());
	/**
	 * 
	 * 
	 * 
	 * 
	 */

	private String name;
	private Class<E> type;
	private Function<IMaterial, E> defaultSupplier;

	private MaterialProperty(String name, Class<E> clazz, Function<IMaterial, E> defaultVal) {
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
	public E getDefaultValue(IMaterial mat) {
		return defaultSupplier.apply(mat);
	}

	public Function<IMaterial, E> getDefaultSupplier() {
		return defaultSupplier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IMaterialProperty mat) {
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
