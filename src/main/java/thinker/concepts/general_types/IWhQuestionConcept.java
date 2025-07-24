package thinker.concepts.general_types;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;

public interface IWhQuestionConcept extends IDescriptiveConcept {

	/**
	 * A question type that can be asked by a wh-question.
	 * 
	 * @author borah
	 *
	 */
	public static interface IQuestionType {
		/**
		 * Return what UniqueType this QuestionType matches
		 * 
		 * @return
		 */
		public UniqueType getUniqueType();

		/**
		 * Returns a "question word" to represent this question
		 * 
		 * @return
		 */
		public String getQuestionWord();

		/**
		 * Return the output of {@link #getUniqueType()} as a collection
		 * 
		 * @return
		 */
		public default Collection<UniqueType> getMatchableTypes() {
			return Collections.singleton(getUniqueType());
		}
	}

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
			return ConceptType.C_WH_QUESTION;
		}

		@Override
		public IQuestionType getQuestionType() {
			return QuestionType.ANY;
		}

		@Override
		public UUID getQuestionID() {
			return new UUID(0, 0);
		}

		@Override
		public Collection<UniqueType> getDescriptiveTypes() {
			return QuestionType.ANY.getMatchableTypes();
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
	public IQuestionType getQuestionType();

	@Override
	default Collection<UniqueType> getDescriptiveTypes() {
		return this.getQuestionType().getMatchableTypes();
	}

	/**
	 * Create a question concept for a general question type
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public static IWhQuestionConcept create(UUID id, QuestionType type) {
		return new NonProfileWhQuestionConcept(id, type);
	}

	/**
	 * Create a question concept for a unique type
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	public static IWhQuestionConcept create(UUID id, UniqueType type) {
		return new WhQuestionProfile(type, id);
	}

	/**
	 * Question types that are not profilable
	 * 
	 * @author borah
	 *
	 */
	public enum QuestionType implements IQuestionType {
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

		private QuestionType(String name) {
			this.name = name;
		}

		@Override
		public UniqueType getUniqueType() {
			return UniqueType.N_A;
		}

		@Override
		public String getQuestionWord() {
			return name;
		}
	}
}
