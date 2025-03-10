package thinker.individual;

import java.util.Collection;

import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.status_effect.IPartStatusEffectInstance;
import thinker.mind.clock.IClock;
import thinker.mind.perception.IPerception;
import thinker.mind.will.IWill;

/**
 * An intangible element of a sentient being which governs their body and how
 * they operate it
 * 
 * @author borah
 *
 */
public interface IMindSpirit extends IBeing {

	/**
	 * The will is the location of the thoughts this spirit thinks
	 * 
	 * @return
	 */
	public IWill getWill();

	/** The perception is the location of the things this spirit senses */
	public IPerception getPerception();

	/**
	 * Get the internal clock of this being
	 * 
	 * @return
	 */
	public IClock getClock();

	/**
	 * Run tick on this being when it is in a body
	 * 
	 * @param part   the central part this spirit is tied to
	 * @param access the parts of the body this spirit has access to, based on the
	 *               ChannelCenter that is running it
	 * @param ticks
	 */
	public void runTick(IComponentPart part, Collection<? extends IComponentPart> access, ISoma body, long ticks);

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
