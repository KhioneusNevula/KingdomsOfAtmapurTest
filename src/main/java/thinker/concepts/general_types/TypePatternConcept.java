package thinker.concepts.general_types;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;

public class TypePatternConcept extends PatternConcept implements ITypePatternConcept {

	private UniqueType type;

	public TypePatternConcept(UniqueType type, UUID id) {
		super(id);
		this.type = type;
	}

	@Override
	public boolean isAction() {
		return false;
	}

	@Override
	public boolean isType() {
		return true;
	}

	@Override
	public Collection<UniqueType> getDescriptiveTypes() {
		return Set.of(type);
	}

	@Override
	public UniqueType getDescriptiveType() {
		return type;
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
		return false;
	}

	@Override
	public IProfile withType(UniqueType newType) {
		throw new UnsupportedOperationException("Cannot create copy of TypePattern");
	}

	@Override
	public UUID getUUID() {
		return this.getID();
	}

	@Override
	public String getUnderlyingName() {
		return "type_" + super.getUnderlyingName();
	}

	@Override
	public String toString() {
		return "Type" + super.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ITypePatternConcept) {
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ITypePatternConcept.class.hashCode() + super.hashCode();
	}

}
