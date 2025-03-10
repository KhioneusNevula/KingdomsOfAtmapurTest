package thinker.concepts.general_types;

import java.util.Collection;

import _utilities.property.IProperty;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IProfile.ProfileType;

/**
 * A label-concept. Also functions as a kind of property
 * 
 * @author borah
 *
 */
public interface ILabelConcept extends IConcept, IProperty<Boolean> {

	/**
	 * Whether this concept encodes an identity belonging to a group
	 * 
	 * @return
	 */
	public boolean isIdentity();

	/**
	 * Return what kind of world-entities this label corresponds to. If this is
	 * empty, then it is considered equivalent to {@link ProfileType#ANY}
	 * 
	 * @return
	 */
	public Collection<ProfileType> matchesProfileTypes();
}
