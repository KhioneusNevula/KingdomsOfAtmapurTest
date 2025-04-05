package thinker.concepts.relations.technical;

import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import things.interfaces.UniqueType;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.application.IConceptAssociationInfo;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.general_types.IDescriptiveConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.general_types.IProcessConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.util.RelationPredicates;
import thinker.goals.IGoalConcept;
import thinker.language.words.ILemmaWord;

/** All technical relation types to hold knowledge about things */
@SuppressWarnings("unchecked")
public enum KnowledgeRelationType implements IConceptRelationType {
	/** there is a relation between X and Y, but it is unknown? */
	UNKNOWN(),
	/**
	 * Describes a relation where X is a {@link IProfile} or
	 * {@link IPrincipleConcept} of some kind, and Y is a {@link IConnectorConcept}
	 * (of the {@link ConnectorType#EVENTIVE} type) that represents the version of
	 * this profile or event at a specific time. {@linkplain #bidirectional()
	 * Bidirectional}, since it doesn't matter what direction it is.
	 */
	WAS(RelationPredicates.requireConnectorConceptType(ConnectorType.EVENTIVE), IConnectorConcept.class),

	/**
	 * A utility relation to mark Wh-questions in a knowledge graph so they are easy
	 * to find. Inverse of {@link #WH_IS_INDEFINITE_TYPE_OF}
	 */
	WH_OF_INDEFINITE_TYPE(RelationPredicates.requireIndefiniteProfile(), IProfile.class),
	/** Inverse of {@link #WH_OF_INDEFINITE_TYPE} */
	WH_IS_INDEFINITE_TYPE_OF(WH_OF_INDEFINITE_TYPE, IWhQuestionConcept.class),
	/**
	 * Indicates that X is referred to by the word or name Y. Inverse of
	 * {@link #NAME_OF}
	 */
	NAMED(ILemmaWord.class),
	/** inverse of {@link #NAMED} */
	NAME_OF(NAMED),
	/**
	 * Another kind of "is" relation where X is a descriptive concept that is a
	 * supertype of Y. Inverse of {@link #IS_SUBTYPE_OF}
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
	 * Indicates that X is a Process that requires a given condition Y. Inverse of
	 * {@link #P_REQUIRED_BY}
	 */
	P_REQUIRES(IGoalConcept.class),
	/** Inverse of {@link #P_REQUIRES} */
	P_REQUIRED_BY(P_REQUIRES, IProcessConcept.class),
	/** Indicates that X is a PROCESS which begins with action Y */
	P_ENTAILS(IActionConcept.class),
	/** Inverse of {@link #P_ENTAILS} */
	P_ENTAILED_BY(P_ENTAILS, IProcessConcept.class),
	/**
	 * Used to link a wh-question to its answer. Inverse of {@link #WH_IS_ANSWER_TO}
	 */
	WH_HAS_ANSWER,
	/** Inverse of {@link #Q_HAS_ANSWER} */
	WH_IS_ANSWER_TO(WH_HAS_ANSWER, IWhQuestionConcept.class),

	/**
	 * Indicates that X is an action of a process which answered a knowledge
	 * condition as Y
	 */
	A_ANSWERED(IGoalConcept.class),
	/** Inverse of {@link #P_ANSWERED} */
	A_ANSWERED_BY(A_ANSWERED, IActionConcept.class),
	/**
	 * Usded to indicate a question asked by X action, inverse of
	 * {@link #A_ASKED_BY}
	 */
	A_ASKED(IWhQuestionConcept.class),
	/** Inverse of {@link #A_ASKED} */
	A_ASKED_BY(A_ASKED, IActionConcept.class),
	/** describes that X is done after Y, used for PROCESSES. */
	A_FOLLOWS(IActionConcept.class),
	/** inverse of {@link #P_FOLLOWS} */
	A_PRECEDES(A_FOLLOWS, IActionConcept.class),
	/**
	 * This relation indicates that two traits are considered mutually exclusive.
	 * Mainly applies to traits that are not actually mutually exclusive (e.g.
	 * sexes) but can be perceived as such. {@linkplain #bidirectional()
	 * Bidirectional.}
	 */
	MUTUALY_EXCLUSIVE_WITH(IDescriptiveConcept.class);

	private KnowledgeRelationType opposite;
	private ConceptType endType = ConceptType.NONE;
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
	public ConceptType getEndType() {
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
