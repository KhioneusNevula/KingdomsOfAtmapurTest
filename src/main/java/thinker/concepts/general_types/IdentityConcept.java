package thinker.concepts.general_types;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import thinker.concepts.general_types.IProfile.ProfileType;

public class IdentityConcept implements IIdentityConcept {

	private UUID group;
	private static final Set<ProfileType> matches = Set.of(ProfileType.GROUP, ProfileType.INDIVIDUAL);

	public IdentityConcept(UUID group) {
		this.group = group;
	}

	@Override
	public Collection<ProfileType> matchesProfileTypes() {
		return matches;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.IDENTITY;
	}

	@Override
	public UUID getGroupID() {
		return this.group;
	}

	@Override
	public String getUnderlyingName() {
		return "identity_" + this.group.toString().replace("-", "_");
	}

	@Override
	public String getPropertyName() {
		return this.getUnderlyingName();
	}

	@Override
	public Boolean defaultValue() {
		return false;
	}

	@Override
	public Class<? super Boolean> getType() {
		return boolean.class;
	}

	@Override
	public int hashCode() {
		return group.hashCode();
	}

	@Override
	public String toString() {
		return "identity(" + this.group + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ILabelConcept other) {
			return this.getPropertyName().equals(other.getPropertyName());
		}
		return super.equals(obj);
	}

	@Override
	public boolean isIdentity() {
		return true;
	}

}
