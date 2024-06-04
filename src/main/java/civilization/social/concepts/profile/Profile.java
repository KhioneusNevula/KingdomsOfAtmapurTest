package civilization.social.concepts.profile;

import java.util.UUID;

import civilization.social.concepts.IConcept;

/**
 * a conceptual projection of an individual being, group, or something more
 * complex such as a language, location, or natural landform
 * 
 * @author borah
 *
 */
public class Profile implements IConcept {

	private UUID id;
	private ProfileType type;

	public Profile(UUID id, ProfileType type) {
		this.id = id;
		this.type = type;
	}

	@Override
	public ConceptType getConceptType() {
		return type.getConceptType();
	}

	@Override
	public String getUniqueName() {
		return "profile_" + id.getMostSignificantBits() + "_" + id.getLeastSignificantBits();
	}

	@Override
	public String toString() {
		return "profile_" + this.type.toString().toLowerCase() + "(" + id.toString().substring(0, 5) + ")";
	}

	/**
	 * Profile type
	 * 
	 * @return
	 */
	public ProfileType getType() {
		return type;
	}

	/**
	 * Unique id of this profile
	 * 
	 * @return
	 */
	public UUID getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Profile p) {
			return this.id.equals(p.id);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}
