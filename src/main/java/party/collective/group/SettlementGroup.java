package party.collective.group;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import _sim.GameUniverse;
import _sim.vectors.IVector;
import _sim.world.MapTile;
import party.PartyRelationGraph;
import party.kind.spawning.IKindSpawningContext;
import things.form.kinds.IKind;
import thinker.knowledge.base.noosphere.INoosphereKnowledgeBase;
import thinker.mind.emotions.IFeeling;
import thinker.mind.personality.IPersonality;
import thinker.mind.personality.ITendencies;
import thinker.mind.will.IWill;

public class SettlementGroup extends AbstractGroup implements ISettlementGroup {

	private MapTile tile;
	private IVector center = IVector.ZERO;
	private PartyRelationGraph partyGraph;

	public SettlementGroup(UUID id, String identifierName, INoosphereKnowledgeBase noosphere, MapTile tile,
			PartyRelationGraph partyGraph, GameUniverse universe) {
		super(id, identifierName, noosphere, universe);
		this.tile = tile;
		this.partyGraph = partyGraph;
	}

	public void setCenter(IVector center) {
		if (center.getTile().equals(tile)) {
			this.center = center;
		} else {
			this.center = center.withTile(tile);
		}
	}

	@Override
	public IWill getWill() {
		// TODO Group will
		return null;
	}

	@Override
	public ITendencies getPersonality() {
		// TODO settlement personalty
		return null;
	}

	@Override
	public float getFeeling(IFeeling feeling) {
		// TODO Group feelings
		return 0;
	}

	@Override
	public Collection<IFeeling> getFeelings() {
		// TODO Group feeligns
		return null;
	}

	@Override
	public void runTick(GameUniverse universe, long ticks) {
		// TODO settlement tick
		if (partyGraph.contains(this)) {
			if (this.roles.isEmpty()) {
				this.createRolesFromNeeds();
			}
		}
	}

	@Override
	public MapTile getMapTile() {
		return this.tile;
	}

	@Override
	public IVector getCenter() {
		return this.center;
	}

	@Override
	public String report() {
		return this.getClass().getSimpleName() + "{count=" + count
				+ (this.roles.isEmpty() ? "" : ", roles=" + this.roles)
				+ (this.center == IVector.ZERO ? "" : ", center=" + center)
				+ (this.postedNeeds.isEmpty() ? "" : ", postedNeeds=" + this.postedNeeds) + "}";
	}

	@Override
	public Collection<IKind> memberKinds() {
		// TODO get member kinds
		return Collections.emptySet();
	}

	@Override
	public IKindSpawningContext getMemberSpawnContext(IKind forKind) {
		return IKindSpawningContext.NEVER_SPAWN;
	}

}
