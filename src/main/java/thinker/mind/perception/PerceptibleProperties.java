package thinker.mind.perception;

import _sim.vectors.IVector;
import _utilities.property.IProperty;

/** Properties perceptible about Things based on their visage */
public class PerceptibleProperties {

	/** The exact map location of this Thing */
	public static final IProperty<IVector> LOCATION = IProperty.make("location", IVector.class, IVector.ZERO);
	/** The imprecise vector direction of this Thing */
	public static final IProperty<IVector> DIRECTION = IProperty.make("direction", IVector.class, IVector.ZERO);
	/** The general distance of this Thing */
	public static final IProperty<Double> DISTANCE = IProperty.make("distance", double.class, -1.0);
	/** The planar extent of this Thing */
	public static final IProperty<Integer> PLANES = IProperty.make("planes", int.class, 1);

}
