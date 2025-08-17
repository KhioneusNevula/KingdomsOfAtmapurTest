package thinker.constructs;

import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;
import thinker.concepts.IConcept;
import thinker.concepts.application.IConceptAssociationInfo;

/** A RecipeConcept is a concept dictating how to construct something */
public interface IRecipeConcept extends IConcept, IConceptAssociationInfo {

	/** Whether this recipe is correct? idk */
	public boolean isCorrect();

	/**
	 * Overriden as true
	 */
	@Override
	default boolean doDecayChecks() {
		return true;
	}

	@Override
	default ConceptType getConceptType() {
		return ConceptType.RECIPE;
	}

	/** Returns what Kind this recipe creates */
	public IKind getCreatableKind();

	/**
	 * Generates the kind settings to construct a new instance of the result of this
	 * recipe, based on an {@link IReactionInstance}
	 */
	public IKindSettings createSettingsFromReaction(IReactionInstance reaction);

}
