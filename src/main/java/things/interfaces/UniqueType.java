package things.interfaces;

import metaphysics.being.IFigure;
import party.collective.ICollective;
import things.form.IForm;
import things.form.IPart;
import things.phenomena.IPhenomenon;
import thinker.concepts.general_types.IWhQuestionConcept.IQuestionType;
import thinker.constructs.IPlace;

public enum UniqueType implements IQuestionType {
	/**
	 * A profile representing (the form/visage of) an individual person or object
	 */
	FORM(IForm.class, "what_thing"),
	/**
	 * A profile representing an individual figure that can have perceptions thought
	 * about it and seemingly take actions, whether person, god, animal, etc. It
	 * also may or may not be real!
	 */
	FIGURE(IFigure.class, "who"),
	/** A profile representing a collective entity */
	COLLECTIVE(ICollective.class, "what_group"),
	/** A specific part of a specific individual */
	PART(IPart.class, "what_part"),
	/**
	 * A profile representing a specific phenomenon, typically World phenomena like
	 * the Sun
	 */
	PHENOMENON(IPhenomenon.class /** TODO phenomenon profile type */
			, "what_phenomenon"),
	/** A profile representing a specific location of interest */
	PLACE(IPlace.class, "where"),
	/** Something else, e.g. a concept or system */
	OTHER(IUnique.class, "what_X"),
	/** A non-applicable case */
	N_A(IUnique.class, "N/A"),
	/** Represents "any profile", useful for actions */
	ANY(IUnique.class, "what");

	private Class<? extends IUnique> superclass;
	private String whword;

	private UniqueType(Class<? extends IUnique> superclass, String whword) {
		this.superclass = superclass;
		this.whword = whword;
	}

	/** Returns the class associated with this profile type, if any */
	public Class<? extends IUnique> getAssociatedClass() {
		return superclass;
	}

	@Override
	public UniqueType getUniqueType() {
		return this;
	}

	@Override
	public String getQuestionWord() {
		return whword;
	}

	/**
	 * If this unique type is something strictly physical
	 * @return
	 */
	public boolean isPhysical() {
		return this == FORM || this == PART || this == PLACE || this == PHENOMENON;
	}
	
}