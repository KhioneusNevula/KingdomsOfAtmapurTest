package things.form.kinds;

import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import _sim.plane.Plane;
import _utilities.property.IProperty;
import _utilities.property.PropertyImpl;
import things.form.channelsystems.IChannelSystem;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.IPartDestructionCondition;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.stats.IPartStat;

public class BasicKindProperties {

	public static final PropertyImpl<Integer> PLANES = IProperty.make("planes", int.class, Plane.PHYSICAL.getPrime());

	public static final PropertyImpl<IMaterial> MATERIAL = IProperty.make("material", IMaterial.class, IMaterial.NONE);

	/**
	 * Setting for what circumstances a part is destroyed under
	 */
	public static final PropertyImpl<IPartDestructionCondition> DESTRUCTION_CONDITION = IProperty.make(
			"destruction_condition", IPartDestructionCondition.class, IPartDestructionCondition.Standard.GRANULATED);

	public static final PropertyImpl<IShape> SHAPE = IProperty.make("shape", IShape.class, IShape.AMORPHOUS);

	public static final PropertyImpl<Float> SIZE = IProperty.make("size", float.class, 1f);

	public static final PropertyImpl<Float> MASS = IProperty.make("mass", float.class, 10f);

	public static final PropertyImpl<Color> COLOR = IProperty.make("color", Color.class, Color.RED);

	/**
	 * Part abilities for a single part
	 */
	public static final PropertyImpl<Set<? extends IPartAbility>> SINGLE_PART_ABILITIES = IProperty
			.make("single_part_abilities", Set.class, Collections.emptySet());

	/**
	 * Name of a single component of a single-component body
	 */
	public static final PropertyImpl<String> SINGLE_PART_NAME = IProperty.make("single_part_name", String.class,
			"body");

	/**
	 * Part abilities for a single part
	 */
	public static final PropertyImpl<Set<? extends IMaterial>> SINGLE_PART_EMBEDDED_MATERIALS = IProperty
			.make("single_part_embedded_materials", Set.class, Collections.emptySet());

	/**
	 * Part stats for a single part
	 */
	public static final IProperty<Map<? extends IPartStat<?>, ? extends Object>> SINGLE_PART_STATS = IProperty
			.make("single_part_stats", Map.class, Collections.emptyMap());

	/**
	 * Channel systems for a body
	 */
	public static final IProperty<Set<? extends IChannelSystem>> CHANNEL_SYSTEMS = IProperty.make("channel_systems",
			Set.class, Collections.emptySet());

}
