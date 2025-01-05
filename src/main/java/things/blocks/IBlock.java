package things.blocks;

import java.util.Map;

import _sim.vectors.IVector;
import _sim.world.GameMap;
import things.blocks.stateproperties.BlockIntProperty;
import things.blocks.stateproperties.IBlockStateProperty;
import things.form.material.IMaterial;

/**
 * Interface representing a distinct block
 * 
 * @author borah
 *
 */
public interface IBlock {

	/**
	 * The block property for fluid level, ranging from 0 to 15
	 */
	public static final BlockIntProperty FLUID_LEVEL = IBlockStateProperty.intProperty("level", 0, 0, 15);

	/**
	 * The name of this block
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Material of this block
	 * 
	 * @return
	 */
	public IMaterial getMaterial();

	/**
	 * If this block is a fluid (liquid or gas)
	 * 
	 * @return
	 */
	public boolean isFluid();

	/**
	 * Return the default block state
	 * 
	 * @return
	 */
	public IBlockState getDefaultState();

	/**
	 * Get the blockstate with the given properties (based on the default state)
	 * 
	 * @param properties
	 * @return
	 */
	public IBlockState getState(Map<IBlockStateProperty<?>, ? extends Object> properties);

	/**
	 * Return an iterable of all block states
	 * 
	 * @return
	 */
	public Iterable<? extends IBlockState> getBlockStates();

	/**
	 * Run a random tick for this block
	 * 
	 * @param world
	 * @param blockPos
	 * @param tick
	 */
	public void randomTick(GameMap world, IVector blockPos, IBlockState inState, long tick);
}
