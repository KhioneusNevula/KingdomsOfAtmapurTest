package thinker.concepts.general_types;

import java.util.UUID;

public class WhQuestionConcept implements IWhQuestionConcept {

	private UUID id;
	private QuestionType type;

	WhQuestionConcept(UUID qID, QuestionType type) {
		this.id = qID;
		this.type = type;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.WH_QUESTION;
	}

	@Override
	public String getUnderlyingName() {
		return "question_" + this.getQuestionType().name + "(" + this.id + ")";
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
		return "q_" + this.getQuestionType().name;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (obj instanceof IWhQuestionConcept wqc) {
			return this.type == wqc.getQuestionType() && this.id.equals(wqc.getQuestionID());
		}
		return false;
	}

}
