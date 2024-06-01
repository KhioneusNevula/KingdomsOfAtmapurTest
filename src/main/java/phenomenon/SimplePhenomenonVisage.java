package phenomenon;

import actor.construction.IVisage;
import sim.interfaces.ITemplate;
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
	public ITemplate getSpecies() {
		return owner.getSpecies();
	}

	@Override
	public int visibilityMode() {
		return visi;
	}

}
