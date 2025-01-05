package things.spirit;

import mental.IMemoryStorage;
import things.form.graph.connections.IPartConnection;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.interfaces.IUnique;
import utilities.graph.IRelationGraph;

/**
 * An intangible element of a sentient being which governs their body and how
 * they operate it
 * 
 * @author borah
 *
 */
public interface ISpirit extends IUnique {

	/**
	 * Return this spirit's memory storage
	 * 
	 * @return
	 */
	public IMemoryStorage getMemories();

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
	 * Run tick on this spirit
	 * 
	 * @param part
	 * @param access the parts of the body this spirit has access to, based on the
	 *               ChannelCenter that is running it
	 * @param ticks
	 */
	public void runTick(IComponentPart part, IRelationGraph<IComponentPart, IPartConnection> access, ISoma body,
			long ticks);

}
