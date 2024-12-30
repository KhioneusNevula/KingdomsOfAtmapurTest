package things.physical_form;

import java.util.Collection;

import sim.RelativeSide;
import things.IMultipart;
import things.physical_form.channelsystems.IChannel;
import things.physical_form.channelsystems.IChannelCenter;
import things.physical_form.channelsystems.IChannelSystem;
import things.physical_form.components.IComponentPart;
import things.physical_form.components.IPartStat;
import things.physical_form.graph.IPartConnection;
import things.physical_form.material.IMaterial;
import things.spirit.ISpirit;
import utilities.graph.IRelationGraph;

/**
 * Representation of a "body" or the physical structure of an object
 * 
 * @author borah
 *
 * @param <P>
 */
public interface ISoma<P extends IComponentPart> extends IMultipart<P> {

	/**
	 * Get the center part, i.e. the part which remains part of the original Soma
	 * and never is severed into subparts. E.g. for a human this might be body(?)
	 * 
	 * @return
	 */
	public P getCenterPart();

	/**
	 * Add a channel to this body
	 * 
	 * @param one
	 * @param channel
	 * @param two
	 */
	public void addChannel(IComponentPart one, IChannel channel, IComponentPart two);

	/**
	 * Attempt to sever a connection between these two parts and return a subgraph
	 * if any part of the Soma is now disconnected, or null if nothing was
	 * disconnected
	 * 
	 * @param partOne
	 * @param partTwo
	 * @return
	 */
	public ISoma<P> severConnection(IComponentPart partOne, IComponentPart partTwo);

	/**
	 * Get all parts which are connected to this part by JOINED or MERGED
	 * 
	 * @param fromPart
	 * @return
	 */
	public Collection<P> getConnectedParts(IComponentPart fromPart);

	/**
	 * Get all parts of this that are joined/merged to the central body part
	 * 
	 * @return
	 */
	public Collection<P> getContiguousParts();

	/**
	 * Get all parts of this which are not directly joined or merged to the central
	 * part
	 * 
	 * @return
	 */
	public Collection<P> getNonConnectedParts();

	/**
	 * Attempts to grow a part out of another part
	 * 
	 * @param newPart
	 * @param fromPart
	 * @param connectionType
	 * @param coveringsides  the sides of the other part that this body part covers
	 * @return
	 */
	public boolean growPart(IComponentPart newPart, IComponentPart fromPart, IPartConnection connectionType,
			Collection<RelativeSide> coveringsides);

	/**
	 * Return all channel systems in this Soma
	 * 
	 * @return
	 */
	public Collection<IChannelSystem> getChannelSystems();

	/**
	 * Get all channelcenters of the given type
	 * 
	 * @param type
	 * @return
	 */
	public Collection<P> getChannelCenters(IChannelCenter type);

	/**
	 * Get all channels that connect to this part
	 * 
	 * @param channel
	 * @return
	 */
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart);

	/**
	 * Get all channels that connect to this part that are part of the given
	 * ChannelSystem
	 * 
	 * @param channel
	 * @return
	 */
	public Collection<IChannel> getConnectingChannels(IComponentPart toPart, IChannelSystem system);

	/**
	 * Returns a subgraph of all parts connecting to this one through edges in the
	 * given channelsystem, e.g. all parts connected to the brain by nerves
	 * 
	 * @param toPart
	 * @param bySystem
	 * @return
	 */
	public IRelationGraph<P, IPartConnection> getChanneledParts(IComponentPart toPart, IChannelSystem bySystem);

	/**
	 * Return all materials that make up this object
	 * 
	 * @return
	 */
	public Collection<IMaterial> getAllBodilyMaterials();

	/**
	 * This material is used for some physics calculations, such as friction.
	 * 
	 * @return
	 */
	public IMaterial getMainMaterial();

	/**
	 * Return the aggregate value of a given part stat for the whole body
	 * 
	 * @param <E>
	 * @param forStat
	 * @return
	 */
	public <E> E getAggregateStat(IPartStat<E> forStat);

	/**
	 * Attach this spirit to the given part. Call
	 * {@link ISpirit#onAttachHost(IComponentPart, ISoma)} after doing so.
	 * 
	 * @param spirit
	 * @param part
	 */
	public void attachSpirit(ISpirit spirit, IComponentPart part);

	/**
	 * Remove this spirit from the given part. Run
	 * {@link ISpirit#onRemove(IComponentPart, things.physical_form.ISoma)} when
	 * doing so.
	 * 
	 * @param spirit
	 * @param part
	 */
	public void removeSpirit(ISpirit spirit, IComponentPart part);

	/**
	 * Mass of this in kg
	 * 
	 * @return
	 */
	public float mass();

}
