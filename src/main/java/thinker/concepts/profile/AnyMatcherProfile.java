package thinker.concepts.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import things.interfaces.UniqueType;

/**
 * A class representing a profile that represents any profile of a certain kind
 * 
 * @author borah
 *
 */
final class AnyMatcherProfile implements IProfile {

	private UniqueType type;
	static final Map<UniqueType, IProfile> ALL;

	static {
		ImmutableMap.Builder<UniqueType, IProfile> builder = ImmutableMap.builder();
		for (UniqueType type : UniqueType.values()) {
			if (type == UniqueType.ANY) {
				builder.put(UniqueType.ANY, ANY_PROFILE);
			} else {
				builder.put(type, new AnyMatcherProfile(type));
			}
		}
		ALL = builder.build();
	}

	private AnyMatcherProfile(UniqueType type) {
		this.type = type;
	}

	@Override
	public boolean isAnyMatcher() {
		return true;
	}

	@Override
	public boolean isTypeProfile() {
		return false;
	}

	@Override
	public IProfile withType(UniqueType newType) {
		return IProfile.anyOf(newType);
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
	public UniqueType getDescriptiveType() {
		return type;
	}

	@Override
	public Collection<UniqueType> getDescriptiveTypes() {
		return Collections.singleton(type);
	}

	@Override
	public String toString() {

		return "|[" + this.getIdentifierName() + "]|";
	}

}
