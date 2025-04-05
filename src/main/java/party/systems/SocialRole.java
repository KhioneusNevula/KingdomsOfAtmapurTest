package party.systems;

import java.util.Collection;
import java.util.UUID;

import party.collective.group.IGroup;
import party.kind.spawning.IKindSpawningContext;
import things.form.kinds.IKind;
import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.Profile;
import thinker.knowledge.base.noosphere.INoosphereKnowledgeBase;
import thinker.knowledge.base.section.SectionKnowledgeBase;

public class SocialRole implements IRole {

	private SectionKnowledgeBase roleKnowledge;
	private IGroup parent;

	public SocialRole(UUID uuid, String identifierName, IGroup parent, INoosphereKnowledgeBase noosphere) {
		this.roleKnowledge = new SectionKnowledgeBase(
				new Profile(uuid, UniqueType.COLLECTIVE).setIdentifierName(identifierName), noosphere);
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

	@Override
	public String report() {
		return this.getClass().getSimpleName() + "{parent=" + this.parent + "}";
	}

	@Override
	public Collection<IKind> memberKinds() {
		return this.parent.memberKinds(); // TODO control for what kinds might be restricted
	}

	@Override
	public IKindSpawningContext getMemberSpawnContext(IKind forKind) {
		return this.parent.getMemberSpawnContext(forKind);
	}

}
