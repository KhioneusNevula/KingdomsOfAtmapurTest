package party.systems;

import party.collective.ICollective;
import party.collective.group.IGroup;

/**
 * A role is a subdivision of a group indicating a function to be carried out of
 * some kind
 */
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
