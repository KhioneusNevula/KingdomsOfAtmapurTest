package actor;

import java.util.Random;

import actor.construction.IVisage;
import phenomenon.IPhenomenon;
import sim.interfaces.IExistsInWorld;
import sim.interfaces.ITemplate;
import sim.interfaces.IUnique;

/**
 * Thing which can be perceived and assigned properties
 * 
 * @author borah
 *
 */
public interface IUniqueExistence extends IUnique, IExistsInWorld {

	/**
	 * Return the primary vessel of sensing this thing. <br>
	 * TODO Later, change this to also depend on the sense? maybe
	 * 
	 * @return
	 */
	public IVisage getVisage();

	/**
	 * Gets the "species" (type) of actor, phenomenon, etc that this is
	 * 
	 * @return
	 */
	public ITemplate getSpecies();

	/**
	 * How different this thing is from the 'prototypical' example of its template.
	 * 
	 * @return
	 */
	public default float uniqueness() {
		return this.getSpecies().averageUniqueness();
	}

	public default boolean isActor() {
		return this instanceof Actor;
	}

	public default Actor getAsActor() {
		return (Actor) this;
	}

	public default boolean isPhenomenon() {
		return this instanceof IPhenomenon;
	}

	public default IPhenomenon getAsPhenomenon() {
		return (IPhenomenon) this;
	}

	public Random rand();

	/**
	 * Get the name used for display purposes in testing this from other similar
	 * entities
	 * 
	 * @return
	 */
	String getSimpleName();

}
