package thinker.knowledge.base.individual;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Functions;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

import _utilities.collections.CollectionFromIterator;
import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import _utilities.graph.ImmutableGraphView;
import party.relations.social_bonds.ISocialBondTrait;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.util.RelationProperties;
import thinker.knowledge.ConceptNodeGraph;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.knowledge.node.ConceptNode;
import thinker.knowledge.node.IConceptNode;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;

public class IndividualKnowledgeBase implements IIndividualKnowledgeBase {

	protected ConceptNodeGraph<IConceptNode> conceptGraph;
	private ImmutableGraphView<IConceptNode, IConceptRelationType> immutableView;
	protected Set<IKnowledgeBase> parents = new HashSet<>();
	private IConcept self;

	public IndividualKnowledgeBase(IConcept self) {
		conceptGraph = new ConceptNodeGraph<>();
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
		IConceptNode cnode = this.conceptGraph.get(new ConceptNode(concept));
		if (cnode != null && cnode.getStorageType() == StorageType.FORGOTTEN) {
			return false;
		}
		return this.knowsConceptCheckParent(concept) != null;
	}

	@Override
	public IKnowledgeBase knowsConceptCheckParent(IConcept concept) {
		if (this.conceptGraph.contains(new ConceptNode(concept))) {
			if (this.conceptGraph.get(new ConceptNode(concept)).getStorageType() != StorageType.FORGOTTEN) {
				return this;
			}
		}
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
		return learnConcept(concept, StorageType.CONFIDENT);
	}

	@Override
	public boolean learnConcept(IConcept concept, StorageType type) {
		IConceptNode connode = conceptGraph.get(new ConceptNode(concept));
		if (connode != null) {
			if (connode.getStorageType() != type) {
				conceptGraph.set(connode, new ConceptNode(concept));
				return true;
			} else {
				return false;
			}
		}
		return conceptGraph.add(new ConceptNode(concept));
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
		return this.conceptGraph.getOutgoingEdgeTypes(new ConceptNode(fromConcept)).size();
	}

	@Override
	public boolean addConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		ConceptNode fromN = new ConceptNode(from);
		ConceptNode toN = new ConceptNode(to);
		this.conceptGraph.addEdge(fromN, relation, toN);
		return conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE,
				StorageType.CONFIDENT) == StorageType.CONFIDENT;
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
		this.conceptGraph.addEdge(fromN, relation, toN);
		StorageType stype = conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE,
				StorageType.DUBIOUS);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.CONFIDENCE, confidence);
		return stype == StorageType.DUBIOUS;
	}

	@Override
	public boolean addTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		ConceptNode fromN = new ConceptNode(from);
		ConceptNode toN = new ConceptNode(to);
		this.conceptGraph.addEdge(fromN, relation, toN);
		StorageType stype = conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE,
				StorageType.TEMPORARY);
		return stype == StorageType.TEMPORARY;
	}

	@Override
	public boolean forgetConcept(IConcept concept) {
		if (concept.equals(self)) {
			throw new IllegalArgumentException("Cannot forget self");
			// TODO maybe you can forget yourself? what does that entail?
		}
		if (!this.conceptGraph.remove(new ConceptNode(concept))) { // if it's not in local memory
			return this.conceptGraph.add(new ConceptNode(concept, StorageType.FORGOTTEN));
		}
		return true;
	}

	@Override
	public boolean removeRelation(IConcept from, IConceptRelationType type, IConcept to) {
		if (this.hasRelationCheckParent(from, type, to) != this) {
			this.conceptGraph.addEdge(new ConceptNode(from), type, new ConceptNode(to));
			if (this.getStorageTypeOfRelation(from, type, to) == StorageType.FORGOTTEN) {
				return false;
			}
			this.setStorageTypeOfRelation(from, type, to, StorageType.FORGOTTEN);
			return true;
		} else {
			if (this.hasRelation(from, type, to)
					&& this.getStorageTypeOfRelation(from, type, to) == StorageType.FORGOTTEN) {
				return false;
			}
			return conceptGraph.removeEdge(new ConceptNode(from), type, new ConceptNode(to));
		}
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConcept to) {
		boolean answer = false;
		for (IConceptRelationType edge : conceptGraph.getEdgeTypesBetween(new ConceptNode(from), new ConceptNode(to))) {
			answer = answer || this.removeRelation(from, edge, to);
		}
		return answer;
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConceptRelationType type) {
		boolean answer = false;
		for (IConceptNode to : conceptGraph.getNeighbors(new ConceptNode(from), type)) {
			answer = answer || this.removeRelation(from, type, to.getConcept());
		}
		return answer;
	}

	@Override
	public boolean hasRelation(IConcept from, IConceptRelationType type, IConcept to) {
		if (conceptGraph.containsEdge(new ConceptNode(from), type, new ConceptNode(to))
				&& conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to),
						RelationProperties.STORAGE_TYPE) == StorageType.FORGOTTEN) {
			return false;
		}
		return hasRelationCheckParent(from, type, to) != null;
	}

	@Override
	public IKnowledgeBase hasRelationCheckParent(IConcept from, IConceptRelationType type, IConcept to) {
		if (this.conceptGraph.containsEdge(from, type, to))
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
		if (conceptGraph.containsEdge(new ConceptNode(from), new ConceptNode(to))) {
			boolean foundx = false;
			for (IConceptRelationType type : conceptGraph.getEdgeTypesBetween(new ConceptNode(from),
					new ConceptNode(to))) {
				if (conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to),
						RelationProperties.STORAGE_TYPE) != StorageType.FORGOTTEN) {
					foundx = true;
					break;
				}
			}
			if (!foundx) {
				return false;
			}
		}
		return hasRelationCheckParent(from, to) != null;
	}

	@Override
	public IKnowledgeBase hasRelationCheckParent(IConcept from, IConcept to) {
		if (this.conceptGraph.containsEdge(from, to))
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
		return new CollectionFromIterator<>(() -> getRelationTypesFromCheckParent(from));
	}

	@Override
	public IMultiKnowledgeBaseIterator<IConceptRelationType> getRelationTypesFromCheckParent(IConcept from) {
		return new ConceptStorageParentIterator<IConceptRelationType>(
				() -> conceptGraph.getOutgoingEdgeTypes(new ConceptNode(from)).iterator(), (parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getRelationTypesFromCheckParent(from);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								Streams.stream(parent.getOutgoingEdges(from))
										.filter((trip) -> this.getStorageTypeOfRelation(trip.getFirst(),
												trip.getSecond(), trip.getThird()) != StorageType.FORGOTTEN)
										.map((a) -> a.center()).distinct().iterator());
				});
	}

	@Override
	public Iterable<IConcept> getConnectedConcepts(IConcept from) {
		return () -> getConnectedConceptsCheckParent(from);
	}

	@Override
	public IMultiKnowledgeBaseIterator<IConcept> getConnectedConceptsCheckParent(IConcept from) {
		return new ConceptStorageParentIterator<>(() -> conceptGraph.getNeighbors(new ConceptNode(from)).stream()
				.map(IConceptNode::getConcept).iterator(), (parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getConnectedConceptsCheckParent(from);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								Streams.stream(parent.getConnectedConcepts(from))
										.filter((a) -> this.getStorageType(a) != StorageType.FORGOTTEN).iterator());
				});
	}

	@Override
	public Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdges(IConcept fromC) {
		return getOutgoingEdgesCheckParent(fromC);
	}

	@Override
	public Iterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdges(IConcept fromC,
			IConceptRelationType forRelation) {
		return getOutgoingEdgesCheckParent(fromC, forRelation);
	}

	@Override
	public IMultiKnowledgeBaseIterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdgesCheckParent(
			IConcept from, IConceptRelationType type) {
		return new ConceptStorageParentIterator<>(
				() -> Streams.stream(conceptGraph.outgoingEdges(new ConceptNode(from), type))
						.map((e) -> Triplet.of(e.left().getConcept(), e.center(), e.right().getConcept())).iterator(),
				(parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getOutgoingEdgesCheckParent(from);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								parent.getMappedConceptGraphView().outgoingEdges(from));
				});
	}

	@Override
	public IMultiKnowledgeBaseIterator<Triplet<IConcept, IConceptRelationType, IConcept>> getOutgoingEdgesCheckParent(
			IConcept from) {
		return new ConceptStorageParentIterator<>(
				() -> Streams.stream(conceptGraph.outgoingEdges(new ConceptNode(from)))
						.map((e) -> Triplet.of(e.left().getConcept(), e.center(), e.right().getConcept())).iterator(),
				(parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getOutgoingEdgesCheckParent(from);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								Streams.stream(parent.getMappedConceptGraphView().outgoingEdges(from))
										.filter((trip) -> this.getStorageTypeOfRelation(trip.getFirst(),
												trip.getSecond(), trip.getThird()) != StorageType.FORGOTTEN)
										.iterator());
				});
	}

	@Override
	public Iterable<IConcept> getConnectedConcepts(IConcept from, IConceptRelationType type) {
		return () -> getConnectedConceptsCheckParent(from, type);
	}

	@Override
	public IMultiKnowledgeBaseIterator<IConcept> getConnectedConceptsCheckParent(IConcept from,
			IConceptRelationType type) {
		return new ConceptStorageParentIterator<>(() -> conceptGraph.getNeighbors(new ConceptNode(from), type).stream()
				.map(IConceptNode::getConcept).iterator(), (parent) -> {
					if (parent instanceof IIndividualKnowledgeBase ikb)
						return ikb.getConnectedConceptsCheckParent(from, type);
					else
						return new SingleParentConceptStorageParentIterator<>(parent,
								Streams.stream(parent.getConnectedConcepts(from, type))
										.filter((a) -> this.getStorageType(a) != StorageType.FORGOTTEN).iterator());
				});
	}

	@Override
	public StorageType getStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to),
				RelationProperties.STORAGE_TYPE, false);
	}

	@Override
	public boolean isOpposite(IConcept from, IConceptRelationType type, IConcept to) {
		return !isOppositeCheckParents(from, type, to).isEmpty();
	}

	@Override
	public Set<IKnowledgeBase> isOppositeCheckParents(IConcept from, IConceptRelationType type, IConcept to) {
		Set<IKnowledgeBase> types = new HashSet<>();

		if (conceptGraph.containsEdge(new ConceptNode(from), type, new ConceptNode(to))) {
			if (conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to),
					RelationProperties.OPPOSITE)) {
				types.add(this);
			}
		}
		for (IKnowledgeBase para : parents) {
			if (para instanceof IIndividualKnowledgeBase ikb)
				types.addAll(ikb.isNotCheckParents(from, type, to));
			else {
				if (para.isOpposite(from, type, to)) {
					types.add(para);
				}
			}
		}
		return types;
	}

	@Override
	public boolean isNot(IConcept from, IConceptRelationType type, IConcept to) {
		return !isNotCheckParents(from, type, to).isEmpty();
	}

	@Override
	public Set<IKnowledgeBase> isNotCheckParents(IConcept from, IConceptRelationType type, IConcept to) {
		Set<IKnowledgeBase> types = new HashSet<>();
		if (conceptGraph.containsEdge(new ConceptNode(from), type, new ConceptNode(to))) {
			if (conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.NOT)) {
				types.add(this);
			}
		}
		for (IKnowledgeBase para : parents) {
			if (para instanceof IIndividualKnowledgeBase ikb)
				types.addAll(ikb.isNotCheckParents(from, type, to));
			else {
				if (para.isNot(from, type, to)) {
					types.add(para);
				}
			}
		}
		return types;
	}

	@Override
	public void setStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype) {
		conceptGraph.setProperty(new ConceptNode(from), type, new ConceptNode(to), RelationProperties.STORAGE_TYPE,
				stype);
	}

	@Override
	public TruthType getTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return this.getTruthTypeOfRelationCheckParents(from, type, to).values().stream().findFirst()
				.orElse(RelationProperties.TRUTH_TYPE.defaultValue());
	}

	@Override
	public Map<IKnowledgeBase, TruthType> getTruthTypeOfRelationCheckParents(IConcept from, IConceptRelationType type,
			IConcept to) {
		Map<IKnowledgeBase, TruthType> types = new LinkedHashMap<>();
		types.put(this, conceptGraph.getProperty(new ConceptNode(from), type, new ConceptNode(to),
				RelationProperties.TRUTH_TYPE, false));
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
		return getSocialBondValueCheckParents(from, trait, to).values().stream().findFirst()
				.orElse(trait.defaultValue());
	}

	@Override
	public Map<IKnowledgeBase, Float> getSocialBondValueCheckParents(IConcept from, ISocialBondTrait trait,
			IConcept to) {
		Map<IKnowledgeBase, Float> types = new LinkedHashMap<>();
		types.put(this, conceptGraph.getProperty(new ConceptNode(from), ProfileInterrelationType.HAS_SOCIAL_BOND_TO,
				new ConceptNode(to), trait, false));
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
		conceptGraph.addEdge(new ConceptNode(from), ProfileInterrelationType.HAS_SOCIAL_BOND_TO, new ConceptNode(to));
		conceptGraph.setProperty(new ConceptNode(from), ProfileInterrelationType.HAS_SOCIAL_BOND_TO,
				new ConceptNode(to), trait, value);
	}

	@Override
	public IRelationGraph<IConceptNode, IConceptRelationType> getUnmappedConceptGraphView() {
		return this.immutableView;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getMappedConceptGraphView() {
		return ImmutableGraphView
				.of(((IRelationGraph<IConceptNode, IConceptRelationType>) getUnmappedConceptGraphView()).mappedView(
						(a) -> new ConceptNode(a), IConceptNode::getConcept, Functions.identity(),
						Functions.identity()));
	}

	@Override
	public void addConceptNodeSubgraph(IRelationGraph<? extends IConceptNode, IConceptRelationType> graph) {
		conceptGraph.addAll((IRelationGraph) graph);
		graph.edgeIterator().forEachRemaining((trip) -> {
			if (conceptGraph.getProperty(trip.getFirst(), trip.getSecond(), trip.getThird(),
					RelationProperties.STORAGE_TYPE) == StorageType.FORGOTTEN) {
				conceptGraph.setProperty(trip.getFirst(), trip.getSecond(), trip.getThird(),
						RelationProperties.STORAGE_TYPE,
						(StorageType) ((IRelationGraph) graph).getProperty(trip.getFirst(), trip.getSecond(),
								trip.getThird(), RelationProperties.STORAGE_TYPE));
			}
		});
	}

	@Override
	public void learnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph) {
		conceptGraph.addAll(graph.mapCopy(ConceptNode::new, Functions.identity()));
		graph.edgeIterator().forEachRemaining((trip) -> {
			if (getStorageTypeOfRelation(trip.getFirst(), trip.getSecond(), trip.getThird()) == StorageType.FORGOTTEN) {
				setStorageTypeOfRelation(trip.getFirst(), trip.getSecond(), trip.getThird(),
						(StorageType) graph.getProperty(trip.getFirst(), trip.getSecond(), trip.getThird(),
								RelationProperties.STORAGE_TYPE));
			}
		});
	}

	@Override
	public Collection<? extends IKnowledgeBase> getParents() {
		return this.parents;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{graph=" + this.conceptGraph.representation() + ",parents="
				+ this.parents + "}";
	}

	@Override
	public IndividualKnowledgeBase clone() {
		IndividualKnowledgeBase base;
		try {
			base = (IndividualKnowledgeBase) super.clone();
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
		IMultiKnowledgeBaseIterator<? extends E> currentParentIterator = null;
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
			return currentIterator != null ? IndividualKnowledgeBase.this : currentParentIterator.justReturnedStorage();
		}
	}

}
