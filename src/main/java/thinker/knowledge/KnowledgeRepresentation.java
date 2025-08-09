package thinker.knowledge;

import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Functions;
import com.google.common.collect.Streams;

import _utilities.couplets.Triplet;
import _utilities.graph.EmptyGraph;
import _utilities.graph.IRelationGraph;
import party.relations.social_bonds.ISocialBondTrait;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.concepts.relations.util.RelationProperties;
import thinker.knowledge.node.ConceptNode;
import thinker.knowledge.node.IConceptNode;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;

/**
 * A general wrapper around a relation graph, as a representation of knowledge
 * 
 * @author borah
 *
 */
public class KnowledgeRepresentation implements IKnowledgeRepresentation {

	private ConceptNodeGraph<ConceptNode> graph;

	private ConceptNode n(IConcept c) {
		return new ConceptNode(c);
	}

	public KnowledgeRepresentation(IRelationGraph<IConcept, IConceptRelationType> graph) {
		this.graph = new ConceptNodeGraph<>(graph.mappedView(IConceptNode::getConcept, (a) -> new ConceptNode(a),
				Functions.identity(), Functions.identity()));
	}

	@Override
	public boolean knowsConcept(IConcept concept) {
		return graph.contains(n(concept));
	}

	@Override
	public StorageType getStorageType(IConcept concept) {
		IConceptNode no = graph.get(n(concept));
		if (no != null)
			return no.getStorageType();
		return null;
	}

	@Override
	public boolean learnConcept(IConcept concept) {
		return graph.add(n(concept));
	}

	@Override
	public boolean learnConcept(IConcept concept, StorageType type) {
		return graph.add(new ConceptNode(concept, type));
	}

	@Override
	public boolean forgetConcept(IConcept concept) {
		return graph.remove(n(concept));
	}

	@Override
	public int countConcepts() {
		return graph.size();
	}

	@Override
	public int countRelations(IConcept forConcept) {
		return graph.degree(n(forConcept));
	}

	@Override
	public int countRelationsOfType(IConcept forConcept, IConceptRelationType relation) {
		return graph.degree(n(forConcept), relation);
	}

	@Override
	public int countRelationsBetween(IConcept fromConcept, IConcept toConcept) {
		return graph.getEdgeTypesBetween(n(fromConcept), n(toConcept)).size();
	}

	@Override
	public int countConnectedConcepts(IConcept fromConcept) {

		return graph.getNeighbors(n(fromConcept)).size();
	}

	@Override
	public int countRelationTypesFrom(IConcept fromConcept) {
		return graph.getOutgoingEdgeTypes(n(fromConcept)).size();
	}

	@Override
	public boolean addConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		boolean rela = graph.addEdge(n(from), relation, n(to));
		graph.setProperty(n(from), relation, n(to), RelationProperties.STORAGE_TYPE, StorageType.CONFIDENT);
		return rela;
	}

	@Override
	public boolean addDubiousRelation(IConcept from, IConceptRelationType relation, IConcept to, float confidence) {
		boolean rela = graph.addEdge(n(from), relation, n(to));
		graph.setProperty(n(from), relation, n(to), RelationProperties.STORAGE_TYPE, StorageType.DUBIOUS);
		graph.setProperty(n(from), relation, n(to), RelationProperties.CONFIDENCE, confidence);
		return rela;
	}

	@Override
	public boolean addTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		boolean rela = graph.addEdge(n(from), relation, n(to));
		graph.setProperty(n(from), relation, n(to), RelationProperties.STORAGE_TYPE, StorageType.TEMPORARY);
		return rela;
	}

	@Override
	public boolean removeRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return graph.removeEdge(n(from), type, n(to));
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConcept to) {
		return graph.removeAllConnections(n(from), n(to));
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConceptRelationType type) {
		return graph.removeAllConnections(n(from), type);
	}

	@Override
	public boolean hasAnyValenceRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return graph.containsEdge(n(from), type, n(to));
	}

	@Override
	public boolean hasAnyValenceRelation(IConcept from, IConcept to) {
		return graph.containsEdge(n(from), n(to));
	}

	@Override
	public Set<? extends IConceptRelationType> getRelationTypesFrom(IConcept from) {
		return graph.getOutgoingEdgeTypes(n(from));
	}

	@Override
	public Iterable<? extends IConcept> getConnectedConcepts(IConcept from) {
		return () -> graph.getNeighbors(n(from)).stream().map(IConceptNode::getConcept).iterator();
	}

	@Override
	public Iterable<? extends IConcept> getConnectedConcepts(IConcept from, IConceptRelationType type) {
		return () -> graph.getNeighbors(n(from), type).stream().map(IConceptNode::getConcept).iterator();
	}

	@Override
	public Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdges(IConcept fromC) {
		return Streams.stream(graph.outgoingEdges(n(fromC)))
				.map((t) -> Triplet.of(t.getFirst().getConcept(), t.getSecond(), t.getThird().getConcept())).iterator();
	}

	@Override
	public Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdges(IConcept fromC,
			IConceptRelationType forRelation) {
		return Streams.stream(graph.outgoingEdges(n(fromC), forRelation))
				.map((t) -> Triplet.of(t.getFirst().getConcept(), t.getSecond(), t.getThird().getConcept())).iterator();

	}

	@Override
	public StorageType getStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return graph.getProperty(n(from), type, n(to), RelationProperties.STORAGE_TYPE);
	}

	@Override
	public boolean isNot(IConcept from, IConceptRelationType type, IConcept to) {
		if (!graph.containsEdge(n(from), type, n(to))) {
			return true;
		}
		return !(graph.getProperty(n(from), type, n(to), RelationProperties.NOT) == Boolean.FALSE);
	}

	@Override
	public boolean isOpposite(IConcept from, IConceptRelationType type, IConcept to) {
		if (!graph.containsEdge(n(from), type, n(to))) {
			return false;
		}
		return graph.getProperty(n(from), type, n(to), RelationProperties.OPPOSITE) == Boolean.TRUE;
	}

	@Override
	public void setStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype) {
		graph.setProperty(n(from), type, n(to), RelationProperties.STORAGE_TYPE, stype);
	}

	@Override
	public TruthType getTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return graph.getProperty(n(from), type, n(to), RelationProperties.TRUTH_TYPE);
	}

	@Override
	public void setTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, TruthType ttype) {
		graph.setProperty(n(from), type, n(to), RelationProperties.TRUTH_TYPE, ttype);
	}

	@Override
	public float getConfidence(IConcept from, IConceptRelationType type, IConcept to) {
		return graph.getProperty(n(from), type, n(to), RelationProperties.CONFIDENCE);
	}

	@Override
	public void setConfidence(IConcept from, IConceptRelationType type, IConcept to, float val) {
		graph.setProperty(n(from), type, n(to), RelationProperties.CONFIDENCE, val);
	}

	@Override
	public float getSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to) {
		return graph.getProperty(n(from), ProfileInterrelationType.HAS_SOCIAL_BOND_TO, n(to), trait);
	}

	@Override
	public void setSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to, float value) {
		graph.addEdge(n(from), ProfileInterrelationType.HAS_SOCIAL_BOND_TO, n(to));
		graph.setProperty(n(from), ProfileInterrelationType.HAS_SOCIAL_BOND_TO, n(to), trait, value);
	}

	@Override
	public void setOpposite(IConcept focus, IConceptRelationType hasTrait, IConcept key) {
		graph.addEdge(n(focus), hasTrait, n(key));
		graph.setProperty(n(focus), hasTrait, n(key), RelationProperties.OPPOSITE, true);
	}

	@Override
	public float getDistance(IConcept prf) {
		if (graph.containsEdge(n(prf), KnowledgeRelationType.EXISTS_IN, n(IConcept.ENVIRONMENT))) {
			return graph.getProperty(n(prf), KnowledgeRelationType.EXISTS_IN, n(IConcept.ENVIRONMENT),
					RelationProperties.DISTANCE);
		}
		return -1;
	}

	@Override
	public void setDistance(IConcept prf, float distance) {
		graph.add(n(IConcept.ENVIRONMENT));
		graph.addEdge(n(prf), KnowledgeRelationType.EXISTS_IN, n(IConcept.ENVIRONMENT));
		graph.setProperty(n(prf), KnowledgeRelationType.EXISTS_IN, n(IConcept.ENVIRONMENT), RelationProperties.DISTANCE,
				distance);
	}

	@Override
	public IRelationGraph<? extends IConceptNode, IConceptRelationType> getUnmappedConceptGraphView() {
		return graph.immutable();
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getMappedConceptGraphView() {
		return graph.mappedView(this::n, IConceptNode::getConcept, IConcept.class, IConceptNode.class,
				Functions.identity(), Functions.identity(), IConceptRelationType.class, IConceptRelationType.class);
	}

	@Override
	public void learnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph) {
		this.graph.addAll(graph.mappedView(IConceptNode::getConcept, this::n, IConceptNode.class, IConcept.class,
				Functions.identity(), Functions.identity(), IConceptRelationType.class, IConceptRelationType.class));
	}

	@Override
	public void addConceptNodeSubgraph(IRelationGraph<? extends IConceptNode, IConceptRelationType> graph) {
		this.graph.addAll((IRelationGraph) graph);
	}

	@Override
	public int countRelations() {
		return graph.edgeCount();
	}

	@Override
	public IKnowledgeRepresentation clone() {
		KnowledgeRepresentation rep = new KnowledgeRepresentation(EmptyGraph.instance());
		rep.graph = graph.deepCopy((a) -> new ConceptNode(a.getConcept(), a.getStorageType()));
		return rep;
	}

}
