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
public enum EventRoleRelationType implements IEventRoleRelationType {
	/** describes that the action is performed by Y agent */
	DONE_BY(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE), IProfile.class),
	/** Inverse of {@link #DONE_BY} */
	DOES(DONE_BY),
	/** describes that the action or Process happens at Y place */
	HAPPENS_AT(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PLACE), IProfile.class),
	/** Inverse of {@link #HAPPENS_AT} */
	LOCATION_OF_HAPPENING(HAPPENS_AT),
	/** describes that the action acts on Y thing */
	ACTS_ON(IProfile.class),
	/** Inverse of {@link #ACTS_ON} */
	ACTED_ON_BY(ACTS_ON),

	/** For objects and phenomena the action uses to complete itself */
	USES_MEANS(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM, UniqueType.PHENOMENON),
			IProfile.class),
	/** Inverse of {@link #USES_MEANS} */
	USED_BY(USES_MEANS),
	/**
	 * describes that the action or Process uses Y part of something to complete
	 * itself
	 */
	USES_PART(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.PART), IProfile.class),
	/** Inverse of {@link #USES_PART} */
	USED_BY_PART(USES_PART);

	private EventRoleRelationType opposite;
	private Set<ConceptType> endType = Set.of();
	private Set<Class<?>> endClass = Set.of();
	private Predicate<IConcept> allowable = Predicates.alwaysTrue();
	private Integer c;

	private EventRoleRelationType(Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
	}

	private EventRoleRelationType(Integer x, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		c = x;
	}

	private EventRoleRelationType(ConceptType et, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.of(et);
		this.endClass = Set.of(ec);
		allowable = pre;
	}

	private EventRoleRelationType(Integer c, Collection<ConceptType> et, Predicate<IConcept> pre,
			Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.copyOf(et);
		this.endClass = Set.of(ec);
		allowable = pre;
		this.c = c;
	}

	private EventRoleRelationType(Collection<ConceptType> et, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.copyOf(et);
		this.endClass = Set.of(ec);
	}

	private EventRoleRelationType(Integer c, Collection<ConceptType> et, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endType = Set.copyOf(et);
		this.endClass = Set.of(ec);
		this.c = c;
	}

	private EventRoleRelationType(Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		allowable = pre;
	}

	private EventRoleRelationType(Integer c, Predicate<IConcept> pre, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		allowable = pre;
		this.c = c;
	}

	private EventRoleRelationType(EventRoleRelationType opposite) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(IPrincipleConcept.class);
		this.endType = Set.of(ConceptType.ACTION, ConceptType.C_PATTERN);
	}

	private EventRoleRelationType(EventRoleRelationType opposite, Integer c) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(IPrincipleConcept.class);
		this.endType = Set.of(ConceptType.ACTION, ConceptType.C_PATTERN);
		this.c = c;
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
			Object supermsg = IEventRoleRelationType.super.checkEndType(node);
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

}
