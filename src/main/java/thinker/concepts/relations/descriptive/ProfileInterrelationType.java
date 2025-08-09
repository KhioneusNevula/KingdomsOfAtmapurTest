package thinker.concepts.relations.descriptive;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.util.RelationPredicates;

/**
 * Static relation types that can only exist between two unique entities,
 * whether specific or nonspecific/indefinite or definite
 */
@SuppressWarnings({ "unchecked" })
public enum ProfileInterrelationType implements IProfileInterrelationType {

	/**
	 * describes a relation where X is some type profile Y. Used for ACTIONS to find
	 * targets equivalent to one of their targets. Additionally, this is used to
	 * mark the significant relation of Wh-questions when referring to things such
	 * as action-conditions that ask questions like, what is the TARGET?
	 */
	IS(PRelationCategory.KIND, PRelationNature.NATURAL),

	/**
	 * Describes a relation where X is a type of some category Y; can apply between
	 * unique profiles and type profiles, or between two type profiles. Good to use
	 * between a type profile and an AnyMatcher
	 */
	IS_TYPE_OF(PRelationCategory.KIND, PRelationNature.NATURAL, RelationPredicates.requireIndefiniteProfile()),

	/**
	 * Describes a relation where Y is a type of X, inverse of {@link #IS_TYPE_OF}
	 */
	IS_SUPERTYPE_OF(IS_TYPE_OF),

	/**
	 * Indicates that a X, a group, or individual, has a physical form (or its
	 * members have a physical form) that is Y
	 */
	HAS_BODY(PRelationCategory.CONNECTION, PRelationNature.METAPHYSICAL, UniqueType.FORM),
	/** Inverse of {@link #HAS_BODY} */
	IS_BODY_OF(HAS_BODY, UniqueType.FIGURE, UniqueType.COLLECTIVE),

	/** describes a relationship where X has a social bond with Y */
	HAS_SOCIAL_BOND_TO(PRelationCategory.CONNECTION, PRelationNature.SOCIAL, UniqueType.FIGURE, UniqueType.COLLECTIVE),
	/** inverse of {@link #HAS_SOCIAL_BOND_TO} */
	RECIPIENT_OF_SOCIAL_BOND_FROM(HAS_SOCIAL_BOND_TO, UniqueType.FIGURE, UniqueType.COLLECTIVE),
	/** describes a relation where X is a part that makes up Y */
	PART_OF(PRelationCategory.CONNECTION, PRelationNature.PHYSICAL, UniqueType.FORM),
	/** inverse of {@link #PART_OF} */
	HAS_PART(PART_OF, UniqueType.PART),
	/** describes a relation where X is a member of group, kind, or role Y */
	MEMBER_OF(PRelationCategory.CONNECTION, PRelationNature.SOCIAL, UniqueType.COLLECTIVE),
	/** inverse of {@link #MEMBER_OF} */
	HAS_MEMBER(MEMBER_OF, UniqueType.COLLECTIVE, UniqueType.FIGURE),
	/** describes that X is found at the location Y */
	AT_LOCATION(PRelationCategory.LOCATION, PRelationNature.PHYSICAL, UniqueType.PLACE),
	/** inverse of {@link #AT_LOCATION} */
	PLACE_WHERE(AT_LOCATION),
	/** describes that X is found at the part Y, i.e. held or worn by it */
	HELD_BY(PRelationCategory.LOCATION, PRelationNature.PHYSICAL, UniqueType.PART),
	/** inverse of {@link #HELD_BY} */
	HOLDER_OF(HELD_BY);

	private ProfileInterrelationType opposite;
	private Predicate<IConcept> pred = Predicates.alwaysTrue();
	private Collection<UniqueType> acceptableTypes;
	private Integer c;
	private PRelationCategory relcat;
	private PRelationNature relnat;

	private ProfileInterrelationType(PRelationCategory relcat, PRelationNature relnat, UniqueType... ut) {
		opposite = this;
		this.acceptableTypes = Set.of(ut);
		this.relcat = relcat;
		this.relnat = relnat;
	}

	private ProfileInterrelationType(PRelationCategory relcat, PRelationNature relnat, Predicate<IConcept> p,
			UniqueType... ut) {
		opposite = this;
		this.pred = p;
		this.acceptableTypes = Set.of(ut);
		this.relcat = relcat;
		this.relnat = relnat;
	}

	private ProfileInterrelationType(Integer x, PRelationCategory relcat, PRelationNature relnat, UniqueType... ut) {
		opposite = this;
		this.acceptableTypes = Set.of(ut);
		c = x;
		this.relcat = relcat;
		this.relnat = relnat;
	}

	private ProfileInterrelationType(Integer x, PRelationCategory relcat, PRelationNature relnat, Predicate<IConcept> p,
			UniqueType... ut) {
		opposite = this;
		this.pred = p;
		this.acceptableTypes = Set.of(ut);
		c = x;
		this.relcat = relcat;
		this.relnat = relnat;
	}

	private ProfileInterrelationType(ProfileInterrelationType opposite, Predicate<IConcept> p, UniqueType... ut) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.pred = p;
		this.acceptableTypes = Set.of(ut);
		this.relcat = opposite.relcat;
		this.relnat = opposite.relnat;
	}

	private ProfileInterrelationType(ProfileInterrelationType opposite, Integer x, Predicate<IConcept> p,
			UniqueType... ut) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.pred = p;
		this.acceptableTypes = Set.of(ut);
		c = x;
		this.relcat = opposite.relcat;
		this.relnat = opposite.relnat;
	}

	private ProfileInterrelationType(ProfileInterrelationType opposite, UniqueType... ut) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.acceptableTypes = Set.of(ut);
		this.relcat = opposite.relcat;
		this.relnat = opposite.relnat;
	}

	private ProfileInterrelationType(ProfileInterrelationType opposite, Integer x, UniqueType... ut) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.acceptableTypes = Set.of(ut);
		c = x;
		this.relcat = opposite.relcat;
		this.relnat = opposite.relnat;
	}

	@Override
	public Collection<Class<?>> getEndClasses() {
		return Collections.singleton(IProfile.class);
	}

	@Override
	public Object checkEndType(Object node) {

		if (node instanceof IProfile) {
			IProfile descnode = (IProfile) node;
			Object supermsg = IProfileInterrelationType.super.checkEndType(descnode);
			if (supermsg instanceof String) {
				return supermsg;
			}
			if (!acceptableTypes.isEmpty() && !acceptableTypes.contains(UniqueType.ANY)
					&& !acceptableTypes.containsAll(descnode.getDescriptiveTypes())) {
				return node + " is not of one of the acceptable " + UniqueType.class.getSimpleName() + "s: "
						+ acceptableTypes;
			}
			if (pred.test(descnode)) {
				return maxPermitted();
			}
			return node + " failed expected predicate: " + pred;
		}
		return node + " is not instanceof " + IProfile.class.getSimpleName();
	}

	@Override
	public Integer maxPermitted() {
		return c;
	}

	@Override
	public Collection<UniqueType> getAcceptableUniqueTypes() {
		return acceptableTypes;
	}

	@Override
	public Collection<ConceptType> getEndTypes() {
		return Set.of(ConceptType.PROFILE);
	}

	@Override
	public ProfileInterrelationType invert() {
		return opposite;
	}

	@Override
	public boolean bidirectional() {
		return opposite == this;
	}

	@Override
	public PRelationCategory getRelationCategory() {

		return relcat;
	}

	@Override
	public PRelationNature getRelationNature() {
		return relnat;
	}

}
