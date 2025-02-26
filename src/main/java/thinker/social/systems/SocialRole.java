package thinker.social.systems;

import java.util.UUID;

import thinker.concepts.general_types.IProfile;
import thinker.concepts.general_types.IProfile.ProfileType;
import thinker.concepts.general_types.Profile;
import thinker.concepts.knowledge.INoosphereKnowledgeBase;
import thinker.concepts.knowledge.SectionKnowledgeBase;
import thinker.social.group.IGroup;

public class SocialRole implements IRole {

	private SectionKnowledgeBase roleKnowledge;
	private IGroup parent;

	public SocialRole(UUID uuid, String identifierName, IGroup parent, INoosphereKnowledgeBase noosphere) {
		this.roleKnowledge = new SectionKnowledgeBase(
				new Profile(uuid, ProfileType.ROLE).setIdentifierName(identifierName), noosphere);
		this.parent = parent;
	}

	@Override
	public IGroup getParentGroup() {
		return parent;
	}

	@Override
	public SectionKnowledgeBase getKnowledge() {
		return roleKnowledge;
	}

	@Override
	public IProfile getProfile() {
		return (IProfile) roleKnowledge.getSelfConcept();
	}

	@Override
	public boolean isLoaded() {
		return this.parent.isLoaded();
	}

	@Override
	public Integer getCount() {
		return null;
	}

	@Override
	public UUID getUUID() {
		return this.getProfile().getUUID();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IRole rol) {
			return this.getUUID().equals(rol.getUUID());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.getUUID().hashCode();
	}

	@Override
	public String toString() {
		return "Role(" + (this.roleKnowledge.getSelfConcept().getIdentifierName().isEmpty() ? this.getUUID()
				: "\"" + this.roleKnowledge.getSelfConcept().getIdentifierName() + "\"") + ")";
	}

}
