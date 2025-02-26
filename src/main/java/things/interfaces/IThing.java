package things.interfaces;

import thinker.concepts.general_types.IProfile;

public interface IThing extends IExistsInWorld, ISensable, IUnique {

	/**
	 * Get the profile of this unique entity
	 * 
	 * @return
	 */
	public IProfile getProfile();
}
