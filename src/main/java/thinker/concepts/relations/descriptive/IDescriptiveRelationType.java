package thinker.concepts.relations.descriptive;

import thinker.concepts.relations.IConceptRelationType;

/**
 * A kind of relation type which expresses a static relation between unique
 * entities and other things
 */
public interface IDescriptiveRelationType extends IConceptRelationType {

	@Override
	public IDescriptiveRelationType invert();
}
