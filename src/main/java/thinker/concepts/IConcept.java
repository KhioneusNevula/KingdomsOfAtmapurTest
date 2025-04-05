package thinker.concepts;

import things.form.soma.ISoma;
import thinker.actions.IActionConcept;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.general_types.IProcessConcept;
import thinker.concepts.general_types.IPropertyConcept;
import thinker.concepts.general_types.IValueConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
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
		/** a profile representing an individual */
		PROFILE,
		/**
		 * Used to designate a pseudo-concept storing info about how to identify a
		 * physical phenomenon using a concept.
		 */
		C_ASSOCIATION_INFO,
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
		WORD,
		/**
		 * an identity-less profile-like concept that acts as the answer to a question,
		 * e.g. "(WH_Question) -place_of-> Food. These are often associated with Actions
		 * and have independent ids
		 */
		WH_QUESTION,
		/** A concept indicating a desired set of circumstances */
		GOAL,
		/** TODO a linguistic feature, used for stuff like featural marking of gender */
		LFEATURE,
		/** An AND or OR connector to connect multiple relations */
		CONNECTOR,
		/** Used by {@link DeicticConcept}s */
		DEIXIS,
		/** something else */
		OTHER,
		/** nothing */
		NONE
	}

	/**
	 * The idea of need as a foundation to action. Linked to needs by the
	 * {@link KnowledgeRelationType#IS_PRINCIPLE_OF} relation.
	 */
	public static final IPrincipleConcept NECESSITY = IPrincipleConcept.createGenericFundamentalPrinciple("NECESSITY");

	/**
	 * The idea of wanting as a foundation to action. Linked to
	 * {@link IGoalConcept}s by the {@link KnowledgeRelationType#IS_PRINCIPLE_OF}
	 * relation.
	 */
	public static final IPrincipleConcept DESIRE = IPrincipleConcept.createGenericFundamentalPrinciple("DESIRE");

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
