package thinker.social;

import thinker.IParty;

public interface ICollective extends IParty {

	@Override
	default boolean isCollective() {
		return true;
	}

	/**
	 * If this collective has a nonfixed count, a purpose, and membership because it
	 * is a social role (as opposed to being a Group)
	 * 
	 * @return
	 */
	public default boolean isRole() {
		return false;
	}

}
