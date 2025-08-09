package thinker.concepts.application;

import thinker.mind.perception.sensation.ISensation;

/** Checks ia physical sensation is represented by a conceptual one */
public interface ISensationApplier extends IConceptApplier {

	/** Returns the physical sensation this applier is comparing to */
	public ISensation getPhysicalSensation();
}
