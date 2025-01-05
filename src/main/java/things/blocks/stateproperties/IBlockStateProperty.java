package things.blocks.stateproperties;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.IntStream;

import utilities.property.IProperty;

/**
 * A property of a block state
 * 
 * @author borah
 *
 * @param <E>
 */
public interface IBlockStateProperty<E> extends IProperty<E> {

	/**
	 * Return all possible values of this block state
	 * 
	 * @return
	 */
	public Iterable<E> allValues();

	/**
	 * Number of possible values this property has
	 * 
	 * @return
	 */
	public int numValues();

	/**
	 * If this value is an acceptable value for this block state
	 * 
	 * @param value
	 * @return
	 */
	public boolean isValidValue(E value);

	/**
	 * Make new enum property using all valus of the appropriate enum
	 * 
	 * @param <E>
	 * @param name
	 * @param defaultVal
	 * @return
	 */
	public static <E extends Enum<E>> BlockEnumProperty<E> enumProperty(String name, E defaultVal) {
		return new BlockEnumProperty<E>(name, defaultVal, EnumSet.allOf((Class<E>) defaultVal.getClass()));
	}

	/**
	 * Make new enum property using all valus in the given collection
	 */
	public static <E extends Enum<E>> BlockEnumProperty<E> enumProperty(String name, E defaultVal,
			Collection<E> allowed) {
		EnumSet<E> passage = null;
		if (allowed instanceof EnumSet<E>s) {
			passage = s;
		} else {
			passage = EnumSet.copyOf(allowed);
		}

		return new BlockEnumProperty<E>(name, defaultVal, passage);
	}

	/**
	 * Make a new int property with the given range of values
	 * 
	 * @param name
	 * @param defaultVal
	 * @param values
	 * @return
	 */
	public static BlockIntProperty intProperty(String name, int defaultVal, int[] values) {
		Arrays.sort(values);
		if (Arrays.binarySearch(values, defaultVal) < 0) {
			throw new IllegalArgumentException("(int " + name + ") " + defaultVal + ":" + Arrays.toString(values));
		}
		return new BlockIntProperty(name, defaultVal, values);
	}

	/**
	 * Make a new int property with the given range. Please be careful to ensure the
	 * range is not untenably large
	 * 
	 * @param name
	 * @param defaultVal
	 * @param lowerBound
	 * @param upperBound
	 * @return
	 */
	public static BlockIntProperty intProperty(String name, int defaultVal, int lowerBound, int upperBound) {

		if (defaultVal < lowerBound || defaultVal > upperBound) {
			throw new IllegalArgumentException(
					"(int " + name + ") " + defaultVal + ":[" + lowerBound + "," + upperBound + "]");
		}

		return new BlockIntProperty(name, defaultVal, IntStream.range(lowerBound, upperBound).toArray());
	}

	/**
	 * Make an int proprty allowing the givne values
	 * 
	 * @param name
	 * @param defaultVal
	 * @param range
	 * @return
	 */
	public static BlockIntProperty intProperty(String name, int defaultVal, List<Integer> range) {
		if (!range.contains(defaultVal)) {
			throw new IllegalArgumentException("(int " + name + ") " + defaultVal + ":" + range);
		}
		return new BlockIntProperty(name, defaultVal, range.stream().mapToInt(Integer::intValue).sorted().toArray());
	}

}
