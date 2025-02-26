package thinker.mind.memory.node;

import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.mind.memory.StorageType;

/**
 * A node in the concept graph
 * 
 * @author borah
 *
 */
public interface IConceptNode {

	/**
	 * A "Null" concept node
	 */
	public static final IConceptNode NULL = new IConceptNode() {

		@Override
		public StorageType getStorageType() {
			return StorageType.CONFIDENT;
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.OTHER;
		}

		@Override
		public IConcept getConcept() {
			return null;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IConceptNode nod) {
				return nod.getConcept() == null && nod.getConceptType() == ConceptType.NONE;
			}
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public String toString() {
			return "_null_";
		}
	};

	/**
	 * Return the way this node is stored
	 * 
	 * @return
	 */
	public StorageType getStorageType();

	/**
	 * The type of the stored concept
	 * 
	 * @return
	 */
	public ConceptType getConceptType();

	/**
	 * Return the concept stored in this node. Should never be null except if
	 * {@link #NULL}
	 * 
	 * @return
	 */
	public IConcept getConcept();

	/**
	 * Equality should be based on the stored concept but not storage type
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	boolean equals(Object obj);

	/**
	 * HashCode should be based on the stored concept but not storage type
	 * 
	 * @return
	 */
	@Override
	int hashCode();
}
