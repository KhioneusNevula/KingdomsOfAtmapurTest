package things.blocks.fluid;

import java.util.Arrays;
import java.util.Map;

import sim.IVector;
import sim.world.GameMap;
import things.blocks.IBlock;
import things.blocks.IBlockState;
import things.blocks.stateproperties.IBlockStateProperty;
import things.physical_form.material.IMaterial;
import things.physical_form.material.Material;

public enum BasicFluidBlock implements IBlock {

	WATER(Material.WATER), AIR(Material.AIR);

	private IMaterial material;
	private BasicFluidBlockState[] states;

	private BasicFluidBlock(IMaterial material) {
		this.material = material;
		this.states = new BasicFluidBlockState[16];
		for (int i = 0; i < states.length; i++) {
			states[i] = new BasicFluidBlockState(this, i);
		}
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public boolean isFluid() {
		return true;
	}

	@Override
	public IBlockState getDefaultState() {
		return states[15];
	}

	public IBlockState getEmptyState() {
		return states[0];
	}

	public IBlockState getState(int level) {
		if (level < 0 || level > 15)
			throw new IllegalArgumentException(this + "" + level + "");
		return states[level];
	}

	@Override
	public IBlockState getState(Map<IBlockStateProperty<?>, ? extends Object> properties) {
		Integer x = (Integer) properties.get(FLUID_LEVEL);
		if (x == null) {
			return this.getDefaultState();
		}
		return states[x];
	}

	@Override
	public Iterable<BasicFluidBlockState> getBlockStates() {
		return () -> Arrays.stream(states).iterator();
	}

	@Override
	public void randomTick(GameMap world, IVector blockPos, IBlockState inState, long tick) {
		// TODO mixed fluids? for rn just push fluids out
		int level = inState.getValue(IBlock.FLUID_LEVEL);
		int[][] pairs = { { -1, 0 }, { 0, -1 }, { 1, 0 }, { 0, 1 } };
		for (int[] pair : pairs) {
			IVector newPos = blockPos.add(pair[0], pair[1]);
			// TODO handle fluids
		}
	}

	@Override
	public String toString() {
		return "fluid_" + name();
	}

}
