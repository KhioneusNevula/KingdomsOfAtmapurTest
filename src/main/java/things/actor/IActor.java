package things.actor;

import _sim.IRenderable;
import _sim.world.GameMap;
import things.form.kinds.IKind;
import things.form.soma.ISoma;
import things.form.visage.IVisage;
import things.interfaces.IThing;
import things.physics_and_chemistry.IPhysicsObject;

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
	 * Radius in meters
	 * 
	 * @return
	 */
	public float size();

	/**
	 * Spawn actor into given map; called by map when spawning actors
	 * 
	 * @param map
	 */
	void onSpawnIntoMap(GameMap map);

	/**
	 * Run ticks
	 * 
	 * @param ticks
	 * @param ticksPerSecond
	 */
	public void tick(long ticks, float ticksPerSecond);

	/**
	 * What kind of actor this is
	 * 
	 * @return
	 */
	public IKind getKind();

	/**
	 * Return details about the actor's construction
	 * 
	 * @return
	 */
	public String report();

	public void onRemoveFromMap(GameMap gameMap);

}
