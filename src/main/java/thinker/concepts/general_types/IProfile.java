package thinker.concepts.general_types;

import java.util.UUID;

import things.form.IForm;
import things.form.IPart;
import things.interfaces.IUnique;
import thinker.concepts.IConcept;
import thinker.constructs.IPlace;
import thinker.individual.IFigure;
import thinker.social.group.IGroup;
import thinker.social.systems.IRole;

/**
 * A profile represents a unique individual thing in the world, which may be an
 * Actor, Phenomenon (e.g. the Sun), Group, Place, Language, System, or possibly
 * a Part
 * 
 * @author borah
 *
 */
public interface IProfile extends IConcept, IUnique {

	/**
	 * A profile representing any profile
	 */
	public static final IProfile ANY_PROFILE = new IProfile() {

		@Override
		public UUID getUUID() {
			return new UUID(0, 0);
		}

		@Override
		public boolean isIndefinite() {
			return true;
		}

		@Override
		public String getUnderlyingName() {
			return "any_profile";
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.PROFILE;
		}

		@Override
		public ProfileType getProfileType() {
			return ProfileType.ANY;
		}

		@Override
		public String toString() {
			return "concept_any_profile";
		}
	};

	public enum ProfileType {
		/**
		 * A profile representing (the form/visage of) an individual person or object
		 */
		FORM(IForm.class),
		/**
		 * A profile representing an individual figure that can have perceptions thought
		 * about it and seemingly take actions, whether person, god, animal, etc. It
		 * also may or may not be real!
		 */
		FIGURE(IFigure.class),
		/** A specific part of a specific individual */
		PART(IPart.class),
		/**
		 * A profile representing a specific phenomenon, typically World phenomena like
		 * the Sun
		 */
		PHENOMENON(null /** TODO phenomenon profile type */
		),
		/** A profile representing a specific group or collective entity */
		GROUP(IGroup.class),
		/**
		 * A profile representing a role in a social system (technically a type of
		 * Group, but distinguished due to having different purpose and significance
		 */
		ROLE(IRole.class),
		/** A profile representing a specific location of interest */
		PLACE(IPlace.class),
		/** Represents "any profile", useful for actions */
		ANY(IUnique.class);

		private Class<? extends IUnique> superclass;

		private ProfileType(Class<? extends IUnique> superclass) {
			this.superclass = superclass;
		}

		/** Returns the class associated with this profile type, if any */
		public Class<? extends IUnique> getAssociatedClass() {
			return superclass;
		}
	}

	/**
	 * Returns an indefinite profile to match the given profile type, representing
	 * any of the given type
	 * 
	 * @param forType
	 * @return
	 */
	public static IProfile anyTypeOf(ProfileType forType) {
		return IndefiniteProfile.ALL.get(forType);
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
		if (one.getProfileType() == ProfileType.ANY || two.getProfileType() == ProfileType.ANY) {
			return true;
		}
		if (one.isIndefinite() || two.isIndefinite()) {
			return one.getProfileType() == two.getProfileType();
		}
		return one.equals(two);
	}

	public static boolean match(IProfile p, IUnique with) {
		if (p.getProfileType().getAssociatedClass().isAssignableFrom(with.getClass())) {
			if (p.isIndefinite()) {
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
	 * Whether this profile is not a definite entity, but an indefinite matcher
	 * 
	 * @return
	 */
	public boolean isIndefinite();

	/**
	 * What type of thing this profile represents
	 * 
	 * @return
	 */
	public ProfileType getProfileType();

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
}
