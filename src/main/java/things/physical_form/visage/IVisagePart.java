package things.physical_form.visage;

import things.physical_form.IPart;

public interface IVisagePart extends IPart {

	/**
	 * What planes this part can be sensed on
	 * 
	 * @return
	 */
	public int detectionPlanes();
}
