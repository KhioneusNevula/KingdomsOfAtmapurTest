package metaphysical.soul;

import metaphysical.ISpiritObject;

public interface ISoul extends ISpiritObject {

	/**
	 * Get the entity which generated the soul of this object
	 * 
	 * @return
	 */
	ISoulGenerator getSoulGenerator();

}
