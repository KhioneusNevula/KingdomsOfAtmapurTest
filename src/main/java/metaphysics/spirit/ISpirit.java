package metaphysics.spirit;

import java.util.Collection;

import metaphysics.being.IBeing;
import things.form.soma.IPartHealth;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.status_effect.IPartStatusEffectInstance;
import thinker.mind.memory.IMindKnowledgeBase;
import thinker.mind.perception.IPerception;
import thinker.mind.personality.IPersonality;

/**
 * An intangible being which can attach to a body to act within it. The main
 * thing that spirits have that an {@link IBeing} doesn't is the ability to
 * attach to parts and track those, and also have an {@link IPerception}
 * 
 * @author borah
 *
 */
public interface ISpirit extends IBeing {

	/** The perception is the things this being senses */
	public IPerception getPerception();

	/**
	 * Run tick on this being when it is in a body
	 * 
	 * @param part   the central part this spirit is tied to
	 * @param access the parts of the body this spirit has access to, based on the
	 *               ChannelCenter that is running it
	 * @param ticks
	 */
	public void runTick(IComponentPart part, Collection<? extends IComponentPart> access, long ticks);

	/**
	 * Have this spirit determine the "health" of a specific component part, usually
	 * using {@link IPartHealth}
	 */
	public float determineHealthOfPart(IComponentPart part, ISoma body);

	/**
	 * Whether this spirit can attach itself to the given part in the given body
	 * 
	 * @param part
	 * @param body
	 * @return
	 */
	public boolean canAttachHost(IComponentPart part, ISoma body);

	/**
	 * Run this method when attaching this spirit to a given part and body
	 * 
	 * @param part
	 * @param body
	 */
	public void onAttachHost(IComponentPart part, ISoma body);

	/**
	 * Called when the host changes material or shape; return a new ComponentPart if
	 * the resulting state change causes this spirit to retether to another part;
	 * return the same part if the state change causes no reaction; return null if
	 * the spirit can no longer be tethered
	 * 
	 * @return
	 */
	public IComponentPart onHostStateChange(IComponentPart part, ISoma body);

	/**
	 * Called when the host removes this spirit from itself. Use this to, e.g.,
	 * generate a ghost or something similar
	 * 
	 * @param part
	 * @param body
	 */
	public void onRemove(IComponentPart part, ISoma body);

	/**
	 * Called when the host gains a new status effect; return same things as
	 * {@link #onHostStateChange(IComponentPart, ISoma)}
	 * 
	 * @param part
	 * @param body
	 * @param effect
	 */
	public IComponentPart onHostEffectApplied(IComponentPart part, ISoma body, IPartStatusEffectInstance effect);

	/**
	 * Called when the host removes a status effect; return same things as
	 * {@link #onHostStateChange(IComponentPart, ISoma)}
	 * 
	 * @param part
	 * @param body
	 * @param effect
	 */
	public IComponentPart onHostEffectRemoved(IComponentPart part, ISoma body, IPartStatusEffectInstance effect);

	/**
	 * Called when any part in the host body changes. If this spirit has access to
	 * said part, it may reanalyze itself. Return same things as
	 * {@link #onHostStateChange(IComponentPart, ISoma)}
	 */
	public IComponentPart onAnyPartStateChange(IComponentPart thisPart, ISoma body, IComponentPart changedPart);

	/**
	 * Called when any parts of the host is severed. Return a part in any of the
	 * bodies if this triggers a retethering instance, or null if the spirit is
	 * detethered. If this is in a severed part, it will automatically be shunted
	 * into the severed part anyway.
	 */
	public IComponentPart onAnySeverances(IComponentPart thisPart, ISoma hostBody, ISoma... severed);

}
