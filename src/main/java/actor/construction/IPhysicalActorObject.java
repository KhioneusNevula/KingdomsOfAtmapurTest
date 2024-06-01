package actor.construction;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

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
	 * Get parts that are outermost (and usually sense-able)
	 * 
	 * @return
	 */
	public Map<UUID, ? extends IComponentPart> getOutermostParts();

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
	public IBlueprintTemplate getSpecies();

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

	public Collection<? extends IComponentPart> getParts();

	/**
	 * If this template consists only of one main part. For example, a rock. s
	 * 
	 * @return
	 */
	public boolean hasSinglePart();

	/**
	 * For single-part actors, the component encompassing its entire entity. Throw
	 * exception if not single-part
	 * 
	 * @return
	 */
	public IComponentPart mainComponent();

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
	 * Change the visibility of this thing
	 * 
	 * @param newVisibility
	 */
	public void changeVisibility(int newVisibility);

	/**
	 * Return true if this actor is compltely destroyed and ought to be removed
	 * 
	 * @return
	 */
	public boolean completelyDestroyed();

	/**
	 * Cause a physical change to the given part and update any relevant tracking
	 * 
	 * @param part
	 */
	public void updatePart(IComponentPart part);

}
