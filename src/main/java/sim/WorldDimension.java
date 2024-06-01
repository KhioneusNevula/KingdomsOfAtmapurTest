package sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import actor.Actor;
import actor.construction.IPhysicalActorObject.HitboxType;
import phenomenon.IPhenomenon;
import sim.physicality.IInteractability.CollisionType;
import utilities.ImmutableCollection;
import utilities.MathHelp;

public class WorldDimension {

	protected Map<UUID, Actor> actors = new HashMap<>();
	private Map<UUID, IPhenomenon> phenomena = new HashMap<>();
	private ImmutableCollection<Actor> actorCollection = new ImmutableCollection<>(actors.values());
	private ImmutableCollection<IPhenomenon> phenCollection = new ImmutableCollection<>(phenomena.values());
	private UUID id = UUID.randomUUID();
	private long seed;
	private Random rand = new Random();
	private String name;
	private final int width, height;

	public WorldDimension(String name, int width, int height) {
		this.width = width;
		this.height = height;
		this.name = name;
	}

	public void setSeed(long seed) {
		this.seed = seed;
		this.rand = new Random(seed);
	}

	public long getSeed() {
		return seed;
	}

	public Random getRand() {
		return rand;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public UUID getId() {
		return id;
	}

	/**
	 * Coefficient of friction between given actor and ground. For now, just 0.5
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public float getFrictionMu(Actor a, int x, int y) {
		return 0.5f;
	}

	/**
	 * Static friction. For now, 0.7
	 * 
	 * @param a
	 * @param x
	 * @param y
	 * @return
	 */
	public float getStaticFrictionMu(Actor a, int x, int y) {
		return 0.7f;
	}

	/**
	 * Coefficient of drag (density * CD) between actor and air. For now, same as
	 * ground friction
	 * 
	 * @param a
	 * @param x
	 * @param y
	 * @return
	 */
	public float getDragCoefficient(Actor a, int x, int y) {
		return 0.5f;
	}

	/**
	 * Acceleration of gravity (m/s^2) .
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public float getG(int x, int y) {
		return 10f;
	}

	public synchronized <T extends Actor> T spawnActor(T a) {
		this.actors.put(a.getUUID(), a);
		System.out.println("Spawned " + a + " in " + name);
		return a;
	}

	public synchronized <T extends IPhenomenon> T createPhenomenon(T a) {
		this.phenomena.put(a.getUUID(), a);
		return a;
	}

	public Collection<Actor> getActors() {
		return actorCollection;
	}

	public Collection<IPhenomenon> getPhenomena() {
		return phenCollection;
	}

	/**
	 * TODO make world load from save
	 */
	public void load() {
		System.out.println("loading " + this.name);
	}

	public synchronized void worldTick(long tick) {
		// synchronized (actors) {
		Iterator<Actor> iter = actors.values().iterator();
		while (iter.hasNext()) {
			Actor e = iter.next();
			if (e.isRemoved()) {

				iter.remove();
			}
			e.movementTick(tick);
			e.tick(tick);
			e.senseTick(tick);
			e.thinkTick(tick);

		}

		synchronized (phenomena) {
			Iterator<IPhenomenon> iter2 = phenomena.values().iterator();
			while (iter2.hasNext()) {
				IPhenomenon p = iter2.next();
				p.tick();
				if (p.isComplete()) {
					iter2.remove();
				}
			}
		}
		for (Actor e : actors.values()) {
			e.actionTick(tick);
			e.finalTick(tick);
		}
		// }
	}

	public CollisionType areColliding(Actor a, Actor other) {
		CollisionType intersectType = MathHelp.findFirstPrimeFactor(a.physicality(), other.physicality()) != 1
				? CollisionType.COLLISION
				: CollisionType.CROSS_PLANE_INTERSECTION;
		if (a.getPhysical().getHitboxType() == HitboxType.CIRCLE) {
			if (other.getPhysical().getHitboxType() == HitboxType.CIRCLE) { // circle x circle
				if (a.getRadius() + other.getRadius() >= a.distance(other)) {
					return intersectType;
				}
			} else { // circle x rectangle
				if (MathHelp.circleRectIntersects(a.getX(), a.getY(), a.getPhysical().getHitboxRadius(), other.getX(),
						other.getY(), other.getPhysical().getHitboxWidth(), other.getPhysical().getHitboxHeight())) {
					return intersectType;
				}
			}
		} else {
			if (other.getPhysical().getHitboxType() == HitboxType.RECTANGLE) { // rectangle x rectangle
				if (MathHelp.rectsIntersect(a.getX(), a.getY(), a.getPhysical().getHitboxWidth(),
						a.getPhysical().getHitboxHeight(), other.getX(), other.getY(),
						other.getPhysical().getHitboxWidth(), other.getPhysical().getHitboxHeight())) {
					return intersectType;
				}
			} else { // rectangle x circle
				if (MathHelp.circleRectIntersects(other.getX(), other.getY(), other.getPhysical().getHitboxRadius(),
						a.getX(), a.getY(), a.getPhysical().getHitboxWidth(), a.getPhysical().getHitboxHeight())) {
					return intersectType;
				}
			}
		}
		return CollisionType.NO_INTERSECTION;
	}

	/**
	 * Get all actors which are colliding with the given one
	 * 
	 * @param for_
	 * @param pred
	 * @param allowCrossPlane whether to allow cross-plane collisions
	 * @return
	 */
	public Set<Actor> getCollisions(Actor for_, Predicate<Actor> pred, CollisionType type) {
		return actors.values().stream().filter(pred).filter((a) -> a != for_ && areColliding(for_, a) == type)
				.collect(Collectors.toSet());
	}

	public Set<Actor> getAt(int x, int y) {
		return actors.values().stream().filter((a) -> a.pointInHitbox(x, y)).collect(Collectors.toSet());
	}

	public int clampX(int x) {
		return Math.max(Math.min(x, this.width), 0);
	}

	public int clampY(int y) {
		return Math.max(Math.min(y, this.height), 0);
	}

}
