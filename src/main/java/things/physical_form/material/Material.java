package things.physical_form.material;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * implementation of material
 * 
 * @author borah
 *
 */
public class Material implements IMaterial {

	private String name;
	private Map<IMaterialProperty<?>, Object> properties;

	public static class MaterialBuilder {

		private Material innerInstance;

		private MaterialBuilder(String name) {
			innerInstance = new Material(name);
		}

		public <E> MaterialBuilder prop(IMaterialProperty<E> prop, E val) {
			innerInstance.properties.put(prop, val);
			return this;
		}

		public MaterialBuilder prop(Map<? extends IMaterialProperty<?>, ?> props) {
			innerInstance.properties.putAll(props);
			return this;
		}

		public MaterialBuilder prop(Map.Entry<? extends IMaterialProperty<?>, ?>... pairs) {
			for (Map.Entry<? extends IMaterialProperty<?>, ?> entry : pairs) {
				innerInstance.properties.put(entry.getKey(), entry.getValue());
			}
			return this;
		}

		public Material build() {
			return innerInstance;
		}
	}

	public static MaterialBuilder builder(String name) {
		return new MaterialBuilder(name);
	}

	private Material(String name) {
		this.name = name;
		this.properties = new HashMap<>();
	}

	@Override
	public <E> E getProperty(IMaterialProperty<E> property) {
		return (E) properties.getOrDefault(property, property.getDefaultValue(this));
	}

	@Override
	public String name() {
		return this.name;
	}

	/**
	 * Returns a material property with this material's properties already filled in
	 * 
	 * @param copyName
	 * @return
	 */
	public MaterialBuilder buildCopy(String copyName) {
		MaterialBuilder builder = new MaterialBuilder(copyName);
		builder.prop(this.properties);
		return builder;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof IMaterial mat) {
			if (!this.name.equals(mat.name()))
				return false;
			for (Map.Entry<IMaterialProperty<?>, Object> entry : this.properties.entrySet()) {
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
		return "{|" + name + "|}";
	}

	/**
	 * The "final" state of physical matter; this is the state all materials reach
	 * when heated past a gas, and converts into air when cooled. It essentially
	 * represents complete annihilation
	 */
	public static final Material PLASMA;

	/**
	 * The most basic solid material; heavy stone
	 */
	public static final Material STONE;

	/**
	 * Stone, in its crumbled/damaged form
	 */
	public static final Material GRAVEL;

	/**
	 * Stone when melted, a hot fluid
	 */
	public static final Material LAVA;

	/**
	 * The ambient gas of the environment. To represent the idea of condensation,
	 * liquefies into water
	 */
	public static final Material AIR;

	/**
	 * Liquid water, the mos basic fluid (basically).
	 */
	public static final Material WATER;

	/**
	 * Gaseous steam produced from hot water
	 */
	public static final Material STEAM;

	/**
	 * Solid ice, produced from water
	 */
	public static final Material ICE;

	static {
		// air/water
		MaterialBuilder waterBuilder = builder("water").prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.LIQUID,
				MaterialProperty.DENSITY, 1f, MaterialProperty.ELECTRIC_CONDUCTIVITY, 5f, MaterialProperty.WASHING,
				0.5f, MaterialProperty.STAINING, 1f, MaterialProperty.OPACITY, 0.1f));
		WATER = waterBuilder.build();
		ICE = WATER.buildCopy("ice")
				.prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.SOLID, MaterialProperty.DENSITY, 0.9f,
						MaterialProperty.CRYSTALLINE, true, MaterialProperty.OPACITY, 0.5f, MaterialProperty.ROUGHNESS,
						0.02f, MaterialProperty.UNEVENNESS, 0.05f, MaterialProperty.HEAT_TRANSITION_ENERGY, 0f,
						MaterialProperty.HEAT_TRANSITION_MATERIAL, WATER))
				.build();
		AIR = WATER.buildCopy("air")
				.prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.GAS, MaterialProperty.DENSITY, 0.0013f,
						MaterialProperty.COLD_TRANSITION_ENERGY, 14f, MaterialProperty.COLD_TRANSITION_MATERIAL,
						WATER, /* this represents the dew point, basically */
						MaterialProperty.ELECTRIC_CONDUCTIVITY, 1e-14f, MaterialProperty.OPACITY, 0f))
				.build();
		STEAM = AIR.buildCopy("steam").prop(ImmutableMap.of(MaterialProperty.COLD_TRANSITION_ENERGY, 100f,
				MaterialProperty.COLD_TRANSITION_MATERIAL, WATER, MaterialProperty.OPACITY, 0.1f)).build();
		waterBuilder.prop(ImmutableMap.of(MaterialProperty.COLD_TRANSITION_ENERGY, 0f,
				MaterialProperty.COLD_TRANSITION_MATERIAL, ICE, MaterialProperty.HEAT_TRANSITION_ENERGY, 100f,
				MaterialProperty.HEAT_TRANSITION_MATERIAL, STEAM));
		PLASMA = AIR.buildCopy("plasma")
				.prop(ImmutableMap.of(MaterialProperty.COLD_TRANSITION_ENERGY, 100f,
						MaterialProperty.COLD_TRANSITION_MATERIAL, AIR, MaterialProperty.OPACITY, 0.1f,
						MaterialProperty.ELECTRIC_CONDUCTIVITY, 5f))
				.build();

		// stone
		MaterialBuilder stoneBuilder = builder("stone").prop(ImmutableMap.of(MaterialProperty.CRUMBLES, true));
		STONE = stoneBuilder.build();
		GRAVEL = STONE.buildCopy("gravel")
				.prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.GRANULAR, MaterialProperty.FINENESS, 0.5f)).build();
		stoneBuilder.prop(MaterialProperty.CRUMBLE_MATERIAL, GRAVEL);
		LAVA = STONE.buildCopy("lava")
				.prop(ImmutableMap.of(MaterialProperty.FLUIDITY, 0.25f, MaterialProperty.STICKINESS, 0.5f,
						MaterialProperty.COLD_TRANSITION_MATERIAL, STONE, MaterialProperty.COLD_TRANSITION_ENERGY,
						1000f, MaterialProperty.DENSITY, 3.1f))
				.build();
		stoneBuilder.prop(MaterialProperty.HEAT_TRANSITION_MATERIAL, LAVA);

	}

	public static final Material NERVE_TISSUE = builder("nerve_tissue").prop(ImmutableMap.of(MaterialProperty.ORGANIC,
			true, MaterialProperty.SOFTNESS, 0.9f, MaterialProperty.FLEXIBILITY, 1f, MaterialProperty.FLAMMABLE, true))
			.build();

}
