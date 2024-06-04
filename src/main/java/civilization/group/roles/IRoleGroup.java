package civilization.group.roles;

import civilization.group.IGroup;

/**
 * A role in a society
 * 
 * @author borah
 *
 */
public interface IRoleGroup extends IGroup {
	/**
	 * Whether this role group is perceived to be based on an inherent trait, as
	 * opposed to being a taken job
	 * 
	 * @return
	 */
	public boolean isInherent();

}
