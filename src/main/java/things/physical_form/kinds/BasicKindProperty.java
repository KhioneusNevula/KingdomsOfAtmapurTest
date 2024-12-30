package things.physical_form.kinds;

import things.physical_form.material.IMaterial;
import utilities.GenericProperty;
import utilities.IGenericProperty;

public class BasicKindProperty {

	public static final GenericProperty<IMaterial> MATERIAL = IGenericProperty.make("material", IMaterial.class,
			IMaterial.NONE);

	public static final GenericProperty<Float> SIZE = IGenericProperty.make("size", float.class, 1f);

	public static final GenericProperty<Float> MASS = IGenericProperty.make("mass", float.class, 10f);
}
