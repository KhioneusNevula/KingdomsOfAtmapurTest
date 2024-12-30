package sim.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import _main.WorldGraphics;
import processing.core.PConstants;
import sim.IRenderable;
import sim.MapLayer;
import things.blocks.BlockMap;
import things.blocks.basic.BasicBlock;
import things.blocks.fluid.BasicFluidBlock;
import things.interfaces.IActor;

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
	private Map<IWorldProperty<?>, Object> worldProperties = new HashMap<>();
	private BlockMap blockMap;
	private Collection<IActor> actors = new HashSet<>();
	private long ticks = 0;

	GameMap(MapTile tile, GameUniverse universe) {
		this.mapTile = tile;
		this.universe = universe;
		this.blockMap = new BlockMap(mapWidth, mapHeight,
				ImmutableMap.of(MapLayer.LOWEST, BasicBlock.STONE.getDefaultState(), MapLayer.FLOOR,
						BasicBlock.STONE.getDefaultState(), MapLayer.WALL, BasicFluidBlock.AIR.getDefaultState(),
						MapLayer.ROOF, BasicFluidBlock.AIR.getDefaultState()));
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
	 * TODO Run a tick on this game map
	 */
	public void tick() {
		for (IActor actor : actors) {
			actor.tick(ticks);
		}
	}

	@Override
	public int visibilityPlanes() {
		return 0;
	}

	@Override
	public void draw(WorldGraphics g) {
		this.drawBackground(g);
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

	public <E> E getProperty(IWorldProperty<E> prop) {
		return (E) this.worldProperties.getOrDefault(prop, prop.defaultValue());
	}

	public <E> void setProperty(IWorldProperty<E> prop, E val) {
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

}
