package _sim.world;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

import _main.WorldGraphics;
import _sim.dimension.DimensionBuilder;
import _sim.dimension.IDimensionTag;
import utilities.collections.IteratorUtils;
import utilities.couplets.Pair;

/**
 * A game universe with maps
 * 
 * @author borah
 *
 */
public class GameUniverse {

	private UUID universeID;
	private Random rand;
	private long worldTicks;

	private Set<IDimensionTag> dimtags;
	private Map<IDimensionTag, Map<Pair<Integer, Integer>, MapTile>> contiguousTiles = new HashMap<>();
	private Map<IDimensionTag, Map<String, MapTile>> noncontiguousTiles = new HashMap<>();
	private Map<IDimensionTag, DimensionBuilder> dimSettings = new HashMap<>();
	// TODO allow multiple game maps(?) with some being on a lower-energy mode
	private Map<MapTile, GameMap> loadedMaps = new HashMap<>();
	private GameMap mainMap;

	/**
	 * UUID is to generate the random as well as to save it
	 * 
	 * @param id
	 */
	public GameUniverse(UUID id) {
		this.universeID = id;
		this.rand = new Random(id.getMostSignificantBits());
		dimtags = ImmutableSet.of();
	}

	/**
	 * Return main loaded map that is shown on screen
	 * 
	 * @return
	 */
	public GameMap getMainMap() {
		return mainMap;
	}

	/**
	 * Get the number of ticks transpired since creation
	 * 
	 * @return
	 */
	public long getWorldTicks() {
		return worldTicks;
	}

	/**
	 * Runs a tick on each world and draws the main game map after
	 * 
	 * @param g
	 */
	public void tickWorlds(WorldGraphics g) {
		if (mainMap != null) {
			mainMap.tick(g.getFps());
		}
		for (GameMap map : this.loadedMaps.values()) {
			if (map == mainMap) {
				continue;
			}
			map.tick(g.getFps());
		}
		if (mainMap != null) {
			mainMap.draw(g);
		}
		worldTicks++;
	}

	/**
	 * Get width of rendered world
	 * 
	 * @return
	 */
	public int getWidth() {
		if (this.mainMap != null) {
			return mainMap.getDisplayWidth();
		}
		return 0;
	}

	public int getHeight() {
		if (this.mainMap != null) {
			return mainMap.getDisplayHeight();
		}
		return 0;
	}

	/**
	 * All actions taken before loading in tiles.
	 */
	public void worldSetup() {

	}

	/**
	 * If this tile is loaded currently
	 * 
	 * @param tile
	 * @return
	 */
	public boolean isLoaded(MapTile tile) {
		return loadedMaps.containsKey(tile);
	}

	public void unloadMap(MapTile tile) {
		if (mainMap != null && tile.equals(mainMap.getMapTile())) {
			mainMap = null;
		}
		GameMap map = loadedMaps.remove(tile);
		if (map != null) {
			map.onUnload();
		}
	}

	/**
	 * mainGame = whether the loaded map will be the main game map
	 * 
	 * @param tile
	 * @param mainGame
	 * @return
	 */
	public GameMap loadMap(MapTile tile, boolean mainGame) {
		if (loadedMaps.containsKey(tile)) {
			return loadedMaps.get(tile).setAsMain(mainGame);
		}
		GameMap map = new GameMap(tile, this, this.dimSettings.get(tile.getDimension())).setAsMain(mainGame);
		if (mainGame) {
			this.mainMap = map;
		}
		this.loadedMaps.put(tile, map);
		map.onLoad();
		return map;
	}

	public GameUniverse setUpWorld(Collection<DimensionBuilder> dims) {
		ImmutableSet.Builder<IDimensionTag> dimtags = ImmutableSet.builder();
		for (DimensionBuilder dim : dims) {
			dimtags.add(dim.getDimension());
			setUpDimension(dim);
		}
		this.dimtags = dimtags.build();
		return this;
	}

	public GameUniverse setUpWorld(DimensionBuilder... dimensions) {
		ImmutableSet.Builder<IDimensionTag> dimtags = ImmutableSet.builder();
		for (DimensionBuilder dim : dimensions) {
			dimtags.add(dim.getDimension());
			setUpDimension(dim);
		}
		this.dimtags = dimtags.build();
		return this;
	}

	private void setUpDimension(DimensionBuilder dim) {
		for (MapTile tile : dim.getTiles()) {
			if (tile.isContiguous()) {
				contiguousTiles.computeIfAbsent(tile.getDimension(), (k) -> new HashMap<>())
						.put(Pair.of(tile.getRow(), tile.getCol()), tile);
			} else {
				noncontiguousTiles.computeIfAbsent(tile.getDimension(), (k) -> new HashMap<>()).put(tile.getName(),
						tile);
			}
		}
		dimSettings.put(dim.getDimension(), dim);
	}

	/**
	 * Returns the dimension tags
	 * 
	 * @return
	 */
	public Collection<IDimensionTag> getDimensions() {
		return dimtags;
	}

	/**
	 * Return the MapTile here, or null if there is no such tile
	 * 
	 * @param tag
	 * @param row
	 * @param col
	 * @return
	 */
	public MapTile getTile(IDimensionTag tag, int row, int col) {
		return contiguousTiles.getOrDefault(tag, Collections.emptyMap()).get(Pair.of(row, col));
	}

	/**
	 * Return the noncontiguous tile with the given name or null if no such tile
	 * 
	 * @param tag
	 * @param name
	 * @return
	 */
	public MapTile getTile(IDimensionTag tag, String name) {
		return noncontiguousTiles.getOrDefault(tag, Collections.emptyMap()).get(name);
	}

	/**
	 * Gets all tiles everywhere
	 * 
	 * @return
	 */
	public Iterator<MapTile> getAllTiles() {
		return Iterators.concat(IteratorUtils.ofMapOfMaps(contiguousTiles),
				IteratorUtils.ofMapOfMaps(noncontiguousTiles));
	}

	/**
	 * Return only the tiles in this dimension that are contiguous or noncontiguous
	 * 
	 * @param dim
	 * @param contiguous
	 * @return
	 */
	public Iterator<MapTile> getTiles(IDimensionTag dim, boolean contiguous) {
		if (contiguous) {
			return contiguousTiles.getOrDefault(dim, Collections.emptyMap()).values().iterator();
		} else {
			return noncontiguousTiles.getOrDefault(dim, Collections.emptyMap()).values().iterator();
		}
	}

	/**
	 * Get an iterator for all tiles in a dimension
	 * 
	 * @param dim
	 * @return
	 */
	public Iterator<MapTile> getTiles(IDimensionTag dim) {
		return Iterators.concat(contiguousTiles.getOrDefault(dim, Collections.emptyMap()).values().iterator(),
				noncontiguousTiles.getOrDefault(dim, Collections.emptyMap()).values().iterator());
	}

	/**
	 * Gets UUID of this universe
	 * 
	 * @return
	 */
	public UUID getUniverseID() {
		return universeID;
	}

	public Random rand() {
		return rand;
	}

}
