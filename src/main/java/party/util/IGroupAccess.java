package party.util;

import java.util.Collection;
import java.util.Optional;

import _sim.vectors.IVector;
import _sim.world.MapTile;
import party.collective.ICollective;
import party.collective.group.ICivilizationGroup;
import party.collective.group.IGroup;
import party.collective.group.ISettlementGroup;
import party.kind.IKindCollective;
import party.systems.IRole;

/**
 * {@link IAgentAccess} but for Collectives
 * 
 * @author borah
 *
 */
public interface IGroupAccess extends IAgentAccess {

	/**
	 * Return the group itself
	 * 
	 * @return
	 */
	public IGroup group();

	/**
	 * If this is an {@link ICivilizationGroup}, return a full optional; else return
	 * empty
	 * 
	 * @return
	 */
	public default Optional<ICivilizationGroup> maybeCivilization() {
		return Optional.of(group()).filter(ICivilizationGroup.class::isInstance).map(ICivilizationGroup.class::cast);
	}

	/**
	 * If this is an {@link ISettlementGroup}, return a full optional; else return
	 * empty
	 * 
	 * @return
	 */
	public default Optional<ISettlementGroup> maybeSettlement() {
		return Optional.of(group()).filter(ISettlementGroup.class::isInstance).map(ISettlementGroup.class::cast);
	}

	/**
	 * What map tiles this collective has access to, if it's an
	 * {@link ICivilizationGroup}
	 * 
	 * @return
	 */
	public Collection<MapTile> mapTileAccess();

	/**
	 * What tile this settlemnet is on, if it is a settlement; if it is not, return
	 * null
	 * 
	 * @return
	 */
	public default Optional<MapTile> maybeSettlementTile() {
		return this.maybeSettlement().map(ISettlementGroup::getMapTile);
	}

	/**
	 * What the center position of this settlement is, if it is a settlement;
	 * otherwise, return null
	 * 
	 * @return
	 */
	public default Optional<IVector> maybeSettlementCenter() {
		return this.maybeSettlement().map(ISettlementGroup::getCenter);
	}

}
