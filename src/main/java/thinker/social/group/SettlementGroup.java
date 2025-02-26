package thinker.social.group;

import java.util.UUID;

import _sim.vectors.IVector;
import _sim.world.GameUniverse;
import _sim.world.MapTile;
import thinker.concepts.knowledge.INoosphereKnowledgeBase;
import thinker.social.PartyRelationGraph;

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

}
