package thinker.concepts.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import things.interfaces.UniqueType;

public class Profile implements IProfile {

	private UniqueType type;
	private UUID uuid;
	private String identifierName;

	public Profile(UUID uuid, UniqueType type) {
		this.uuid = uuid;
		this.type = type;
	}

	@Override
	public boolean isAnyMatcher() {
		return false;
	}

	@Override
	public boolean isTypeProfile() {
		return false;
	}

	@Override
	public boolean isUniqueProfile() {
		return true;
	}

	/**
	 * Only used to make profiles easier to identify; not factored into equality
	 * checks, hashcode, etc
	 * 
	 * @return
	 */
	@Override
	public String getIdentifierName() {
		return identifierName == null ? "" : identifierName;
	}

	/**
	 * Set the identifier name. If the passed input is a blank string, it will be
	 * set to null. Can only be called once.
	 * 
	 * @param identifierName
	 * @return
	 */
	public Profile setIdentifierName(String identifierName) {
		if (this.identifierName != null)
			throw new UnsupportedOperationException("Identifier has been set already");
		if (identifierName == null || identifierName.isBlank())
			this.identifierName = "";
		this.identifierName = identifierName;
		return this;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.PROFILE;
	}

	@Override
	public String getUnderlyingName() {
		String typa = type.toString();
		return "profile" + Character.toTitleCase(typa.charAt(0)) + typa.substring(1) + "-" + uuid;
	}

	@Override
	public UUID getUUID() {
		return uuid;
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
	public Profile withType(UniqueType newType) {
		if (newType == this.type)
			return this;
		return new Profile(uuid, newType).setIdentifierName(identifierName);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IProfile iprof) {
			return this.uuid.equals(iprof.getUUID()) && this.type == iprof.getDescriptiveType();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.uuid.hashCode() + this.type.hashCode();
	}

	@Override
	public String toString() {

		return "|[" + this.getClass().getSimpleName() + ":" + Character.toTitleCase(type.toString().charAt(0))
				+ type.toString().substring(1).toLowerCase() + "("
				+ ((this.identifierName == null || this.identifierName.isEmpty()) ? uuid
						: "\"" + this.identifierName + "\"")
				+ ")" + "]|";
	}

}
