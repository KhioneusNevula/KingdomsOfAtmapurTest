package thinker.concepts.knowledge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import thinker.concepts.IConcept;
import thinker.concepts.general_types.IProfile;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.node.ConceptNode;

public class GroupConceptNode extends ConceptNode implements IGroupConceptNode {

	private Map<IProfile, StorageType> groups;

	public GroupConceptNode(IConcept internal) {
		this(internal, StorageType.CONFIDENT);
	}

	public GroupConceptNode(IConcept internal, StorageType storageType) {
		super(internal, storageType);
	}

	/** initializes groups if it is not initialized */
	private Map<IProfile, StorageType> wgroups() {
		if (groups == null)
			groups = new HashMap<>();
		return groups;
	}

	/** return the collections empmty map if groups is not initialized */
	private Map<IProfile, StorageType> rgroups() {
		if (groups == null)
			return Collections.emptyMap();
		return groups;
	}

	@Override
	public Set<IProfile> knownByGroups() {
		return rgroups().keySet();
	}

	@Override
	public boolean addToGroup(IProfile groupProfile) {
		return wgroups().put(groupProfile, wgroups().getOrDefault(groupProfile, StorageType.CONFIDENT)) == null;
	}

	@Override
	public boolean removeFromGroup(IProfile groupProfile) {
		if (groups == null)
			return false;
		return groups.remove(groupProfile) != null;
	}

	@Override
	public boolean knownByGroup(IProfile group) {
		return rgroups().containsKey(group);
	}

	@Override
	public StorageType getStorageTypeForGroup(IProfile profile) {
		return rgroups().get(profile);
	}

	public void setStorageTypeForGroup(IProfile profile, StorageType stype) {
		wgroups().put(profile, stype);
	}

	@Override
	public GroupConceptNode copy() {
		GroupConceptNode gcn = new GroupConceptNode(this.getConcept(), getStorageType());
		gcn.groups = new HashMap<>(this.groups);
		return gcn;
	}

	@Override
	public String toString() {
		return this.getConcept() + "[" + this.getStorageType().name().charAt(0) + "]("
				+ (this.groups == null ? 0 : this.groups.keySet().size()) + ")";
	}
}
