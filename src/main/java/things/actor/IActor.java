package things.actor;

import _graphics.IRenderable;
import _sim.world.GameMap;
import things.form.kinds.IKind;
import things.form.soma.ISoma;
import things.form.visage.IVisage;
import things.interfaces.IThing;
import things.phenomena.IPhenomenon;
import things.physics_and_chemistry.IPhysicsObject;

/**
 * A movable, independent thing in the world. Unlike {@linkplain IPhenomenon
 * Phenomena}, these things can exist for long periods of time and be made of
 * multiple parts, as well as have things such as effects applied to them, and
 * much more
 */

public interface IActor extends IThing, IPhysicsObject, IRenderable {

	/**
	 * Return the physical form of this Actor
	 * 
	 * @return
	 */
	public ISoma getBody();

	/**
	 * Return the perceived form of this Actor
	 * 
	 * @return
	 */
	public IVisage<?> visage();

	/**
	 * Spawn actor into given map; called by map when spawning actors
	 * 
	 * @param map
	 */
	void onSpawnIntoMap(GameMap map);

	/**
	 * What kind of actor this is
	 * 
	 * @return
	 */
	public IKind getKind();

	/**
	 * Return details about this actor's construction
	 * 
	 * @return
	 */
	public String report();

	public void onRemoveFromMap(GameMap gameMap);

	/** Unloads an actor from the map */
	public void onUnload(GameMap gameMap);

	/** Loads an actor into the map */
	public void onLoad(GameMap gameMap);

}
