package thinker.concepts.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import things.interfaces.IUnique;
import things.interfaces.UniqueType;
import thinker.concepts.general_types.IDescriptiveConcept;
import thinker.concepts.general_types.IPatternConcept;
import thinker.concepts.general_types.IWhQuestionConcept;

/**
 * A profile represents a unique individual thing in the world, which may be an
 * Actor, Phenomenon (e.g. the Sun), Group, Place, Language, System, or possibly
 * a Part
 * 
 * @author borah
 *
 */
public interface IProfile extends IDescriptiveConcept {

	/**
	 * A profile representing any profile
	 */
	public static final IProfile ANY_PROFILE = new IProfile() {

		@Override
		public UUID getUUID() {
			return new UUID(0, 0);
		}

		@Override
		public boolean isAnyMatcher() {
			return true;
		}

		@Override
		public boolean isTypeProfile() {
			return false;
		}

		@Override
		public boolean isUniqueProfile() {
			return false;
		}

		@Override
		public String getUnderlyingName() {
			return "any_profile";
		}

		@Override
		public Collection<UniqueType> getDescriptiveTypes() {
			return Collections.emptySet();
		}

		@Override
		public UniqueType getDescriptiveType() {
			return UniqueType.ANY;
		}

		@Override
		public IProfile withType(UniqueType newType) {
			return IProfile.anyOf(newType);
		}

		@Override
		public String toString() {
			return "concept_any_profile";
		}
	};

	/**
	 * Returns an indefinite profile to match the given profile type, representing
	 * any of the given type
	 * 
	 * @param forType
	 * @return
	 */
	public static IProfile anyOf(UniqueType forType) {
		return AnyMatcherProfile.ALL.get(forType);
	}

	/** Creates a type profile */
	public static IProfile typeOf(UniqueType t, String name) {
		return new TypeProfile(t, name);
	}

	/**
	 * Returns all profiles that match "any" instanceof a specific
	 * {@link UniqueType}
	 */
	public static Collection<IProfile> getAnyMatcherProfiles() {
		return AnyMatcherProfile.ALL.values();
	}

	/**
	 * Slightly more useful than equals -- this checks if the two profiles are the
	 * same, even if one is indefinite (in which case it matches all profiles of its
	 * same type)
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public static boolean match(IProfile one, IProfile two) {
		if (one.getDescriptiveType() == UniqueType.ANY || two.getDescriptiveType() == UniqueType.ANY) {
			return true;
		}
		if (one.isAnyMatcher() || two.isAnyMatcher()) {
			return one.getDescriptiveType() == two.getDescriptiveType();
		}
		return one.equals(two);
	}

	public static boolean match(IProfile p, IUnique with) {
		if (p.getDescriptiveType().getAssociatedClass().isAssignableFrom(with.getClass())) {
			if (p.isAnyMatcher()) {
				return true;
			} else {
				return p.getUUID().equals(with.getUUID());
			}
		} else {
			return false;
		}
	}

	/**
	 * Whether this profile matches the other profile
	 * 
	 * @param other
	 * @return
	 */
	public default boolean matches(IProfile other) {
		return match(this, other);
	}

	/**
	 * Whether this profile is not a definite entity, but a matcher that matches any
	 * Unique object of the associated type
	 * 
	 * @return
	 */
	public boolean isAnyMatcher();

	/**
	 * Whether this profile refers to a unique entity rather than a type;
	 * 
	 * @return
	 */
	public boolean isUniqueProfile();

	/**
	 * Whether this profile is indefinite, i.e. it can match a set of possible
	 * things rather than just one unique thing. Equivalent to
	 * "{@linkplain IProfile#isAnyMatcher()} ||
	 * {@linkplain IProfile#isTypeProfile()} || {@linkplain IProfile#isPattern()}"
	 */
	public default boolean isIndefinite() {
		return isAnyMatcher() || isTypeProfile() || isPattern();
	}

	/**
	 * If this is just a profile pattern for a typeprofile
	 * 
	 * @return
	 */
	public default boolean isPattern() {
		return this instanceof IPatternConcept;
	}

	/**
	 * Whether this profile is a type of thing, i.e. representing a thing with
	 * distinctive properties
	 */
	public boolean isTypeProfile();

	public default boolean isQuestion() {
		return this instanceof IWhQuestionConcept;
	}

	/**
	 * What type of thing this profile represents
	 * 
	 * @return
	 */
	public UniqueType getDescriptiveType();

	@Override
	default ConceptType getConceptType() {
		return ConceptType.PROFILE;
	}

	/**
	 * Return a version of this profile with the given type. May return self if self
	 * already has that type.
	 */
	public IProfile withType(UniqueType newType);

	/**
	 * An identifier used to make profiles easier to identify; not factored into
	 * equality checks, hashcode, etc. Return an empty string if no such identifier
	 * name exists
	 * 
	 * @return
	 */
	public default String getIdentifierName() {
		return "";
	}

	/**
	 * Return UUID of this profiel
	 */
	public UUID getUUID();
}
