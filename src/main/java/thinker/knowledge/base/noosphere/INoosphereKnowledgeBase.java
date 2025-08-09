package thinker.knowledge.base.noosphere;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Functions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import _utilities.graph.ImmutableGraphView;
import _utilities.property.IProperty;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.profile.IProfile;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.knowledge.base.section.ISectionKnowledgeBase;
import thinker.knowledge.base.section.SectionKnowledgeBase;
import thinker.knowledge.node.IConceptNode;
import thinker.knowledge.node.IGroupConceptNode;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;

/**
 * A Noosphere is the graph of all knowledge available in a
 * universe-of-knowledge, that is stored among groups rather than individuals.
 * Although individuals maintain their own knowledge bases for personal
 * knowledge, the noosphere maintains the party knowledge of many individuals
 * across different groups.
 * 
 * @author borah
 *
 */
public interface INoosphereKnowledgeBase extends IKnowledgeBase {

	/**
	 * All properties for an individual relation in a Noosphere, organized by group
	 */
	public static final IProperty<Table<IProfile, IProperty<?>, Object>> GROUP_BASED_PROPERTIES = IProperty
			.make("group_based_properties", Table.class, HashBasedTable.create());

	/** The groups that recognize certain relations */
	public static final IProperty<Set<IProfile>> REL_GROUPS = IProperty.make("relation_groups", Set.class,
			new HashSet<>());

	@Override
	default IConcept getSelfConcept() {
		return IConcept.EXISTENCE;
	}

	/**
	 * Generates a new section knowledge base out of this Noosphere for the given
	 * profile. Can be used to create permanent knowledge bases, or just temporary
	 * ones for temporary purposes as well
	 */
	default ISectionKnowledgeBase generateSection(IProfile forProfile) {
		return new SectionKnowledgeBase(forProfile, this);
	}

	/**
	 * Return all concepts which are unknown, i.e. have no groups that know them
	 * 
	 * @return
	 */
	public Iterable<IConcept> getUnknownConcepts();

	/**
	 * Deletes all unknown concepts
	 */
	public void deleteUnknownConcepts();

	/**
	 * Deletes all unknown concepts and returns them as a collection
	 * 
	 * @return
	 */
	public Collection<IConcept> deleteUnknownConceptsAndReturn();

	/**
	 * Return all groups partaking in this noosphere
	 * 
	 * @return
	 */
	public Set<IProfile> allGroups();

	/**
	 * If a given group knows the given concept
	 * 
	 * @param con
	 * @param group
	 * @return
	 */
	public boolean groupKnowsConcept(IConcept con, IProfile group);

	/**
	 * Return what groups know the given concept
	 * 
	 * @param concept
	 * @return
	 */
	public Set<IProfile> groupsThatKnow(IConcept concept);

	/**
	 * Return the storage type of the given concept for the given group
	 * 
	 * @param concept
	 * @return
	 */
	public StorageType groupGetStorageType(IConcept concept, IProfile forGroup);

	/**
	 * Add a concept to memory for a given group (with CONFIDENT storage type),
	 * creating it in the graph if it wasn't there; return false and change nothing
	 * if it was already part of that group's knowledge.
	 * 
	 * @param concept
	 * @param type
	 */
	public boolean groupLearnConcept(IConcept concept, IProfile forGroup);

	/**
	 * Add a concept to memory with the given storage type for the given group;
	 * return true if anything was added
	 * 
	 * @param concept
	 * @param confidence
	 * @return
	 */
	public boolean groupLearnConcept(IConcept concept, StorageType type, IProfile forGroup);

	/**
	 * Have an individual group forget the given concept; return true if the group
	 * had known the concept before forgetting it; return false if nothing changed.
	 * The concept will not be removed from the noosphere, however
	 * 
	 * @param group
	 * @param concept
	 */
	public boolean groupForgetConcept(IConcept concept, IProfile group);

	/**
	 * Have an individual group forget the given concept and delete the concept if
	 * every single group has forgotten it; return true if the group had known the
	 * concept before forgetting; return false if nothing changed
	 * 
	 * @param group
	 * @param concept
	 */
	public boolean groupForgetConceptAndDelete(IConcept concept, IProfile group);

	/**
	 * Return the number of concepts in this graph known to the given Group
	 */
	public int groupCountConcepts(IProfile forGroup);

	/**
	 * Return the number of connections from this concept for the given group
	 * 
	 * @param forConcept
	 * @return
	 */
	public int groupCountRelations(IConcept forConcept, IProfile forGroup);

	/**
	 * Return the number of connections from this concept of the given relation type
	 * for the given group
	 * 
	 * @param forConcept
	 * @return
	 */
	public int groupCountRelationsOfType(IConcept forConcept, IConceptRelationType relation, IProfile forGroup);

	/**
	 * Return the number of relations between these two concepts
	 * 
	 * @param fromConcept
	 * @param toConcept
	 * @return
	 */
	public int groupCountRelationsBetween(IConcept fromConcept, IConcept toConcept, IProfile forGroup);

	/**
	 * Number of concepts this concept has a relation to for the given group
	 * 
	 * @param fromConcept
	 * @return
	 */
	public int groupCountConnectedConcepts(IConcept fromConcept, IProfile group);

	/**
	 * Number of types of relations this concept has for the given group
	 * 
	 * @param fromConcept
	 * @return
	 */
	public int groupCountRelationTypesFrom(IConcept fromConcept, IProfile forGroup);

	/**
	 * Add a relation of full confidence to this graph for the given group (if the
	 * relation exists, sets its confidence to CONFIDENT for the given group and no
	 * other)
	 * 
	 * @param from
	 * @param relation
	 * @param to
	 * @return
	 */
	public boolean groupAddConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to, IProfile group);

	/**
	 * Add a relation of dubious confidence to this graphm (if the relation exists,
	 * sets its confidence to DUBIOUS and the value given for the given group and no
	 * other)
	 * 
	 * @param from
	 * @param relation
	 * @param to
	 * @return
	 */
	public boolean groupAddDubiousRelation(IConcept from, IConceptRelationType relation, IConcept to, float confidence,
			IProfile group);

	/**
	 * Add a relation that will be pruned away the next time memories are pruned (if
	 * the relation exists, sets its confidence to TEMPORARY for the given group and
	 * no other)
	 * 
	 * @param from
	 * @param relation
	 * @param to
	 * @return
	 */
	public boolean groupAddTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to, IProfile group);

	/**
	 * Remove the given relation only for the given group
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public boolean groupRemoveRelation(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/////////////////////////////

	/**
	 * Remove all relations between the given nodes for the group
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean groupRemoveAllRelations(IConcept from, IConcept to, IProfile group);

	/**
	 * Remove all relations of the given type extending from this node
	 * 
	 * @param from
	 * @param type
	 * @return
	 */
	public boolean groupRemoveAllRelations(IConcept from, IConceptRelationType type, IProfile group);

	/**
	 * Return whether this relation exists between these concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public boolean groupHasAnyValenceRelation(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * Whether these two concepts have any relation
	 */
	public boolean groupHasRelation(IConcept from, IConcept to, IProfile group);

	/**
	 * Get all relation types from this concept (in a set-style unique element
	 * format)
	 * 
	 * @param from
	 * @return
	 */
	public Iterable<? extends IConceptRelationType> groupGetRelationTypesFrom(IConcept from, IProfile group);

	/**
	 * Get all concepts this concept has a relation to
	 * 
	 * @param from
	 * @return
	 */
	public Iterable<? extends IConcept> groupGetConnectedConcepts(IConcept from, IProfile group);

	/**
	 * Get all concepts this concept has a relation of the given type to
	 * 
	 * @param from
	 * @return
	 */
	public Iterable<? extends IConcept> groupGetConnectedConcepts(IConcept from, IConceptRelationType type,
			IProfile group);

	/**
	 * Return the storage type of the given relation between concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public StorageType groupGetStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to,
			IProfile group);

	/**
	 * Set the storage type of the given relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param type
	 */
	public void groupSetStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype,
			IProfile forGroup);

	/**
	 * Return the truth type of the given relation between concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public TruthType groupGetTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * Set the truth type of the given relation between concepts
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param ttype
	 */
	public void groupSetTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, TruthType ttype,
			IProfile group);

	/**
	 * Return the UUID of the source of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public UUID groupGetInfoSource(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * set the id of the source of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param source
	 */
	public void groupSetInfoSource(IConcept from, IConceptRelationType type, IConcept to, UUID source, IProfile group);

	/**
	 * Return the confidence of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @return
	 */
	public float groupGetConfidence(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * Set the confidence value of this relation
	 * 
	 * @param from
	 * @param type
	 * @param to
	 */
	public void groupSetConfidence(IConcept from, IConceptRelationType type, IConcept to, float val, IProfile group);

	/**
	 * Change the confidence value of this relation by the given amount; return the
	 * overflow (i.e. how much it went over 1f or under 0f) or 0f if no overflow
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param delta
	 */
	public default float groupChangeConfidenceBy(IConcept from, IConceptRelationType type, IConcept to, float delta,
			IProfile group) {
		float con = this.groupGetConfidence(from, type, to, group);
		con += delta;
		if (con > 1f) {
			this.groupSetConfidence(from, type, to, 1f, group);
			return con - 1f;
		} else if (con < 0f) {
			this.groupSetConfidence(from, type, to, 0f, group);
			return con;
		}
		this.groupSetConfidence(from, type, to, con, group);
		return 0f;
	}

	/**
	 * Return an immutable view of the stored concept graph
	 * 
	 * @return
	 */
	public IRelationGraph<IGroupConceptNode, IConceptRelationType> getUnmappedConceptGraphView();

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getMappedConceptGraphView();

	/**
	 * Return the number of relations for the given group
	 * 
	 * @return
	 */
	int groupCountRelations(IProfile group);

	/**
	 * Remove a given group from knowing this relation and delete the relation if
	 * all groups forget it
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param group
	 * @return
	 */
	boolean groupRemoveRelationAndDelete(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * Remove a given group from knowing relations between these concepts and delete
	 * the relations if all groups forget it
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param group
	 * @return
	 */
	boolean groupRemoveAllRelationsAndDelete(IConcept from, IConcept to, IProfile group);

	/**
	 * Remove a given group from knowing relations between these concepts and delete
	 * the relations if all groups forget it
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param group
	 * @return
	 */
	boolean groupRemoveAllRelationsAndDelete(IConcept from, IConceptRelationType type, IProfile group);

	/**
	 * Adds a subnetwork of concepts to this knowledge base, only known to certain
	 * groups
	 * 
	 * @param graph
	 * @param groups
	 */
	void groupsLearnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph,
			Collection<? extends IProfile> groups);

	/**
	 * See {@link #isNot(IConcept, IConceptRelationType, IConcept)}. This version
	 * checks if the given group recognizes this property.
	 */
	boolean groupIsNot(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * See {@link #isOpposite(IConcept, IConceptRelationType, IConcept)}. This
	 * version checks if the given group recognizes this property.
	 */
	boolean groupIsOpposite(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * See {@link #is(IConcept, IConceptRelationType, IConcept)}. This version
	 * checks if the given group recognizes this property.
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param group
	 * @return
	 */
	boolean groupIs(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

	/**
	 * Returns an iterator of the outgoing edges from this concept, from the
	 * perspective of this Group
	 */
	Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> groupGetOutgoingEdges(IConcept from,
			IConceptRelationType type, IProfile group);

	/**
	 * Returns an iterator of the outgoing edges from this concept, from the
	 * perspective of this Group
	 */
	Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> groupGetOutgoingEdges(IConcept from, IProfile group);

	/**
	 * Equivalent to {@link #setOpposite(IConcept, IConceptRelationType, IConcept)}
	 * but for group
	 * 
	 * @param from
	 * @param type
	 * @param to
	 * @param group
	 */
	void groupSetOpposite(IConcept from, IConceptRelationType type, IConcept to, IProfile group);

}
