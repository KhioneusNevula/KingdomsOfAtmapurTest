package things.physical_form.channelsystems;

import java.util.Collection;

import things.physical_form.ISoma;
import things.physical_form.channelsystems.IChannelCenter.ChannelRole;
import things.physical_form.components.IComponentPart;

/**
 * A singleton representation of a specific kind of channelSystem
 * 
 * @author borah
 *
 */
public interface IChannelSystem {

	public static enum ChannelType {
		/** for systems conveying embedded materials */
		MATERIAL,
		/** for systems maintaining signals to parts */
		SIGNAL,
		/** for systems conveying other things, e.g. offspring via gestation */
		OTHER
	}

	/**
	 * The name of this channel system
	 * 
	 * @return
	 */
	public String name();

	/**
	 * The type of this system
	 * 
	 * @return
	 */
	public ChannelType getType();

	/**
	 * Return a list of types of channel-centers, parts which play important roles
	 * in these systems
	 * 
	 * @return
	 */
	public Collection<IChannelCenter> getCenterTypes();

	/**
	 * Get all channelcenters of the given role.
	 * 
	 * @param role
	 * @return
	 */
	public Collection<IChannelCenter> getCenterTypes(ChannelRole role);

	/**
	 * The different types of channel connections in this system, e.g. blood
	 * vessels, nerves
	 * 
	 * @return
	 */
	public Collection<IChannel> getChannelConnectionTypes();

	/**
	 * Populate a body with the channels and centers of this system
	 * 
	 * @param body
	 */
	public <E extends IComponentPart> void populateBody(ISoma<E> body);

	/**
	 * Called when a body experiences a significant change in one of its parts (i.e.
	 * material or shape change)
	 * 
	 * @param body
	 * @param updated
	 */
	public <E extends IComponentPart> void onBodyUpdate(ISoma<E> body, E updated);

	/**
	 * Called when a body loses a part
	 * 
	 * @param body
	 * @param lost
	 */
	public <E extends IComponentPart> void onBodyLoss(ISoma<E> body, E lost);

	/**
	 * Called when a body gains this new part
	 * 
	 * @param body
	 * @param gained
	 */
	public <E extends IComponentPart> void onBodyNew(ISoma<E> body, E gained);

}
