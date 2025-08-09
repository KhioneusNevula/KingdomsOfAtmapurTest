package metaphysics.being;

import java.util.Collection;

import party.IParty;
import party.collective.ICollective;
import things.interfaces.IUnique;
import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;

/**
 * A type of unique entity with a recognized identity, but which *may or may not
 * exist!*
 * 
 * @author borah
 *
 */
public interface IFigure extends IUnique, IParty {

	@Override
	default UniqueType getUniqueType() {
		return UniqueType.FIGURE;
	}

	/** Gets the groups this figure is part of */
	public Collection<ICollective> getParentGroups();

	/** Adds this figure to a group */
	public void addToGroup(ICollective group);

	/** Removes this figure from a group */
	public void removeFromGroup(ICollective group);

	/** Return true if part of the given group */
	public default boolean partOfGroup(ICollective g) {
		return this.getParentGroups().contains(g);
	}

	/**
	 * Whether this Figure is loaded into the world or is in an unloaded format
	 * 
	 * @return
	 */
	public boolean isLoaded();

	/** whether this individual exists or not */
	public boolean exists();

	/** whether this figure is persistent and therefore should be stored */
	public boolean isPersistent();

	/** mark this figure as persistent */
	public void markPersistent();

	/**
	 * Whether this being is "active," i.e. it is considered to still be "alive" or
	 * something similar
	 * 
	 * @return
	 */
	public boolean isActive();

	/** set if this figure is active */
	public void setActive(boolean active);

	/**
	 * Return the actual being this figure refers to, if it is loaded; otherwise
	 * null
	 */
	public IBeing getReferent();

	public IProfile getProfile();

}
