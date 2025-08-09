package thinker.mind.emotions;

import java.util.Collection;

import thinker.mind.memory.IFeelingReason;
import thinker.mind.util.IBeingAccess;

/**
 * Something which can have accessible feelings
 * 
 * @author borah
 *
 */
public interface IHasFeelings {

	/** Returns all feelings in this that are being affected by something */
	public Collection<IFeeling> getFeelings();

	/**
	 * Returns the level of this thing's feeling or the default value; this value is
	 * never negative
	 */
	public float getFeeling(IFeeling feeling);

}
