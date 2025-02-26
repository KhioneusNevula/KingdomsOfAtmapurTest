package things.spirit;

import java.util.Collection;

import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.interfaces.IUnique;
import things.status_effect.IPartStatusEffectInstance;
import thinker.mind.clock.IClock;
import thinker.mind.will.IWill;
import thinker.social.IParty;

/**
 * An intangible element of a sentient being which governs their body and how
 * they operate it
 * 
 * @author borah
 *
 */
public interface ISpirit extends IUnique, IParty {

	/**
	 * Whether this spirit is "active," i.e. it is actively thinking. If inactive,
	 * it will not think
	 * 
	 * @return
	 */
	public boolean isActive();

	/**
	 * The will is the location of the thoughts this spirit thinks
	 * 
	 * @return
	 */
	public IWill getWill();

	/**
	 * Get the internal clock of this being
	 * 
	 * @return
	 */
	public IClock getClock();

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
	 * Called when the host gains a new status effect
	 * 
	 * @param part
	 * @param body
	 * @param effect
	 */
	public void onHostEffectApplied(IComponentPart part, ISoma body, IPartStatusEffectInstance effect);

	/**
	 * Called when the host removes a status effect
	 * 
	 * @param part
	 * @param body
	 * @param effect
	 */
	public void onHostEffectRemoved(IComponentPart part, ISoma body, IPartStatusEffectInstance effect);

	/**
	 * Run tick on this spirit
	 * 
	 * @param part
	 * @param access the parts of the body this spirit has access to, based on the
	 *               ChannelCenter that is running it
	 * @param ticks
	 */
	public void runTick(IComponentPart part, Collection<? extends IComponentPart> access, ISoma body, long ticks);

}
