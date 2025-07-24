package thinker.concepts.general_types;

import java.util.UUID;

import thinker.concepts.profile.IProfile;

public class NonProfileWhQuestionConcept implements IWhQuestionConcept {

	private UUID id;
	private QuestionType type;

	NonProfileWhQuestionConcept(UUID qID, QuestionType type) {
		this.id = qID;
		this.type = type;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.C_WH_QUESTION;
	}

	@Override
	public String getUnderlyingName() {
		return "question_" + this.getQuestionType() + "(" + this.id + ")";
	}

	@Override
	public UUID getQuestionID() {
		return this.id;
	}

	@Override
	public QuestionType getQuestionType() {
		return type;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode() * this.type.hashCode();
	}

	@Override
	public String toString() {
		return "q_" + this.getQuestionType().getQuestionWord() + "(" + this.getQuestionType() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof IWhQuestionConcept wqc) {
			return this.type.equals(wqc.getQuestionType()) && this.id.equals(wqc.getQuestionID());
		}
		return false;
	}

}
