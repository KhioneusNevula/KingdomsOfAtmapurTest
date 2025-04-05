package thinker.knowledge.node;

import java.util.Collections;
import java.util.Set;

import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.profile.IProfile;
import thinker.mind.memory.StorageType;

public interface IGroupConceptNode extends IConceptNode {

	/**
	 * A "Null" concept node
	 */
	public static final IGroupConceptNode NULL = new IGroupConceptNode() {

		@Override
		public StorageType getStorageType() {
			return StorageType.CONFIDENT;
		}

		@Override
		public ConceptType getConceptType() {
			return ConceptType.OTHER;
		}

		@Override
		public boolean addToGroup(IProfile groupProfile) {
			return false;
		}

		@Override
		public StorageType getStorageTypeForGroup(IProfile profile) {
			return null;
		}

		@Override
		public void setStorageTypeForGroup(IProfile forGroup, StorageType type) {

		}

		@Override
		public boolean knownByGroup(IProfile group) {
			return false;
		}

		@Override
		public Set<IProfile> knownByGroups() {
			return Collections.emptySet();
		}

		@Override
		public boolean removeFromGroup(IProfile groupProfile) {
			return false;
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
			return false;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public IGroupConceptNode copy() {
			return this;
		}

		@Override
		public String toString() {
			return "_null_";
		}
	};

	public default boolean unknownToAll() {
		return knownByGroups().isEmpty();
	}

	/**
	 * The set of groups that know about this concept
	 * 
	 * @return
	 */
	public Set<IProfile> knownByGroups();

	/**
	 * Add this node to the knowledge of the given group and return true if changes
	 * were made
	 * 
	 * @param groupProfile
	 */
	public boolean addToGroup(IProfile groupProfile);

	/**
	 * Remove this node from the given group's knowledge and return true if anything
	 * changed
	 * 
	 * @param groupProfile
	 */
	public boolean removeFromGroup(IProfile groupProfile);

	/**
	 * Whether this profile is known by the given group
	 * 
	 * @param group
	 */
	public boolean knownByGroup(IProfile group);

	/**
	 * Get the storage type of the concept (StorageType, TruthType) based on the
	 * given group
	 * 
	 * @param <E>
	 * @param prop
	 * @param profile
	 * @return
	 */
	public StorageType getStorageTypeForGroup(IProfile profile);

	public IGroupConceptNode copy();

	public void setStorageTypeForGroup(IProfile forGroup, StorageType type);

}
