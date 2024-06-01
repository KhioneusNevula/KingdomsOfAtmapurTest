package actor.construction;

import java.util.Collection;

import biology.anatomy.SenseProperty;
import sim.physicality.PhysicalState;

/**
 * TODO add force mechanisms and the like
 * 
 * Material layers are comparable, with lower order layers being earlier in
 * sequence
 * 
 * @author borah
 *
 */
public interface IMaterialLayerType extends Comparable<IMaterialLayerType> {
	/**
	 * The name of the material layer; also corresponds to the name of the material
	 * tag for this material/ tissue tag /etc
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Sometimes certain labels ("bundle" tags) represent multiple types of material
	 * altogether; return all such bundle tags that this material can bundle into
	 * 
	 * @return
	 */
	public Collection<String> getBundleNames();

	/**
	 * What layer this material occupies -- a numeric index to help order it among
	 * other layers. Two materials can't occupy the same layer and materials with
	 * higher indices occupy higher layers.
	 * 
	 * @return
	 */
	public int getLayer();

	/**
	 * What other material layers this material layer contains--if present (may not
	 * be)
	 * 
	 * @return
	 */
	public Collection<String> getSublayers();

	/**
	 * The state this material layer starts in
	 * 
	 * @return
	 */
	public PhysicalState initialState();

	public Collection<SenseProperty<?>> getSensableProperties();

	/**
	 * Gets the combined integer of nutrition types
	 * 
	 * @return
	 */
	public int getNutritionTypes();

	<T> T getTrait(SenseProperty<T> prop);
}
