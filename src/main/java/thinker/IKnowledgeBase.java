package thinker;

import java.util.UUID;

import thinker.concepts.IConcept;
import thinker.concepts.relations.ConceptRelationType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;
import thinker.mind.memory.node.IConceptNode;
import thinker.social.relations.social_bond.ISocialBondTrait;
import utilities.graph.IRelationGraph;

/**
 * Interface representing a container of connections for an individual or group.
 * 
 * @author borah
 *
 */
public interface IKnowledgeBase extends Cloneable {

	/**
	 * Return the concept representing the self
	 * 
	 * @return
	 */
	public IConcept getSelfConcept();

	/**
	 * Return true if the given concept is known to this storage
	 * 
	 * @param concept
	 * @return
	 */
	public boolean knowsConcept(IConcept concept);

	/**
	 * Return the storage type of the given concept
	 * 
	 * @param concept
	 * @return
	 */
	public StorageType getStorageType(IConcept concept);

	/**
	 * Add a concept to memory (with CONFIDENT storage type); return false and
	 * change nothing if it already existed
	 * 
	 * @param concept
	 * @param type
	 */
	public boolean learnConcept(IConcept concept);

	/**
	 * Add a concept to memory with the given storage type; return true if anything
	 * was added
	 * 
	 * @param concept
	 * @param confidence
	 * @return
	 */
	public boolean learnConcept(IConcept concept, StorageType type);

	/**
	 * Remove concept from memory; return true if anything was forgotten
	 * 
	 * @param concept
	 */
	public boolean forgetConcept(IConcept concept);

	/**
	 * Return the number of concepts in this graph
	 */
	public int countConcepts();

	/**
	 * Return the number of connections from this concept
	 * 
	 * @param forConcept
	 * @return
	 */
	public int countRelations(IConcept forConcept);

	/**
	 * Return the number of connections from this concept of the given relation type
	 * 
	 * @param forConcept
	 * @return
	 */
	public int countRelationsOfType(IConcept forConcept, IConceptRelationType relation);

	/**
	 * Return the number of relations between these two concepts
	 * 
	 * @param fromConcept
	 * @param toConcept
	 * @return
	 */
	public int countRelationsBetween(IConcept fromConcept, IConcept toConcept);

	/**
	 * Number of concepts this concept has a relation to
	 * 
	 * @param fromConcept
	 * @return
	 */
	public int countConnectedConcepts(IConcept fromConcept);

	/**
	 * Number of types of relations this concept has
	 * 
	 * @param fromConcept
	 * @return
	 */
	public int countRelationTypesFrom(IConcept fromConcept);

	/**
	 * Add a relation of full confidence to this graph
	 * 
	 * @param from
	 * @param relation
	 * @param to
	 * @return
	 */
	public boolean addConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to);

	/**
	 * Add a relation of dubious confidence to this graph
	 * 
	 * @param from
	 * @param relation
	 * @param to
	 * @return
	 */
	public boolean addDubiousRelation(IConcept from, IConceptRelationType relation, IConcept to, float confidence);

	/**
	 * Add a relation that will be pruned away the next time memories are pruned
	 * 
	 * @param from
	 * @param relation
	 * @param to
	 * @return
	 */
	public boolean addTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to);

	/**
	 * Adds an {@link ConceptRelationType#UNKNOWN} relation between these concepts
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean addUnknownRelation(IConcept from, IConcept to);

	/**
	 * Remove the given relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public boolean removeRelation(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Remove all relations between the given nodes
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean removeAllRelations(IConcept from, IConcept to);

	/**
	 * Remove all relations of the given type extending from this node
	 * 
	 * @param from
	 * @param type
	 * @return
	 */
	public boolean removeAllRelations(IConcept from, IConceptRelationType type);

	/**
	 * Return whether this relation exists between these concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public boolean hasRelation(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Whether these two concepts have any relation
	 */
	public boolean hasRelation(IConcept from, IConcept to);

	/**
	 * Get all relation types from this concept (in a set-style unique element
	 * format)
	 * 
	 * @param from
	 * @return
	 */
	public Iterable<? extends IConceptRelationType> getRelationTypesFrom(IConcept from);

	/**
	 * Get all concepts this concept has a relation to
	 * 
	 * @param from
	 * @return
	 */
	public Iterable<? extends IConcept> getConnectedConcepts(IConcept from);

	/**
	 * Get all concepts this concept has a relation of the given type to
	 * 
	 * @param from
	 * @return
	 */
	public Iterable<? extends IConcept> getConnectedConcepts(IConcept from, IConceptRelationType type);

	/**
	 * Return the storage type of the given relation between concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public StorageType getStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Set the storage type of the given relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param type
	 */
	public void setStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype);

	/**
	 * Return the truth type of the given relation between concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public TruthType getTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Set the truth type of the given relation between concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param ttype
	 */
	public void setTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, TruthType ttype);

	/**
	 * Return the UUID of the source of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public UUID getInfoSource(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * set the id of the source of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param source
	 */
	public void setInfoSource(IConcept from, IConceptRelationType type, IConcept to, UUID source);

	/**
	 * Return the confidence of this relation. If this relation is
	 * {@link StorageType#TEMPORARY} then this represents how unlikely it is to be
	 * deleted
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public float getConfidence(IConcept from, IConceptRelationType type, IConcept to);

	/**
	 * Set the confidence value of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 */
	public void setConfidence(IConcept from, IConceptRelationType type, IConcept to, float val);

	/**
	 * Change the confidence value of this relation by the given amount; return the
	 * overflow (i.e. how much it went over 1f or under 0f) or 0f if no overflow
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param delta
	 */
	public default float changeConfidenceBy(IConcept from, IConceptRelationType type, IConcept to, float delta) {
		float con = this.getConfidence(from, type, to);
		con += delta;
		if (con > 1f) {
			this.setConfidence(from, type, to, 1f);
			return con - 1f;
		} else if (con < 0f) {
			this.setConfidence(from, type, to, 0f);
			return con;
		}
		this.setConfidence(from, type, to, con);
		return 0f;
	}

	/**
	 * Return the value of this social bond trait from the first concept to the
	 * second, using a {@link ConceptRelationType#KNOWS} relation
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public float getSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to);

	/**
	 * Set the value of this social bond trait from the first concept to the second,
	 * using a {@link ConceptRelationType#KNOWS} relation
	 * 
	 * @param from
	 * @param trait
	 * @param to
	 * @param value
	 */
	public void setSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to, float value);

	/**
	 * Change the value of a specific social bond trait (using a
	 * {@link ConceptRelationType#KNOWS} relation) by the given amount; return the
	 * overflow (i.e. how much it went over 1f or under 0f) or 0f if no overflow
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param delta
	 */
	public default float changeConfidenceBy(IConcept from, ISocialBondTrait trait, IConcept to, float delta) {
		float con = this.getSocialBondValue(from, trait, to);
		con += delta;
		if (con > trait.getMax()) {
			this.setSocialBondValue(from, trait, to, trait.getMax());
			return con - trait.getMax();
		} else if (con < trait.getMin()) {
			this.setSocialBondValue(from, trait, to, trait.getMin());
			return con - trait.getMin();
		}
		this.setSocialBondValue(from, trait, to, con);
		return 0f;
	}

	/**
	 * Return an immutable view of the stored concept graph
	 * 
	 * @return
	 */
	public IRelationGraph<? extends IConceptNode, IConceptRelationType> getConceptGraphView();

	/**
	 * Adds the elements of a subgraph of concepts to this knowledgebase
	 * 
	 * @param graph
	 */
	public void learnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph);

	/**
	 * Equivalent to {@link #learnConceptSubgraph(IRelationGraph)}, but for a graph
	 * of ConceptNodes rather than concepts
	 * 
	 * @param graph
	 */
	public void addConceptNodeSubgraph(IRelationGraph<? extends IConceptNode, IConceptRelationType> graph);

	/**
	 * Return the number of relations
	 * 
	 * @return
	 */
	int countRelations();

	/**
	 * This should fully clone the internal concept graph data into a new graph, as
	 * well as clone the set of parent knowledge bases (but not the internal
	 * knowledge bases of the parents themselves). Concepts, which are assumed to be
	 * immutable, do not need to be individually cloned.
	 * 
	 * @return
	 */
	public IKnowledgeBase clone();

}
