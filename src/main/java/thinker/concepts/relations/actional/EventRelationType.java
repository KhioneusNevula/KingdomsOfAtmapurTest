package thinker.concepts.relations.actional;

import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.general_types.IDescriptiveConcept;
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
	SATISFIES(ConceptType.GOAL), SATISFIED_BY(SATISFIES),
	/** describes that the action acts on Y thing */
	ACTS_ON(IProfile.class), ACTED_ON_BY(ACTS_ON),
	/** describes that the action is performed by Y agent */
	DONE_BY(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	DOES(DONE_BY),
	/** describes that the action or Process happens at Y place */
	HAPPENS_AT(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PLACE), IProfile.class),
	LOCATION_OF_HAPPENING(HAPPENS_AT),
	/** Describes that an action relation creates things with Y concept */
	CREATES(IProfile.class), CREATED_BY(CREATES),
	/** Describes that an action relation fixes things with Y concept */
	FIXES(IProfile.class), FIXED_BY(FIXES),
	/**
	 * To convey a relation which causes a Thing with label Y to change location.
	 * 
	 */
	MOVES(IProfile.class), MOVED_BY(MOVES),
	/**
	 * Convey a relation where an action X causes something to move to location Y
	 */
	MOVES_TARGET_TO(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PLACE), IProfile.class),
	IS_TARGET_OF_MOVE(MOVES_TARGET_TO),
	/** Convey a relation where an action X causes something to move to part Y */
	PUTS_AT(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART), IProfile.class),
	IS_TARGET_OF_PUT(PUTS_AT),
	/**
	 * To convey a relation which causes a Thing with label Y to become held-b-y a
	 * part
	 * 
	 */
	GRABS(IProfile.class), GRABBED_BY(GRABS),
	/**
	 * describes that the action relation destroys Y thing
	 */
	DESTROYS(IProfile.class), DESTROYED_BY(DESTROYS),
	/** Describes that the action relation damages Y thing */
	DAMAGES(IProfile.class), DAMAGED_BY(DAMAGES),
	/**
	 * Describes an action that transforms the nature of something of concept Y.
	 */
	TRANSFORMS(IProfile.class), TRANSFORMED_BY(TRANSFORMS),
	/**
	 * Describes an action that instills some feeling in something else.
	 */
	CHANGES_FEELINGS_OF(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_FEELINGS_CHANGED_BY(CHANGES_FEELINGS_OF),
	/** For objects and phenomena the action uses to complete itself */
	USES(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM, UniqueType.PHENOMENON), IProfile.class),
	USED_BY(USES),
	/**
	 * describes that the action or Process uses Y part of the doer's form to
	 * complete itself
	 */
	USES_PART(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART), IProfile.class),
	USED_BY_PART(USES_PART),
	/**
	 * Describes an action that influences the social opinions of Y
	 */
	INFLUENCES_SOCIAL_OPINIONS_OF(
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_SOCIAL_OPINIONS_INFLUENCED_BY(INFLUENCES_SOCIAL_OPINIONS_OF),
	/**
	 * Describes an action that gives knowledge to Y
	 */
	GIVES_KNOWLEDGE_TO(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	RECEIVES_KNOWLEDGE_BY(GIVES_KNOWLEDGE_TO),
	/**
	 * Describes an action that changes the group membership of Y
	 */
	CHANGES_MEMBERSHIP(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	HAS_MEMBERSHIP_CHANGED_BY(INFLUENCES_SOCIAL_OPINIONS_OF),
	/**
	 * Describes an action that grants an ability (to do some action) to Y
	 */
	GRANTS_ABILITY_TO(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class),
	GRANTED_ABILITY_BY(GRANTS_ABILITY_TO),
	/**
	 * Describes an action that can tether a spirit to Y
	 */
	TETHERS_SPIRIT_TO(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM), IProfile.class),
	SPIRIT_TETHERED_BY(TETHERS_SPIRIT_TO),
	/**
	 * Describes an action that can change the body of a being Y
	 */
	REEMBODIES(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE), IProfile.class),
	REEMBODIED_BY(REEMBODIES),
	/** what need the action increases the value of */
	SATIATES_NEED(INeedConcept.class), NEED_SATIATED_BY(SATIATES_NEED),
	/** what need this decreases the value of */
	WORSENS_NEED(INeedConcept.class), NEED_WORSENED_BY(WORSENS_NEED),
	/** what proprety action increases */
	INCREASES_VALUE((o) -> ((IPropertyConcept) o).isEnumerable(), IPropertyConcept.class),
	VALUE_INCREASED_BY(INCREASES_VALUE),
	/** what the action decreases, which may be a feeling or a stat */
	DECREASES_VALUE((o) -> ((IPropertyConcept) o).isEnumerable(), IPropertyConcept.class),
	VALUE_DECREASED_BY(DECREASES_VALUE),
	/** What question the action answers */
	ANSWERS(IWhQuestionConcept.class), ANSWERED_BY(ANSWERS);

	private EventRelationType opposite;
	private ConceptType endType = ConceptType.NONE;
	private Set<Class<?>> endClass = Set.of();
	private Predicate<IConcept> allowable = Predicates.alwaysTrue();
	private Integer c;

	private EventRelationType(Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
	}

	private EventRelationType(Integer x, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		c = x;
	}

	private EventRelationType(ConceptType et, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = et;
		this.endClass = Set.of(ec);
		allowable = pre;
	}

	private EventRelationType(Integer c, ConceptType et, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = et;
		this.endClass = Set.of(ec);
		allowable = pre;
		this.c = c;
	}

	private EventRelationType(ConceptType et, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = et;
		this.endClass = Set.of(ec);
	}

	private EventRelationType(Integer c, ConceptType et, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = et;
		this.endClass = Set.of(ec);
		this.c = c;
	}

	private EventRelationType(Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		allowable = pre;
	}

	private EventRelationType(Integer c, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		allowable = pre;
		this.c = c;
	}

	private EventRelationType(EventRelationType opposite) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(IPrincipleConcept.class);
		this.endType = ConceptType.ACTION;
	}

	private EventRelationType(EventRelationType opposite, Integer c) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(IPrincipleConcept.class);
		this.endType = ConceptType.ACTION;
		this.c = c;
	}

	@Override
	public IConceptRelationType invert() {
		return opposite;
	}

	@Override
	public ConceptType getEndType() {
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

}
