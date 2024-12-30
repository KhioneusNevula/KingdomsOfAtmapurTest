package things.actors;

import java.util.UUID;

import _main.WorldGraphics;
import sim.IVector;
import sim.MapLayer;
import sim.Plane;
import sim.world.GameMap;
import sim.world.IDimensionTag;
import things.IMultipart;
import things.blocks.IBlockState;
import things.interfaces.IActor;
import things.interfaces.IUnique;
import things.physical_form.ISoma;
import things.physical_form.IVisage;
import things.physical_form.material.MaterialProperty;

public class Actor implements IActor {

	private String name;
	private UUID id;
	private IVector location;
	private GameMap world;
	private ISoma<?> body;
	private IVisage<?> visage;
	private IVector velocity;

	public Actor(UUID id) {
		this.id = id;
		this.name = "Actor" + id.getMostSignificantBits();
		this.location = IVector.of(0, 0);
		this.velocity = IVector.of(0, 0);
	}

	public Actor setBody(ISoma<?> soma) {
		this.body = soma;
		return this;
	}

	public Actor setVisage(IVisage<?> visage) {
		this.visage = visage;
		return this;
	}

	public Actor setBodyAndVisage(IMultipart<?> thing) {
		this.body = (ISoma<?>) thing;
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
	public void spawnIntoMap(GameMap map) {
		if (this.location.getDimension() != null
				&& !this.location.getDimension().equals(map.getMapTile().getDimension())) {
			this.location = this.location.withDimension(map.getMapTile().getDimension());
		}
		this.world = map;
	}

	protected float getDynamicFrictionCoeff() {
		IBlockState standingOn = world.getBlockMap().getBlock(this.location.down());
		return (this.body.getMainMaterial().getProperty(MaterialProperty.ROUGHNESS)
				+ standingOn.getBlock().getMaterial().getProperty(MaterialProperty.ROUGHNESS)) / 2;
	}

	protected float getStaticFrictionCoeff() {
		IBlockState standingOn = world.getBlockMap().getBlock(this.location.down());
		return (this.body.getMainMaterial().getProperty(MaterialProperty.UNEVENNESS)
				+ standingOn.getBlock().getMaterial().getProperty(MaterialProperty.UNEVENNESS)) / 2;
	}

	@Override
	public void tick(long ticks) {
		// TODO run ticks on actor relating to physical interactions
		if (this.body != null) {
			this.body.runTick(ticks);
			if (this.velocity.mag() != 0) {
				float fric = friction(this.getDynamicFrictionCoeff(), this.world.gravity());
				this.location = this.location.add(this.velocity.clampSubtract(fric));
			}
		}
		if (this.visage != null)
			this.visage.runTick(ticks);
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
	public IVector getLocation() {
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
		if (this.visage != null)
			this.visage.draw(g);
	}

	@Override
	public ISoma<?> getBody() {
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
		return "[|" + this.name + "|]";
	}

}
