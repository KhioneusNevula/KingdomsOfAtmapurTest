package things.biology.kinds;

import java.awt.Color;

import things.biology.genes.IGenome;
import things.form.material.condition.IMaterialCondition;
import utilities.property.IProperty;

public class OrganicKindProperties {

	private OrganicKindProperties() {
	}

	/**
	 * The genome of this body
	 */
	public static final IProperty<IGenome> GENOME = IProperty.make("genome", IGenome.class, IGenome.NONE);

	/**
	 * The color of this body's blood
	 */
	public static final IProperty<Color> BLOOD_COLOR = IProperty.make("blood_color", Color.class, Color.RED);

	/**
	 * The condition for what this can eat
	 */
	public static final IProperty<IMaterialCondition> CAN_EAT = IProperty.make("can_eat", IMaterialCondition.class,
			IMaterialCondition.ORGANIC);

}
