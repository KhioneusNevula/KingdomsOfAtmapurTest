package things.blocks.stateproperties;

import java.util.Arrays;

public class BlockIntProperty implements IBlockStateProperty<Integer> {

	private int defaultValue;
	private int[] range;
	private int numInts;
	private String name;

	BlockIntProperty(String name, int defaultVal, int[] range) {
		this.defaultValue = defaultVal;
		this.range = range;
		this.name = name;
		this.numInts = range.length;
	}

	@Override
	public int numValues() {
		return numInts;
	}

	@Override
	public Class<Integer> getType() {
		return int.class;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Integer defaultValue() {
		return defaultValue;
	}

	@Override
	public Iterable<Integer> allValues() {
		return () -> Arrays.stream(range).boxed().iterator();
	}

	@Override
	public boolean isValidValue(Integer value) {
		return Arrays.binarySearch(range, value) >= 0;
	}

}
