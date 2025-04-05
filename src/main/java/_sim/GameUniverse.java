package _sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Table;

import _graphics.WorldGraphics;
import _sim.dimension.DimensionBuilder;
import _sim.dimension.IDimensionTag;
import _sim.world.GameMap;
import _sim.world.IMapData;
import _sim.world.MapData;
import _sim.world.MapTile;
import _utilities.couplets.Pair;
import metaphysics.being.IFigure;
import party.IParty;
import party.PartyRelationGraph;
import party.collective.ICollective;
import party.collective.group.IGroup;
import party.kind.IKindCollective;
import things.form.IForm;
import things.form.kinds.IKind;
import things.interfaces.IUnique;
import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;
import thinker.constructs.IPlace;
import thinker.knowledge.base.noosphere.INoosphereKnowledgeBase;
import thinker.knowledge.base.noosphere.NoosphereKnowledgeBase;

/**
 * A game universe with maps
 * 
 * @author borah
 *
 */
public class GameUniverse {

	private UUID universeID;
	private Random rand;
	private long universeTicks;

	private Set<IDimensionTag> dimtags;
	private Table<IDimensionTag, Pair<Integer, Integer>, MapTile> contiguousTiles = HashBasedTable.create();
	private Table<IDimensionTag, String, MapTile> noncontiguousTiles = HashBasedTable.create();
	private Map<IDimensionTag, DimensionBuilder> dimSettings = new HashMap<>();
	private Map<UUID, IFigure> savedFigures = new HashMap<>();
	private Map<UUID, IForm<?>> savedForms = new HashMap<>();
	private Map<UUID, IPlace> savedPlaces = new HashMap<>();
	private Map<UUID, ICollective> savedCollectives = new HashMap<>();
	private Map<UUID, IUnique> miscSavedUniques = new HashMap<>();
	// TODO allow multiple game maps(?) with some being on a lower-energy mode
	private Map<MapTile, GameMap> loadedMaps = new HashMap<>();
	private GameMap mainMap;
	private PartyRelationGraph partyRelations;
	private INoosphereKnowledgeBase noosphere;
	private Map<String, IKind> entityKinds;
	private Map<IKind, IKindCollective> kindGroups;
	private Map<MapTile, IMapData> mapData = new HashMap<>();
	private String saveFolder;

	/**
	 * UUID is to generate the random as well as to save it
	 * 
	 * @param id
	 */
	public GameUniverse(UUID id, String saveFolder) {
		this.universeID = id;
		this.rand = new Random(id.getMostSignificantBits());
		dimtags = ImmutableSet.of();
		this.partyRelations = new PartyRelationGraph();
		this.noosphere = new NoosphereKnowledgeBase();
		this.entityKinds = new HashMap<>();
		this.kindGroups = new HashMap<>();
		this.saveFolder = saveFolder;
	}

	public INoosphereKnowledgeBase getNoosphere() {
		return noosphere;
	}

	public void setNoosphere(INoosphereKnowledgeBase noosphere) {
		this.noosphere = noosphere;
	}

	/** Path of save folder */
	public String getSaveFolder() {
		return saveFolder;
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
	 * Get the graph of relations of the different parties in the world
	 * 
	 * @return
	 */
	public PartyRelationGraph getPartyRelations() {
		return partyRelations;
	}

	/** Returns all collectives that are stored persistently */
	public Collection<ICollective> getPersistentCollectives() {
		return savedCollectives.values();
	}

	/** Returns all figures that are stored persistently. */
	public Collection<IFigure> getPersistentFigures() {
		return savedFigures.values();
	}

	/** Returns all forms that are stored persistenyly */
	public Collection<IForm<?>> getPersistentForms() {
		return savedForms.values();
	}

	/** Returns all places that are tracked persistently */
	public Collection<IPlace> getPersistentPlaces() {
		return savedPlaces.values();
	}

	/** Removes a persistent unique entity from this universe. */
	public void removePersistentUnique(IUnique unique) {
		savedFigures.remove(unique.getUUID(), unique);
		savedForms.remove(unique.getUUID(), unique);
		savedCollectives.remove(unique.getUUID(), unique);
		savedPlaces.remove(unique.getUUID(), unique);
		miscSavedUniques.remove(unique.getUUID(), unique);
	}

	/** Adds a persistent unique entity to this universe as the given type. */
	public void addPersistentUnique(IUnique unique, UniqueType type) {
		switch (type) {
		case FIGURE:
			savedFigures.put(unique.getUUID(), (IFigure) unique);
			return;
		case FORM:
			savedForms.put(unique.getUUID(), (IForm) unique);
			return;
		case COLLECTIVE:
			savedCollectives.put(unique.getUUID(), (ICollective) unique);
			return;
		case PLACE:
			savedPlaces.put(unique.getUUID(), (IPlace) unique);
			return;

		default:
			miscSavedUniques.put(unique.getUUID(), unique);
			return;
		}
	}

	/** Returns the Unique referent of a given profile. */
	public <T extends IUnique> T getPersistentUniqueFromProfile(IProfile profile) {
		switch (profile.getDescriptiveType()) {
		case ANY:
			for (Map<UUID, ? extends IUnique> uMap : new Map[] { savedFigures, savedForms, savedPlaces,
					savedCollectives, miscSavedUniques }) {
				if (uMap.containsKey(profile.getUUID())) {
					return (T) uMap.get(profile.getUUID());
				}
			}
			return null;
		case FIGURE:
			return (T) savedFigures.get(profile.getUUID());
		case FORM:
			return (T) savedForms.get(profile.getUUID());
		case COLLECTIVE:
			return (T) savedCollectives.get(profile.getUUID());
		case PLACE:
			return (T) savedPlaces.get(profile.getUUID());

		default:
			return (T) miscSavedUniques.get(profile.getUUID());
		}
	}

	public Collection<IKind> getEntityKinds() {
		return entityKinds.values();
	}

	public IKind getKindByName(String nam) {
		return entityKinds.get(nam);
	}

	public IKindCollective getKindCollective(IKind kind) {
		return kindGroups.get(kind);
	}

	public void registerKind(IKind kind) {
		if (this.entityKinds.containsKey(kind.name())) {
			throw new IllegalArgumentException(
					kind + " has same name as existing kind: " + entityKinds.get(kind.name()));
		}
		this.entityKinds.put(kind.name(), kind);
		IKindCollective col = kind.generateCollective(this);
		if (col != null) {
			this.kindGroups.put(kind, col);
			this.partyRelations.add(col);
		}
	}

	/**
	 * Get the number of ticks transpired since creation
	 * 
	 * @return
	 */
	public long getUniverseTicks() {
		return universeTicks;
	}

	/**
	 * Runs a tick on each world and draws the main game map after
	 * 
	 * @param g
	 */
	public void tickWorlds(WorldGraphics g) {
		for (IParty party : this.partyRelations) {
			if (party instanceof IGroup group) {
				group.runTick(this, universeTicks);
			}
		}
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
		universeTicks++;
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
			map.unloadTo(this.mapData.computeIfAbsent(tile, k -> new MapData(tile, this)));
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
		if (!this.mapData.containsKey(tile)) {
			map.onFirstLoad(this.mapData.computeIfAbsent(tile, (t) -> new MapData(t, this)));
		} else {
			map.loadFrom(this.mapData.get(tile));
		}
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
				contiguousTiles.put(tile.getDimension(), Pair.of(tile.getRow(), tile.getCol()), tile);
			} else {
				noncontiguousTiles.put(tile.getDimension(), tile.getName(), tile);
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
		return contiguousTiles.get(tag, Pair.of(row, col));
	}

	/**
	 * Return the noncontiguous tile with the given name or null if no such tile
	 * 
	 * @param tag
	 * @param name
	 * @return
	 */
	public MapTile getTile(IDimensionTag tag, String name) {
		return noncontiguousTiles.get(tag, name);
	}

	/**
	 * Gets all tiles everywhere
	 * 
	 * @return
	 */
	public Iterator<MapTile> getAllTiles() {
		return Iterators.concat(contiguousTiles.values().iterator(), noncontiguousTiles.values().iterator());
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
			return contiguousTiles.row(dim).values().iterator();
		} else {
			return noncontiguousTiles.row(dim).values().iterator();
		}
	}

	/**
	 * Get an iterator for all tiles in a dimension
	 * 
	 * @param dim
	 * @return
	 */
	public Iterator<MapTile> getTiles(IDimensionTag dim) {
		return Iterators.concat(contiguousTiles.row(dim).values().iterator(),
				noncontiguousTiles.row(dim).values().iterator());
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
