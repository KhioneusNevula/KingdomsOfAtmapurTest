package things.physical_form.visage;

import things.physical_form.IPart;
import things.physical_form.IVisage;

public interface IVisagePart extends IPart {

	/**
	 * What planes this part can be sensed on
	 * 
	 * @return
	 */
	public int detectionPlanes();

	/**
	 * Get the owner of this part, cast as a visage
	 * 
	 * @return
	 */
	default IVisage<?> getVisageOwner() {
		return (IVisage<?>) getOwner();
	}

	public void setOwner(IVisage<?> owner);
}
