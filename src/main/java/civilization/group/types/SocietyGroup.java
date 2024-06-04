package civilization.group.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import civilization.IAgent;
import civilization.group.CultureKnowledge;
import civilization.group.agents.SocietyGroupAgent;
import civilization.group.purpose.IGroupPurpose;
import civilization.group.roles.IRoleGroup;
import civilization.social.concepts.profile.Profile;
import sim.interfaces.IObjectType;

public abstract class SocietyGroup implements ISocietyGroup {

	private Profile self;
	private SocietyGroupAgent agent;
	private CultureKnowledge culture;
	private Collection<IRoleGroup> roles;

	public SocietyGroup(SocietyGroupAgent agent, Profile self) {
		this.self = self;
		this.agent = agent;
		agent.setEntity(this);
		this.culture = new CultureKnowledge(self);
		if (agent.getParent() != null && agent.getParent().isKnowledgeAccessible())
			this.culture.setParent(agent.getParent().getKnowledgeBase());
		if (agent.getParent() != null && agent.getParent().isReducedKnowledgeAccessible())
			this.culture.setParent(agent.getParent().getReducedKnowledgeBase());
		roles = new ArrayList<>();
	}

	@Override
	public Profile getSelfProfile() {
		return self;
	}

	@Override
	public IAgent getAgentRepresentation() {
		return agent;
	}

	@Override
	public IAgent getParentAgent() {
		return this.agent.getParent();
	}

	@Override
	public UUID getUUID() {
		return agent.getUUID();
	}

	@Override
	public IObjectType getObjectType() {
		return agent.getObjectType();
	}

	@Override
	public String getUniqueName() {
		return agent.getUniqueName();
	}

	@Override
	public IGroupPurpose getPurpose() {
		return agent.getPurpose();
	}

	@Override
	public CultureKnowledge getKnowledge() {
		return culture;
	}

	@Override
	public Collection<IRoleGroup> getRoles() {
		return roles;
	}

}
