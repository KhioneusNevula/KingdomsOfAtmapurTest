package things.form.sensing;

import java.awt.Color;
import java.util.Set;

import things.form.material.property.DirectMaterialSensableProperty;
import things.form.material.property.DirectShapeSensableProperty;
import things.form.material.property.MaterialProperty;
import things.form.material.property.Phase;
import things.form.sensing.sensors.ISensor;
import things.form.shape.property.ShapeProperty;

/** General class representing all typical sensable properties */
public class SensableProperties {

	/** A sensing property representing that this Part has text in ink/pigment */
	public static final WritingSensableProperty INKED_TEXT = new WritingSensableProperty("sensable_inked_text",
			Set.of(ISensor.SIGHT_SENSOR), true);

	/** A sensing property representing that this Part has text engraved */
	public static final WritingSensableProperty ENGRAVED_TEXT = new WritingSensableProperty("sensable_engraved_text",
			Set.of(ISensor.SIGHT_SENSOR), true);

	/** A sensing property representing that this Part is producing speech sounds */
	public static final WritingSensableProperty SPEECH = new WritingSensableProperty("sensable_speech", Set.of(),
			false);

	/** Color of a material */
	public static final DirectMaterialSensableProperty<Color> COLOR_PROPERTY = new DirectMaterialSensableProperty<>(
			MaterialProperty.COLOR, Color.black, Set.of(ISensor.SIGHT_SENSOR));
	/** Material phase */
	public static final DirectMaterialSensableProperty<Phase> PHASE_PROPERTY = new DirectMaterialSensableProperty<>(
			MaterialProperty.PHASE, Phase.SOLID, Set.of(ISensor.SIGHT_SENSOR));

	/** Shape length */
	public static final DirectShapeSensableProperty<ShapeProperty.Length> LENGTH_PROPERTY = new DirectShapeSensableProperty<>(
			ShapeProperty.LENGTH, ShapeProperty.Length.MEDIUM, Set.of(ISensor.SIGHT_SENSOR));

}
