package thinker.concepts.general_types;

import _sim.vectors.IVector;
import thinker.concepts.IConcept;

/**
 * A concept representing a position/direction
 * 
 * @author borah
 *
 */
public interface IVectorConcept extends IConcept {

	public static final IVectorConcept ZERO = new VectorConcept(IVector.ZERO);

	@Override
	default ConceptType getConceptType() {
		return ConceptType.VECTOR;
	}

	/**
	 * Return the vector this concept represents
	 * 
	 * @return
	 */
	public IVector getVector();

	/**
	 * Return a new vector concept
	 * 
	 * @param vec
	 * @return
	 */
	public static IVectorConcept of(IVector vec) {
		if (vec == IVector.ZERO) {
			return ZERO;
		}
		return new VectorConcept(vec);
	}

	public static IVectorConcept of(int x, int y) {
		return of(IVector.of(0, 0));
	}

}
