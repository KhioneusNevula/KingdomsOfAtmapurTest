package things.form.material;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import _utilities.collections.ImmutableSetView;
import things.biology.genes.IGenomeEncoding;
import things.form.kinds.settings.IKindSettings;
import things.form.material.condition.IMaterialCondition;
import things.form.material.property.IMaterialProperty;
import things.form.material.property.MaterialProperty;
import things.form.material.property.Phase;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.stains.IStain;
import things.stains.Stain;

/**
 * implementation of material
 * 
 * @author borah
 *
 */
public class Material implements IMaterial {

	String name;
	Map<IMaterialProperty<?>, Object> properties;

	Material(String name) {
		this.name = name;
		this.properties = new HashMap<>();
	}

	@Override
	public <E> E getProperty(IMaterialProperty<E> property) {
		return (E) properties.getOrDefault(property, property.getDefaultValue(this));
	}

	@Override
	public Collection<IMaterialProperty<?>> getDistinctProperties() {
		return ImmutableSetView.from(this.properties.keySet());
	}

	@Override
	public void stainTick(IComponentPart onPart, IStain stainInstance, ISoma parentForm, long ticks) {
		// TODO material staining behavior
		float washing = this.getProperty(MaterialProperty.WASHING);
		float corrosion = this.getProperty(MaterialProperty.CORROSIVENESS);
		if (washing != 0) {
			for (IStain stain : onPart.getStains()) {
				if (stain.equals(stainInstance))
					continue;
				if (washing > stain.getSubstance().getProperty(MaterialProperty.STAINING)) {
					parentForm.getOwner().getMap().queueAction(() -> onPart.removeStain(stain.getSubstance(), true));
				}
			}
		}
		if (corrosion != 0) {
			// TODO corrosion?
		}
		float remAmount = 1;
		double randa = Math.random() * 0.5 + Math.random() * 0.5;
		if (this.getProperty(MaterialProperty.PHASE).isGaseous()
				|| randa < 1 - this.getProperty(MaterialProperty.STAINING)) {
			if (randa < 1 - this.getProperty(MaterialProperty.STAINING)) {
				remAmount = 1 - this.getProperty(MaterialProperty.STAINING);
			}
			int amt = (int) (stainInstance.getAmount() - remAmount);
			parentForm.getOwner().getMap().queueAction(() -> {
				// TODO generate a puddle
				onPart.removeStain(this, true);
				if (amt > 0) {
					onPart.addStain(new Stain(this, amt), true);
				}
			});

		}
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
		MaterialBuilder builder = IMaterial.builder(copyName);
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
			if (!this.getDistinctProperties().equals(mat.getDistinctProperties()))
				return false;
			for (Map.Entry<IMaterialProperty<?>, Object> entry : this.properties.entrySet()) {
				if (!mat.getProperty(entry.getKey()).equals(entry.getValue())) {
					return false;
				}
			}
			return true;
		} else if (obj instanceof IMaterialCondition iec) {
			if (!this.getCheckedProperties().equals(iec.getCheckedProperties()))
				return false;
			for (IMaterialProperty<?> pro : this.properties.keySet()) {
				if (!this.getAllowedValues(pro).equals(iec.getAllowedValues(pro))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public IMaterial generateMaterialFromSettings(IKindSettings genome) {
		return this;
	}

	@Override
	public boolean isGenerator() {
		return false;
	}

	@Override
	public boolean isBasisOf(IMaterial other) {
		return this.equals(other);
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
	 * Blood, a standard material. This is blood without genetics; to make blood
	 * with genetics, do .buildCopy and add genetics to it.
	 */
	public static final Material BLOOD;

	/**
	 * Gaseous steam produced from hot water
	 */
	public static final Material STEAM;

	/**
	 * Solid ice, produced from water
	 */
	public static final Material ICE;

	/**
	 * Wood material
	 */
	public static final Material WOOD;

	/**
	 * What wood burns into -- charcoal
	 */
	public static final Material WOOD_ASH;

	/**
	 * Carbon-based smoke as a material
	 */
	public static final Material WOOD_SMOKE;

	static {
		// air/water
		MaterialBuilder waterBuilder = IMaterial.builder("water")
				.prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.LIQUID, MaterialProperty.DENSITY, 1f,
						MaterialProperty.ELECTRIC_CONDUCTIVITY, 5f, MaterialProperty.WASHING, 0.5f,
						MaterialProperty.STAINING, 1f, MaterialProperty.OPACITY, 0.1f, MaterialProperty.COLOR,
						Color.blue));
		WATER = waterBuilder.build();
		ICE = WATER.buildCopy("ice").prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.SOLID, MaterialProperty.DENSITY,
				0.9f, MaterialProperty.CRYSTALLINE, true, MaterialProperty.OPACITY, 0.5f, MaterialProperty.ROUGHNESS,
				0.02f, MaterialProperty.UNEVENNESS, 0.05f, MaterialProperty.HEAT_TRANSITION_ENERGY, 0f,
				MaterialProperty.HEAT_TRANSITION_MATERIAL, WATER, MaterialProperty.COLOR, new Color(145, 199, 237)))
				.build();
		AIR = WATER.buildCopy("air")
				.prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.GAS, MaterialProperty.DENSITY, 0.0013f,
						MaterialProperty.COLD_TRANSITION_ENERGY, 14f, MaterialProperty.COLD_TRANSITION_MATERIAL,
						WATER, /* this represents the dew point, basically */
						MaterialProperty.ELECTRIC_CONDUCTIVITY, 1e-14f, MaterialProperty.OPACITY, 0f))
				.build();
		BLOOD = WATER.buildCopy("blood")
				.prop(ImmutableMap.of(MaterialProperty.DENSITY, 1.06f, MaterialProperty.OPACITY, 0.9f,
						MaterialProperty.WASHING, 0f, MaterialProperty.ORGANIC, true, MaterialProperty.STAINING, 0.9f,
						MaterialProperty.VISCOSITY, 0.5f, MaterialProperty.COLOR, Color.red))
				.build();
		STEAM = AIR.buildCopy("steam").prop(ImmutableMap.of(MaterialProperty.COLD_TRANSITION_ENERGY, 100f,
				MaterialProperty.COLD_TRANSITION_MATERIAL, WATER, MaterialProperty.OPACITY, 0.1f)).build();
		waterBuilder.prop(ImmutableMap.of(MaterialProperty.COLD_TRANSITION_ENERGY, 0f,
				MaterialProperty.COLD_TRANSITION_MATERIAL, ICE, MaterialProperty.HEAT_TRANSITION_ENERGY, 100f,
				MaterialProperty.HEAT_TRANSITION_MATERIAL, STEAM, MaterialProperty.COLOR, Color.white));
		PLASMA = AIR.buildCopy("plasma")
				.prop(ImmutableMap.of(MaterialProperty.COLD_TRANSITION_ENERGY, 100f,
						MaterialProperty.COLD_TRANSITION_MATERIAL, AIR, MaterialProperty.OPACITY, 0.1f,
						MaterialProperty.ELECTRIC_CONDUCTIVITY, 5f))
				.build();

		// stone
		MaterialBuilder stoneBuilder = IMaterial.builder("stone")
				.prop(ImmutableMap.of(MaterialProperty.CRUMBLES, true));
		STONE = stoneBuilder.build();
		GRAVEL = STONE.buildCopy("gravel")
				.prop(ImmutableMap.of(MaterialProperty.PHASE, Phase.GRANULAR, MaterialProperty.FINENESS, 0.5f)).build();
		stoneBuilder.prop(MaterialProperty.CRUMBLE_MATERIAL, GRAVEL);
		LAVA = STONE.buildCopy("lava")
				.prop(ImmutableMap.of(MaterialProperty.FLUIDITY, 0.25f, MaterialProperty.STICKINESS, 0.5f,
						MaterialProperty.COLD_TRANSITION_MATERIAL, STONE, MaterialProperty.COLD_TRANSITION_ENERGY,
						1000f, MaterialProperty.DENSITY, 3.1f, MaterialProperty.COLOR, Color.ORANGE))
				.build();
		stoneBuilder.prop(MaterialProperty.HEAT_TRANSITION_MATERIAL, LAVA);

		// wood
		MaterialBuilder woodBuilder = IMaterial.builder("wood")
				.prop(ImmutableMap.of(MaterialProperty.COARSENESS, 1f, MaterialProperty.CORRODIBILITY, 0.9f,
						MaterialProperty.DENSITY, 1.5f, MaterialProperty.FLAMMABLE, true, MaterialProperty.ORGANIC,
						true, MaterialProperty.RESISTANCE, 500f, MaterialProperty.ROUGHNESS, 0.4f,
						MaterialProperty.SOFTNESS, 0.1f, MaterialProperty.COLOR, new Color(92, 48, 15)));
		WOOD = woodBuilder.build();
		WOOD_ASH = WOOD.buildCopy("wood_ash")
				.prop(ImmutableMap.of(MaterialProperty.DENSITY, 0.2f, MaterialProperty.CORRODIBILITY, 0.5f,
						MaterialProperty.FLAMMABLE, false, MaterialProperty.ORGANIC, false, MaterialProperty.RESISTANCE,
						1f, MaterialProperty.ROUGHNESS, 0.2f, MaterialProperty.COLOR, new Color(94, 81, 66)))
				.build();
		WOOD_SMOKE = AIR.buildCopy("wood_smoke")
				.prop(ImmutableMap.of(MaterialProperty.OPACITY, 0.1f, MaterialProperty.COLOR, Color.black)).build();
		woodBuilder.prop(
				ImmutableMap.of(MaterialProperty.BURN_MATERIAL, WOOD_ASH, MaterialProperty.SMOKE_MATERIAL, WOOD_SMOKE));

	}

	/**
	 * Flexible tissue
	 */
	public static final Material GENERIC_TISSUE = IMaterial.builder("generic_tissue")
			.prop(ImmutableMap.of(MaterialProperty.ORGANIC, true, MaterialProperty.MEAT, true,
					MaterialProperty.SOFTNESS, 0.9f, MaterialProperty.FLEXIBILITY, 1f, MaterialProperty.FLAMMABLE,
					true))
			.build();

	/**
	 * Flesh of an organism
	 */
	public static final Material GENERIC_FLESH = IMaterial.builder("generic_flesh")
			.prop(ImmutableMap.of(MaterialProperty.ORGANIC, true, MaterialProperty.MEAT, true,
					MaterialProperty.SOFTNESS, 0.5f, MaterialProperty.FLAMMABLE, true, MaterialProperty.COLOR,
					new Color(179, 129, 82)))
			.build();

}
