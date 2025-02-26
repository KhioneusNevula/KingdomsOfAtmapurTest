package thinker.concepts.relations;

import utilities.graph.IInvertibleRelationType;

public interface IConceptRelationType extends IInvertibleRelationType {

	@Override
	public IConceptRelationType invert();

	/**
	 * Whether this relation encodes a trait that characterizes the thing on the
	 * other end
	 * 
	 * @return
	 */
	public boolean characterizesOther();

	/**
	 * Whether this relation encodes a trait that characterizes this via the value
	 * on the other end
	 * 
	 * @return
	 */
	public boolean isCharacterizedByOther();
}
