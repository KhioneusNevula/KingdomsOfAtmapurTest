package things.interfaces;

import sim.IRenderable;
import sim.world.GameMap;
import things.physical_form.ISoma;
import things.physical_form.IVisage;

public interface IActor extends IThing, IPhysicsObject, IRenderable {

	/**
	 * Return the physical form of this Actor
	 * 
	 * @return
	 */
	public ISoma<?> getBody();

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
	void spawnIntoMap(GameMap map);

	/**
	 * Run ticks
	 * 
	 * @param ticks
	 */
	public void tick(long ticks);
}
