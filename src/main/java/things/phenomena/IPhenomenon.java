package things.phenomena;

import _sim.vectors.IVector;
import things.actor.IActor;
import things.interfaces.IThing;
import things.interfaces.IUnique;
import things.interfaces.UniqueType;

/**
 * A phenomenon is generated when something happens. Phenomena are temporary, do
 * not have parts or bodies, and only do things to surrounding Things, as well
 * as render events
 */
public interface IPhenomenon extends IThing, IUnique {

	@Override
	default UniqueType getUniqueType() {
		return UniqueType.PHENOMENON;
	}

	/** Return NULL if this cannot be rendered in the world */
	public IVector getRenderLocation();

	/**
	 * If this phenomenon is tethered to an actor (e.g. fire can start on an actor)
	 * return that actor. Otherwise, null.
	 */
	public IActor getTetherActor();
}
