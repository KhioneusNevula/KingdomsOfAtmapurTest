package thinker.knowledge.base.section;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Functions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import _utilities.couplets.Pair;
import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import _utilities.graph.ImmutableGraphView;
import party.relations.social_bonds.ISocialBondTrait;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.knowledge.base.noosphere.INoosphereKnowledgeBase;
import thinker.knowledge.node.GroupConceptNode;
import thinker.knowledge.node.IConceptNode;
import thinker.knowledge.node.IGroupConceptNode;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;

/**
 * Implementation of {@link ISectionKnowledgeBase}
 * 
 * @author borah
 *
 */
public class SectionKnowledgeBase implements ISectionKnowledgeBase {

	private INoosphereKnowledgeBase noosphere;
	private IProfile self;
	private Table<Pair<IConcept, IConcept>, ISocialBondTrait, Float> socialbonds = HashBasedTable.create();

	public SectionKnowledgeBase(IProfile self, INoosphereKnowledgeBase noosphere) {
		this.noosphere = noosphere;
		this.self = self;
		noosphere.groupLearnConcept(self, self);
	}

	@Override
	public INoosphereKnowledgeBase getNoosphere() {
		return noosphere;
	}

	@Override
	public IProfile getSelfConcept() {
		return self;
	}

	@Override
	public boolean knowsConcept(IConcept concept) {

		return noosphere.groupKnowsConcept(concept, self);
	}

	@Override
	public StorageType getStorageType(IConcept concept) {
		return noosphere.groupGetStorageType(concept, self);
	}

	@Override
	public boolean learnConcept(IConcept concept) {
		return noosphere.groupLearnConcept(concept, self);
	}

	@Override
	public boolean learnConcept(IConcept concept, StorageType type) {
		return noosphere.groupLearnConcept(concept, type, self);
	}

	@Override
	public boolean forgetConcept(IConcept concept) {
		return noosphere.groupForgetConcept(concept, self);
	}

	@Override
	public int countConcepts() {
		return noosphere.groupCountConcepts(self);
	}

	@Override
	public int countRelations(IConcept forConcept) {
		return noosphere.groupCountRelations(forConcept, self);
	}

	@Override
	public int countRelationsOfType(IConcept forConcept, IConceptRelationType relation) {
		return noosphere.groupCountRelationsOfType(forConcept, relation, self);
	}

	@Override
	public int countRelationsBetween(IConcept fromConcept, IConcept toConcept) {
		return noosphere.groupCountRelationsBetween(fromConcept, toConcept, self);
	}

	@Override
	public int countConnectedConcepts(IConcept fromConcept) {
		return noosphere.groupCountConnectedConcepts(fromConcept, self);
	}

	@Override
	public int countRelationTypesFrom(IConcept fromConcept) {
		return noosphere.groupCountRelationTypesFrom(fromConcept, self);
	}

	@Override
	public boolean addConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		return noosphere.groupAddConfidentRelation(from, relation, to, self);
	}

	@Override
	public boolean addDubiousRelation(IConcept from, IConceptRelationType relation, IConcept to, float confidence) {
		return noosphere.groupAddDubiousRelation(from, relation, to, confidence, self);
	}

	@Override
	public boolean addTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		return noosphere.groupAddTemporaryRelation(from, relation, to, self);
	}

	@Override
	public boolean removeRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupRemoveRelation(from, type, to, self);
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConcept to) {
		return noosphere.groupRemoveAllRelations(from, to, self);
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConceptRelationType type) {
		return noosphere.groupRemoveAllRelations(from, type, self);
	}

	@Override
	public boolean hasAnyValenceRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupHasAnyValenceRelation(from, type, to, self);
	}

	@Override
	public boolean hasAnyValenceRelation(IConcept from, IConcept to) {
		return noosphere.groupHasRelation(from, to, self);
	}

	@Override
	public Iterable<? extends IConceptRelationType> getRelationTypesFrom(IConcept from) {
		return noosphere.groupGetRelationTypesFrom(from, self);
	}

	@Override
	public Iterable<? extends IConcept> getConnectedConcepts(IConcept from) {
		return noosphere.groupGetConnectedConcepts(from, self);
	}

	@Override
	public Iterable<? extends IConcept> getConnectedConcepts(IConcept from, IConceptRelationType type) {
		return noosphere.groupGetConnectedConcepts(from, type, self);
	}

	@Override
	public Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdges(IConcept fromC) {
		return noosphere.groupGetOutgoingEdges(fromC, self);
	}

	@Override
	public Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdges(IConcept fromC,
			IConceptRelationType forRelation) {
		return noosphere.groupGetOutgoingEdges(fromC, forRelation, self);
	}

	@Override
	public StorageType getStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupGetStorageTypeOfRelation(from, type, to, self);
	}

	@Override
	public void setStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype) {
		noosphere.groupSetStorageTypeOfRelation(from, type, to, stype, self);
	}

	@Override
	public TruthType getTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupGetTruthTypeOfRelation(from, type, to, self);
	}

	@Override
	public boolean isNot(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupIsNot(from, type, to, self);
	}

	@Override
	public boolean isOpposite(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupIsOpposite(from, type, to, self);
	}

	@Override
	public void setTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, TruthType ttype) {
		noosphere.groupSetTruthTypeOfRelation(from, type, to, ttype, self);
	}

	@Override
	public UUID getInfoSource(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupGetInfoSource(from, type, to, self);
	}

	@Override
	public void setInfoSource(IConcept from, IConceptRelationType type, IConcept to, UUID source) {
		noosphere.groupSetInfoSource(from, type, to, source, self);
	}

	@Override
	public float getConfidence(IConcept from, IConceptRelationType type, IConcept to) {
		return noosphere.groupGetConfidence(from, type, to, self);
	}

	@Override
	public void setConfidence(IConcept from, IConceptRelationType type, IConcept to, float val) {
		noosphere.groupSetConfidence(from, type, to, val, self);
	}

	@Override
	public float getSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to) {
		Float f = this.socialbonds.get(Pair.of(from, to), trait);
		if (f == null)
			return trait.defaultValue();
		return f;
	}

	@Override
	public void setSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to, float value) {
		this.socialbonds.put(Pair.of(from, to), trait, value);
	}

	@Override
	public void setOpposite(IConcept focus, IConceptRelationType hasTrait, IConcept key) {
		this.noosphere.groupSetOpposite(focus, hasTrait, key, self);
	}

	@Override
	public IRelationGraph<IGroupConceptNode, IConceptRelationType> getUnmappedConceptGraphView() {
		return (this.noosphere.getUnmappedConceptGraphView()).subgraph((node) -> node.knownByGroup(self), (edge) -> this
				.hasAnyValenceRelation(edge.getFirst().getConcept(), edge.getSecond(), edge.getThird().getConcept()));
	}

	/**
	 * Returns an immutable view of the internal knowledge graph but mapped to only
	 * expose concepts
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getMappedConceptGraphView() {
		return ImmutableGraphView.of((getUnmappedConceptGraphView()).mappedView((a) -> new GroupConceptNode(a),
				IConceptNode::getConcept, Functions.identity(), Functions.identity()));
	}

	@Override
	public void addConceptNodeSubgraph(IRelationGraph<? extends IConceptNode, IConceptRelationType> graph) {
		this.noosphere.addConceptNodeSubgraph(graph);
	}

	@Override
	public void learnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph) {
		this.noosphere.groupsLearnConceptSubgraph(graph, Set.of(this.self));
	}

	@Override
	public int countRelations() {
		return noosphere.groupCountRelations(self);
	}

	@Override
	public IKnowledgeBase clone() {
		SectionKnowledgeBase noso = new SectionKnowledgeBase(self, noosphere);
		noso.socialbonds = HashBasedTable.create(this.socialbonds);
		return noso;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + (this.self) + ")" + "{" + this.noosphere.toString() + "}";
	}

}
