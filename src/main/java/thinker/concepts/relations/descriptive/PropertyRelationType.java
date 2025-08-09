package thinker.concepts.relations.descriptive;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.general_types.IDescriptiveConcept;
import thinker.concepts.general_types.IIntegerValueConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.ITypePatternConcept;
import thinker.concepts.general_types.IValueConcept;
import thinker.concepts.profile.IProfile;
import thinker.mind.emotions.IFeeling;
import thinker.mind.needs.INeedConcept;

/**
 * Static relation types used in conditions or to create knowledge baout things
 */
@SuppressWarnings({ "unchecked" })
public enum PropertyRelationType implements IDescriptiveRelationType {

	/** Used to describe that a profile has this trait */
	HAS_TRAIT(IPropertyConcept.class),
	/** Inverse of {@link PropertyRelationType#HAS_TRAIT} */
	IS_TRAIT_OF(HAS_TRAIT, IProfile.class, ITypePatternConcept.class),
	/**
	 * A relation thnat expresses a relation where X is a ConnectorConcept
	 * connecting to a property Z via {@link #IS}; this indicates that the Concept
	 * connected to X via {@link #IS} has a value of Y for property Z. Inverse of
	 * {@link #VALUE_OF}
	 */
	HAS_VALUE(1, IValueConcept.class),
	/** Inverse of {@link #HAS_VALUE} */
	VALUE_OF(HAS_VALUE,
			(o) -> o instanceof INeedConcept
					|| (o instanceof IConnectorConcept c && c.getConnectorType() == ConnectorType.PROPERTY_AND_VALUE),
			IConnectorConcept.class, INeedConcept.class),

	// VALUE_GREATER_THAN()
	/**
	 * describes a relation where X is a principle and Y is an event or other kind
	 * of concept characterized by this principle, e.g. X may be Death and Y may be
	 * an event of death. Only one principle can be linked to.
	 */
	IS_PRINCIPLE_OF,
	/** inverse of {@link #IS_PRINCIPLE_OF} */
	OF_PRINCIPLE(IS_PRINCIPLE_OF, 1, IPrincipleConcept.class),

	/**
	 * Indicates that a concept X is personified as the Profile Y, which is usually
	 * a deity for example. Inverse of {@link #PERSONIFIES}
	 */
	PERSONIFIED_AS(IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE, UniqueType.COLLECTIVE),
			IProfile.class, ITypePatternConcept.class),
	/** Inverse of {@link #HAS_BODY} */
	PERSONIFIES(PERSONIFIED_AS),
	/** To indicate what is known about what another party knows, so to speak */
	KNOWS,
	/** inverse of {@link #KNOWS} */
	KNOWN_BY(KNOWS, IProfile.class, ITypePatternConcept.class),
	/**
	 * Indicates X has the ability to be the agent of an event Y. Used to connect
	 * what actions one can do and kknow what actions others can do
	 */
	HAS_ABILITY_TO,
	/** inverse of {@link #HAS_ABILITY_TO} */
	IS_ABILITY_OF(HAS_ABILITY_TO,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.COLLECTIVE, UniqueType.FIGURE),
			IProfile.class, ITypePatternConcept.class),
	/** describes that X happened at the time Y */
	AT_TIME,
	/** inverse of {@link #AT_TIME} */
	TIME_OF(AT_TIME),
	/** describes that X triggers Y feeling or has Y feeling */
	FEELS_LIKE(IFeeling.class),
	/** inverse of {@link #FEELS_LIKE} */
	IS_FELT(FEELS_LIKE),
	/** describes that X is a number that counts Y */
	QUANTIFIES(IDescriptiveConcept.class),
	/** inverse of {@link #QUANTIFIES} */
	QUANTIFIED_AS(QUANTIFIES, IIntegerValueConcept.class),
	/** describes that X causes Y to occur */
	CAUSES,
	/** inverse of {@link #CAUSES} */
	CAUSED_BY(CAUSES);

	private PropertyRelationType opposite;
	private Set<ConceptType> endType = Set.of();
	private Set<Class<?>> endClass = Set.of();
	private Predicate<IConcept> pred = Predicates.alwaysTrue();
	private Integer c = null;

	private PropertyRelationType(Class<?>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
	}

	private PropertyRelationType(Integer c, Class<?>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		this.c = c;
	}

	/*
	 * private PropertyRelationType(ConceptType eT, Predicate<IConcept> p,
	 * Class<?>... ec) { opposite = this; this.endType = eT; this.endClass =
	 * Set.of(ec); this.pred = p; }
	 */

	private PropertyRelationType(Predicate<IConcept> p, Class<?>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
	}

	private PropertyRelationType(Integer c, Predicate<IConcept> p, Class<?>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
		this.c = c;
	}

	private PropertyRelationType(PropertyRelationType opposite, Class<?>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
	}

	private PropertyRelationType(PropertyRelationType opposite, Integer c, Class<?>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
		this.c = c;
	}

	private PropertyRelationType(PropertyRelationType opposite, Predicate<IConcept> p, Class<?>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
	}

	private PropertyRelationType(PropertyRelationType opposite, Integer c, Predicate<IConcept> p, Class<?>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
		this.c = c;
	}

	@Override
	public Collection<Class<?>> getEndClasses() {
		return endClass;
	}

	@Override
	public Object checkEndType(Object node) {
		if (node instanceof IConcept cc) {
			Object supermsg = IDescriptiveRelationType.super.checkEndType(node);
			if (supermsg instanceof String) {
				return supermsg;
			}
			if (pred.test(cc)) {
				return supermsg;
			}
			return node + " failed expected predicate: " + pred;
		}
		return node + " is not instanceof " + IConcept.class.getSimpleName();
	}

	@Override
	public Integer maxPermitted() {
		return c;
	}

	@Override
	public Collection<ConceptType> getEndTypes() {
		return endType;
	}

	@Override
	public PropertyRelationType invert() {
		return opposite;
	}

	@Override
	public boolean bidirectional() {
		return opposite == this;
	}

}
