package actor.construction.physical;

import actor.IUniqueEntity;
import sim.interfaces.IObjectType;
import sim.physicality.IInteractability;

public interface IVisage {

	/**
	 * Using {@link IInteractability} to indicate visibility
	 * 
	 * @return
	 */
	public int visibilityMode();

	/**
	 * gets the "type" of this visage, the species, etc
	 * 
	 * @return
	 */
	public IObjectType getObjectType();

	/**
	 * If this visage is a multipart entity
	 * 
	 * @return
	 */
	default boolean isMultipart() {
		return this instanceof IPhysicalActorObject;
	}

	/**
	 * Returns this as a multipart entity
	 * 
	 * @param <A>
	 * @param <B>
	 * @param <C>
	 * @return
	 */
	default <A extends IComponentType, B extends IMaterialLayerType, C extends IComponentPart> IPhysicalActorObject getAsMultipart() {
		return (IPhysicalActorObject) this;
	}

	/**
	 * Returns the owner of this visage
	 * 
	 * @return
	 */
	public IUniqueEntity getOwner();

}
