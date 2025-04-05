package thinker.knowledge.node;

import java.util.Objects;

import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.mind.memory.StorageType;

public class ConceptNode implements IConceptNode {

	private IConcept internal;
	private StorageType storageType;

	public ConceptNode(IConcept internal) {
		if (internal == null) {
			Objects.requireNonNull(internal);
		}
		this.internal = internal;
		this.storageType = StorageType.CONFIDENT;
	}

	public ConceptNode(IConcept internal, StorageType storageType) {
		Objects.requireNonNull(internal);
		this.internal = internal;
		this.storageType = storageType;
	}

	@Override
	public StorageType getStorageType() {
		return storageType;
	}

	@Override
	public IConcept getConcept() {
		return internal;
	}

	@Override
	public ConceptType getConceptType() {
		return internal.getConceptType();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IConcept con) {
			return this.internal.equals(con);
		} else if (obj instanceof IConceptNode cn) {
			return this.internal.equals(cn.getConcept());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.internal.hashCode();
	}

	@Override
	public String toString() {
		return internal + "[" + this.storageType.name().charAt(0) + "]";
	}

}
