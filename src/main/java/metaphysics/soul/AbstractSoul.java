package metaphysics.soul;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import _sim.world.PersistentFigure;
import metaphysics.being.IFigure;
import party.collective.ICollective;
import things.actor.IActor;
import things.form.soma.IPartDestroyedCondition;
import things.form.soma.IPartHealth;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.Profile;
import thinker.mind.clock.IClock;
import thinker.mind.emotions.IFeeling;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.perception.IPerception;
import thinker.mind.perception.Perception;
import thinker.mind.perception.sensation.DamageSensationReceptor;
import thinker.mind.perception.sensation.Sensation;
import thinker.mind.personality.IPersonality;
import thinker.mind.will.IThinkerWill;

/**
 * An abstract class representing a soul of some kind
 * 
 * @author borah
 *
 */
public abstract class AbstractSoul implements ISoul {

	private UUID id;
	protected IPartDestroyedCondition detachCondition;
	protected IPerception perception;
	protected IMindKnowledgeBase memory;
	protected IThinkerWill will;
	protected IClock clock;
	private Profile profile;
	protected boolean active = true;
	protected IPartHealth healthTracker;
	protected IPersonality personality;
	private boolean persistent;
	protected Set<ICollective> groups;

	public AbstractSoul(UUID id, String idName, IPartDestroyedCondition whenDetach, IPartHealth healthTracker,
			IMindKnowledgeBase mind) {
		this.healthTracker = healthTracker;
		this.perception = new Perception().addSensationReceptor(Sensation.PAIN,
				new DamageSensationReceptor(healthTracker));
		this.id = id;
		this.detachCondition = whenDetach;
		this.memory = mind;
		this.profile = new Profile(id, UniqueType.FIGURE).setIdentifierName(idName);
		this.groups = new HashSet<>();
	}

	@Override
	public void addToGroup(ICollective group) {
		this.groups.add(group);
		this.memory.addParents(Collections.singleton(group.getKnowledge()));
	}

	@Override
	public void removeFromGroup(ICollective group) {
		this.groups.remove(group);
		this.memory.removeParents(Collections.singleton(group.getKnowledge()));
	}

	@Override
	public Collection<ICollective> getParentGroups() {
		return this.groups;
	}

	protected void setPersonality(IPersonality will) {
		this.personality = will;
	}

	protected void setWill(IThinkerWill will) {
		this.will = will;
	}

	@Override
	public IFigure createPersistentFigure(Optional<IComponentPart> part) {
		PersistentFigure fig = new PersistentFigure(this);
		part.map(IComponentPart::getSomaOwner).map(ISoma::getOwner).map(IActor::getPosition)
				.ifPresent(fig::setLastLocation);
		return fig;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	public void setClock(IClock clo) {
		this.clock = clo;
	}

	@Override
	public IClock getClock() {
		return clock;
	}

	@Override
	public IPerception getPerception() {
		return perception;
	}

	@Override
	public UUID getUUID() {
		return id;
	}

	@Override
	public IMindKnowledgeBase getKnowledge() {
		return memory;
	}

	@Override
	public IThinkerWill getWill() {
		return will;
	}

	@Override
	public IPersonality getPersonality() {
		return personality;
	}

	@Override
	public float getFeeling(IFeeling feeling) {
		return this.memory.getFeeling(feeling);
	}

	@Override
	public Collection<IFeeling> getFeelings() {
		return this.memory.getFeelings();
	}

	@Override
	public IProfile getProfile() {
		return profile;
	}

	@Override
	public boolean isCollective() {
		return false;
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

	@Override
	public boolean isPersistent() {
		return persistent;
	}

	@Override
	public void markPersistent() {
		persistent = true;
	}

	@Override
	public float determineHealthOfPart(IComponentPart part, ISoma body) {
		return healthTracker.health(body, part);
	}

	public IPartDestroyedCondition getDetachCondition() {
		return detachCondition;
	}

	@Override
	public IComponentPart onHostStateChange(IComponentPart part, ISoma body) {
		if (this.detachCondition.isDestroyed(body, part)) {
			return null;
		}
		return part;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractSoul) {
			return ((AbstractSoul) obj).id.equals(this.id);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}
