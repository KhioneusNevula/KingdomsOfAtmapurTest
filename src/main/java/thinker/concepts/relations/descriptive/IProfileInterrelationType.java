package thinker.concepts.relations.descriptive;

import java.util.Collection;

import things.interfaces.UniqueType;

/**
 * The type for relations between profiles
 * 
 * @author borah
 *
 */
public interface IProfileInterrelationType extends IDescriptiveRelationType {

	/**
	 * Return what sort of relation exists between the profiles
	 * 
	 * @return
	 */
	public PRelationCategory getRelationCategory();

	/**
	 * Return what the nature of the relation is
	 * 
	 * @return
	 */
	public PRelationNature getRelationNature();

	/**
	 * If this relation is strictly material in nature
	 * 
	 * @return
	 */
	public default boolean isStrictlyPhysical() {
		return this.getRelationNature() == PRelationNature.PHYSICAL
				&& this.getAcceptableUniqueTypes().stream().allMatch(UniqueType::isPhysical);
	}

	/**
	 * Acceptable unique types to connect this relation to
	 * 
	 * @return
	 */
	public Collection<UniqueType> getAcceptableUniqueTypes();

}
