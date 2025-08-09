package thinker.concepts.relations.actional;

import thinker.concepts.relations.IConceptRelationType;

/**
 * A kind of concept relation type which implies state change rather than static
 * existence
 */
public interface IEventRelationType extends IConceptRelationType {

	/**
	 * What kind of event this is
	 * 
	 * @return
	 */
	public EventCategory getEventType();
}
