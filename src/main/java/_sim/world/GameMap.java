package _sim.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import _main.WorldGraphics;
import _sim.IRenderable;
import _sim.dimension.DimensionBuilder;
import _sim.vectors.IVector;
import _utilities.collections.ImmutableCollection;
import _utilities.property.IProperty;
import processing.core.PConstants;
import things.actor.IActor;
import things.blocks.BlockMap;
import thinker.individual.IBeing;

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
	private Map<UUID, IActor> actorsById = new HashMap<>();
	/** Return a map of all beings untethered to the world */
	private Map<UUID, IBeing> untetheredBeings = new HashMap<>();
	private long ticks = 0;
	private List<Runnable> nextTicksQueue = new ArrayList<>();

	GameMap(MapTile tile, GameUniverse universe, DimensionBuilder builder) {
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
		return ImmutableCollection.from(actorsById.values());
	}

	/** Returns an actor by id */
	public IActor getActorByUUID(IActor act) {
		synchronized (actorsById) {
			return actorsById.get(act);
		}
	}

	/** Returns an untethered being by its id */
	public IBeing getUntetheredByUUID(IBeing being) {
		synchronized (untetheredBeings) {
			return untetheredBeings.get(being);
		}
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
		synchronized (nextTicksQueue) {
			this.nextTicksQueue.add(action);
		}
	}

	/**
	 * TODO Run a tick on this game map
	 */
	public void tick(float ticksPerSecond) {
		synchronized (nextTicksQueue) {
			for (Runnable run : nextTicksQueue) {
				run.run();
			}
			nextTicksQueue.clear();
		}
		synchronized (untetheredBeings) {
			Iterator<IBeing> unteth = untetheredBeings.values().iterator();
			if (unteth.hasNext()) {
				for (IBeing being = unteth.next(); unteth.hasNext(); being = unteth.next()) {
					being.runUntetheredTick(this, ticks);
					if (being.readyForDeletion(this, ticks)) {
						unteth.remove();
					}
				}
			}
		}
		this.actorsTicking = true;
		synchronized (actorsById) {
			for (IActor actor : actorsById.values()) {
				actor.tick(ticks, ticksPerSecond);
			}
		}
		this.actorsTicking = false;

	}

	@Override
	public int visibilityPlanes() {
		return 0;
	}

	/**
	 * Spawn actor into world. Please queue this into the next tick to avoid race
	 * conditions!
	 * 
	 * @param actor
	 */
	public void spawnIntoWorld(IActor actor) {
		synchronized (actorsById) {
			this.actorsById.put(actor.getUUID(), actor);
			actor.onSpawnIntoMap(this);
		}

	}

	/**
	 * Spawns an untethered being into this world. Please queue this to avoid race
	 * conditions!
	 */
	public void addUntetheredBeingToWorld(IBeing being) {
		synchronized (untetheredBeings) {
			this.untetheredBeings.put(being.getUUID(), being);
			being.onUntethering(this, this.ticks);
		}
	}

	/**
	 * Remove an untethered being from the world. Please queue this into the next
	 * tick to avoid race conditions!
	 */
	public void removeFromWorld(IBeing being) {
		synchronized (untetheredBeings) {
			being.onRemoveFromMap(this, ticks);
			this.untetheredBeings.remove(being.getUUID());
		}

	}

	/**
	 * Remove actor from world. Please queue this into the next tick to avoid race
	 * conditions!
	 */
	public void removeFromWorld(IActor actor) {
		synchronized (actorsById) {
			actor.onRemoveFromMap(this);
			this.actorsById.remove(actor.getUUID());
		}

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

	}

	private void drawBackground(WorldGraphics g) {
		g.pushStyle();
		g.colorMode(PConstants.HSB);
		for (int y = 0; y < getDisplayHeight(); y += TILE_SIZE) {
			for (int x = 0; x < getDisplayWidth(); x += TILE_SIZE) {
				float ticker = universe.getWorldTicks() / 10.0f;
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
	 * What to do when loaded <br>
	 * TODO load from save(?)
	 */
	public void onLoad() {

	}

	/**
	 * What to do when unloaded <br>
	 * TODO save when unloaded(?)
	 */
	public void onUnload() {

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
