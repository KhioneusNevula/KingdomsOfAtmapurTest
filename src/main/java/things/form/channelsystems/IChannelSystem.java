package things.form.channelsystems;

import java.util.Collection;
import java.util.Collections;

import _sim.world.GameMap;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.settings.IKindSettings;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import thinker.mind.util.IMindAccess;

/**
 * A singleton representation of a specific kind of channelSystem
 * 
 * @author borah
 *
 */
public interface IChannelSystem {

	public static enum ChannelType {
		/** for systems conveying embedded materials (material type, usually) */
		MATERIAL,
		/** for systems maintaining signals to parts (boolean type, usually) */
		SIGNAL,
		/** for systems diffusing some kind of energy to parts (float type, usually) */
		ENERGY,
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
	 * Returns a set of Needs that represent any quantity this channel system
	 * requires to remain full or high-level
	 * 
	 * @return
	 */
	public default Collection<IChannelNeed> getChannelSystemNeeds() {
		return Collections.emptySet();
	}

	/**
	 * Return the amount of the given Need this body has from the perspective of
	 * this body part (the brain body part, so to speak) and the given spirit (as
	 * well as what the spirit can access), where 1f means the need is in fine
	 * condition and 0f means the body is in critical condition. Return -1 if the
	 * need cannot be detected
	 * 
	 * @param part
	 * @return
	 */
	public default float getNeedLevel(IChannelNeed forNeed, IMindAccess info) {
		return 1f;
	}

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
	 * Return the channel resources this system makes use of
	 * 
	 * @return
	 */
	public Collection<? extends IResource<?>> getChannelResources();

	/**
	 * Populate a body with the channels and centers of this system and return the
	 * parts that had channel centers applied to them
	 * 
	 * @param body
	 */
	public Collection<? extends IComponentPart> populateBody(ISoma body, IKindSettings set, GameMap world);

	/**
	 * Called when a body experiences a significant change in one of its parts (i.e.
	 * material or shape change)
	 * 
	 * @param body
	 * @param updated
	 */
	public void onBodyUpdate(ISoma body, IComponentPart updated);

	/**
	 * Called when a body loses a part. Return false if the channel system can no
	 * longer exist in the body
	 * 
	 * @param body
	 * @param lost
	 */
	public boolean onBodyLoss(ISoma body, IComponentPart lost);

	/**
	 * Called when a new connection is formed in the body;
	 * 
	 * @param body
	 * @param gained
	 * @param isNew  true if "gained" is a new part rather than an existing part
	 */
	public void onBodyNew(ISoma body, IComponentPart gained, IPartConnection connect, IComponentPart to, boolean isNew);

}
