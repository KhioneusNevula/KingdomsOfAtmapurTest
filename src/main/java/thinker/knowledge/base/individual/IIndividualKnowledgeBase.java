package thinker.knowledge.base.individual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import _utilities.couplets.Triplet;
import _utilities.graph.GraphCombinedView;
import _utilities.graph.IRelationGraph;
import party.relations.social_bonds.ISocialBondTrait;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.mind.memory.TruthType;

/**
 * Interface representing a container of connections for an individual which can
 * be part of a parent entity. All operations may return knowledge from a
 * parent. All methods also have a "checkParent" version, which will return more
 * info about what parent it pulled info from
 * 
 * Additionally, all operations for adding knowledge are permitted to simply
 * change the storage type of concepts marked Forgotten; similarly, all
 * operations for querying knowledge need to keep track of concepts marked
 * Forgotten and skip them
 * 
 * @author borah
 *
 */
public interface IIndividualKnowledgeBase extends IKnowledgeBase {

	/**
	 * Purpose is equivalent to {@link IKnowledgeBase#forgetConcept(IConcept)}, but
	 * this method can also mark the concept as being 'forgotten' in local memory to
	 * 'hide' it in higher memory
	 */
	@Override
	public boolean forgetConcept(IConcept concept);

	/**
	 * This method's purpose is equivalent to
	 * {@link IKnowledgeBase#removeAllRelations(IConcept, IConcept)}, but it can
	 * also mark them as being 'forgotten' in local memory to 'hide' them in higher
	 * memory
	 */
	@Override
	public boolean removeAllRelations(IConcept from, IConcept to);

	/**
	 * Like
	 * {@link IKnowledgeBase#removeAllRelations(IConcept, IConceptRelationType)},
	 * but also can makr relations as being 'forgotten' by local memory without
	 * removing them from higher memory
	 */
	@Override
	public boolean removeAllRelations(IConcept from, IConceptRelationType type);

	/**
	 * Like
	 * {@link IKnowledgeBase#removeRelation(IConcept, IConceptRelationType, IConcept)},
	 * but also can makr relations as being 'forgotten' by local memory without
	 * removing them from higher memory
	 */
	@Override
	public boolean removeRelation(IConcept from, IConceptRelationType type, IConcept to);

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
	public IKnowledgeBase hasAnyValenceRelationCheckParent(IConcept from, IConceptRelationType type, IConcept to);

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
	 * second, using a {@link KnowledgeRelationType#HAS_SOCIAL_BOND_TO} relation,
	 * mapped to each parent storage and this storage
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

	/**
	 * See {@link #isNot(IConcept, IConceptRelationType, IConcept)}.
	 */
	Set<IKnowledgeBase> isNotCheckParents(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * See {@link #isOpposite(IConcept, IConceptRelationType, IConcept)}.
	 */
	Set<IKnowledgeBase> isOppositeCheckParents(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Whether a relation exists that is positive. See
	 * {@link #is(IConcept, IConceptRelationType, IConcept)}.
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	Set<IKnowledgeBase> isCheckParents(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Returns an iterator of outgoing edges from the given concept, checking the
	 * parents for more info
	 */
	IMultiKnowledgeBaseIterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdgesCheckParent(
			IConcept from);

	/**
	 * Returns an iterator of outgoing edges from the given concept, checking the
	 * parents for more info
	 */
	IMultiKnowledgeBaseIterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdgesCheckParent(
			IConcept from, IConceptRelationType type);

	default IRelationGraph<IConcept, IConceptRelationType> getConglomeratedConceptGraphView() {
		List<IKnowledgeBase> parents = new ArrayList<>();
		List<IRelationGraph<IConcept, IConceptRelationType>> visited = new ArrayList<>();
		parents.add(this);
		while (!parents.isEmpty()) {
			IKnowledgeBase bas = parents.remove(parents.size() - 1);
			visited.add(bas.getMappedConceptGraphView());
			if (bas instanceof IIndividualKnowledgeBase ikb)
				parents.addAll(ikb.getParents());
		}
		return new GraphCombinedView<>(visited);
	}

	/**
	 * Returns the distance, but checks parents. How {@link #getDistance(IConcept)}
	 * is implemented for this version
	 * 
	 * @param prf
	 * @return
	 */
	Map<IKnowledgeBase, Float> getDistanceCheckParents(IConcept prf);

	/**
	 * Same as {@link #getRelationTypesBetween(IConcept, IConcept)} but checks
	 * parents and all that
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	IMultiKnowledgeBaseIterator<? extends IConceptRelationType> getRelationTypesBetweenCheckParent(IConcept from,
			IConcept to);

}
