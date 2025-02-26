package things.form.channelsystems;

import java.util.Collection;

import things.form.graph.connections.IPartConnection;
import things.form.material.IMaterial;

/**
 * A type of relation between body parts characterized by a channel
 */
public interface IChannel extends IPartConnection {

	@Override
	public IChannel invert();

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
	public Collection<? extends IResource<?>> conveys();

	/**
	 * Whether this is the "active" direction of the channel, i.e. the direction the
	 * channel conveys resources. The channel can go both ways of course, in which
	 * case both directions return true
	 * 
	 * @return
	 */
	public boolean isActiveDirection();

	/**
	 * The system of this channel
	 * 
	 * @return
	 */
	public IChannelSystem getSystem();

}
