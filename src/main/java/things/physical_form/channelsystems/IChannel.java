package things.physical_form.channelsystems;

import java.util.Collection;

import things.physical_form.graph.IPartConnection;
import things.physical_form.material.IMaterial;

/**
 * A type of relation between body parts characterized by a channel
 */
public interface IChannel extends IPartConnection {

	/**
	 * Get the embedded materials this channel is conveyed through, if any (e.g.
	 * nerve (signals) are conveyed through nerve tissue). These materials are
	 * embbedded into the body parts connected by this channel
	 * 
	 * @return
	 */
	public Collection<? extends IMaterial> getVectorMaterials();

	/**
	 * What quantities or properties this Channel conveys, if any
	 * 
	 * @return
	 */
	public Collection<? extends IChannelResource<?>> conveys();

	/**
	 * Whether this is the "active" direction of the channel, i.e. the direction the
	 * channel conveys resources. The channel can go both ways of course, in which
	 * case both directions return true
	 * 
	 * @return
	 */
	public boolean isActiveDirection();

}
