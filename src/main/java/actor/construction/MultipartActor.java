package actor.construction;

import actor.Actor;
import sim.WorldDimension;

public abstract class MultipartActor extends Actor {

	protected IPhysicalActorObject body;

	public MultipartActor(WorldDimension world, String name, IBlueprintTemplate template, int startX, int startY,
			int radius) {
		super(world, name, template, startX, startY, radius);
	}

	public IBlueprintTemplate getSpecies() {
		return (IBlueprintTemplate) super.getSpecies();
	}

	protected abstract void initBody();

	protected void setBody(IPhysicalActorObject body) {
		this.body = body;
	}

	/**
	 * get the body
	 * 
	 * @return
	 */
	public IPhysicalActorObject getBody() {
		return body;
	}

	@Override
	public IPhysicalActorObject getPhysical() {
		return getBody();
	}

	/**
	 * For transformations; swap out the body
	 * 
	 * @param newBody
	 */
	public void transformBody(IPhysicalActorObject newBody) {
		this.body = newBody;
	}

	@Override
	public IPhysicalActorObject getVisage() {
		return body;
	}

}
