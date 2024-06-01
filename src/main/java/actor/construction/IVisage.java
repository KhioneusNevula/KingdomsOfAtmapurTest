package actor.construction;

import actor.IUniqueExistence;
import sim.interfaces.ITemplate;
import sim.physicality.IInteractability;

public interface IVisage {

	/**
	 * Using {@link IInteractability} to indicate visibility
	 * 
	 * @return
	 */
	public int visibilityMode();

	/**
	 * gets the "species" of this visage, the type or whatever
	 * 
	 * @return
	 */
	public ITemplate getSpecies();

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
	public IUniqueExistence getOwner();

}
