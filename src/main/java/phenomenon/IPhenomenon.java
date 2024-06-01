package phenomenon;

import java.util.Collection;

import actor.Actor;
import actor.IUniqueExistence;
import sim.WorldDimension;
import sim.interfaces.IRenderable;

public interface IPhenomenon extends IUniqueExistence, IRenderable {

	/**
	 * Gets the first cause(s) (if any) of this phenomenon. E.g. a fire phenomenon
	 * may have been lit by a specific sentient entity. This is for when phenomena
	 * cause each other in a way that obscures the initial cause, so this tracks the
	 * initial cause. The map is to indicate whether the causal connection is clear
	 * to the given senses
	 * 
	 * @return
	 */
	public Collection<IUniqueExistence> cause();

	/**
	 * Gets the direct cause/source(s) (if any) of this phenomenon. This does not
	 * include a sentient entity source. E.g. a Burn Up event may have a Source
	 * which is an Explosion or a Flame
	 * 
	 * @return
	 */
	public Collection<IUniqueExistence> source();

	/**
	 * Gets the undergoer(s) (if any) of this phenomenon, if it is a phenomenon
	 * which is based on an actor
	 * 
	 * @return
	 */
	public Actor object();

	/**
	 * Gets the result of this phenomenon if it is
	 * transformative/destructive/creative
	 * 
	 * @return
	 */
	public Collection<IUniqueExistence> products();

	/**
	 * Gets the type of phenomenon this is
	 * 
	 * @return
	 */
	public IPhenomenonType type();

	/**
	 * Whether the phenomenon is complete and thereby ready to be removed
	 * 
	 * @return
	 */
	public boolean isComplete();

	/**
	 * What to do each tick
	 */
	public void tick();

	public WorldDimension getWorld();

	public boolean isRelational();
}
