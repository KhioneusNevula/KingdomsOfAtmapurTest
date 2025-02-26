package things.spirit;

import java.util.Collection;
import java.util.UUID;

import things.form.soma.IPartDestructionCondition;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.status_effect.BasicStatusEffect;
import things.status_effect.IPartStatusEffectInstance;
import thinker.IIndividualKnowledgeBase;
import thinker.concepts.general_types.IProfile;
import thinker.concepts.general_types.IProfile.ProfileType;
import thinker.concepts.general_types.Profile;
import thinker.mind.clock.IClock;
import thinker.mind.memory.IMemoryBase;
import thinker.mind.will.IWill;

public class Spirit implements ISpirit {

	private UUID id;
	private IPartDestructionCondition detachCondition;
	private IMemoryBase mind;
	private IWill will;
	private IClock clock;
	private Profile profile;
	private boolean active = true;

	public Spirit(UUID id, String idName, IPartDestructionCondition whenDetach, IMemoryBase mind) {
		this.id = id;
		this.detachCondition = whenDetach;
		this.mind = mind;
		this.profile = new Profile(id, ProfileType.INDIVIDUAL).setIdentifierName(idName);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setClock(IClock clo) {
		this.clock = clo;
	}

	@Override
	public IClock getClock() {
		return clock;
	}

	@Override
	public UUID getUUID() {
		return id;
	}

	@Override
	public IIndividualKnowledgeBase getKnowledge() {
		return mind;
	}

	@Override
	public IWill getWill() {
		return will;
	}

	@Override
	public IProfile getProfile() {
		return profile;
	}

	@Override
	public Integer getCount() {
		return 1;
	}

	@Override
	public boolean isCollective() {
		return false;
	}

	@Override
	public boolean isLoaded() {
		return true;
	}

	public IPartDestructionCondition getDetachCondition() {
		return detachCondition;
	}

	@Override
	public boolean canAttachHost(IComponentPart part, ISoma body) {
		// TODO check viability of attaching to a given host
		return true;
	}

	@Override
	public void onAttachHost(IComponentPart part, ISoma body) {
		// TODO when attached to host
	}

	@Override
	public IComponentPart onHostStateChange(IComponentPart part, ISoma body) {
		if (this.detachCondition.isDestroyed(body, part)) {
			return null;
		}
		return part;
	}

	@Override
	public void onRemove(IComponentPart part, ISoma body) {
		// TODO on death of spirit, afterlife?
	}

	@Override
	public void runTick(IComponentPart part, Collection<? extends IComponentPart> access, ISoma body, long ticks) {
		// TODO run ticks of spirit
		this.will.willTick(this, part, access, ticks);
	}

	@Override
	public void onHostEffectApplied(IComponentPart part, ISoma body, IPartStatusEffectInstance effect) {
		// TODO What to do when the host gains an effect (sleep?)
		if (effect.getEffect() == BasicStatusEffect.UNCONSCIOUS) {
			this.active = false;
		}
	}

	@Override
	public void onHostEffectRemoved(IComponentPart part, ISoma body, IPartStatusEffectInstance effect) {
		// TODO what to do when host loses effect (sleep?)
		if (effect.getEffect() == BasicStatusEffect.UNCONSCIOUS) {
			this.active = true;
		}
	}

}
