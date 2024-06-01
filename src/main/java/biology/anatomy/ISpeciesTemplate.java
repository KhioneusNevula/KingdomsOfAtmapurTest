package biology.anatomy;

import java.util.Map;

import actor.construction.IBlueprintTemplate;

/**
 * A template type for a living entity, typically iwth many physical parts
 * 
 * @author borah
 *
 */
public interface ISpeciesTemplate extends IBlueprintTemplate {

	/**
	 * More name-specific implementation of
	 * {@link IBlueprintTemplate#materialLayerTypes()}
	 * 
	 * @return
	 */
	public Map<String, ITissueLayerType> tissueTypes();

	@Override
	public Map<String, IBodyPartType> partTypes();

	/**
	 * TODO Generates a default, static culture that all members of this species
	 * subscribe to, e.g. to define their actions and whatnot. may be null for
	 * nonsentient species
	 * 
	 * @return
	 */

}
