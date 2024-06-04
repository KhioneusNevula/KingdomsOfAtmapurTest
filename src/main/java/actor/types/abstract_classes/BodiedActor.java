package actor.types.abstract_classes;

import biology.anatomy.Body;
import biology.anatomy.ISpecies;
import sim.GameMapTile;

public abstract class BodiedActor extends MultipartActor {

	public BodiedActor(GameMapTile world, String name, ISpecies template, int startX, int startY) {
		super(world, name, template, startX, startY);
	}

	protected void initBody() {
		if (species == null)
			this.body = new Body(this, 10, 70);
		else {
			this.body = new Body(this, (ISpecies) species);

		}
		((Body) this.body).buildBody();
	}

	@Override
	public Body getBody() {
		return (Body) super.getBody();
	}

	@Override
	public Body getPhysical() {
		return (Body) super.getPhysical();
	}

	@Override
	public ISpecies getObjectType() {
		return (ISpecies) super.getObjectType();
	}

	@Override
	public Body getVisage() {
		return (Body) super.getVisage();
	}

	@Override
	public int physicality() {
		return this.getBody().physicalityMode();
	}

}
