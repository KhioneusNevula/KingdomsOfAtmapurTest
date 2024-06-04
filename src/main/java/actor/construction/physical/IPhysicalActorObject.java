package actor.construction.physical;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import metaphysical.ISpiritObject;
import metaphysical.ISpiritObject.SpiritType;
import metaphysical.soul.AbstractSoul;
import metaphysical.soul.ISoulGenerator;

/**
 * Used to represent an objct or visage of a physical actor, with multiple parts
 * and whatnot
 * 
 * @author borah
 *
 */
public interface IPhysicalActorObject extends IVisage {

	public static enum HitboxType {
		CIRCLE, RECTANGLE;

		public boolean isCircle() {
			return this == CIRCLE;
		}

		public boolean isRect() {
			return this == RECTANGLE;
		}
	}

	/**
	 * Whether hitbox is circle or rect
	 * 
	 * @return
	 */
	public HitboxType getHitboxType();

	/**
	 * radius of hitbox, if circle. undefined behavior if not
	 * 
	 * @return
	 */
	public int getHitboxRadius();

	/**
	 * width if rectangle
	 * 
	 * @return
	 */
	public int getHitboxWidth();

	public int getHitboxHeight();

	/**
	 * Mass of this object in kilgorams
	 */
	public float getMass();

	/**
	 * Whether all the necessary steps have been performed to properly make this
	 * body
	 * 
	 * @return
	 */
	public boolean isBuilt();

	/**
	 * Get parts that do not move with another part
	 * 
	 * @return
	 */
	public Map<UUID, ? extends IComponentPart> getPartsWithoutParent();

	/**
	 * Gets the specific type of this thing
	 * 
	 * @return
	 */
	public IActorType getObjectType();

	/**
	 * Get all parts with this specific ability
	 * 
	 * @param ability
	 * @return
	 */
	public Collection<? extends IComponentPart> getPartsWithAbility(IPartAbility ability);

	Map<String, ? extends IComponentType> getPartTypes();

	/**
	 * Return a list of body parts, tissues, and other information
	 * 
	 * @return
	 */
	public String report();

	/**
	 * What physicality this body has
	 * 
	 * @return
	 */
	int physicalityMode();

	/**
	 * Change the physicality of this thing
	 * 
	 * @param newPhysicality
	 */
	public void changePhysicality(int newPhysicality);

	/**
	 * Return true if this actor is compltely destroyed and ought to be removed
	 * 
	 * @return
	 */
	public boolean completelyDestroyed();

	/**
	 * Cause a physical change to the given part and update any relevant tracking,
	 * including attached spirits!
	 * 
	 * @param part
	 */
	public void updatePart(IComponentPart part);

	/**
	 * Return true if this is an ensouled being which is now dead. Not relevant for
	 * non-souled beings
	 * 
	 * @return
	 */
	public boolean isDead();

	/**
	 * Get all spirits in this actor of given type
	 * 
	 * @return
	 */
	public Collection<? extends ISpiritObject> getContainedSpirits(SpiritType type);

	/**
	 * Gets all spirits on the given part
	 * 
	 * @param part
	 * @return
	 */
	public Collection<? extends ISpiritObject> getContainedSpirits(IComponentPart part);

	/**
	 * Whether the part contains the spirit
	 * 
	 * @param spir
	 * @param part
	 * @return
	 */
	public boolean containsSpirit(ISpiritObject spir, IComponentPart part);

	/**
	 * Update stored data, removing references to this spirit
	 * 
	 * @param spirit
	 */
	public void removeSpirit(ISpiritObject spirit);

	/**
	 * Attach the given spirit to this object. Leave tethers as null if tethered to
	 * whole
	 * 
	 * @param spirit
	 * @param tethers
	 */
	public void tetherSpirit(ISpiritObject spirit, Collection<IComponentPart> tethers);

	/**
	 * If the given spirit is contained in this object
	 * 
	 * @param spirit
	 * @return
	 */
	public boolean containsSpirit(ISpiritObject spirit);

	/**
	 * Simple method called right after when a soul generator gives a soul to a
	 * being upon its first creation. not called for subsequent creations.
	 * 
	 * @param soul
	 * @return
	 */
	public default void onGiveFirstSoul(AbstractSoul soul, ISoulGenerator soulgen) {

	}

}
