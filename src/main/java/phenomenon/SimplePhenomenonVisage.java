package phenomenon;

import actor.construction.physical.IVisage;
import sim.interfaces.IObjectType;
import sim.physicality.ExistencePlane;

public class SimplePhenomenonVisage implements IVisage {

	private IPhenomenon owner;
	private int visi = ExistencePlane.ALL_PLANES.primeFactor();

	public SimplePhenomenonVisage(IPhenomenon owner) {
		this.owner = owner;
	}

	@Override
	public IPhenomenon getOwner() {
		return owner;
	}

	@Override
	public IObjectType getObjectType() {
		return owner.getObjectType();
	}

	@Override
	public int visibilityMode() {
		return visi;
	}

}
