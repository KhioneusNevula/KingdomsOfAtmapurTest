package biology.sensing;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import actor.Actor;
import actor.IUniqueEntity;
import actor.construction.physical.IComponentPart;
import actor.construction.physical.IMaterialLayer;
import actor.construction.physical.IPartAbility;

/**
 * A method of sensing which obtains world info, as employed by an individual
 * entity.
 * 
 * @author borah
 *
 */
public interface ISense extends IPartAbility {

	/**
	 * Gets all visible entities this can sense using the given part
	 * 
	 * @param sensingActor
	 * @param usingPart
	 * @return
	 */
	public Stream<IUniqueEntity> getSensedVisages(Actor sensingActor, IComponentPart usingPart);

	/**
	 * Returns all parts AND material layers that are sensed from a given Actor,
	 * when sensing actors. Note that sensable properties are extracted based on
	 * what senses *they* express to
	 * 
	 * @param sensingActor the being using senses
	 * @param usingPart    the body part that is sensing
	 * @param forEntity    the entity being sensed
	 * @return
	 */
	public Map<? extends IComponentPart, ? extends Collection<? extends IMaterialLayer>> getSensableParts(
			Actor sensingActor, IComponentPart usingPart, Actor forEntity);

}
