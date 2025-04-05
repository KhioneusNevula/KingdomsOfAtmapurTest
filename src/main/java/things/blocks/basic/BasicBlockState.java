package things.blocks.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import things.blocks.IBlock;
import things.blocks.IBlockState;
import things.blocks.stateproperties.IBlockStateProperty;

class BasicBlockState implements IBlockState {

	private IBlock block;

	BasicBlockState(IBlock block) {
		this.block = block;
	}

	@Override
	public IBlock getBlock() {
		return block;
	}

	@Override
	public <E> E getValue(IBlockStateProperty<E> property) {
		return property.defaultValue();
	}

	@Override
	public <E> IBlockState changeValue(IBlockStateProperty<E> prop, E val) {
		return this;
	}

	@Override
	public IBlockState changeValues(Map<? extends IBlockStateProperty<?>, ? extends Object> val) {
		return this;
	}

	@Override
	public Collection<IBlockStateProperty<?>> getProperties() {
		return Collections.emptySet();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof IBlockState s) {
			return this.block.equals(s.getBlock());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return block.hashCode();
	}

	@Override
	public String toString() {
		return this.block + "[]";
	}

}
