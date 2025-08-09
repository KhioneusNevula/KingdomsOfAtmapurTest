package thinker.concepts.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import things.interfaces.UniqueType;

/**
 * A profile representing an "argument type", that is, some kind of
 * multi-traited object of an action, event, relation, etc
 */
public class TypeProfile implements IProfile {

	private UniqueType type;
	private UUID id;
	private String nam;

	protected TypeProfile(UniqueType type, UUID id, String identifier) {
		this.type = type;
		this.id = id;
		this.nam = identifier;
	}

	@Override
	public UniqueType getDescriptiveType() {
		return type;
	}

	@Override
	public Collection<UniqueType> getDescriptiveTypes() {
		return Collections.singleton(type);
	}

	@Override
	public UUID getUUID() {
		return id;
	}

	@Override
	public IProfile withType(UniqueType newType) {
		throw new UnsupportedOperationException("Cannot make copy of TypeProfile");
	}

	@Override
	public boolean isAnyMatcher() {
		return false;
	}

	@Override
	public boolean isUniqueProfile() {
		return false;
	}

	@Override
	public boolean isTypeProfile() {
		return true;
	}

	@Override
	public String getUnderlyingName() {
		return "argtype_" + this.id;
	}

	@Override
	public String getIdentifierName() {
		return nam;
	}

	@Override
	public String toString() {
		return "TypeP_" + this.type + "(\"" + nam + "\", " + this.id + ")";
	}

	@Override
	public int hashCode() {
		return (TypeProfile.class.hashCode() + this.id.hashCode()) * this.type.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IProfile && ((IProfile) o).isTypeProfile() && ((IProfile) o).getUUID().equals(this.id)
				&& ((IProfile) o).getDescriptiveType().equals(this.type);
	}

}
