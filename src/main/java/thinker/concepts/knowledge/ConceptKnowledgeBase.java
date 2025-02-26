package thinker.concepts.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Functions;
import com.google.common.collect.Sets;

import thinker.IIndividualKnowledgeBase;
import thinker.IKnowledgeBase;
import thinker.concepts.IConcept;
import thinker.concepts.relations.ConceptRelationType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.RelationProperties;
import thinker.mind.memory.IMultiKnowledgeBaseIterator;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;
import thinker.mind.memory.node.ConceptNode;
import thinker.mind.memory.node.IConceptNode;
import thinker.social.relations.social_bond.ISocialBondTrait;
import utilities.graph.IRelationGraph;
import utilities.graph.ImmutableGraphView;

public class ConceptKnowledgeBase implements IIndividualKnowledgeBase {

	private ConceptGraph<IConceptNode> conceptGraph;
	private ImmutableGraphView<IConceptNode, IConceptRelationType> immutableView;
	private Set<IKnowledgeBase> parents = new HashSet<>();
	private IConcept self;

	public ConceptKnowledgeBase(IConcept self) {
		conceptGraph = new ConceptGraph<>();
		immutableView = ImmutableGraphView.of(conceptGraph);
		this.self = self;
		this.learnConcept(self);
	}

	@Override
	public IConcept getSelfConcept() {
		return self;
	}

	@Override
	public void addParents(Iterable<? extends IKnowledgeBase> parents) {
		this.parents.addAll(Sets.newHashSet(parents));
	}

	@Override
	public void removeParents(Iterable<? extends IKnowledgeBase> parents) {
		this.parents.removeAll(Sets.newHashSet(parents));
	}

	@Override
	public boolean knowsConcept(IConcept concept) {
		return conceptGraph.contains(new ConceptNode(concept));
	}

	@Override
	public IKnowledgeBase knowsConceptCheckParent(IConcept concept) {
		if (knowsConcept(concept))
			return this;
		for (IKnowledgeBase parent : this.parents) {
			if (parent instanceof IIndividualKnowledgeBase ikb) {
				IKnowledgeBase par = ikb.knowsConceptCheckParent(concept);
				if (par != null)
					return par;
			} else {
				if (parent.knowsConcept(concept)) {
					return parent;
				}
			}
		}
		return null;
	}

	@Override
	public StorageType getStorageType(IConcept concept) {
		IConceptNode nod = conceptGraph.get(new ConceptNode(concept));
		if (nod != null)
			return nod.getStorageType();
		return null;
	}

	@Override
	public boolean learnConcept(IConcept concept) {
		return conceptGraph.add(new ConceptNode(concept));
	}

	@Override
	public boolean learnConcept(IConcept concept, StorageType type) {
		return this.conceptGraph.add(new ConceptNode(concept, type));
	}

	@Override
	public boolean forgetConcept(IConcept concept) {
		if (concept.equals(self)) {
			throw new IllegalArgumentException("Cannot forget self");
			// TODO maybe you can forget yourself? what does that entail?
		}
		return this.conceptGraph.remove(new ConceptNode(concept));
	}

	@Override
	public int countConcepts() {
		return this.conceptGraph.size();
	}

	@Override
	public int countRelations() {
		return this.conceptGraph.edgeCount();
	}

	@Override
	public int countRelations(IConcept forConcept) {
		return this.conceptGraph.degree(new ConceptNode(forConcept));
	}

	@Override
	public int countRelationsOfType(IConcept forConcept, IConceptRelationType relation) {
		return this.conceptGraph.degree(new ConceptNode(forConcept), relation);
	}

	@Override
	public int countRelationsBetween(IConcept fromConcept, IConcept toConcept) {
		return this.conceptGraph.getEdgeTypesBetween(new ConceptNode(fromConcept), new ConceptNode(toConcept)).size();
	}

	@Override
	public int countConnectedConcepts(IConcept fromConcept) {
		return this.conceptGraph.getNeighbors(new ConceptNode(fromConcept)).size();
	}

	@Override
	public int countRelationTypesFrom(IConcept fromConcept) {
		return this.conceptGraph.getConnectingEdgeTypes(new ConceptNode(fromConcept)).size();
	}

	@Override
	public boolean addConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		ConceptNode fromN = new ConceptNode(from);
		ConceptNode toN = new ConceptNode(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE, StorageType.CONFIDENT);
		return added;
	}

	@Override
	public boolean addDubiousRelation(IConcept from, IConceptRelationType relation, IConcept to, float confidence) {
		if (confidence > 1f || confidence < 0f) {
			throw new IllegalArgumentException("Cannot make edge with " + confidence + " confidence between" + from
					+ " and " + to + " of type " + relation);
		}
		if (confidence == 1f) {
			return this.addConfidentRelation(from, relation, to);
		}
		ConceptNode fromN = new ConceptNode(from);
		ConceptNode toN = new ConceptNode(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE, StorageType.DUBIOUS);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.CONFIDENCE, confidence);
		return added;
	}

	@Override
	public boolean addTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		ConceptNode fromN = new ConceptNode(from);
		ConceptNode toN = new ConceptNode(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE, StorageType.TEMPORARY);
		return added;
	}

	@Override
	public boolean addUnknownRelation(IConcept from, IConcept to) {
		ConceptNode fromN = new ConceptNode(from);
		ConceptNode toN = new ConceptNode(to);
		boolean added = this.conceptGraph.addEdge(fromN, ConceptRelationType.UNKNOWN, toN);
		conceptGraph.setProperty(fromN, ConceptRelationType.UNKNOWN, toN, RelationProperties.STORAGE_TYPE,
				StorageType.UNKNOWN);
		return added;
	}

	@Override
	public boolean removeRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.removeEdge(new ConceptNode(from), type, new ConceptNode(to));
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConcept to) {
		return conceptGraph.removeAllConnections(new ConceptNode(from), new ConceptNode(to));
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConceptRelationType type) {
		return conceptGraph.removeAllConnections(new ConceptNode(from), type);
	}

	@Override
	public boolean hasRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.containsEdge(new ConceptNode(from), type, new ConceptNode(to));
	}

	@Override
	public IKnowledgeBase hasRelationCheckParent(IConcept from, IConceptRelationType type, IConcept to) {
		if (this.hasRelation(from, type, to))
			return this;
		for (IKnowledgeBase parent : this.parents) {
			if (parent instanceof IIndividualKnowledgeBase ikb) {
				IKnowledgeBase par = ikb.hasRelationCheckParent(from, type, to);
				if (par != null)
					return par;
			} else {
				if (parent.hasRelation(from, type, to)) {
					return parent;
				}
			}
		}
		return null;
	}

	@Override
	public boolean hasRelation(IConcept from, IConcept to) {
		return conceptGraph.containsEdge(new ConceptNode(from), new ConceptNode(to));
	}

	@Override
	public IKnowledgeBase hasRelationCheckParent(IConcept from, IConcept to) {
		if (this.hasRelation(from, to))
			return this;
		for (IKnowledgeBase parent : this.parents) {
			if (parent instanceof IIndividualKnowledgeBase ikb) {
				IKnowledgeBase par = ikb.hasRelationCheckParent(from, to);
				if (par != null)
					return par;
			} else {
				if (parent.hasRelation(from, to)) {
					return parent;
				}
			}
		}
		return null;
	}

	@Override
	public Collection<IConceptRelationType> getRelationTypesFrom(IConcept from) {
		return conceptGraph.getConnectingEdgeTypes(new ConceptNode(from));
	}

	@Override
	public IMultiKnowledgeBaseIterator<IConceptRelationType> getRelationTypesFromCheckParent(IConcept from) {
		return new ConceptStorageParentIterator<IConceptRelationType>(
				() -> conceptGraph.getConnectingEdgeTypes(new ConceptNode(from)).iterator(), (parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getRelationTypesFromCheckParent(from);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								parent.getRelationTypesFrom(from).iterator());
				});
	}

	@Override
	public Iterable<IConcept> getConnectedConcepts(IConcept from) {
		return () -> conceptGraph.getNeighbors(new ConceptNode(from)).stream().map(IConceptNode::getConcept).iterator();
	}

	@Override
	public IMultiKnowledgeBaseIterator<? extends IConcept> getConnectedConceptsCheckParent(IConcept from) {
		return new ConceptStorageParentIterator<>(() -> conceptGraph.getNeighbors(new ConceptNode(from)).stream()
				.map(IConceptNode::getConcept).iterator(), (parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getConnectedConceptsCheckParent(from);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								parent.getConnectedConcepts(from).iterator());
				});
	}

	@Override
	public Iterable<IConcept> getConnectedConcepts(IConcept from, IConceptRelationType type) {
		return () -> conceptGraph.getNeighbors(new ConceptNode(from), type).stream().map(IConceptNode::getConcept)
				.iterator();
	}

	@Override
	public IMultiKnowledgeBaseIterator<? extends IConcept> getConnectedConceptsCheckParent(IConcept from,
			IConceptRelationType type) {
		return new ConceptStorageParentIterator<>(() -> conceptGraph.getNeighbors(new ConceptNode(from), type).stream()
				.map(IConceptNode::getConcept).iterator(), (parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getConnectedConceptsCheckParent(from, type);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								parent.getConnectedConcepts(from, type).iterator());
				});
	}

	@Override
	public StorageType getStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to),
				RelationProperties.STORAGE_TYPE, false);
	}

	@Override
	public void setStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype) {
		conceptGraph.setProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.STORAGE_TYPE,
				stype);
	}

	@Override
	public TruthType getTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.TRUTH_TYPE,
				false);
	}

	@Override
	public Map<IKnowledgeBase, TruthType> getTruthTypeOfRelationCheckParents(IConcept from, IConceptRelationType type,
			IConcept to) {
		Map<IKnowledgeBase, TruthType> types = new HashMap<>();
		types.put(this, this.getTruthTypeOfRelation(from, type, to));
		for (IKnowledgeBase para : parents) {
			if (para instanceof IIndividualKnowledgeBase ikb)
				types.putAll(ikb.getTruthTypeOfRelationCheckParents(from, type, to));
			else
				types.put(para, para.getTruthTypeOfRelation(from, type, to));
		}
		return types;
	}

	@Override
	public void setTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, TruthType ttype) {
		conceptGraph.setProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.TRUTH_TYPE,
				ttype);
	}

	@Override
	public UUID getInfoSource(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to),
				RelationProperties.INFO_SOURCE, false);
	}

	@Override
	public void setInfoSource(IConcept from, IConceptRelationType type, IConcept to, UUID source) {
		conceptGraph.setProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.INFO_SOURCE,
				source);
	}

	@Override
	public float getConfidence(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.CONFIDENCE,
				false);
	}

	@Override
	public void setConfidence(IConcept from, IConceptRelationType type, IConcept to, float val) {
		conceptGraph.setProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.CONFIDENCE, val);
	}

	@Override
	public float getSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to) {
		return conceptGraph.getProperty(new ConceptNode(from), ConceptRelationType.KNOWS, new ConceptNode(to), trait,
				false);
	}

	@Override
	public Map<IKnowledgeBase, Float> getSocialBondValueCheckParents(IConcept from, ISocialBondTrait trait,
			IConcept to) {
		Map<IKnowledgeBase, Float> types = new HashMap<>();
		types.put(this, this.getSocialBondValue(from, trait, to));
		for (IKnowledgeBase para : parents) {
			if (para instanceof IIndividualKnowledgeBase ikb) {
				types.putAll(ikb.getSocialBondValueCheckParents(from, trait, to));
			} else {
				types.put(para, para.getSocialBondValue(from, trait, to));
			}
		}
		return types;
	}

	@Override
	public void setSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to, float value) {
		conceptGraph.setProperty(new ConceptNode(from), ConceptRelationType.KNOWS, new ConceptNode(to), trait, value);
	}

	@Override
	public IRelationGraph<IConceptNode, IConceptRelationType> getConceptGraphView() {
		return this.immutableView;
	}

	@Override
	public void addConceptNodeSubgraph(IRelationGraph<? extends IConceptNode, IConceptRelationType> graph) {
		conceptGraph.addAll(graph);
	}

	@Override
	public void learnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph) {
		conceptGraph.addAll(graph.mapCopy(ConceptNode::new, Functions.identity()));
	}

	@Override
	public Collection<? extends IKnowledgeBase> getParents() {
		return this.parents;
	}

	@Override
	public ConceptKnowledgeBase clone() {
		ConceptKnowledgeBase base;
		try {
			base = (ConceptKnowledgeBase) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		base.conceptGraph = this.conceptGraph.copy();
		base.immutableView = ImmutableGraphView.of(base.conceptGraph);
		base.parents = new HashSet<>(this.parents);
		return base;
	}

	private class SingleParentConceptStorageParentIterator<E> implements IMultiKnowledgeBaseIterator<E> {
		IKnowledgeBase parent;
		Iterator<? extends E> internalIterator;

		public SingleParentConceptStorageParentIterator(IKnowledgeBase parent, Iterator<? extends E> internalIter) {
			this.parent = parent;
			this.internalIterator = internalIter;
		}

		@Override
		public boolean hasNext() {
			return internalIterator.hasNext();
		}

		@Override
		public IKnowledgeBase justReturnedStorage() {
			return parent;
		}

		@Override
		public E next() {
			return internalIterator.next();
		}
	}

	private class ConceptStorageParentIterator<E> implements IMultiKnowledgeBaseIterator<E> {

		Iterator<IKnowledgeBase> parentAccessorIterator = parents.iterator();
		Function<IKnowledgeBase, IMultiKnowledgeBaseIterator<? extends E>> parentAccessor;
		IMultiKnowledgeBaseIterator<? extends E> currentParentIterator = this;
		Iterator<? extends E> currentIterator = null;

		ConceptStorageParentIterator(Supplier<Iterator<? extends E>> accessor,
				Function<IKnowledgeBase, IMultiKnowledgeBaseIterator<? extends E>> accessor2) {
			this.currentIterator = accessor.get();
			this.parentAccessor = accessor2;
			this.iterateUntilAvailable();
		}

		private void iterateUntilAvailable() {
			boolean hasNext = false;
			if (currentIterator != null && currentIterator.hasNext()) // if the currentIterator (of this storage) has a
																		// next item, stop
				return;
			while (!hasNext) {
				currentIterator = null;
				if (currentParentIterator != null && currentParentIterator.hasNext()) // if we have a current parent
																						// iterator and it has a next
																						// item, stop
					return;
				if (!parentAccessorIterator.hasNext()) { // move on to accessing next parent storage
					currentParentIterator = null;
					break;
				}
				currentParentIterator = parentAccessor.apply(parentAccessorIterator.next()); // get the next
																								// currentParentIterator
				hasNext = currentParentIterator.hasNext();
			}
		}

		@Override
		public boolean hasNext() {
			if (currentIterator != null) {
				return currentIterator.hasNext();
			} else if (currentParentIterator != null) {
				return currentParentIterator.hasNext();
			}
			return false;
		}

		@Override
		public E next() {
			this.iterateUntilAvailable();
			if (currentIterator != null)
				return currentIterator.next();
			if (currentParentIterator != null)
				return currentParentIterator.next();
			throw new NoSuchElementException();
		}

		@Override
		public IKnowledgeBase justReturnedStorage() {
			return currentIterator != null ? ConceptKnowledgeBase.this : currentParentIterator.justReturnedStorage();
		}
	}

}
