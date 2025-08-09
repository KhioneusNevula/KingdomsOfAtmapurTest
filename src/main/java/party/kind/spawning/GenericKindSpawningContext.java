package party.kind.spawning;

import _sim.GameUniverse;
import _sim.MapLayer;
import _sim.vectors.IVector;
import _sim.world.GameMap;
import _sim.world.MapTile;
import party.collective.ICollective;
import party.kind.IKindCollective;
import things.actor.IActor;
import things.form.kinds.settings.IKindSettings;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;

/**
 * A spawning context which spawns a number of members of a certain kind on
 * every {@link MapTile} in the given amounts
 */
public class GenericKindSpawningContext implements IKindSpawningContext {

	private float sCount;

	public GenericKindSpawningContext(float spawnCount) {
		this.sCount = spawnCount;
	}

	@Override
	public float getSpawnNumberOnMapGeneration(MapTile tile, GameUniverse universe, ICollective collective) {
		return sCount;
	}

	@Override
	public float getSpawnNumberOnNewLoad(MapTile tile, GameUniverse universe, int nonPersistent, int persistent,
			long time, ICollective collective) {
		return sCount;
	}

	@Override
	public IKindSettings createKindSettings(GameMap map, long time, ICollective collective) {
		return IKindSettings.NONE;
	}

	@Override
	public IVector findSpawnPosition(IActor actor, GameMap map, long time, ICollective collective) {
		return IVector.of(map.getMapWidth() * map.random(), map.getMapHeight() * map.random(), MapLayer.STANDARD_LAYER,
				map.getMapTile());
	}

	@Override
	public void doPostSpawnAdjustments(IActor actor, IVector position, GameMap map, long time, ICollective collective) {
		if (collective != null) {
			actor.getBody().getAllTetheredSpirits().forEach((spirit) -> {
				spirit.addToGroup(collective);
				spirit.getKnowledge().learnConcept(spirit.getProfile());
				spirit.getKnowledge().learnConcept(collective.getProfile());
				spirit.getKnowledge().addConfidentRelation(spirit.getProfile(), ProfileInterrelationType.MEMBER_OF,
						collective.getProfile());
				((IKindCollective) collective).maybeGetFigureTypeProfile().ifPresent((kprof) -> {
					spirit.getKnowledge().learnConcept(kprof);
					spirit.getKnowledge().addConfidentRelation(spirit.getProfile(), ProfileInterrelationType.IS, kprof);
				});
			});
		}
	}

}
