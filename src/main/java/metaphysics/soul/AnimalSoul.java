package metaphysics.soul;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import _sim.world.GameMap;
import metaphysics.magic.ITether;
import metaphysics.magic.ITether.TetherType;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.soma.IPartDestroyedCondition;
import things.form.soma.IPartHealth;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.status_effect.BasicStatusEffect;
import things.status_effect.IPartStatusEffectInstance;
import thinker.actions.searching.RelationMutability;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.personality.BasicTendency;
import thinker.mind.personality.Personality;
import thinker.mind.personality.PersonalityTraits;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.ThinkerWill;

/**
 * Implementation of the soul of a mind that tethers to a physical body using
 * biological processes like nerves
 */
public class AnimalSoul extends AbstractSoul {

	private boolean removed;

	public AnimalSoul(UUID id, String idName, IPartDestroyedCondition whenDetach, IPartHealth healthTracker,
			IMindKnowledgeBase mind) {
		super(id, idName, whenDetach, healthTracker, mind);
		List<RelationMutability> relmut = Lists.newArrayList(RelationMutability.values());
		Collections.reverse(relmut);
		setWill(new ThinkerWill(0.005f, 5, relmut));
		setPersonality(Personality.fromTraits(PersonalityTraits.WIMPINESS, PersonalityTraits.RETALIATION,
				PersonalityTraits.PAIN_SENSITIVITY, PersonalityTraits.PAIN_DISTRACTION,
				PersonalityTraits.DEMORALIZATION, PersonalityTraits.SHAME, BasicTendency.FORM_MUTABILITY,
				BasicTendency.POSITION_MUTABILITY));
	}

	@Override
	public String report() {
		return this.getClass().getSimpleName() + "{active=" + this.active + (this.removed ? ", (removed)" : "")
				+ ", detachCondition=" + this.detachCondition + ", clock=" + this.clock + ", memory=" + this.memory
				+ (groups.isEmpty() ? "" : ", parentGroups=" + this.groups) + ", perception=" + this.perception
				+ ", will=" + this.will + ", personality=" + this.personality + "}";
	}

	@Override
	public void runTick(IComponentPart part, Collection<? extends IComponentPart> access, long ticks) {
		IBeingAccess info = IBeingAccess.create(this, part, access, ticks);
		this.perception.update(info);
		this.will.willTick(info);
		this.memory.tickMemoriesAndFeelings(info);
	}

	@Override
	public void runUntetheredTick(GameMap world, long ticks) {
		IBeingAccess info = IBeingAccess.create(this, ticks, world);
		this.perception.update(info);
		this.will.willTick(info);
		this.memory.tickMemoriesAndFeelings(info);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" + this.getUUID() + "}";
	}

	@Override
	public boolean canAttachHost(IComponentPart part, ISoma body) {
		return !this.detachCondition.isDestroyed(body, part)
				&& body.getChannelSystems().stream().flatMap((a) -> a.getCenterTypes(ChannelRole.CONTROL).stream())
						.anyMatch((a) -> part.getAbilities().contains(a));
	}

	@Override
	public void onAttachHost(IComponentPart part, ISoma body) {
		part.addTether(ITether.create(getProfile(), TetherType.POSSESSOR), true);

	}

	@Override
	public void onRemove(IComponentPart part, ISoma body) {
		part.removeTether(TetherType.POSSESSOR, getProfile(), true);
		part.addTether(ITether.create(getProfile(), TetherType.FORMER_POSSESSOR), true);
	}

	@Override
	public boolean readyForDeletion(GameMap world, long ticks) {
		return false;
	}

	@Override
	public void onRemoveFromMap(GameMap gameMap, long ticks) {
		// TODO Auto-generated method stub
		System.out.println(this + " passed on.");

	}

	@Override
	public void onUntethering(GameMap gameMap, long ticks) {
		// TODO Auto-generated method stub
		System.out.println("!!!" + this + " died and is now untethered.");

	}

	@Override
	public IComponentPart onHostEffectApplied(IComponentPart part, ISoma body, IPartStatusEffectInstance effect) {
		// TODO What to do when the host gains an effect (sleep?)
		if (effect.getEffect() == BasicStatusEffect.UNCONSCIOUS) {
			this.active = false;
		}
		return part;
	}

	@Override
	public IComponentPart onHostEffectRemoved(IComponentPart part, ISoma body, IPartStatusEffectInstance effect) {
		// TODO what to do when host loses effect (sleep?)
		if (effect.getEffect() == BasicStatusEffect.UNCONSCIOUS) {
			this.active = true;
		}
		return part;
	}

	@Override
	public IComponentPart onAnyPartStateChange(IComponentPart thisPart, ISoma body, IComponentPart changedPart) {
		// TODO Auto-generated method stub
		return thisPart;
	}

	@Override
	public IComponentPart onAnySeverances(IComponentPart thisPart, ISoma hostBody, ISoma... severed) {
		if (!hostBody.getPartGraph().contains(thisPart)) {
			return null;
		}
		return thisPart;
	}

	@Override
	public IComponentPart onHostStateChange(IComponentPart part, ISoma body) {
		if (this.detachCondition.isDestroyed(body, part)) {
			return null;
		}
		return super.onHostStateChange(part, body);
	}

	@Override
	public boolean isRemoved() {
		return removed;
	}

	@Override
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

}
