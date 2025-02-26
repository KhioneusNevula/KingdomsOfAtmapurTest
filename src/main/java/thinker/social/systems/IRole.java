package thinker.social.systems;

import thinker.social.ICollective;
import thinker.social.group.IGroup;

public interface IRole extends ICollective {

	@Override
	default boolean isRole() {
		return true;
	}

	/**
	 * Returns the group this role is a child of
	 * 
	 * @return
	 */
	public IGroup getParentGroup();

}
