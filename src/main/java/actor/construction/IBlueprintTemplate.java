package actor.construction;

import java.util.Map;

import sim.interfaces.ITemplate;

/**
 * Indicates a "type" of object, i.e. a blueprint
 * 
 * @author borah
 *
 */
public interface IBlueprintTemplate extends ITemplate {

	/**
	 * The different part types in this thing
	 * 
	 * @return
	 */
	public Map<String, ? extends IComponentType> partTypes();

	/**
	 * If this template consists only of one main type of part. For example, a rock,
	 * or something made of pieces of only one ttype
	 * 
	 * @return
	 */
	public boolean hasSinglePartType();

	/**
	 * If this template contains not only one single part type, but a part type with
	 * just one unit
	 * 
	 * @return
	 */
	public default boolean hasOnlyOnePart() {
		return this.hasSinglePartType() && this.mainComponent().count() == 1;
	}

	/**
	 * For single-part actors, the component encompassing its entire entity. Throw
	 * exception if not single-part
	 * 
	 * @return
	 */
	public IComponentType mainComponent();

	/**
	 * The name of this thing
	 * 
	 * @return
	 */
	public String name();
}
