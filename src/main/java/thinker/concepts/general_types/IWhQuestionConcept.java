package thinker.concepts.general_types;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import things.interfaces.UniqueType;

public interface IWhQuestionConcept extends IDescriptiveConcept {

	/**
	 * Indicates any possible question
	 */
	public static IWhQuestionConcept ANY_QUESTION = new IWhQuestionConcept() {

		@Override
		public String getUnderlyingName() {
			return "any_question";
		}

		@Override
		public String toString() {
			return "concept_any_question";
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.WH_QUESTION;
		}

		@Override
		public QuestionType getQuestionType() {
			return QuestionType.ANY;
		}

		@Override
		public UUID getQuestionID() {
			return new UUID(0, 0);
		}

		@Override
		public Collection<UniqueType> getDescriptiveTypes() {
			return QuestionType.ANY.matchableTypes;
		}
	};

	/**
	 * Returns the unique id of this question slot
	 * 
	 * @return
	 */
	public UUID getQuestionID();

	/**
	 * Returns what type of question this answers
	 * 
	 * @return
	 */
	public QuestionType getQuestionType();

	@Override
	default Collection<UniqueType> getDescriptiveTypes() {
		return this.getQuestionType().matchableTypes;
	}

	/**
	 * Creates a WHAT question, i.e. {@link QuestionType#ENTITY}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept what(UUID id) {
		return new WhQuestionConcept(id, QuestionType.ENTITY);
	}

	/**
	 * Creates a WHAT_PART question, i.e. {@link QuestionType#PART}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept whatPart(UUID id) {
		return new WhQuestionConcept(id, QuestionType.PART);
	}

	/**
	 * Creates a WHAT_PHENOMENON question, i.e. {@link QuestionType#PHENOMENON}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept whatPhenomenon(UUID id) {
		return new WhQuestionConcept(id, QuestionType.PHENOMENON);
	}

	/**
	 * Creates a WHERE question, i.e. {@link QuestionType#PLACE}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept where(UUID id) {
		return new WhQuestionConcept(id, QuestionType.PLACE);
	}

	/**
	 * Creates a WHEN (timeline) question, i.e. {@link QuestionType#TIMELINE_TIME}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept whenTimeline(UUID id) {
		return new WhQuestionConcept(id, QuestionType.TIMELINE_TIME);
	}

	/**
	 * Creates a WHEN (clock) question, i.e. {@link QuestionType#CLOCK_TIME}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept whenClock(UUID id) {
		return new WhQuestionConcept(id, QuestionType.CLOCK_TIME);
	}

	/**
	 * Creates a WHAT (event) question, i.e. {@link QuestionType#EVENT}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept whatEvent(UUID id) {
		return new WhQuestionConcept(id, QuestionType.EVENT);
	}

	/**
	 * Creates a WHAT (action) question, i.e. {@link QuestionType#ACTION}
	 * 
	 * @param id
	 * @return
	 */
	public static IWhQuestionConcept whatAction(UUID id) {
		return new WhQuestionConcept(id, QuestionType.ACTION);
	}

	/**
	 * Create a question concept
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public static IWhQuestionConcept create(UUID id, QuestionType type) {
		return new WhQuestionConcept(id, type);
	}

	public enum QuestionType {
		/** A question type that represents an entity, item, or group */
		ENTITY("what", UniqueType.FIGURE, UniqueType.FORM, UniqueType.COLLECTIVE),
		/** A question type that represents a part */
		PART("what_part", UniqueType.PART),
		/** A question type that represents a phenomenon */
		PHENOMENON("what_phenomenon", UniqueType.PHENOMENON),
		/** A question type that represents a location */
		PLACE("where", UniqueType.PLACE),
		/** A question type representing a time in history */
		TIMELINE_TIME("when_timeline"),
		/** A question type representing a cyclic time */
		CLOCK_TIME("when_clock"),
		/**
		 * A question type representing an event(e.g. one that causes something else)
		 */
		EVENT("what_event"),
		/**
		 * A question type representing an action (e.g. one that can be used to
		 * accomplish something else)
		 */
		ACTION("what_action"),
		/**
		 * A question type representing any possible kind of question
		 */
		ANY("whatever");

		public final String name;
		private Set<UniqueType> matchableTypes;

		private QuestionType(String name, UniqueType... cvT) {
			this.name = name;
			this.matchableTypes = Set.of(cvT);
		}
	}
}
