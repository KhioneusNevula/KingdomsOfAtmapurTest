package party.collective.group;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import _sim.GameUniverse;
import party.relations.types.PartyRelationType;
import party.systems.IRole;
import party.systems.SocialRole;
import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.Profile;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.knowledge.base.noosphere.INoosphereKnowledgeBase;
import thinker.knowledge.base.section.SectionKnowledgeBase;
import thinker.mind.needs.INeedConcept;

public abstract class AbstractGroup implements IGroup {

	protected SectionKnowledgeBase knowledge;
	protected int count;
	protected Set<IRole> roles;
	protected Set<INeedConcept> postedNeeds;
	protected GameUniverse universe;

	public AbstractGroup(UUID id, String identifierName, INoosphereKnowledgeBase noosphere, GameUniverse universe) {
		this.knowledge = new SectionKnowledgeBase(new Profile(id, UniqueType.COLLECTIVE).setIdentifierName(identifierName),
				noosphere);
		this.roles = new HashSet<>();
		this.postedNeeds = new HashSet<>();
		this.universe = universe;
	}

	@Override
	public Collection<INeedConcept> getPostedNeeds() {
		return this.postedNeeds;
	}

	@Override
	public void postNeed(INeedConcept need) {
		this.postedNeeds.add(need);
	}

	protected void createRolesFromNeeds() {
		if (!this.postedNeeds.isEmpty()) {
			INeedConcept neda = postedNeeds.iterator().next();
			SocialRole role = new SocialRole(UUID.randomUUID(), neda.getSimpleName() + " Worker", this,
					this.getKnowledge().getNoosphere());
			neda.getRequirements().modifyRole(role, this, this.universe.getPartyRelations());
		}
	}

	@Override
	public SectionKnowledgeBase getKnowledge() {
		return knowledge;
	}

	@Override
	public IProfile getProfile() {
		return (IProfile) knowledge.getSelfConcept();
	}

	public void changeCount(int by) {
		this.count += by;
	}

	public void setCount(int to) {
		this.count = to;
	}

	@Override
	public Integer getCount() {
		return count;
	}

	@Override
	public UUID getUUID() {
		return this.getProfile().getUUID();
	}

	@Override
	public Collection<IRole> getRoles() {
		return this.roles;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IGroup gr) {
			return this.getUUID().equals(gr.getUUID());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.getUUID().hashCode();
	}

	@Override
	public String toString() {
		return "Group(" + (this.knowledge.getSelfConcept().getIdentifierName().isEmpty() ? this.getUUID()
				: "\"" + this.knowledge.getSelfConcept().getIdentifierName() + "\"") + ")";
	}

}
