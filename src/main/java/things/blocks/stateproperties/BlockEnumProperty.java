package things.blocks.stateproperties;

import java.util.EnumSet;

public class BlockEnumProperty<E extends Enum<E>> implements IBlockStateProperty<E> {

	private E defaultValue;
	private EnumSet<E> range;
	private String name;
	private Class<E> type;

	BlockEnumProperty(String name, E defaultVal, EnumSet<E> allowedValues) {
		this.defaultValue = defaultVal;
		this.type = (Class<E>) defaultVal.getClass();
		this.name = name;
		this.range = allowedValues;
		if (!range.contains(defaultVal)) {
			throw new IllegalArgumentException(
					"(" + type.getSimpleName() + " " + name + ") " + defaultVal + ":" + range);
		}
	}

	@Override
	public int numValues() {
		return range.size();
	}

	@Override
	public Class<E> getType() {
		return type;
	}

	@Override
	public String getPropertyName() {
		return name;
	}

	@Override
	public E defaultValue() {
		return defaultValue;
	}

	@Override
	public Iterable<E> allValues() {
		return range;
	}

	@Override
	public boolean isValidValue(E value) {
		return range.contains(value);
	}

}
