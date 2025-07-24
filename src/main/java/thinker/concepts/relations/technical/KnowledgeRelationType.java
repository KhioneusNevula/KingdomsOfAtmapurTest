package thinker.concepts.relations.technical;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import things.interfaces.UniqueType;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.application.IConceptAssociationInfo;
import thinker.concepts.general_types.IActionPatternConcept;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.general_types.IDescriptiveConcept;
import thinker.concepts.general_types.IMemoryConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.general_types.IProcessConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.ITypePatternConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.util.RelationPredicates;
import thinker.goals.IGoalConcept;
import thinker.goals.IGoalMemoryConcept;
import thinker.language.words.ILemmaWord;

/** All technical relation types to hold knowledge about things */
@SuppressWarnings("unchecked")
public enum KnowledgeRelationType implements IConceptRelationType {
	/**
	 * Describes a relation where X is a {@link IProfile} or
	 * {@link IPrincipleConcept} of some kind, and Y is a {@link IConnectorConcept}
	 * (of the {@link ConnectorType#EVENTIVE} type) that represents the version of
	 * this profile or event at a specific time. {@linkplain #bidirectional()
	 * Bidirectional}, since it doesn't matter what direction it is.
	 */
	WAS(RelationPredicates.requireConnectorConceptType(ConnectorType.EVENTIVE), IConnectorConcept.class),
	/**
	 * A relation where some concept has to be quick access to the FOCUS. Inverse of
	 * {@link #QUICKLY_ACCESSIBLE_TO}.
	 */
	QUICK_ACCESS,
	/** Inverse of {@link #QUICK_ACCESS} */
	QUICKLY_ACCESSIBLE_TO(QUICK_ACCESS, RelationPredicates.requireExactConcept(IConcept.FOCUS),
			IPrincipleConcept.class),

	/**
	 * Used to indicate that the connected action concept is an action ready to
	 * execute
	 */
	A_READY(IActionConcept.class),
	/** Inverse of {@link #A_READY} */
	A_READY_inv(A_READY, RelationPredicates.requireExactConcept(IConcept.FOCUS), IPrincipleConcept.class),

	/**
	 * A relation where some concept has to be of quick Access to
	 * {@link IConcept#ENVIRONMENT}, {@link IConcept#SENSING}, or
	 * {@link IConcept#RELEVANCE} so that it is known as something accessible or
	 * otherwise part of one's memory
	 */
	THERE_EXISTS,
	/** Inverse of {@link #S_THERE_EXISTS} */
	EXISTS_IN(THERE_EXISTS,
			RelationPredicates.requireExactConcept(IConcept.SENSING, IConcept.RELEVANCE, IConcept.ENVIRONMENT),
			IPrincipleConcept.class),
	/**
	 * Run from a {@link IMemoryConcept} to a concept that features centrally in
	 * this memory concept. E.g. a {@link IGoalMemoryConcept} must have one of these
	 * to the goal it represents. Inverse of {@link #M_TOPIC_OF}
	 */
	M_ABOUT,
	/** Inverse of {@link #M_ABOUT} */
	M_TOPIC_OF(M_ABOUT, IMemoryConcept.class),

	/**
	 * A possible pattern an action can use with roles; link {@link #A_PATTERN_OF}.
	 * An inherently "or-like" relation
	 */
	A_HAS_PATTERN(IActionPatternConcept.class),

	/**
	 * Inverse of {@link #A_HAS_PATTERN}
	 */
	A_PATTERN_OF(A_HAS_PATTERN, 1, IActionConcept.class),
	/**
	 * A possible pattern of traits that a type profile can have; an inherently
	 * or-like relation. Inverse of {@link #T_PATTERN_OF}
	 */
	T_HAS_PATTERN(ITypePatternConcept.class),
	/**
	 * Inverse of {@link #T_HAS_PATTERN}
	 */
	T_PATTERN_OF(T_HAS_PATTERN, RelationPredicates.requireTypeProfile(), IProfile.class),

	/**
	 * A utility relation to mark Wh-questions in a knowledge graph as being a type
	 * of another profile, so they can reference something else. Inverse of
	 * {@link #WH_IS_REFERENCE_TYPE_OF}
	 */
	WH_REFERENCES_TYPE(RelationPredicates.requireIndefiniteProfile(), IProfile.class),
	/** Inverse of {@link #WH_REFERENCES_TYPE} */
	WH_IS_REFERENCE_TYPE_OF(WH_REFERENCES_TYPE, IWhQuestionConcept.class),
	/**
	 * Indicates that X is referred to by the word or name Y. Inverse of
	 * {@link #NAME_OF}
	 */
	NAMED(ILemmaWord.class),
	/** inverse of {@link #NAMED} */
	NAME_OF(NAMED, RelationPredicates.requireNonUtilityConcept(), IConcept.class),
	/**
	 * Another kind of "is" relation where X is a descriptive concept that is a
	 * supertype of Y. Inverse of {@link #IS_SUBTYPE_OF}.
	 */
	IS_SUPERTYPE_OF(IPropertyConcept.class),
	/** Inverse of {@link #IS_SUPERTYPE_OF} */
	IS_SUBTYPE_OF(IS_SUPERTYPE_OF, IPropertyConcept.class),
	/**
	 * Used to connect some concept X to an applier/associator pseudo-concept Y (of
	 * type {@link IConceptAssociationInfo} Inverse of
	 * {@link #C_ASSOCIATOR_CHECKED_BY}
	 */
	C_CHECKS_ASSOCIATOR(1, IConceptAssociationInfo.class),
	/** Inverse of {@link #C_CHECKS_ASSOCIATOR} */
	C_ASSOCIATOR_CHECKED_BY(C_CHECKS_ASSOCIATOR,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FORM, UniqueType.PLACE, UniqueType.PHENOMENON,
					UniqueType.PART),
			IDescriptiveConcept.class),
	/**
	 * Used to connect some concept X to a perceptor pseudo-concept Y (of type
	 * {@link IConceptAssociationInfo} Inverse of {@link #C_PERCEPTOR_OF}
	 */
	C_ASSOCIATED_TO_PERCEPTOR(1, IConceptAssociationInfo.class),
	/** Inverse of {@link #C_ASSOCIATED_TO_PERCEPTOR} */
	C_PERCEPTOR_OF(C_ASSOCIATED_TO_PERCEPTOR, 1),
	/**
	 * Highly similar to {@link #C_CHECKS_ASSOCIATOR}, but specifically connecting a
	 * Profile/concept relating to the nature of a Being, which is more abstract
	 * than a form, to an associator of a form; basically linking the identity of a
	 * Being to a Form. Inverse of {@link #C_FORM2BEING_ASSOCIATOR_CHECKED_BY}
	 */
	C_CHECKS_FORM2BEING_ASSOCIATOR(1, (e) -> ((IConceptAssociationInfo) e).getApplier().forType() == UniqueType.FORM,
			IConceptAssociationInfo.class),
	/** Inverse of {@link #C_CHECKS_FORM2BEING_ASSOCIATOR} */
	C_FORM2BEING_ASSOCIATOR_CHECKED_BY(C_CHECKS_FORM2BEING_ASSOCIATOR,
			IDescriptiveConcept.matchesAnyUniqueTypesPredicate(UniqueType.FIGURE), IDescriptiveConcept.class),
	/**
	 * Used for a {@link IGoalConcept} to represent that this goal concept involves
	 * Y in some way. inverse of {@link #G_INVOLVED_IN}
	 */
	G_INVOLVES,
	/** Inverse of {@link #G_INVOLVES} */
	G_INVOLVED_IN(G_INVOLVES, IGoalConcept.class),
	/**
	 * Indicates that X is a Process or action that requires a given condition Y.
	 * Inverse of {@link #P_REQUIRED_BY}
	 */
	P_REQUIRES(IGoalConcept.class),
	/** Inverse of {@link #P_REQUIRES} */
	P_REQUIRED_BY(P_REQUIRES, IProcessConcept.class, IActionConcept.class),
	/**
	 * An expected resolution of a goal used to generate actions. E.g.
	 */
	P_EXPECTS(IGoalConcept.class),
	/** Inverse of {@link #P_EXPECTS} */
	P_EXPECTED_BY(P_EXPECTS),
	/** Indicates that X is a goal which is solved by some some action Y */
	P_SOLVED_BY(IActionConcept.class, IActionPatternConcept.class),
	/** Inverse of {@link #P_SOLVED_BY} */
	P_SOLVES(P_SOLVED_BY, IGoalConcept.class),
	/**
	 * Indicates a goal X has been failed by an action Y, i.e. the action failed to
	 * address it
	 */
	P_FAILED_BY(IActionConcept.class, IActionPatternConcept.class),
	/** Inverse of {@link #P_FAILED_BY} */
	P_FAILS(P_FAILED_BY, IGoalConcept.class),
	/**
	 * Indicates that X is an action of a process which answered a knowledge
	 * condition as Y
	 */
	P_ANSWERED(IGoalConcept.class),
	/** Inverse of {@link #P_ANSWERED} */
	P_ANSWERED_BY(P_ANSWERED, IActionConcept.class),

	/**
	 * Usded to indicate a question Y asked by X goal concept, inverse of
	 * {@link #P_ASKED_BY}
	 */
	P_ASKS(IWhQuestionConcept.class),
	/** Inverse of {@link #P_ASKS} */
	P_ASKED_BY(P_ASKS, IGoalConcept.class),
	/** describes that X is done after Y, used for PROCESSES. */
	P_FOLLOWS(IActionConcept.class, IProcessConcept.class),
	/** inverse of {@link #P_FOLLOWS} */
	P_PRECEDES(P_FOLLOWS, IActionConcept.class),
	/**
	 * Used to link a wh-question to its answer. Inverse of {@link #WH_ANSWERS}
	 */
	WH_ANSWERED_BY,
	/** Inverse of {@link #Q_HAS_ANSWER} */
	WH_ANSWERS(WH_ANSWERED_BY, IWhQuestionConcept.class),
	/**
	 * This relation indicates that two traits are considered mutually exclusive.
	 * Mainly applies to traits that are not actually mutually exclusive (e.g.
	 * sexes) but can be perceived as such. {@linkplain #bidirectional()
	 * Bidirectional.}
	 */
	MUTUALY_EXCLUSIVE_WITH(IDescriptiveConcept.class);

	private KnowledgeRelationType opposite;
	private Set<ConceptType> endType = Set.of();
	private Set<Class<?>> endClass = Set.of();
	private Predicate<IConcept> pred = Predicates.alwaysTrue();
	private Integer c = null;

	private KnowledgeRelationType(Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
	}

	private KnowledgeRelationType(Integer c, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		this.c = c;
	}

	private KnowledgeRelationType(Predicate<IConcept> p, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
	}

	private KnowledgeRelationType(Integer c, Predicate<IConcept> p, Class<? extends IConcept>... ec) {
		opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
		this.c = c;
	}

	private KnowledgeRelationType(KnowledgeRelationType opposite, Class<? extends IConcept>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
	}

	private KnowledgeRelationType(KnowledgeRelationType opposite, Integer c, Class<? extends IConcept>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
		this.c = c;
	}

	private KnowledgeRelationType(KnowledgeRelationType opposite, Predicate<IConcept> p,
			Class<? extends IConcept>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
	}

	private KnowledgeRelationType(KnowledgeRelationType opposite, Integer c, Predicate<IConcept> p,
			Class<? extends IConcept>... ec) {
		this.opposite = opposite;
		opposite.opposite = this;
		this.endClass = Set.of(ec);
		this.pred = p;
		this.c = c;
	}

	@Override
	public Set<Class<?>> getEndClasses() {
		return endClass;
	}

	@Override
	public Object checkEndType(Object node) {
		if (node instanceof IConcept cc) {
			Object supermsg = IConceptRelationType.super.checkEndType(node);
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
	public IConceptRelationType invert() {
		return opposite;
	}

	@Override
	public boolean bidirectional() {
		return opposite == this;
	}

}
