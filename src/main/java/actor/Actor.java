
package actor;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import actor.construction.IBlueprintTemplate;
import actor.construction.IPhysicalActorObject;
import actor.construction.MultipartActor;
import biology.systems.ESystem;
import biology.systems.ISystemHolder;
import biology.systems.SystemType;
import biology.systems.types.LifeSystem;
import main.WorldGraphics;
import processing.core.PApplet;
import sim.WorldDimension;
import sim.interfaces.ILocatable;
import sim.interfaces.IPhysicalExistence;
import sim.interfaces.IRenderable;
import sim.physicality.ExistencePlane;
import utilities.Location;

public abstract class Actor implements IUniqueExistence, IRenderable, IPhysicalExistence, ISystemHolder {

	private final static int STEP = 10;
	private final static int REACH = 15;

	private Map<SystemType<?>, ESystem> systems = new TreeMap<>();

	private int x;
	private int y;

	protected Random rand = new Random();
	private boolean removed;

	/**
	 * Probably gonna become unnecessary
	 */
	private String name;

	private WorldDimension world;

	private int radius;

	private Integer optionalColor = null;
	private Location location;

	private UUID uuid = UUID.randomUUID();

	protected IBlueprintTemplate species;

	/**
	 * Amount of milliseconds it takes properties to fade
	 */
	// private long propertyDecayTime = 10000;// change this to 86400000L;

	public Actor(WorldDimension world, String name, IBlueprintTemplate species, int startX, int startY, int radius) {
		this.world = world;
		this.name = name;
		this.radius = radius;
		this.x = startX;
		this.y = startY;
		location = new Location(startX, startY);
		this.species = species;
	}

	protected void addSystems(ESystem... sys) {
		for (ESystem s : sys) {
			this.systems.put(s.getType(), s);

		}

	}

	@Override
	public boolean canRender() {
		return true;
	}

	public Collection<ESystem> getSystems() {
		return systems.values();
	}

	public boolean hasSystem(String name) {
		for (SystemType<?> t : this.systems.keySet())
			if (t.getId().equals(name))
				return true;
		return false;
	}

	public Actor setOptionalColor(int optionalColor) {
		this.optionalColor = optionalColor;
		return this;
	}

	public int getOptionalColor() {
		return optionalColor;
	}

	@Override
	public UUID getUUID() {
		return this.uuid;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getSimpleName() {
		return name;
	}

	public WorldDimension getWorld() {
		return world;
	}

	public IBlueprintTemplate getSpecies() {
		return species;
	}

	@Override
	public int physicality() {
		return this.getPhysical().physicalityMode();
	}

	/**
	 * Returns the physical representation of the actor
	 * 
	 * @return
	 */
	public abstract IPhysicalActorObject getPhysical();

	public void movementTick(long tick) {
	}

	public void senseTick(long tick) {
		// TODO sensetick may be useless with systemholder

	}

	public void thinkTick(long tick) {
		// TODO thinktick
	}

	public void actionTick(long tick) {
		// TODO actiontick
	}

	public void tick(long tick) {

		if (this.getPhysical().completelyDestroyed()) {
			this.remove();
		}

		for (ESystem sys : this.getSystems()) {
			if (sys instanceof LifeSystem && ((LifeSystem) sys).isDead()) {
				this.remove(); // TODO make some corpse feature idk idfk
			}
			if (sys.canUpdate()) {
				sys._update(tick);
			}
		}
	}

	public void finalTick(long tick) {
		if (tick % 5 == 0) {
			// TODO post sensory
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void move(int xplus, int yplus) {
		IPhysicalExistence.super.move(xplus, yplus);
	}

	public void moveLeft() {
		move(-STEP, 0);
	}

	public void moveRight() {
		move(STEP, 0);
	}

	public void moveUp() {
		move(0, STEP);
	}

	public void moveDown() {
		move(0, -STEP);
	}

	public boolean reachable(ILocatable other) {
		return this.distance(other) <= REACH;
	}

	public int getReach() {
		return REACH;
	}

	public boolean at(Actor other) {
		return other.x == this.x && other.y == this.y;
	}

	public String getStatus() {
		return this.name + " is at " + this.x + ", " + this.y;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public final void draw(WorldGraphics g) {
		g.push();
		render(g);
		g.pop();
	}

	protected void render(WorldGraphics g) {
		g.ellipseMode(PApplet.RADIUS);
		if (optionalColor == null) {
			g.fill(255, 255, 0, 100);
			g.stroke(g.color(100, 100, 0));
		} else {
			g.fill(optionalColor, 100);
			g.stroke(optionalColor);
		}
		g.strokeWeight(1.4f);
		g.circle(x, y, radius);
		g.textAlign(PApplet.CENTER, PApplet.CENTER);
		boolean danger = false; // TODO make danger more clear
		boolean dead = false; // TODO make death more clear

		if (this.hasSystem(SystemType.LIFE)) {
			LifeSystem ensys = this.getSystem(SystemType.LIFE);
			if (ensys.isSevere())
				danger = true;
			if (ensys.isDead())
				dead = true;
		}
		if (dead) {
			g.fill(g.color(255, 255, 0));
		} else if (danger) {
			g.fill(g.color(255, 0, 0));
		} else {
			g.fill(0);

		}
		g.text(this.name, x, y);

	}

	public Location getLocation() {
		if (this.location.getX() != this.x || this.location.getY() != this.y) {
			this.location = new Location(x, y);
		}
		return location;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":\"" + name + "\":(" + x + "," + y + ")";
	}

	public String report() {
		return "actor:" + this.name + "\nbody:" + this.getPhysical().report() + "\nphysical planes:"
				+ ExistencePlane.decomposeCombinedValue(this.physicality(), true) + "\nvisibility planes:"
				+ ExistencePlane.decomposeCombinedValue(this.getVisage().visibilityMode(), true);
	}

	public Random rand() {
		return rand;
	}

	public boolean hasSystem(SystemType<?> system) {
		return this.systems.containsKey(system);
	}

	public <T extends ESystem> T getSystem(SystemType<T> system) {
		return (T) this.systems.get(system);
	}

	public Collection<SystemType<?>> getSystemTokens() {
		return this.systems.keySet();
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Actor a && a.uuid.equals(this.uuid);
	}

	public boolean isMultipart() {
		return this instanceof MultipartActor;
	}

	public MultipartActor getAsMultipart() {
		return (MultipartActor) this;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void remove() {
		this.removed = true;
	}

	public double getStep() {
		return STEP;
	}

}
