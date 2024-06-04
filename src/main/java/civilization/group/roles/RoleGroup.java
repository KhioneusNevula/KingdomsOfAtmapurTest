package civilization.group.roles;

import java.util.UUID;

import civilization.IAgent;
import civilization.group.ICultureKnowledge;
import civilization.social.concepts.profile.Profile;
import sim.interfaces.IObjectType;

public class RoleGroup implements IRoleGroup {

	private RoleAgent agent;

	public RoleGroup(RoleAgent agent) {
		this.agent = agent;
	}

	@Override
	public void tick(long ticks) {
		// TODO Auto-generated method stub

	}

	@Override
	public IRoleConcept getPurpose() {
		return agent.getPurpose();
	}

	@Override
	public ICultureKnowledge getKnowledge() {
		return agent.getKnowledgeBase();
	}

	@Override
	public boolean isSmallGroup() {
		return false;
	}

	@Override
	public boolean actsInAbstract() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IAgent getCentralPower() {
		return null;
	}

	@Override
	public boolean hasCentralPower() {
		return false;
	}

	@Override
	public Profile getSelfProfile() {
		return agent.getKnowledgeBase().getSelfProfile();
	}

	@Override
	public IAgent getAgentRepresentation() {
		return agent;
	}

	@Override
	public IAgent getParentAgent() {
		return agent.getParent();
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
	public boolean isInherent() {
		return agent.isInherent();
	}

}
