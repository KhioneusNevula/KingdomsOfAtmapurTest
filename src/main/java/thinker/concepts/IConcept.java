package thinker.concepts;

import things.form.soma.ISoma;
import thinker.actions.IActionConcept;
import thinker.concepts.application.IConceptAssociationInfo;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.general_types.IProcessConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.IValueConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.IGoalConcept;
import thinker.knowledge.timeline.ITimeline.ITimelineNode;
import thinker.language.words.ILexicon.DeicticConcept;
import thinker.mind.emotions.IFeeling;
import thinker.mind.needs.INeedConcept;

/**
 * A Concept is an individual piece of info that can be stored in a relations
 * graph to draw connections between things Some concept rules:
 * 
 * @author borah
 *
 */
public interface IConcept {

	/**
	 * The type of a concept
	 * 
	 * @author borah
	 *
	 */
	public static enum ConceptType {
		/** a property of something, either binary-valued or having enumerated values */
		PROPERTY,
		/** the value of a property, or a numeric count */
		VALUE,
		/**
		 * a profile representing something that can have traits and may or may not be
		 * unique.
		 */
		PROFILE,
		/**
		 * Used to designate a pseudo-concept storing info about how to identify a
		 * physical phenomenon using a concept.
		 */
		C_ASSOCIATION_INFO,
		/** A pattern concept for an action or profile */
		C_PATTERN,
		/** a concept of a general idea or event type defined only by its relations */
		PRINCIPLE,
		/** The concept of an action performed to complete a task */
		ACTION,
		/** a concept of a physical {@link ISoma} that can be constructed */
		RECIPE,
		/** a process with a unique ID */
		PROCESS,
		/** TODO a concept that describes a historical event */
		MEMORY,
		/** a concept to encapsulate TimelineNodes */
		TIMELINE_POINT,
		/** TODO A concept to encapsulate times on a Clock */
		CLOCK_POINT,
		/** a concept that describes the in/decrease of an emotion or Feeling */
		FEELING,
		/** an individual need */
		NEED,
		/** TODO a ritual */
		RITUAL,
		/** TODO a linguistic lexical item */
		C_WORD,
		/**
		 * an identity-less profile-like concept that acts as the answer to a question,
		 * e.g. "(WH_Question) -place_of-> Food. These are often associated with Actions
		 * and have independent ids
		 */
		C_WH_QUESTION,
		/** A concept indicating a set of desired circumstances as a graph */
		GOAL,
		/** TODO a linguistic feature, used for stuff like featural marking of gender */
		LFEATURE,
		/** An AND or OR connector to connect multiple relations */
		C_CONNECTOR,
		/** Used by {@link DeicticConcept}s */
		DEIXIS,
		/** something else */
		OTHER,
		/** nothing */
		NONE;

		public boolean isUtilityType() {
			return this.name().startsWith("C_");
		}
	}

	/**
	 * The idea of need as a foundation to action. Linked to needs by the
	 * {@link KnowledgeRelationType#IS_PRINCIPLE_OF} relation.
	 */
	public static final IPrincipleConcept NECESSITY = IPrincipleConcept.createGenericFundamentalPrinciple("NECESSITY");
	/**
	 * The idea of the "center" of the mind. Serves specific purposes:
	 * <ul>
	 * <li>Linked to unfinished actions using the
	 * {@link KnowledgeRelationType#P_FOLLOWS} relation, the
	 * {@link KnowledgeRelationType#P_REQUIRED_BY} type, or the
	 * {@link KnowledgeRelationType#P_EXPECTED_BY}.
	 * 
	 * <li>Processes are linked to it by the
	 * {@link KnowledgeRelationType#QUICK_ACCESS} relation
	 * </ul>
	 */
	public static final IPrincipleConcept FOCUS = IPrincipleConcept.createGenericFundamentalPrinciple("FOCUS");

	/**
	 * The idea of the "sensing" part of the mind. Any profile or
	 * {@link IConceptAssociationInfo} that is connected to this by a
	 * {@link KnowledgeRelationType#THERE_EXISTS} relation is something that is
	 * being sensed currently
	 */
	public static final IPrincipleConcept SENSING = IPrincipleConcept.createGenericFundamentalPrinciple("SENSING");
	/**
	 * The idea of all profiles that are in the local enviornment, i.e. on the map
	 * or otherwise accessible. Any profile or {@link IConceptAssociationInfo} that
	 * is connected to this by a {@link KnowledgeRelationType#THERE_EXISTS} relation
	 * is something that is accessible in the environment
	 */
	public static final IPrincipleConcept ENVIRONMENT = IPrincipleConcept
			.createGenericFundamentalPrinciple("ENVIRONMENT");

	/**
	 * The idea of all profiles that are known to the bearer of knowledge, including
	 * people as well as important objects. Any profile or
	 * {@link IConceptAssociationInfo} that is connected to this by a
	 * {@link KnowledgeRelationType#THERE_EXISTS} relation is something that is
	 * known personally
	 */
	public static final IPrincipleConcept RELEVANCE = IPrincipleConcept.createGenericFundamentalPrinciple("RELEVANCE");

	/**
	 * The idea of having goals as a foundation to action. Linked to
	 * {@link IGoalConcept}s at the ends of {@link KnowledgeRelationType#P_SOLVES}
	 * relations relation.
	 */
	public static final IPrincipleConcept INTENTIONALITY = IPrincipleConcept
			.createGenericFundamentalPrinciple("INTENTIONALITY");

	/**
	 * The idea of having goals that want to be completed. Linked to
	 * {@link IGoalConcept}s at the ends of
	 * {@link KnowledgeRelationType#P_EXPECTED_BY} relations relation.
	 */
	public static final IPrincipleConcept UNFULFILLMENT = IPrincipleConcept
			.createGenericFundamentalPrinciple("UNFULFILLMENT");

	/**
	 * The idea of having questions about certain things, e.g. having an intention
	 * of eating and wondering What to eat. Questions are linked to this at the end
	 * of {@link KnowledgeRelationType#P_ANSWERS} relations
	 */
	public static final IPrincipleConcept QUESTIONING = IPrincipleConcept
			.createGenericFundamentalPrinciple("QUESTIONING");

	/**
	 * The concept of having the ability to do actions. Linked to all
	 * {@link IActionConcept}s by the {@link PropertyRelationType#IS_PRINCIPLE_OF}
	 * relation.
	 */
	public static final IPrincipleConcept CAPABILITY = IPrincipleConcept
			.createGenericFundamentalPrinciple("CAPABILITY");

	/**
	 * The idea of memories that are discrete timebound events (as opposed to more
	 * freeform memories). Connected using
	 * {@link PropertyRelationType#IS_PRINCIPLE_OF}
	 */
	public static final IPrincipleConcept RECOLLECTION = IPrincipleConcept
			.createGenericFundamentalPrinciple("RECOLLECTION");

	/** The idea of existence itself */
	public static final IPrincipleConcept EXISTENCE = IPrincipleConcept.createGenericFundamentalPrinciple("EXISTENCE");

	/** The idea of nonexistence or anti-existence itself */
	public static final IPrincipleConcept NOTHING = IPrincipleConcept.createGenericFundamentalPrinciple("NOTHING");

	/**
	 * Return the type of this concept
	 * 
	 * @return
	 */
	public ConceptType getConceptType();

	/**
	 * Whether this concept is merely for utility and should not be treated as
	 * something independent (and nameable)
	 * 
	 * @return
	 */
	public default boolean isUtilityConcept() {
		return this.getConceptType().isUtilityType();
	}

	/**
	 * The name of this concept used to compare it cross-culturally or store it
	 * uniquely from other concepts
	 * 
	 * @return
	 */
	public String getUnderlyingName();

	@Override
	public String toString();

	/**
	 * Casts this to an action concept
	 * 
	 * @return
	 */
	public default IActionConcept asAction() {
		return (IActionConcept) this;
	}

	/**
	 * Casts this to a Principle concept
	 * 
	 * @return
	 */
	public default IPrincipleConcept asPrinciple() {
		return (IPrincipleConcept) this;
	}

	/**
	 * Casts this to a label concept
	 * 
	 * @return
	 */
	public default IPropertyConcept asLabel() {
		return (IPropertyConcept) this;
	}

	/**
	 * Casts this to a value concept
	 * 
	 * @return
	 */
	public default IValueConcept asValue() {
		return (IValueConcept) this;
	}

	/**
	 * Casts this to a process concept
	 * 
	 * @return
	 */
	public default IProcessConcept asProcess() {
		return (IProcessConcept) this;
	}

	/**
	 * Casts this to an Enum Trait concept
	 * 
	 * @return
	 */
	/**
	 * public default IEnumTraitConcept asEnumTrait() { return (IEnumTraitConcept)
	 * this; }
	 */

	/**
	 * Casts this to a Sensable Trait concept
	 * 
	 * @return
	 */
	/**
	 * public default ISensableTraitConcept asSensableTrait() { return
	 * (ISensableTraitConcept) this; }
	 */

	/**
	 * Casts this to a Part concept
	 * 
	 * @return
	 */
	/**
	 * public default IPartConcept asPart() { return (IPartConcept) this; }
	 */

	/**
	 * Casts this to an event concept
	 * 
	 * @return
	 */
	/**
	 * public default IMemoryConcept asEvent() { return (IMemoryConcept) this; }
	 */

	/**
	 * Casts this to a timeline point concept
	 * 
	 * @return
	 */
	public default ITimelineNode asTimelinePoint() {
		return (ITimelineNode) this;
	}

	/**
	 * Casts this to a Clock Point concept
	 * 
	 * @return
	 */
	/**
	 * public default IClockNode asClockPoint() { return (IClockNode) this; }
	 */

	/**
	 * Casts this to a Feeling concept
	 * 
	 * @return
	 */
	public default IFeeling asFeeling() {
		return (IFeeling) this;
	}

	/**
	 * Casts this to a Need concept
	 * 
	 * @return
	 */
	public default INeedConcept asNeed() {
		return (INeedConcept) this;
	}

	/**
	 * Casts this to a ritual concept
	 * 
	 * @return
	 */
	/**
	 * public default IRitualConcept asRitual() { return (IRitualConcept) this; }
	 */

	/**
	 * Casts this to a Profile concept
	 * 
	 * @return
	 */
	public default IProfile asProfile() {
		return (IProfile) this;
	}

	/**
	 * Casts this to a name concept
	 * 
	 * @return
	 */
	/**
	 * public default IName asName() { return (IName) this; }
	 */

	/**
	 * Casts this to a question concept
	 * 
	 * @return
	 */
	public default IWhQuestionConcept asWhQuestion() {
		return (IWhQuestionConcept) this;
	}

	/**
	 * Casts this to a logical connector
	 * 
	 * @return
	 */
	public default IConnectorConcept asLogic() {
		return (IConnectorConcept) this;
	}

}
