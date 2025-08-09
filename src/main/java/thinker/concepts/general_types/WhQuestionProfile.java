package thinker.concepts.general_types;

import java.util.UUID;

import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.TypeProfile;

public class WhQuestionProfile extends TypeProfile implements IWhQuestionConcept {

	private UUID id;

	public WhQuestionProfile(UniqueType type, UUID identifier) {
		super(type, identifier, "Q-" + identifier.toString());
		this.id = identifier;
	}

	@Override
	public IProfile withType(UniqueType newType) {
		throw new UnsupportedOperationException("Cannot make copy of WhQuestion");
	}

	@Override
	public String getUnderlyingName() {
		return "qtype_" + this.getQuestionID();
	}

	@Override
	public UUID getQuestionID() {
		return this.id;
	}

	@Override
	public String toString() {
		return "QuestionP_" + this.getDescriptiveType() + "(" + this.id + ")";
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.C_WH_QUESTION;
	}

	@Override
	public UniqueType getQuestionType() {
		return this.getDescriptiveType();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IWhQuestionConcept) {
			return super.equals(o);
		}
		return false;
	}

}
