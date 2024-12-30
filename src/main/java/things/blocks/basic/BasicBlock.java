package things.blocks.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import sim.IVector;
import sim.world.GameMap;
import things.blocks.IBlock;
import things.blocks.IBlockState;
import things.blocks.stateproperties.IBlockStateProperty;
import things.physical_form.material.IMaterial;
import things.physical_form.material.Material;

/**
 * Basic blocks which have no complex behaviors
 * 
 * @author borah
 *
 */
public enum BasicBlock implements IBlock {
	STONE(Material.STONE), ICE(Material.ICE);

	private IMaterial mat;
	private IBlockState defaultState;
	private Collection<IBlockState> defaultStateSingleton;

	private BasicBlock(IMaterial material) {
		mat = material;
		this.defaultState = new BasicBlockState(this);
		this.defaultStateSingleton = Collections.singleton(defaultState);
	}

	@Override
	public IMaterial getMaterial() {
		return mat;
	}

	@Override
	public Iterable<IBlockState> getBlockStates() {
		return this.defaultStateSingleton;
	}

	@Override
	public IBlockState getDefaultState() {
		return this.defaultState;
	}

	/**
	 * Properties are not permitted to be used forr a basicblock
	 */
	@Override
	public IBlockState getState(Map<IBlockStateProperty<?>, ? extends Object> properties) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFluid() {
		return false;
	}

	@Override
	public void randomTick(GameMap world, IVector blockPos, IBlockState inState, long tick) {

	}

	@Override
	public String toString() {
		return "block_" + name();
	}

}
