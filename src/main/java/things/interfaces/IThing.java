package things.interfaces;

import java.util.UUID;

import thinker.concepts.profile.IProfile;

/** A unique object that exists in the world */
public interface IThing extends IExistsInWorld, ISensable {

	/** Has a unique UUID */
	public UUID getUUID();

	/**
	 * Get the profile of this unique entity
	 * 
	 * @return
	 */
	public IProfile getProfile();

	/**
	 * Run ticks
	 * 
	 * @param ticks
	 * @param ticksPerSecond
	 */
	public void tick(long ticks, float ticksPerSecond);

	/**
	 * Radius in meters
	 * 
	 * @return
	 */
	public float size();
}
