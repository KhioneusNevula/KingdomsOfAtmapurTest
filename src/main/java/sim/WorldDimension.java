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
import phenomenon.IPhenomenon;
import utilities.ImmutableCollection;

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

	public boolean isColliding(Actor a, Actor other) {
		return a.distance(other) <= (a.getRadius() + other.getRadius());
	}

	public Set<Actor> getCollisions(Actor for_, Predicate<Actor> pred) {
		return actors.values().stream().filter(pred).filter((a) -> a != for_ && isColliding(for_, a))
				.collect(Collectors.toSet());
	}

	public Set<Actor> getAt(int x, int y) {
		return actors.values().stream().filter((a) -> a.distance(x, y) <= a.getRadius()).collect(Collectors.toSet());
	}

	public int clampX(int x) {
		return Math.max(Math.min(x, this.width), 0);
	}

	public int clampY(int y) {
		return Math.max(Math.min(y, this.height), 0);
	}

}
