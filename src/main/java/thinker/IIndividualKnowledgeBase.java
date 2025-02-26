package thinker;

import java.util.Collection;
import java.util.Map;

import thinker.concepts.IConcept;
import thinker.concepts.relations.ConceptRelationType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.mind.memory.IMultiKnowledgeBaseIterator;
import thinker.mind.memory.TruthType;
import thinker.social.relations.social_bond.ISocialBondTrait;

/**
 * Interface representing a container of connections for an individual which can
 * be part of a parent entity. All operations have a "checkParent" version,
 * which will return a result from a parent if it cannot find a result from the
 * immediate storage item
 * 
 * @author borah
 *
 */
public interface IIndividualKnowledgeBase extends IKnowledgeBase {

	/**
	 * Return the storage item which knows this concept, either this item or the
	 * first ancestor item, or null if none know
	 * 
	 * @param concept
	 * @return
	 */
	public IKnowledgeBase knowsConceptCheckParent(IConcept concept);

	/**
	 * Return self if this relation exists between these concepts, or a parent
	 * storage if it exists in a parent storage
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public IKnowledgeBase hasRelationCheckParent(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Return self if any relation exists between these concepts, or a parent
	 * storage if it exists in a parent storage
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public IKnowledgeBase hasRelationCheckParent(IConcept from, IConcept to);

	/**
	 * Return an iterator for all relation types from this concept, including every
	 * parent storage in the hierarchy
	 * 
	 * @param from
	 * @return
	 */
	public IMultiKnowledgeBaseIterator<? extends IConceptRelationType> getRelationTypesFromCheckParent(IConcept from);

	/**
	 * Return an iterator for all concepts connected to this concept, including
	 * every parent storage in the hierarchy
	 * 
	 * @param from
	 * @return
	 */
	public IMultiKnowledgeBaseIterator<? extends IConcept> getConnectedConceptsCheckParent(IConcept from);

	/**
	 * Return iterator of all concepts this concept has a relation of the given type
	 * to, acknowledging the storages
	 * 
	 * @param from
	 * @return
	 */
	public IMultiKnowledgeBaseIterator<? extends IConcept> getConnectedConceptsCheckParent(IConcept from,
			IConceptRelationType type);

	/**
	 * Return a map mapping what each parent storage considers to be the truth type
	 * of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public Map<IKnowledgeBase, TruthType> getTruthTypeOfRelationCheckParents(IConcept from, IConceptRelationType type,
			IConcept to);

	/**
	 * Return the value of this social bond trait from the first concept to the
	 * second, using a {@link ConceptRelationType#KNOWS} relation, mapped to each
	 * parent storage and this storage
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public Map<IKnowledgeBase, Float> getSocialBondValueCheckParents(IConcept from, ISocialBondTrait trait,
			IConcept to);

	/**
	 * Return the storage entities that can be referenced in addition to this one
	 * 
	 * @return
	 */
	public Collection<? extends IKnowledgeBase> getParents();

	/**
	 * Add parent groups to this storage
	 * 
	 * @param parents
	 */
	void addParents(Iterable<? extends IKnowledgeBase> parents);

	/**
	 * Remove parent groups from this storage
	 * 
	 * @param parents
	 */
	void removeParents(Iterable<? extends IKnowledgeBase> parents);

	public IIndividualKnowledgeBase clone();

}
