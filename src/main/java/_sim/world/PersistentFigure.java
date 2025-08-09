package _sim.world;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import _sim.vectors.IVector;
import metaphysics.being.IBeing;
import party.collective.ICollective;
import thinker.concepts.profile.IProfile;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.memory.IMindKnowledgeBase;

/** A persistent figure created from a given being */
public class PersistentFigure implements IPersistentFigure {

	private IProfile figureProfile;
	private boolean active;
	private IMindKnowledgeBase knowledge;
	private Set<ICollective> pars;
	private IVector lastLocation = null;

	public PersistentFigure(IBeing fromBeing) {
		// TODO creating a persistent figure
		this.figureProfile = fromBeing.getProfile();
		this.knowledge = fromBeing.getKnowledge();
		this.pars = new HashSet<>(fromBeing.getParentGroups());
	}

	/** Sets the last known location of this figure */
	public PersistentFigure setLastLocation(IVector lastLocation) {
		this.lastLocation = lastLocation;
		return this;
	}

	@Override
	public IVector getLastLocation() {
		return lastLocation;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public boolean isLoaded() {
		return false;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getSaveFilename() {
		return "figure" + this.getUUID().toString().replace("-", "_") + ".fig";
	}

	@Override
	public UUID getUUID() {
		return figureProfile.getUUID();
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public boolean isPersistent() {
		return true;
	}

	@Override
	public void markPersistent() {
	}

	@Override
	public IBeing getReferent() {
		return null;
	}

	@Override
	public IProfile getProfile() {
		return this.figureProfile;
	}

	@Override
	public String report() {
		return this.toString() + "{knowledge=" + this.getKnowledgeGraph().representation() + "}";
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" + this.figureProfile + "}";
	}

	@Override
	public IKnowledgeBase getKnowledge() {
		return knowledge;
	}

	@Override
	public void addToGroup(ICollective group) {
		pars.add(group);
	}

	@Override
	public Collection<ICollective> getParentGroups() {
		return pars;
	}

	@Override
	public void removeFromGroup(ICollective group) {
		pars.remove(group);
	}

}
