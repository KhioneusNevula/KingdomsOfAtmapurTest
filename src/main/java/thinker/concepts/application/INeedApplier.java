package thinker.concepts.application;

import things.form.channelsystems.IChannelNeed;

/** Checks ia physical need is represented by a conceptual one */
public interface INeedApplier extends IConceptApplier {

	/** Returns the physical need this applier is comparing to */
	public IChannelNeed getPhysicalNeed();
}
