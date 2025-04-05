package things.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import _graphics.WorldGraphics;
import _sim.MapLayer;
import _sim.RelativeSide;
import _sim.dimension.IDimensionTag;
import _sim.plane.Plane;
import _sim.plane.PlaneHelper;
import _sim.vectors.IVector;
import _sim.world.GameMap;
import _utilities.graph.IRelationGraph;
import things.blocks.IBlockState;
import things.form.IForm;
import things.form.graph.connections.IPartConnection;
import things.form.graph.connections.PartConnection;
import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;
import things.form.material.property.MaterialProperty;
import things.form.shape.property.ShapeProperty;
import things.form.shape.property.ShapeProperty.Shapedness;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.visage.IVisage;
import things.interfaces.IUnique;
import things.interfaces.UniqueType;
import things.physics_and_chemistry.ForceResult;
import things.physics_and_chemistry.ForceType;
import thinker.concepts.profile.Profile;

public class Actor implements IActor {

	private String name;
	private UUID id;
	private Profile profile;
	private IVector location;
	private GameMap world;
	private ISoma body;
	private IVisage<?> visage;
	private IVector velocity;
	private IKind kind;
	/** whether this actor is ready to be deleted or not */
	private boolean toDelete;

	public Actor(UUID id) {
		this(id, null);
	}

	public Actor(UUID id, String name) {
		this.id = id;
		this.profile = new Profile(id, UniqueType.FORM).setIdentifierName(name);
		this.name = name == null ? "Actor" + id.getMostSignificantBits() : name;
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
	public ISoma makeBody(IKindSettings settings, boolean setVisage, GameMap world) {
		if (kind == null)
			throw new IllegalStateException(this + "");
		this.setBody(kind.generateSoma(settings, this));
		this.body.setUUID(this.id);
		this.body.populateChannelSystems(this, world, world.getTicks());
		body.getPartsByName("brain")
				.forEach((p) -> System.out.println("Part (" + this.id + ") -- " + p.componentReport()));
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
		if (thing != null)
			thing.setOwner(this);
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
		if (this.location.getTile() != null && !this.location.getTile().equals(map.getMapTile())) {
			this.location = this.location.withTile(map.getMapTile());
		}
		this.world = map;
	}

	@Override
	public boolean needsToBeRemoved() {
		return toDelete;
	}

	@Override
	public void onRemoveFromMap(GameMap gameMap) {
		// TODO on remove?
		System.out.println("Removed " + this + " from map.");
	}

	@Override
	public void onUnload(GameMap map) {
		System.out.println("Unloaded " + this + " from map.");

	}

	@Override
	public void onLoad(GameMap map) {
		System.out.println("Loaded " + this + " into map.");

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
						Actor newActor = new Actor(UUID.randomUUID()).setBodyAndVisage(soma);
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

			} else {
				this.toDelete = true;
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
		this.profile.setIdentifierName(name);
		return this;
	}

	@Override
	public IDimensionTag getDimension() {
		return location != null ? (location.getTile() != null ? location.getTile().getDimension() : null) : null;
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

	public void setUUID(UUID id) {
		this.id = id;
		this.profile = new Profile(id, UniqueType.FORM).setIdentifierName(this.profile.getIdentifierName());
	}

	@Override
	public Profile getProfile() {
		return this.profile;
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
	public ForceResult applyForce(IComponentPart at, IComponentPart connection, IVector force, ForceType type,
			IComponentPart generatedBy) {
		// TODO do complex force interactions
		return this.applyForce(at, connection, force, type, RelativeSide.fromVector(force),
				generatedBy.interactionPlanes());
	}

	@Override
	public ForceResult applyForce(IComponentPart at, IComponentPart connection, IVector force, ForceType type,
			RelativeSide side, int acrossPlanes) {
		if (!body.getPartGraph().contains(at)) {
			throw new IllegalStateException(body.getPartGraph() + " does not contain " + at);
		}
		if (!PlaneHelper.canInteract(at.interactionPlanes(), acrossPlanes)) {
			return ForceResult.NOTHING;
		}
		IRelationGraph<IComponentPart, IPartConnection> pgraph = body.getPartGraph();
		// TODO do complex force interactions
		switch (type) {
		case PUSH:
			this.accelerate(force.scaleMagnitudeBy(1 / mass()));
			return ForceResult.MOVED;
		case SCRATCH:
			// TODO scratching mechanic
		case BLUNT:
			float mag = (float) force.mag();
			this.accelerate(force.scaleMagnitudeBy(0.5 / (0.000001f + mass())));

			float resistance = at.getMaterial().getProperty(MaterialProperty.RESISTANCE)
					* at.getShape().getProperty(ShapeProperty.INTEGRITY);
			if (mag >= resistance) {
				Set<IComponentPart> neibs = PartConnection.attachments().stream()
						.flatMap((ata) -> pgraph.getNeighbors(at, ata).stream()).collect(Collectors.toSet());
				if (at.getMaterial().getProperty(MaterialProperty.CRUMBLES)) { // TODO crystalline??
					at.changeMaterial(at.getMaterial().getProperty(MaterialProperty.CRUMBLE_MATERIAL), true);

				} else {
					at.changeShape(at.getShape().copyBuilder().addProperty(ShapeProperty.INTEGRITY, 0f)
							.addProperty(ShapeProperty.SHAPEDNESS, Shapedness.AMORPHIC).build(), true);
				}
				if (body.isDestroyed(at)) {
					neibs.forEach((neib) -> {
						neib.changeShape(neib.getShape().copyBuilder().addProperty(ShapeProperty.INTEGRITY,
								Math.max(1f / (neibs.size() + 1), (mag / (neibs.size() + 1))
										/ (0.000000001f + neib.getMaterial().getProperty(MaterialProperty.RESISTANCE)))
										* neib.getShape().getProperty(ShapeProperty.INTEGRITY))
								.build(), true);
					});

					return ForceResult.DESTROYED;
				}
				return ForceResult.DAMAGED_STRUCTURE;
			} else {
				at.changeShape(at.getShape().copyBuilder()
						.addProperty(ShapeProperty.INTEGRITY, (float) mag / resistance).build(), true);
				return ForceResult.DAMAGED_INTEGRITY;
			}
		case SLICE:
			resistance = at.getMaterial().getProperty(MaterialProperty.RESISTANCE)
					* at.getShape().getProperty(ShapeProperty.INTEGRITY);
			if (connection != null) {
				if (!this.body.getPartGraph().containsEdge(at, PartConnection.JOINED, connection)) {
					return ForceResult.SLIPPED_THROUGH;
				}
				float secresistance = connection.getMaterial().getProperty(MaterialProperty.RESISTANCE)
						* connection.getShape().getProperty(ShapeProperty.INTEGRITY);
				float usedRes = Math.min(resistance, secresistance);
				if (force.mag() >= usedRes) {
					this.body.severConnection(at, connection);
					return ForceResult.SEVERED;
				} else {
					this.body.setConnectionIntegrity(at, connection,
							this.body.getConnectionIntegrity(at, connection) * (float) force.mag() / usedRes);
					return ForceResult.DAMAGED_CONNECTION;
				}
			} else {
				// TODO if you don't attack between two parts, slice the first part in half
				return ForceResult.CUT_PART;
			}
		case STAB:
			// TODO stabbing mechanics
			return ForceResult.MADE_HOLE;
		}
		return ForceResult.NOTHING;
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
		if (this == obj)
			return true;
		if (obj instanceof IUnique iu) {
			return this.getUUID().equals(iu.getUUID());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "[|" + this.name + "|]" + this.kind + (this.location != IVector.ZERO ? this.location + "" : "");
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
