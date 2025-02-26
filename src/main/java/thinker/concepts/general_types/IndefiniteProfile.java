package thinker.concepts.general_types;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

/**
 * A class representing a profile that represents any profile of a certain kind
 * 
 * @author borah
 *
 */
final class IndefiniteProfile implements IProfile {

	private ProfileType type;
	static final Map<ProfileType, IProfile> ALL;

	static {
		ImmutableMap.Builder<ProfileType, IProfile> builder = ImmutableMap.builder();
		for (ProfileType type : ProfileType.values()) {
			if (type == ProfileType.ANY) {
				builder.put(ProfileType.ANY, ANY_PROFILE);
			} else {
				builder.put(type, new IndefiniteProfile(type));
			}
		}
		ALL = builder.build();
	}

	private IndefiniteProfile(ProfileType type) {
		this.type = type;
	}

	@Override
	public boolean isIndefinite() {
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
		return "any " + this.type.toString().toLowerCase();
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.PROFILE;
	}

	@Override
	public String getUnderlyingName() {
		return "profile_" + this.getIdentifierName().replace(" ", "_");
	}

	@Override
	public UUID getUUID() {
		return new UUID(0, 0);
	}

	@Override
	public ProfileType getProfileType() {
		return type;
	}

	@Override
	public String toString() {

		return "|[" + this.getIdentifierName() + "]|";
	}

}
