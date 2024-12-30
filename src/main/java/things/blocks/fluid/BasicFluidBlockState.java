package things.blocks.fluid;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import things.blocks.IBlock;
import things.blocks.IBlockState;
import things.blocks.stateproperties.IBlockStateProperty;

public class BasicFluidBlockState implements IBlockState {

	private int level;
	private IBlock material;

	public BasicFluidBlockState(IBlock material, int level) {
		this.level = level;
		this.material = material;

	}

	@Override
	public IBlock getBlock() {
		return material;
	}

	@Override
	public <E> E getValue(IBlockStateProperty<E> property) {

		return (E) (property == BasicFluidBlock.FLUID_LEVEL ? level : null);
	}

	@Override
	public Collection<IBlockStateProperty<?>> getProperties() {
		return Collections.singleton(BasicFluidBlock.FLUID_LEVEL);
	}

	@Override
	public <E> IBlockState changeValue(IBlockStateProperty<E> prop, E val) {
		Builder<IBlockStateProperty<?>, Object> builder = ImmutableMap.builder();
		builder.put(prop, val);
		if (prop != BasicFluidBlock.FLUID_LEVEL) {
			builder.put(BasicFluidBlock.FLUID_LEVEL, level);
		}
		return material.getState(builder.build());
	}

	@Override
	public IBlockState changeValues(Map<? extends IBlockStateProperty<?>, ? extends Object> val) {
		Builder<IBlockStateProperty<?>, Object> builder = ImmutableMap.builder();
		builder.putAll(val);
		if (!val.containsKey(BasicFluidBlock.FLUID_LEVEL)) {
			builder.put(BasicFluidBlock.FLUID_LEVEL, level);
		}
		return material.getState(builder.build());
	}

	@Override
	public int hashCode() {
		return this.material.hashCode() * this.level;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IBlockState bs) {
			return this.material.equals(bs.getBlock()) && bs.getValue(IBlock.FLUID_LEVEL).equals(level);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return this.material + "[l=" + this.level + "]";
	}

}
