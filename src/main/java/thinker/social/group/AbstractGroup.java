package thinker.social.group;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import _sim.world.GameUniverse;
import thinker.concepts.general_types.IProfile;
import thinker.concepts.general_types.IProfile.ProfileType;
import thinker.concepts.general_types.Profile;
import thinker.concepts.knowledge.INoosphereKnowledgeBase;
import thinker.concepts.knowledge.SectionKnowledgeBase;
import thinker.concepts.relations.ConceptRelationType;
import thinker.mind.needs.INeedConcept;
import thinker.social.relations.party_relations.PartyRelationType;
import thinker.social.systems.IRole;
import thinker.social.systems.SocialRole;

public abstract class AbstractGroup implements IGroup {

	protected SectionKnowledgeBase knowledge;
	protected int count;
	protected Set<IRole> roles;
	protected Set<INeedConcept> postedNeeds;
	protected GameUniverse universe;

	public AbstractGroup(UUID id, String identifierName, INoosphereKnowledgeBase noosphere, GameUniverse universe) {
		this.knowledge = new SectionKnowledgeBase(new Profile(id, ProfileType.GROUP).setIdentifierName(identifierName),
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
			SocialRole role = new SocialRole(UUID.randomUUID(), neda.getPropertyName() + " Worker", this,
					this.getKnowledge().getNoosphere());
			role.getKnowledge().addConfidentRelation(role.getProfile(), PartyRelationType.GIVES_TO, this.getProfile());
			role.getKnowledge().addConfidentRelation(role.getProfile(), ConceptRelationType.CREATES, neda);
			this.universe.getPartyRelations().addEdge(role, PartyRelationType.GIVES_TO, this);
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

	@Override
	public boolean isLoaded() {
		return true;
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
