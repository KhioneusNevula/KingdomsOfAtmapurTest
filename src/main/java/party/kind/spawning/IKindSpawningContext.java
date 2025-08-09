package party.kind.spawning;

import _sim.GameUniverse;
import _sim.vectors.IVector;
import _sim.world.GameMap;
import _sim.world.MapTile;
import party.collective.ICollective;
import things.actor.IActor;
import things.form.kinds.settings.IKindSettings;

/**
 * An {@link IKindSpawningContext} is the details about where and how different
 * kinds can spawn, particularly as relating to a Group
 */
public interface IKindSpawningContext {

	/** Prevents a kind from ever spawning */
	public static IKindSpawningContext NEVER_SPAWN = new IKindSpawningContext() {
		@Override
		public float getSpawnNumberOnNewLoad(MapTile tile, GameUniverse universe, int x, int y, long time,
				ICollective collective) {
			return 0;
		}

		@Override
		public float getSpawnNumberOnMapGeneration(MapTile tile, GameUniverse universe, ICollective collective) {
			return 0;
		}

		@Override
		public IVector findSpawnPosition(IActor actor, GameMap map, long time, ICollective collective) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IKindSettings createKindSettings(GameMap map, long time, ICollective collective) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void doPostSpawnAdjustments(IActor actor, IVector position, GameMap map, long time,
				ICollective collective) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "NEVER_SPAWN";
		}
	};

	/**
	 * Return the spawn count of this Kind when a given map tile is loaded in for
	 * the very first time, based on the Collective it is from. If a fractional
	 * number is given, the fractional portion will represent the proportion of the
	 * population that can be randomly alotted. E.g. a value of 12.0f generates
	 * exactly 12 beings; a value of 12.1f generates 12 beings with a probability of
	 * generating up to 1 extra being; a value of 12.5f generates 12 beings with a
	 * probability of generating up to 6 more beings, and so on
	 */
	public float getSpawnNumberOnMapGeneration(MapTile tile, GameUniverse universe, ICollective collective);

	/**
	 * Return the spawn number of this Kind (in a similar way to
	 * {@link #getSpawnNumberOnMapGeneration(MapTile, GameUniverse, ICollective)})
	 * when a given map tile is loaded at a given time, based on the Collective it
	 * is from and the number of non-persistent and persistent instances last time
	 * the map tile was loaded.
	 */
	public float getSpawnNumberOnNewLoad(MapTile tile, GameUniverse universe, int nonPersistent, int persistent,
			long time, ICollective collective);

	/**
	 * Return a set of kind settings to spawn an individual member of this kind
	 * using, once a game map has loaded; or return null if not possible. May only
	 * be called after
	 * {@link #getSpawnNumberOnMapGeneration(MapTile, GameUniverse, ICollective)} or
	 * {@link #getSpawnChanceOnNewLoad(MapTile, GameUniverse, long, ICollective)}
	 */
	public IKindSettings createKindSettings(GameMap map, long time, ICollective collective);

	/**
	 * Return a spawn position (or null if not possible) to spawn an individual
	 * member of this kind into, once an actor has been created. May only be called
	 * after {@link #createKindSettings(GameMap, long, ICollective)} has been used
	 * to make an actor
	 */
	public IVector findSpawnPosition(IActor actor, GameMap map, long time, ICollective collective);

	/**
	 * Make adjustments necessary after spawning an individual, e.g. registering
	 * them in subgroups, changing mental traits, and so on. May only be called
	 * after {@link #findSpawnPosition(IActor, GameMap, long, ICollective)} has been
	 * used to find a spawn position for this individual
	 */
	public void doPostSpawnAdjustments(IActor actor, IVector position, GameMap map, long time, ICollective collective);
}
