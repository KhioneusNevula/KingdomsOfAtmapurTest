package thinker.concepts.general_types;

import _sim.vectors.IVector;

/**
 * Implementation of {@link IVectorConcept}
 * 
 * @author borah
 *
 */
public class VectorConcept implements IVectorConcept {

	private IVector vec;

	VectorConcept(IVector vec) {
		this.vec = vec;
	}

	@Override
	public String getUnderlyingName() {
		return "concept_vector_" + (vec.getX() < 0 ? "neg" + -vec.getX() : vec.getX()) + "_"
				+ (vec.getY() < 0 ? "neg" + -vec.getY() : vec.getY());
	}

	@Override
	public IVector getVector() {
		return vec;
	}

	@Override
	public String toString() {
		return "VectorConcept{" + vec + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof IVectorConcept vc) {
			return this.vec.equals(vc.getVector());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.vec.hashCode();
	}

}
