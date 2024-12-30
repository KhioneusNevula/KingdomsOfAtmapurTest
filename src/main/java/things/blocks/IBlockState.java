package things.blocks;

import java.util.Collection;
import java.util.Map;

import things.blocks.stateproperties.IBlockStateProperty;

public interface IBlockState {

	public IBlock getBlock();

	/**
	 * Get the value of this block state property
	 * 
	 * @param <E>
	 * @param property
	 * @return
	 */
	public <E> E getValue(IBlockStateProperty<E> property);

	/**
	 * Return all properties of this block state
	 * 
	 * @return
	 */
	public Collection<IBlockStateProperty<?>> getProperties();

	/**
	 * Change this property of the given block state and return the appropriate
	 * state with the property changed
	 * 
	 * @param prop
	 * @param val
	 * @return
	 */
	public <E> IBlockState changeValue(IBlockStateProperty<E> prop, E val);

	/**
	 * Change these properties of the given block state and return the appropriate
	 * state with the properties changed
	 * 
	 * @param prop
	 * @param val
	 * @return
	 */
	public IBlockState changeValues(Map<? extends IBlockStateProperty<?>, ? extends Object> val);
}
