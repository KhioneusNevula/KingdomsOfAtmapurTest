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
	 * See {@link #isNot(IConcept, IConceptRelationType, IConcept)}. This also
	 * checks parents
	 */
	Set<IKnowledgeBase> isNotCheckParents(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * See {@link #isOpposite(IConcept, IConceptRelationType, IConcept)}. This also
	 * checks parents.
	 */
	Set<IKnowledgeBase> isOppositeCheckParents(IConcept from, IConceptRelationType type, IConcept to);

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

}
