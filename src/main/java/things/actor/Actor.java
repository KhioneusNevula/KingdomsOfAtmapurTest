package things.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import _main.WorldGraphics;
import _sim.MapLayer;
import _sim.RelativeSide;
import _sim.dimension.IDimensionTag;
import _sim.plane.Plane;
import _sim.vectors.IVector;
import _sim.world.GameMap;
import things.blocks.IBlockState;
import things.form.IForm;
import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;
import things.form.material.property.MaterialProperty;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.visage.IVisage;
import things.interfaces.IUnique;
import things.physics.ForceType;

public class Actor implements IActor {

	private String name;
	private UUID id;
	private IVector location;
	private GameMap world;
	private ISoma body;
	private IVisage<?> visage;
	private IVector velocity;
	private IKind kind;

	public Actor(UUID id) {
		this.id = id;
		this.name = "Actor" + id.getMostSignificantBits();
		this.location = IVector.of(0, 0);
		this.velocity = IVector.of(0, 0);
		this.kind = IKind.MISCELLANEOUS;
	}

	/**
	 * Make a body for this entity based on its existing Kind; set the appropriate
	 * fields and also return the body
	 * 
	 * @param settings the settings used to make the body
	 */
	public ISoma makeBody(IKindSettings settings, boolean setVisage) {
		if (kind == null)
			throw new IllegalStateException(this + "");
		this.setBody(kind.generate(settings));
		if (setVisage)
			this.setVisage((IVisage<?>) this.body);
		return this.body;
	}

	@Override
	public IKind getKind() {
		return kind;
	}

	public void setKind(IKind kind) {
		this.kind = kind;
	}

	public Actor setBody(ISoma soma) {
		this.body = soma;
		soma.setOwner(this);
		return this;
	}

	public Actor setVisage(IVisage<?> visage) {
		this.visage = visage;
		visage.setOwner(this);
		return this;
	}

	public Actor setBodyAndVisage(IForm<?> thing) {
		this.body = (ISoma) thing;
		this.visage = (IVisage<?>) thing;
		return this;
	}

	/**
	 * Set the position of this Actor
	 * 
	 * @param newLoc
	 */
	@Override
	public void setPosition(IVector position) {
		this.location = position;
	}

	@Override
	public void onSpawnIntoMap(GameMap map) {
		if (this.location.getDimension() != null
				&& !this.location.getDimension().equals(map.getMapTile().getDimension())) {
			this.location = this.location.withDimension(map.getMapTile().getDimension());
		}
		this.world = map;
	}

	@Override
	public void onRemoveFromMap(GameMap gameMap) {
		// TODO on remove?
	}

	protected float getDynamicFrictionCoeff() {
		IBlockState standingOn = world.getBlockMap().getBlock(this.location.down());
		IBlockState immersedIn = world.getBlockMap().getBlock(this.location);
		float avg = (this.body.getMainMaterial().getProperty(MaterialProperty.ROUGHNESS)
				+ standingOn.getBlock().getMaterial().getProperty(MaterialProperty.ROUGHNESS)) / 2;
		float drag = (1 - avg) * immersedIn.getBlock().getMaterial().getProperty(MaterialProperty.VISCOSITY);
		return avg + drag;
	}

	protected float getStaticFrictionCoeff() {
		IBlockState standingOn = world.getBlockMap().getBlock(this.location.down());
		return (this.body.getMainMaterial().getProperty(MaterialProperty.UNEVENNESS)
				+ standingOn.getBlock().getMaterial().getProperty(MaterialProperty.UNEVENNESS)) / 2;
	}

	@Override
	public void tick(long ticks, float ticksPerSecond) {
		// TODO run ticks on actor relating to physical interactions
		if (this.body != null) {
			this.body.runTick(ticks);
			if (!this.body.isDestroyed()) {
				if (this.body.hasBrokenOffParts()) {
					// TODO calculate momentum and physics and all that whee
					List<Actor> toSpawn = new ArrayList<>();
					float totalmass = this.mass();
					for (ISoma soma : this.body.popBrokenOffParts()) {
						Actor newActor = new Actor(soma.getCenterPart().getID()).setBodyAndVisage(soma);
						newActor.setPosition(this.getPosition());
						totalmass += newActor.mass();
						toSpawn.add(newActor);
					}
					IVector totalMomentum = this.velocity.scaleMagnitudeBy(totalmass);
					for (Actor a : toSpawn) {
						a.setVelocity(totalMomentum.scaleMagnitudeBy(1f / a.mass()));
					}
					this.velocity = totalMomentum.scaleMagnitudeBy(1f / this.mass());
					this.getMap().queueAction(() -> toSpawn.forEach(this.getMap()::spawnIntoWorld));
				}
				if (Math.abs(this.velocity.mag()) > 0.000001f) {

					/**
					 * IBlockState standingOn = world.getBlockMap().getBlock(this.location.down());
					 * float brough =
					 * this.body.getMainMaterial().getProperty(MaterialProperty.ROUGHNESS); float
					 * sorough =
					 * standingOn.getBlock().getMaterial().getProperty(MaterialProperty.ROUGHNESS);
					 */
					float mu = this.getDynamicFrictionCoeff();
					float fric = friction(mu, this.world.gravity() / ticksPerSecond);
					this.velocity = this.velocity.clampSubtract(fric / mass());
					IVector newloc = this.location.add(velocity);
					/**
					 * System.out.println("son:" + standingOn + " mu1:" + brough + " mu2:" + sorough
					 * + "=mu:" + mu + " to:" + newloc + " v:" + this.velocity + " m:" +
					 * this.mass());
					 */
					if (!this.getMap().outOfBounds(newloc)) {
						this.location = newloc;
					}
				}

			}
			if (this.visage != null)
				this.visage.runTick(ticks);
		} else {

		}
	}

	/**
	 * Gets a simple identifying name for this actor
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set a simple display name for this actor
	 * 
	 * @param name
	 * @return
	 */
	public Actor setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public IDimensionTag getDimension() {
		return location.getDimension();
	}

	@Override
	public GameMap getMap() {
		return world;
	}

	@Override
	public MapLayer getLayer() {
		return location.getLayer();
	}

	@Override
	public UUID getUUID() {
		return id;
	}

	@Override
	public void move(IVector difference) {
		this.location = this.location.add(difference);
	}

	@Override
	public void accelerate(IVector acceleration) {
		this.velocity = this.velocity.add(acceleration);
	}

	@Override
	public void applyForce(IComponentPart at, IVector force, ForceType type, IComponentPart generatedBy) {
		// TODO do complex force interactions
		switch (type) {
		case BLUNT:
		case PUSH:
		case SCRATCH:
		case SLICE:
		case STAB:
		}
	}

	@Override
	public void applyForce(IComponentPart at, IVector force, ForceType type, RelativeSide side) {
		// TODO do complex force interactions
		switch (type) {
		case BLUNT:

		case PUSH:

		case SCRATCH:

		case SLICE:

		case STAB:
		}

	}

	@Override
	public IVector velocity() {
		return this.velocity;
	}

	/**
	 * Set this actor's velocity
	 * 
	 * @param velocity
	 */
	public void setVelocity(IVector velocity) {
		this.velocity = velocity;
	}

	@Override
	public IVector getPosition() {
		return location;
	}

	@Override
	public boolean canRender() {
		return visage == null ? false : visage.canRender();
	}

	@Override
	public int visibilityPlanes() {
		return visage == null ? Plane.NO_PLANE.getPrime() : visage.visibilityPlanes();
	}

	@Override
	public void draw(WorldGraphics g) {
		if (this.visage != null && this.visage.canRender())
			this.visage.draw(g);
	}

	@Override
	public ISoma getBody() {
		return this.body;
	}

	@Override
	public IVisage<?> visage() {
		return this.visage;
	}

	@Override
	public float mass() {
		return this.body == null ? 0 : this.body.mass();
	}

	@Override
	public float size() {
		return this.body == null ? 0 : this.body.size();
	}

	@Override
	public int hashCode() {
		return this.getUUID().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IUnique iu) {
			return this.getUUID().equals(iu.getUUID());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "[|" + this.name + "|]" + (this.location != IVector.ZERO ? this.location + "" : "");
	}

	@Override
	public String report() {
		return this + "{\n" + (this.kind != null ? "kind=" + this.kind + ",\n" : "")
				+ (this.body != null ? "body=" + this.body.somaReport() + ",\n" : "")
				+ (this.visage != null && this.visage != this.body ? "visage=" + this.visage.visageReport() + ",\n"
						: "")
				+ (this.velocity != IVector.ZERO ? "velocty=" + this.velocity + ",\n" : "") + "id=" + this.id + "\n}";
	}

}
