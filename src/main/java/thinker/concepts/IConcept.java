package thinker.concepts;

import thinker.actions.IActionConcept;
import thinker.concepts.general_types.IIdentityConcept;
import thinker.concepts.general_types.ILabelConcept;
import thinker.concepts.general_types.ILogicConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.general_types.IProcessConcept;
import thinker.concepts.general_types.IProfile;
import thinker.concepts.general_types.IValueConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.mind.emotions.IFeeling;
import thinker.mind.memory.timeline.ITimeline.ITimelineNode;
import thinker.mind.needs.INeedConcept;

/**
 * A Concept is an individual piece of info that can be stored in a relations
 * graph to draw connections between things
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
		/** The concept of an action performed to complete a task */
		ACTION,
		/** a concept of a general idea or event type defined only by its relations */
		PRINCIPLE,
		/** a label for something, marking it as being or not being that */
		LABEL,
		/** a quantity */
		VALUE,
		/** a process with a unique ID */
		PROCESS,
		/** TODO an enumerated trait */
		ENUM_TRAIT,
		/** TODO a trait that can be sensed */
		SENSABLE_TRAIT,
		/** a concept to hold a group identity */
		IDENTITY,
		/** TODO a concept that describes a historical event */
		EVENT,
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
		/** a profile representing an individual */
		PROFILE,
		/** TODO a linguistic lexical item */
		WORD,
		/** TODO a linguistic feature, used for stuff like featural marking of gender */
		LFEATURE,
		/**
		 * an identity-less profile-like concept that acts as the answer to a question,
		 * e.g. "(WH_Question) -place_of-> Food. These are often associated with Actions
		 * and have independent ids
		 */
		WH_QUESTION,
		/** An AND or OR connector to connect multiple relations */
		LOGIC_CONNECTOR,
		/**
		 * Used to designate a pseudo-concept storing info about associations between
		 * concepts and physical phenomena
		 */
		ASSOCIATION_INFO,
		/**
		 * the concept type representing the Concept that represents something itself.
		 * In the case of a GoalCondition, this indicates whatever satisfies the goal.
		 * In the case of a Noosphere, the Existence concept is used
		 */
		THE_CONCEPT_ITSELF,
		/** something else */
		OTHER,
		/** nothing, only applies to {@link #NONE} */
		NONE
	}

	public static final IConcept EXISTENCE = new IConcept() {
		@Override
		public ConceptType getConceptType() {
			return ConceptType.THE_CONCEPT_ITSELF;
		}

		@Override
		public String getUnderlyingName() {
			return "existence_itself";
		}

		public String toString() {
			return "Existence";
		}
	};

	public static final IConcept NOTHING = new IConcept() {

		@Override
		public String getUnderlyingName() {
			return "nothing";
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.NONE;
		}

		@Override
		public String toString() {
			return "Nothing";
		}
	};

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
	public default ILabelConcept asLabel() {
		return (ILabelConcept) this;
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
	 * Casts this to an identity concept
	 * 
	 * @return
	 */
	public default IIdentityConcept asIdentity() {
		return (IIdentityConcept) this;
	}

	/**
	 * Casts this to an event concept
	 * 
	 * @return
	 */
	/**
	 * public default IEventConcept asEvent() { return (IEventConcept) this; }
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
	public default ILogicConcept asLogic() {
		return (ILogicConcept) this;
	}

}
