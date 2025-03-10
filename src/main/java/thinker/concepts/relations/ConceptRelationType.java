package thinker.concepts.relations;

import java.util.EnumSet;
import java.util.Set;

import thinker.concepts.application.IConceptAssociationInfo;

public enum ConceptRelationType implements IConceptRelationType {
	/** there is a relation between X and Y, but it is unknown? */
	UNKNOWN,
	/**
	 * describes a relation where X and Y refer to the same entity. Used for ACTIONS
	 * to find targets equivalent to one of their targets. Additionally, this is
	 * used to mark the significant relation of Wh-questions when referring to
	 * things such as action-conditions that ask questions like, what is the TARGET?
	 * {@linkplain #bidirectional() Bidirectional.}
	 */
	IS,
	/**
	 * Another kind of "is" relation where X is a supertype of Y. Inverse of
	 * {@link #IS_SUBTYPE_OF}
	 */
	IS_SUPERTYPE_OF,
	/** Inverse of {@link #IS_SUPERTYPE_OF} */
	IS_SUBTYPE_OF(IS_SUPERTYPE_OF),
	/**
	 * Indicates that X is referred to by the word or name Y. Inverse of
	 * {@link #NAME_OF}
	 */
	NAMED,
	/** inverse of {@link #NAMED} */
	NAME_OF,
	/**
	 * Describes a relation where X is a property or label of Y (or a logical
	 * connector), or X is a concept that characterizes Y concept as well, e.g. a
	 * Hero is both Good and Strong. Inverse of {@link #CHARACTERIZED_BY}
	 */
	CHARACTERIZES,
	/** Inverse of {@link #CHARACTERIZES} */
	CHARACTERIZED_BY(CHARACTERIZES),

	/**
	 * Used to connect some concept X to an applier/associator pseudo-concept Y (of
	 * type {@link IConceptAssociationInfo} Inverse of
	 * {@link #C_ASSOCIATOR_CHECKED_BY}
	 */
	C_CHECKS_ASSOCIATOR,
	/** Inverse of {@link #C_CHECKS_ASSOCIATOR} */
	C_ASSOCIATOR_CHECKED_BY(C_CHECKS_ASSOCIATOR),
	/**
	 * Highly similar to {@link #C_CHECKS_ASSOCIATOR}, but specifically connecting a
	 * Profile/concept relating to the nature of a Being, which is more abstract
	 * than a form, to an associator of a form; basically linking the identity of a
	 * Being to a Form. Inverse of {@link #C_FORM2BEING_ASSOCIATOR_CHECKED_BY}
	 */
	C_CHECKS_FORM2BEING_ASSOCIATOR,
	/** Inverse of {@link #C_CHECKS_FORM2BEING_ASSOCIATOR} */
	C_FORM2BEING_ASSOCIATOR_CHECKED_BY(C_CHECKS_FORM2BEING_ASSOCIATOR),
	/**
	 * A relation where X is a LogicConcept connecting to a property Z via
	 * {@link #CHARACTERIZED_BY}; this indicates that the Concept connected to X via
	 * {@link #CHARACTERIZES} has a value of Y for property Z. Inverse of
	 * {@link #VALUE_OF}
	 */
	HAS_VALUE,
	/** Inverse of {@link #HAS_VALUE} */
	VALUE_OF(HAS_VALUE),
	/**
	 * describes a relation where X is a principle and Y is an event characterized
	 * by this principle, e.g. X may be Death and Y may be an event of death
	 */
	IS_PRINCIPLE_OF,
	/** inverse of {@link #IS_PRINCIPLE_OF} */
	OF_PRINCIPLE,
	/** describes a relationship where X creates Y, e.g. a Tree CREATES Fruit */
	CREATES,
	/** inverse of {@link #CREATES} */
	CREATED_BY(CREATES),
	/** describes a relationship where X has a social bond with Y */
	HAS_SOCIAL_BOND_TO,
	/** inverse of {@link #HAS_SOCIAL_BOND_TO} */
	RECIPIENT_OF_SOCIAL_BOND_FROM(HAS_SOCIAL_BOND_TO),
	/** To indicate what is known about what another party knows, so to speak */
	KNOWS,
	/** inverse of {@link #KNOWS} */
	KNOWN_BY(KNOWS),
	/** describes a relation where X is a part that makes up Y */
	PART_OF,
	/** inverse of {@link #PART_OF} */
	HAS_PART(PART_OF),
	/** describes a relation where X is a member of group/association Y */
	MEMBER_OF,
	/** inverse of {@link #MEMBER_OF} */
	HAS_MEMBER(MEMBER_OF),
	/** describes a relation where X is the agent of event Y */
	DOES,
	/** inverse of {@link #DOES} */
	DONE_BY(DOES),
	/**
	 * Indicates X has the ability to be the agent of an event Y. Used to connect
	 * what actions one can do and kknow what actions others can do
	 */
	HAS_ABILITY_TO,
	/** inverse of {@link #HAS_ABILITY_TO} */
	IS_ABILITY_OF(HAS_ABILITY_TO),
	/** describes a relation where event X affects the patient Y */
	AFFECTS,
	/** inverse of {@link #AFFECTS} */
	AFFECTED_BY(AFFECTS),
	/** describes a relation where event X acts upon the Theme Y */
	ACTS_UPON,
	/** inverse of {@link #ACTS_UPON} */
	ACTED_UPON_BY(ACTS_UPON),
	/** describes a relation where X gets rid of Y */
	CONSUMES,
	/** inverse of {@link #CONSUMES} */
	CONSUMED_BY(CONSUMES),
	/** Relation where X changes Y in some way */
	CHANGES,
	/** Inverse of {@link #CHANGES} */
	CHANGED_BY(CHANGES),
	/**
	 * X and Y have the same location. {@linkplain #bidirectional() Bidirectional.}
	 */
	WITH,
	/** describes that X happens or is found at the location Y */
	AT_LOCATION,
	/** inverse of {@link #HAPPENS_AT} */
	PLACE_WHERE(AT_LOCATION),
	/**
	 * describes that X and Y have the same time. {@linkplain #bidirectional()
	 * Bidirectional.}
	 */
	DURING,
	/** describes that X happened at the time Y */
	AT_TIME,
	/** inverse of {@link #AT_TIME} */
	TIME_OF,
	/** describes that X triggers Y feeling */
	FEELS_LIKE,
	/** inverse of {@link #FEELS_LIKE} */
	IS_FELT(FEELS_LIKE),
	/** describes that X is a number that counts Y */
	QUANTIFIES,
	/** inverse of {@link #QUANTIFIES} */
	QUANTIFIED_AS(QUANTIFIES),
	/** describes that X causes Y to occur */
	CAUSES,
	/** inverse of {@link #CAUSES} */
	CAUSED_BY(CAUSES),
	/** Indicates that X is a PROCESS which includes action Y */
	ENTAILS,
	/** Inverse of {@link #ENTAILS} */
	ENTAILED_BY,
	/** describes that X is done after Y, used for PROCESSES. */
	FOLLOWS,
	/** inverse of {@link #FOLLOWS} */
	PRECEDES(FOLLOWS),
	/**
	 * X is something which sustains Y (e.g. food). Inverse of {@link #SUSTAINED_BY}
	 */
	SUSTAINS,
	/** inverse of {@link #SUSTAINS} */
	SUSTAINED_BY,
	/**
	 * This relation indicates that two traits are considered mutually exclusive.
	 * Mainly applies to traits that are not actually mutually exclusive (e.g.
	 * sexes) but can be perceived as such. {@linkplain #bidirectional()
	 * Bidirectional.}
	 */
	MUTUAL_EXCLUSION;

	private static final Set<ConceptRelationType> IS_OR_SUBCLASS = EnumSet.of(IS, IS_SUPERTYPE_OF);
	private static final Set<ConceptRelationType> IS_OR_SUPERCLASS = EnumSet.of(IS, IS_SUBTYPE_OF);

	private ConceptRelationType opposite;

	private ConceptRelationType() {
		opposite = this;
	}

	private ConceptRelationType(ConceptRelationType opposite) {
		this.opposite = opposite;
		opposite.opposite = this;
	}

	@Override
	public IConceptRelationType invert() {
		return opposite;
	}

	@Override
	public boolean bidirectional() {
		return opposite == this;
	}

	@Override
	public boolean characterizesOther() {
		return this == CHARACTERIZES;
	}

	@Override
	public boolean isCharacterizedByOther() {
		return this == CHARACTERIZED_BY;
	}

	/**
	 * Returns a set contianing the relations that represent that two things are
	 * equivalent or that the other thing is a subtype or member of this type
	 */
	public static Set<ConceptRelationType> getIsOrSubclass() {
		return IS_OR_SUBCLASS;
	}

	/**
	 * Returns a set contianing the relations that represent that two things are
	 * equivalent or that the other thing is the superclass of this
	 */
	public static Set<ConceptRelationType> getIsOrSuperclass() {
		return IS_OR_SUPERCLASS;
	}

}
