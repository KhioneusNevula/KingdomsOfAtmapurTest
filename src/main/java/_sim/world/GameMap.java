package _sim.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import _graphics.IRenderable;
import _graphics.WorldGraphics;
import _sim.GameUniverse;
import _sim.dimension.DimensionBuilder;
import _sim.vectors.IVector;
import _utilities.collections.ImmutableCollection;
import _utilities.property.IProperty;
import metaphysics.being.IBeing;
import metaphysics.being.IFigure;
import metaphysics.spirit.ISpirit;
import party.kind.IKindCollective;
import party.kind.spawning.IKindSpawningContext;
import processing.core.PConstants;
import things.actor.Actor;
import things.actor.IActor;
import things.blocks.BlockMap;
import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;
import things.interfaces.UniqueType;
import things.phenomena.IPhenomenon;

/**
 * A world map for a game
 * 
 * @author borah
 *
 */
public class GameMap implements IRenderable {

	private static final int WIDTH = 40;
	private static final int HEIGHT = 25;
	private static final int TILE_SIZE = 20;
	private MapTile mapTile;
	/** whether this is the main rendered map **/
	private boolean isMain;
	private GameUniverse universe;
	private int mapWidth = WIDTH;
	private int mapHeight = HEIGHT;
	private int blockRenderSize = TILE_SIZE;
	private int displayWidth = mapWidth * blockRenderSize;
	private int displayHeight = mapHeight * blockRenderSize;
	private Map<IProperty<?>, Object> worldProperties = new HashMap<>();
	private BlockMap blockMap;
	private Map<UUID, IActor> actorsById = Collections.synchronizedMap(new HashMap<>());
	private Map<UUID, IPhenomenon> phenomenaById = Collections.synchronizedMap(new HashMap<>());
	/** Return a map of all beings untethered to the world */
	private Map<UUID, IBeing> untetheredBeings = Collections.synchronizedMap(new HashMap<>());
	private long ticks = 0;
	private List<Runnable> nextTicksQueue = Collections.synchronizedList(new ArrayList<>());

	public GameMap(MapTile tile, GameUniverse universe, DimensionBuilder builder) {
		this.mapTile = tile;
		this.universe = universe;
		this.blockMap = new BlockMap(mapWidth, mapHeight, builder.getProperty(WorldProperty.LAYER_BLOCKS));
		// TODO make a better way to generate
	}

	/**
	 * Curent amount of time that passed
	 * 
	 * @return
	 */
	public long getTicks() {
		return ticks;
	}

	/**
	 * Returns all actors in this map.
	 */
	public Collection<IActor> getActors() {
		return actorsById.values();
	}

	/** Returns an actor by id */
	public IActor getActorByUUID(UUID act) {
		return actorsById.get(act);
	}

	/** Returns an untethered being by its id */
	public IBeing getUntetheredByUUID(UUID being) {
		return untetheredBeings.get(being);
	}

	/** Return all untethered beings on this map */
	public Collection<IBeing> getUntetheredBeings() {
		return ImmutableCollection.from(untetheredBeings.values());
	}

	/**
	 * Queue a process to run the next tick. Use this whenever you need to
	 * synchronize stuff.
	 * 
	 * @param action
	 */
	public void queueAction(Runnable action) {
		this.nextTicksQueue.add(action);
	}

	/**
	 * TODO Run a tick on this game map
	 */
	public void tick(float ticksPerSecond) {
		List<Runnable> nextTicksRunnables = null;
		synchronized (nextTicksQueue) {
			nextTicksRunnables = new ArrayList<>(nextTicksQueue);
			nextTicksQueue.clear();
		}
		for (Runnable run : nextTicksRunnables) {
			run.run();
		}
		synchronized (untetheredBeings) {
			Set<IBeing> mustRemove = new HashSet<>();
			for (IBeing being : untetheredBeings.values()) {
				being.runUntetheredTick(this, ticks);
				if (being.readyForDeletion(this, ticks)) {
					mustRemove.add(being);
				}
			}
			for (IBeing being : mustRemove) {
				being.onRemoveFromMap(this, ticks);
				being.setRemoved(true);
				untetheredBeings.remove(being.getUUID(), being);
			}

		}
		this.actorsTicking = true;
		synchronized (actorsById) {
			Set<IActor> mustRemove = new HashSet<>();
			for (IActor actor : actorsById.values()) {
				actor.tick(ticks, ticksPerSecond);
				if (actor.needsToBeRemoved())
					mustRemove.add(actor);
			}
			for (IActor remover : mustRemove) {
				remover.onRemoveFromMap(this);
				actorsById.remove(remover.getUUID(), remover);
			}
		}
		this.actorsTicking = false;

	}

	@Override
	public int visibilityPlanes() {
		return 0;
	}

	/**
	 * Spawn actor into world (FOR THE FIRST TIME). Please queue this into the next
	 * tick to avoid race conditions!
	 * 
	 * @param actor
	 */
	public void spawnIntoWorld(IActor actor) {
		this.actorsById.put(actor.getUUID(), actor);
		actor.onSpawnIntoMap(this);

	}

	/**
	 * load actor into world (NOT FOR THE FIRST TIME). Please queue this into the
	 * next tick to avoid race conditions!
	 * 
	 * @param actor
	 */
	public void load(IActor actor) {
		this.actorsById.put(actor.getUUID(), actor);
		actor.onLoad(this);

	}

	/**
	 * Spawns an untethered being into this world. Please queue this to avoid race
	 * conditions!
	 */
	public void addUntetheredBeingToWorld(IBeing being) {
		this.untetheredBeings.put(being.getUUID(), being);
		being.onUntethering(this, this.ticks);

	}

	/**
	 * Remove a being (untethered) from the world. Please queue this into the next
	 * tick to avoid race conditions!
	 */
	public void removeFromWorld(IBeing being) {
		being.onRemoveFromMap(this, ticks);
		being.setRemoved(true);
		this.untetheredBeings.remove(being.getUUID());
		if (this.universe.getPersistentUniqueFromProfile(being.getProfile()) != null) {
			this.universe.<IFigure>getPersistentUniqueFromProfile(being.getProfile()).setActive(false);
		} else if (being.isPersistent()) {
			IFigure fig = being.createPersistentFigure(Optional.empty());
			fig.setActive(false);
			this.universe.addPersistentUnique(fig, UniqueType.FIGURE);
		}

	}

	/**
	 * Remove actor from world. Please queue this into the next tick to avoid race
	 * conditions!
	 */
	public void removeFromWorld(IActor actor) {
		actor.onRemoveFromMap(this);
		this.actorsById.remove(actor.getUUID());

	}

	/**
	 * Unload actor from world. Different than 'remove', since any associated
	 * {@link IBeing}s will be saved if persistent. Please queue this into the next
	 * tick to avoid race conditions!
	 */
	public void unload(IActor actor) {
		if (actor.getBody() != null) {
			actor.getBody().getAllTetheredSpirits().stream()
					.forEach((spir) -> this.universe.removePersistentUnique(spir));
			actor.getBody().getAllTetheredSpirits().stream().filter(ISpirit::isPersistent)
					.forEach((spir) -> this.universe.addPersistentUnique(
							spir.createPersistentFigure(Optional.of(actor.getBody().getPartForSpirit(spir))),
							UniqueType.FIGURE));
		}
		// TODO check persistency of actor
		actor.onUnload(this);
		this.actorsById.remove(actor.getUUID());

	}

	protected boolean actorsDrawing = false;
	protected boolean actorsTicking = false;

	@Override
	public void draw(WorldGraphics g) {
		this.drawBackground(g);
		synchronized (actorsById) {
			actorsDrawing = true;
			for (IActor actor : actorsById.values()) {

				g.push();
				g.translate((float) (actor.getPosition().getUnadjustedX() * blockRenderSize),
						(float) (actor.getPosition().getUnadjustedY() * blockRenderSize));
				actor.draw(g);
				g.pop();
			}
			actorsDrawing = false;
		}
		g.textAlign(PConstants.LEFT, PConstants.TOP);
		g.textSize(TILE_SIZE);
		g.fill(0);
		g.text("Number of ghosts: " + this.untetheredBeings.size(), 0, 0);

	}

	private void drawBackground(WorldGraphics g) {
		g.pushStyle();
		g.colorMode(PConstants.HSB);
		for (int y = 0; y < getDisplayHeight(); y += TILE_SIZE) {
			for (int x = 0; x < getDisplayWidth(); x += TILE_SIZE) {
				float ticker = universe.getUniverseTicks() / 10.0f;
				float cos = (float) Math.cos(Math.toRadians(ticker % 360));
				float sin = (float) Math.sin(Math.toRadians(ticker % 360));
				float diagon = (float) Math.sqrt((x + 1) * (x + 1) + (y + 1) * (y + 1));
				float bigdiagon = (float) Math
						.sqrt(Math.pow(getDisplayWidth() + 1, 2) + Math.pow(getDisplayHeight() + 1, 2));
				float mx = (x + 1) / cos;
				float my = (y + 1) / sin;
				g.fill(g.color((360 * (diagon / bigdiagon) * cos * sin),
						(100 * ((mx) / (getDisplayWidth() + 1.0f) / cos) + 155),
						(100 * ((my) / (getDisplayHeight() + 1.0f) / sin) + 155)));
				// g.strokeWeight(0);
				// g.line(x, y, x + TILE_SIZE, y);
				// g.line(x + TILE_SIZE, y, x + TILE_SIZE, y + TILE_SIZE);
				// g.line(x, y + TILE_SIZE, x + TILE_SIZE, y + TILE_SIZE);
				// g.line(x, y, x, y + TILE_SIZE);
				g.rectMode(PConstants.CORNERS);
				g.rect(x, y, x + TILE_SIZE, y + TILE_SIZE);
			}
		}
		g.popStyle();
	}

	public GameUniverse getUniverse() {
		return universe;
	}

	public <E> E getProperty(IProperty<E> prop) {
		return (E) this.worldProperties.getOrDefault(prop, prop.defaultValue());
	}

	public <E> void setProperty(IProperty<E> prop, E val) {
		this.worldProperties.put(prop, val);
	}

	/**
	 * Get the gravity of this world (more formally, get the value for
	 * {@link WorldProperty#GRAVITY}
	 * 
	 * @return
	 */
	public float gravity() {
		return getProperty(WorldProperty.GRAVITY);
	}

	/**
	 * Return a random float
	 * 
	 * @return
	 */
	public float random() {
		return universe.random();
	}

	/** Called when the map is loded for the first tim */
	public void onFirstLoad(IMapData dat) {
		for (IKind kind : this.universe.getEntityKinds()) {
			IKindCollective col = this.universe.getKindCollective(kind);
			if (col != null) {
				IKindSpawningContext con = col.getSpawnContext();
				if (con != null) {
					float nums = con.getSpawnNumberOnMapGeneration(mapTile, universe, col);
					if (nums > 0) {
						int count = (int) nums;
						float prob = nums - count;
						int extra = (int) (universe.random() * prob * count);
						for (int i = 0; i < count + extra; i++) {
							IKindSettings sets = con.createKindSettings(this, 0, col);
							if (kind.isDisembodied()) {
								IBeing bing = kind.generateBeing(sets);
								this.untetheredBeings.put(bing.getUUID(), bing);
							} else {
								UUID id1 = UUID.randomUUID();
								Actor actor = new Actor(id1, kind.name() + i);
								actor.setKind(kind);
								actor.makeBody(sets, true, this);
								IVector pos = con.findSpawnPosition(actor, this, 0, col);
								this.queueAction(() -> {
									this.spawnIntoWorld(actor);
									actor.setPosition(pos);
									con.doPostSpawnAdjustments(actor, pos, this, 0, col);
								});
							}

						}
					}
				}
			}
		}
	}

	/**
	 * What to do when loaded. <br>
	 * TODO load from save(?)
	 */
	public void loadFrom(IMapData data) {

	}

	/**
	 * What to do when unloaded <br>
	 * TODO save when unloaded(?)
	 */
	public void unloadTo(IMapData data) {
		for (IActor aca : this.actorsById.values()) {
			data.setNumberOfObjects(aca.getKind(), data.getNumberOfObjects(aca.getKind()) + 1);
		}
	}

	/**
	 * Set this to be the main rendered map
	 * 
	 * @param isMain
	 * @return
	 */
	public GameMap setAsMain(boolean isMain) {
		this.isMain = isMain;
		return this;
	}

	/**
	 * Is this the main rendered map?
	 * 
	 * @return
	 */
	public boolean isMain() {
		return isMain;
	}

	public MapTile getMapTile() {
		return mapTile;
	}

	@Override
	public boolean canRender() {
		return isMain;
	}

	/**
	 * The height of the display
	 * 
	 * @return
	 */
	public int getDisplayHeight() {
		return displayHeight;
	}

	/**
	 * The width of the display screen
	 * 
	 * @return
	 */
	public int getDisplayWidth() {
		return displayWidth;
	}

	/**
	 * The width of the block map
	 * 
	 * @return
	 */
	public int getMapWidth() {
		return mapWidth;
	}

	/**
	 * The height of the block map
	 * 
	 * @return
	 */
	public int getMapHeight() {
		return mapHeight;
	}

	/**
	 * The representation of blocks in this map
	 * 
	 * @return
	 */
	public BlockMap getBlockMap() {
		return blockMap;
	}

	/**
	 * How big each block is drawn
	 * 
	 * @return
	 */
	public int getBlockRenderSize() {
		return blockRenderSize;
	}

	public boolean outOfBounds(IVector location) {
		return location.getX() < 0 || location.getY() < 0 || location.getX() >= this.mapWidth
				|| location.getY() >= this.mapHeight;
	}

}
