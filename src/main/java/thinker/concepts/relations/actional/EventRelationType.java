package thinker.concepts.relations.actional;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import things.interfaces.UniqueType;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.general_types.IDescriptiveConcept;
import thinker.concepts.general_types.IPatternConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.mind.needs.INeedConcept;

/**
 * 
 * Action Relations encode state changes in the relational space, as opposed to
 * ConceptRelations which indicate static states. Usually meant to be drawn from
 * the "THIS_ACTION" or "SATISFIER" concept to something else, or an action to
 * something else in the Concept Space.
 * 
 * 
 * @author borah
 *
 */
@SuppressWarnings("unchecked")
public enum EventRelationType implements IEventRelationType {
	/** describes that the action satisfies Y Goal */
	SATISFIES(EventCategory.SATISFACTION, Set.of(ConceptType.GOAL)), SATISFIED_BY(SATISFIES),
	/** Describes that an action relation creates things with Y concept */
	CREATES(EventCategory.GENERATION, IProfile.class), CREATED_BY(CREATES),
	/**
	 * Describes that an action relation joins parts with Y concept to a form; a
	 * counterpart to {@link #APPEND_TO}
	 */
	ATTACHES(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART),
			IProfile.class),
	ATTACHED_BY(ATTACHES),

	/**
	 * Describes that an action relation joins some part to a form Y; a counterpart
	 * to {@link #ATTACHES}C
	 */
	APPEND_TO(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM),
			IProfile.class),
	APPENDED_BY(APPEND_TO),

	/**
	 * Describes that an action relation detaches some part with Y concept from a
	 * form; a counterpart to {@link #REMOVE_FROM}
	 */
	DETACHES(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART),
			IProfile.class),
	DETACHED_BY(DETACHES),

	/**
	 * Describes that an action relation detaches some part from a form Y; a
	 * counterpart to {@link #DETACHES}
	 */
	REMOVE_FROM(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM),
			IProfile.class),
	REMOVED_FROM_BY(REMOVE_FROM),
	/**
	 * To convey a relation which causes a Thing with label Y to change location.
	 * Counterpart to {@link #POSITIONS_TARGET_AT} and
	 * {@link #MOVES_TARGET_AWAY_FROM}
	 * 
	 */
	REPOSITIONS(EventCategory.POSITIONING, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM),
			IProfile.class),
	REPOSITIONED_BY(REPOSITIONS),
	/**
	 * Convey a relation where an action X causes something to move to location Y
	 * (whether moved or created or something else); counterpart to
	 * {@link #REPOSITIONS}
	 */
	POSITIONS_TARGET_AT(EventCategory.POSITIONING, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PLACE),
			IProfile.class),
	BECOMES_POSITION_OF_TARGET(POSITIONS_TARGET_AT),

	/**
	 * Convey a relation where an action X causes something to move AWAY from a
	 * location Y (whether moved or created or something else); counterpart to
	 * {@link #REPOSITIONS}
	 */
	MOVES_TARGET_AWAY_FROM(EventCategory.POSITIONING,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PLACE), IProfile.class),
	/** Inverse of {@link #MOVES_TARGET_AWAY_FROM} */
	IS_SOURCE_OF_MOVE(MOVES_TARGET_AWAY_FROM),
	/**
	 * To convey a relation which causes a part with label Y to change what it is
	 * held by. Counterpart to {@link #PUTS_TARGET_AT} and
	 * {@link #PUTS_TARGET_AWAY_FROM}
	 * 
	 */
	PUTS_PART(EventCategory.POSITIONING, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART),
			IProfile.class),
	PART_PUT_BY(PUTS_PART),

	/**
	 * Convey a relation where an action X causes something to move to part Y;
	 * counterpart to {@link #PUTS_PART}
	 */
	PUTS_TARGET_AT(EventCategory.POSITIONING, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART),
			IProfile.class),
	IS_DESTINATION_OF_PUT(PUTS_TARGET_AT),
	/**
	 * Convey a relation where an action X causes something to move *out of* part Y;
	 * counterpart to {@link #PUTS_PART}
	 */
	PUTS_TARGET_AWAY_FROM(EventCategory.POSITIONING, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART),
			IProfile.class),
	/** Inverse of {@link #PUTS_TARGET_AWAY_FROM} */
	IS_SOURCE_OF_PUT_AWAY(PUTS_TARGET_AWAY_FROM),
	/**
	 * describes that the action relation destroys Y thing
	 */
	DESTROYS(EventCategory.DESTRUCTION, IProfile.class), DESTROYED_BY(DESTROYS),
	/**
	 * Describes an action that transforms the nature of something of concept Y.
	 */
	TRANSFORMS(EventCategory.TRANSFORMATION, IProfile.class), TRANSFORMED_BY(TRANSFORMS),
	/**
	 * Describes an action that transforms something into the profile or descriptive
	 * concept Y
	 */
	TRANSFORMS_INTO(EventCategory.TRANSFORMATION),
	/** Inverse of {@link #TRANSFORMS_INTO} */
	RESULT_OF_TRANSFORMATION_BY(TRANSFORMS_INTO),
	/**
	 * Describes an action that instills some feeling in something else.
	 */
	CHANGES_FEELINGS_OF(EventCategory.TRANSFORMATION,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_FEELINGS_CHANGED_BY(CHANGES_FEELINGS_OF),
	/**
	 * Describes an action that changes the knowledge of Y
	 */
	CHANGES_KNOWLEDGE_OF(EventCategory.TRANSFORMATION,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_KNOWLEDGE_CHANGED_BY(CHANGES_KNOWLEDGE_OF),

	/**
	 * Describes an action that gives knowledge of a topic Y
	 */
	GIVES_KNOWLEDGE_ABOUT(EventCategory.TRANSFORMATION, IProfile.class),
	/** Inverse of {@link #GIVES_KNOWLEDGE_ABOUT} */
	KNOWLEDGE_GIVEN_BY(GIVES_KNOWLEDGE_ABOUT),

	/**
	 * Describes an action that removes knowledge of a topic Y
	 */
	REMOVES_KNOWLEDGE_ABOUT(EventCategory.TRANSFORMATION, IProfile.class),
	/** Inverse of {@link #REMOVES_KNOWLEDGE_ABOUT} */
	KNOWLEDGE_REMOVED_BY(REMOVES_KNOWLEDGE_ABOUT),
	/**
	 * Describes an action that removes the membership of Y to a gorup
	 */
	REMOVES_MEMBERSHIP(EventCategory.CONNECTION,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_MEMBERSHIP_REMOVED_BY(REMOVES_MEMBERSHIP),
	/**
	 * Describes an action that adds the membership of Y to a group
	 */
	ADDS_MEMBERSHIP(EventCategory.CONNECTION,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_MEMBERSHIP_ADDED_BY(ADDS_MEMBERSHIP),

	/**
	 * Describes an action that removes a member of a group Y
	 */
	REMOVES_MEMBER(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_MEMBER_REMOVED_BY(REMOVES_MEMBERSHIP),
	/**
	 * Describes an action that adds a membership to a group Y
	 */
	ADDS_MEMBER(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_MEMBER_ADDED_BY(ADDS_MEMBER),

	/**
	 * Describes an action that grants an ability to do some action Y
	 */
	GRANTS_ABILITY(EventCategory.TRANSFORMATION, IActionConcept.class),
	/** Inverse of {@link #GRANTS_ABILITY} */
	GRANTED_BY(GRANTS_ABILITY),
	/**
	 * Describes an action that removes an ability to do some action Y
	 */
	REVOKE_ABILITY(EventCategory.TRANSFORMATION, IActionConcept.class),
	/** Inverse of {@link #REVOKE_ABILITY} */
	ABILITY_REVOKED_BY(REVOKE_ABILITY),

	/**
	 * Describes an action that grants an ability (to do some action) to Y
	 */
	GRANTS_ABILITY_TO(EventCategory.TRANSFORMATION,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	GRANTED_ABILITY_BY(GRANTS_ABILITY_TO),
	/**
	 * Describes an action that removes an ability (to do some action) to Y
	 */
	REVOKE_ABILITY_FROM(EventCategory.TRANSFORMATION,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	/** Inverse of {@link #REVOKE_ABILITY_FROM} */
	REVOKED_BY(REVOKE_ABILITY_FROM),
	/**
	 * Describes an action that can tether a spirit to Y
	 */
	EMBODIES_AS(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM),
			IProfile.class),
	/** Inverse of {@link #EMBODIES_AS} */
	IS_DESTINATION_OF_EMBODIMENT(EMBODIES_AS),
	/**
	 * Describes an action that can detether a being from Y
	 */
	DISEMBODIES_FROM(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM),
			IProfile.class),
	/** Inverse of {@link #DISEMBODIES_FROM} */
	IS_SOURCE_OF_DISEMBODIMENT(DISEMBODIES_FROM),
	/**
	 * Describes an action that can change the embodiment of a being Y
	 */
	CHANGES_BODY_OF(EventCategory.CONNECTION, IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE),
			IProfile.class),
	HAS_BODY_CHANGED_BY(CHANGES_BODY_OF),
	/** what need the action increases the value of */
	SATIATES_NEED(EventCategory.TRANSFORMATION, INeedConcept.class), NEED_SATIATED_BY(SATIATES_NEED),
	/** what need this decreases the value of */
	WORSENS_NEED(EventCategory.TRANSFORMATION, INeedConcept.class), NEED_WORSENED_BY(WORSENS_NEED),
	/** what proprety or feeling this action increases */
	INCREASES_VALUE(EventCategory.TRANSFORMATION, (o) -> ((IPropertyConcept) o).isEnumerable(), IPropertyConcept.class),
	VALUE_INCREASED_BY(INCREASES_VALUE),
	/**
	 * what property or feeling this action decreases, which may be a feeling or a
	 * stat
	 */
	DECREASES_VALUE(EventCategory.TRANSFORMATION, (o) -> ((IPropertyConcept) o).isEnumerable(), IPropertyConcept.class),
	VALUE_DECREASED_BY(DECREASES_VALUE),
	/** What question the action answers */
	ANSWERS(EventCategory.ANSWERING, IWhQuestionConcept.class), ANSWERED_BY(ANSWERS);

	private EventRelationType opposite;
	private Set<ConceptType> endType = Set.of();
	private Set<Class<?>> endClass = Set.of();
	private Predicate<IConcept> allowable = Predicates.alwaysTrue();
	private Integer c;
	private EventCategory eventType;

	private EventRelationType(EventCategory eventType, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		this.eventType = eventType;
	}

	private EventRelationType(EventCategory event, Integer x, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		c = x;
		this.eventType = event;
	}

	private EventRelationType(EventCategory ty, ConceptType et, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.of(et);
		this.endClass = Set.of(ec);
		allowable = pre;
		this.eventType = ty;
	}

	private EventRelationType(EventCategory ty, Integer c, Collection<ConceptType> et, Predicate<IConcept> pre,
			Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.copyOf(et);
		this.endClass = Set.of(ec);
		allowable = pre;
		this.c = c;
		this.eventType = ty;
	}

	private EventRelationType(EventCategory ty, Collection<ConceptType> et, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.copyOf(et);
		this.endClass = Set.of(ec);
		this.eventType = ty;
	}

	private EventRelationType(EventCategory ty, Integer c, Collection<ConceptType> et, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.copyOf(et);
		this.endClass = Set.of(ec);
		this.c = c;
		this.eventType = ty;
	}

	private EventRelationType(EventCategory ty, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		allowable = pre;
		this.eventType = ty;
	}

	private EventRelationType(EventCategory ty, Integer c, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		allowable = pre;
		this.c = c;
		this.eventType = ty;
	}

	private EventRelationType(EventRelationType opposite) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(IPrincipleConcept.class);
		this.endType = Set.of(ConceptType.ACTION, ConceptType.C_PATTERN);
		this.eventType = opposite.eventType;
	}

	private EventRelationType(EventRelationType opposite, Integer c) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(IPrincipleConcept.class);
		this.endType = Set.of(ConceptType.ACTION, ConceptType.C_PATTERN);
		this.c = c;
		this.eventType = opposite.eventType;
	}

	@Override
	public IConceptRelationType invert() {
		return opposite;
	}

	@Override
	public Set<ConceptType> getEndTypes() {
		return this.endType;
	}

	@Override
	public Set<Class<?>> getEndClasses() {
		return this.endClass;
	}

	@Override
	public Object checkEndType(Object node) {
		if (node instanceof IConcept cc) {
			Object supermsg = IEventRelationType.super.checkEndType(node);
			if (this.endType.contains(ConceptType.C_PATTERN) && node instanceof IPatternConcept ipc) {
				if (!ipc.isAction()) {
					return node + " is not an action pattern";
				}
			}
			if (supermsg instanceof String) {
				return supermsg;
			}
			if (allowable.test(cc)) {
				return supermsg;
			}
			return node + " failed expected predicate: " + allowable;
		}
		return node + " is not instanceof " + IConcept.class.getSimpleName();
	}

	@Override
	public Integer maxPermitted() {
		return c;
	}

	@Override
	public boolean bidirectional() {
		return opposite == this;
	}

	@Override
	public EventCategory getEventType() {
		return eventType;
	}

}
