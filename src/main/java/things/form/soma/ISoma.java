package things.form.soma;

import java.util.Collection;

import _sim.RelativeSide;
import things.form.IForm;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelResource;
import things.form.channelsystems.IChannelSystem;
import things.form.graph.connections.IPartConnection;
import things.form.material.IMaterial;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;
import things.spirit.ISpirit;
import utilities.graph.IRelationGraph;

/**
 * Representation of a "body" or the physical structure of an object
 * 
 * @author borah
 *
 * @param <IComponentPart>
 */
public interface ISoma extends IForm<IComponentPart> {

	/**
	 * Indicates this soma consists only of hole parts
	 * 
	 * @return
	 */
	public boolean isAllHoles();

	/**
	 * Whether this soma is now destroyed (i.e. it has lost all its non-hole parts)
	 * 
	 * @return
	 */
	public boolean isDestroyed();

	/**
	 * Returns a view of all new somas that have broken off this soma through
	 * operations like severance or destroying parts
	 * 
	 * @return
	 */
	public Iterable<ISoma> peekBrokenOffParts();

	/**
	 * Returns all new somas broken off this soma, and deletes them from the soma's
	 * own memory
	 */
	public Iterable<ISoma> popBrokenOffParts();

	/**
	 * Whether this soma has broken off parts to pop
	 * 
	 * @return
	 */
	public boolean hasBrokenOffParts();

	/**
	 * Get the center part, i.e. the part which remains part of the original Soma
	 * and never is severed into subparts. E.g. for a human this might be body(?)
	 * 
	 * @return
	 */
	public IComponentPart getCenterPart();

	/**
	 * Add a channel to this body.
	 * 
	 * @param one
	 * @param channel
	 * @param two
	 * @param callSystemUpdate whether to call an update on the system after this
	 *                         channel is added
	 */
	public void addChannel(IComponentPart one, IChannel channel, IComponentPart two, boolean callSystemUpdate);

	/**
	 * Attempt to sever a connection between these two parts and return a subgraph
	 * if any part of the Soma is now disconnected, or null if nothing was
	 * disconnected
	 * 
	 * @param partOne
	 * @param partTwo
	 * @return
	 */
	public void severConnection(IComponentPart partOne, IComponentPart partTwo);

	/**
	 * Get all parts which are connected to this part by JOINED or MERGED, or
	 * connected to a part connecting to this part
	 * 
	 * @param fromPart
	 * @return
	 */
	public Collection<IComponentPart> getConnectedParts(IComponentPart fromPart);

	/**
	 * Get all parts of this that are joined/merged to the central body part
	 * 
	 * @return
	 */
	public Collection<IComponentPart> getContiguousParts();

	/**
	 * Get all parts of this which are not directly joined or merged to the central
	 * part
	 * 
	 * @return
	 */
	public Collection<IComponentPart> getNonConnectedParts();

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
	 * Return the aggregate value of some channel resource
	 * 
	 * @param <E>
	 * @param resource
	 * @return
	 */
	public <E> E getResourceAggregate(IChannelResource<E> resource);

	/**
	 * Get all channelcenters of the given type
	 * 
	 * @param type
	 * @return
	 */
	public Collection<IComponentPart> getChannelCenters(IChannelCenter type);

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
	public IRelationGraph<IComponentPart, IPartConnection> getChanneledParts(IComponentPart toPart,
			IChannelSystem bySystem);

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
	 * Mass of this in kg
	 * 
	 * @return
	 */
	public float mass();

	/**
	 * Return a string reporting the different components of this Soma
	 * 
	 * @return
	 */
	public String somaReport();

	/**
	 * Called when embedded materials are added or removed from this part
	 * 
	 * @param part
	 * @param changedEmbeddeds the embedded materials that have been added or
	 *                         removed (of course you can check which it is by
	 *                         checking whether they are contained in the part's
	 *                         embeddedmaterials or not)
	 * @param tick
	 */
	public void onPartEmbeddedMaterialsChanged(IComponentPart part, Collection<IMaterial> changedEmbeddeds);

	/**
	 * Called when abilities are added or removed from this part
	 * 
	 * @param part
	 * @param changedEmbeddeds the abilities that have been added or removed (of
	 *                         course you can check which it is by checking whether
	 *                         they are contained in the part's abilities or not)
	 * @param tick
	 */
	public void onPartAbilitiesChange(IComponentPart part, Collection<IPartAbility> changedEmbeddeds);

	/**
	 * Called when a part experiences a change in the value of a stat
	 * 
	 * @param part
	 * @param forStat
	 * @param formerValue
	 */
	public void onPartStatChange(IComponentPart part, IPartStat<?> forStat, Object formerValue);

	/**
	 * Called when a part experiences a change in its contained channel resources
	 * 
	 * @param part
	 * @param forStat
	 * @param formerValue
	 */
	public void onChannelResourceChanged(IComponentPart part, IChannelResource<?> resource, Object formerValue);

	/**
	 * Called when a spirit is added to a part
	 * 
	 * @param spirit
	 * @param part
	 */
	void onAttachSpirit(ISpirit spirit, IComponentPart part);

	/**
	 * Called when a spirit is removed from a part
	 * 
	 * @param spirit
	 * @param part
	 */
	void onRemoveSpirit(ISpirit spirit, IComponentPart part);
}
