package party.collective;

import java.util.Collection;

import party.IParty;
import party.kind.spawning.IKindSpawningContext;
import things.form.kinds.IKind;
import things.interfaces.UniqueType;

/**
 * An interface representing some collection of multiple individuals that acts
 * as one party and has a party knowledge
 */
public interface ICollective extends IParty {

	@Override
	default UniqueType getUniqueType() {
		return UniqueType.COLLECTIVE;
	}

	/**
	 * Return a number if there is a fixed number of individuals in this Collective
	 * (e.g. a singular role has a count of One, whereas a Group might have a count
	 * of its population) or null if there is an uncountable or changing number of
	 * individuals
	 * 
	 * @return
	 */
	public Integer getCount();

	/** Return what kinds of individuals this collective can generate */
	public Collection<IKind> memberKinds();

	/**
	 * Return what kinds of Structures (buildings, machines, furniture, etc) this
	 * party can generate
	 */
	/** public Collection<IStructureType> objectKinds(); */

	/**
	 * Return what kinds of Structures (buildings, machines, furniture, etc) this
	 * party can generate
	 */
	/**
	 * public IStructureGenerationContext
	 * getStructureGenerationContext(IStructureType forStruct);
	 */

	/** Return the context in which a certain member kind can spawn */
	public IKindSpawningContext getMemberSpawnContext(IKind forKind);

	@Override
	default boolean isCollective() {
		return true;
	}

	/**
	 * If this party has a nonfixed count, a purpose, and membership because it is a
	 * social role (as opposed to being a Group)
	 * 
	 * @return
	 */
	public default boolean isRole() {
		return false;
	}

}
