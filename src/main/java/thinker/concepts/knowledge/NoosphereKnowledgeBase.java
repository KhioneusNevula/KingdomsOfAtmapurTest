package thinker.concepts.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import _utilities.couplets.Pair;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.ImmutableGraphView;
import _utilities.graph.NodeNotFoundException;
import _utilities.property.IProperty;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.general_types.ILogicConcept.LogicType;
import thinker.concepts.general_types.IProfile;
import thinker.concepts.relations.ConceptRelationType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.RelationProperties;
import thinker.mind.memory.StorageType;
import thinker.mind.memory.TruthType;
import thinker.mind.memory.node.ConceptNode;
import thinker.mind.memory.node.IConceptNode;
import thinker.social.relations.social_bond.ISocialBondTrait;

/**
 * The sum of all knowledge.
 * 
 * @author borah
 *
 */
public class NoosphereKnowledgeBase implements INoosphereKnowledgeBase {

	private ConceptGraph<GroupConceptNode> conceptGraph;
	private ImmutableGraphView<GroupConceptNode, IConceptRelationType> immutableView;
	/**
	 * all contained groups and the number of (1) nodes and (2) relations each has
	 */
	private Map<IProfile, Pair<Integer, Integer>> containedGroups;

	public NoosphereKnowledgeBase() {
		conceptGraph = new ConceptGraph<GroupConceptNode>(Set.of(GROUP_BASED_PROPERTIES, REL_GROUPS));
		immutableView = ImmutableGraphView.of(conceptGraph);
		containedGroups = new HashMap<>();
		this.learnConcept(IConcept.EXISTENCE);
	}

	protected IGroupConceptNode getOrDefault(IConcept forConcept) {
		IGroupConceptNode noda = conceptGraph.get(new GroupConceptNode(forConcept));
		if (noda == null)
			return IGroupConceptNode.NULL;
		return noda;
	}

	@Override
	public Set<IProfile> allGroups() {
		return this.containedGroups.keySet();
	}

	@Override
	public Iterable<IConcept> getUnknownConcepts() {
		return () -> this.conceptGraph.stream().filter(IGroupConceptNode::unknownToAll)
				.map(IGroupConceptNode::getConcept).iterator();
	}

	@Override
	public void deleteUnknownConcepts() {
		this.conceptGraph.removeIf(IGroupConceptNode::unknownToAll);
	}

	@Override
	public Collection<IConcept> deleteUnknownConceptsAndReturn() {
		Set<GroupConceptNode> con = Sets
				.newHashSet(this.conceptGraph.stream().filter(IGroupConceptNode::unknownToAll).iterator());
		conceptGraph.removeAll(con);
		return Sets.newHashSet(con.stream().map(IGroupConceptNode::getConcept).iterator());
	}

	@Override
	public boolean groupKnowsConcept(IConcept con, IProfile group) {
		if (knowsConcept(con)) {
			return conceptGraph.get(new GroupConceptNode(con)).knownByGroup(group);
		}
		return false;
	}

	@Override
	public boolean knowsConcept(IConcept concept) {
		return conceptGraph.contains(new GroupConceptNode(concept));
	}

	@Override
	public Set<IProfile> groupsThatKnow(IConcept concept) {
		return getOrDefault(concept).knownByGroups();
	}

	@Override
	public StorageType getStorageType(IConcept concept) {
		IGroupConceptNode nod = conceptGraph.get(new GroupConceptNode(concept));
		if (nod != null)
			return nod.getStorageType();
		return null;
	}

	@Override
	public StorageType groupGetStorageType(IConcept concept, IProfile forGroup) {
		IGroupConceptNode nod = conceptGraph.get(new GroupConceptNode(concept));
		if (nod != null)
			return nod.getStorageTypeForGroup(forGroup);
		return null;
	}

	@Override
	public boolean learnConcept(IConcept concept) {
		return conceptGraph.add(new GroupConceptNode(concept));

	}

	protected void setGroupNodeCount(IProfile grou, int to) {
		Pair<Integer, Integer> pair = this.containedGroups.computeIfAbsent(grou, (prof) -> Pair.of(0, 0));
		pair.setFirst(to);
	}

	protected void setGroupEdgeCount(IProfile grou, int to) {
		Pair<Integer, Integer> pair = this.containedGroups.computeIfAbsent(grou, (prof) -> Pair.of(0, 0));
		pair.setSecond(to);
	}

	protected void changeGroupNodeCount(IProfile grou, int by) {
		Pair<Integer, Integer> pair = this.containedGroups.computeIfAbsent(grou, (prof) -> Pair.of(0, 0));
		pair.setFirst(pair.getFirst() + by);
		if (pair.getFirst() == 0) {
			this.containedGroups.remove(grou);
		}

	}

	protected void changeGroupEdgeCount(IProfile grou, int by) {
		Pair<Integer, Integer> pair = this.containedGroups.computeIfAbsent(grou, (prof) -> Pair.of(0, 0));
		pair.setSecond(pair.getSecond() + by);
	}

	protected static final Pair<Integer, Integer> ZERO = Pair.of(0, 0);

	protected int getGroupNodeCount(IProfile grou) {
		return this.containedGroups.getOrDefault(grou, ZERO).getFirst();
	}

	protected int getGroupEdgeCount(IProfile grou) {
		return this.containedGroups.getOrDefault(grou, ZERO).getSecond();
	}

	@Override
	public boolean groupLearnConcept(IConcept concept, IProfile forGroup) {

		GroupConceptNode con = new GroupConceptNode(concept);
		if (conceptGraph.contains(con)) {
			con = conceptGraph.get(con);
		} else {
			conceptGraph.add(con);
		}
		if (con.addToGroup(forGroup)) {
			this.changeGroupNodeCount(forGroup, 1);
			return true;
		}
		return false;
	}

	@Override
	public boolean learnConcept(IConcept concept, StorageType type) {
		return this.conceptGraph.add(new GroupConceptNode(concept, type));
	}

	@Override
	public boolean groupLearnConcept(IConcept concept, StorageType type, IProfile forGroup) {

		GroupConceptNode con = new GroupConceptNode(concept);
		if (conceptGraph.contains(con)) {
			con = conceptGraph.get(con);
		} else {
			conceptGraph.add(con);
		}
		con.setStorageTypeForGroup(forGroup, type);
		if (con.addToGroup(forGroup)) {

			this.changeGroupNodeCount(forGroup, 1);
			return true;
		}
		return false;
	}

	@Override
	public boolean forgetConcept(IConcept concept) {
		if (concept.equals(this.getSelfConcept())) {
			throw new IllegalArgumentException("Cannot forget self");
		}
		for (IProfile group : this.containedGroups.keySet()) {
			if (group.equals(concept)) {

				throw new IllegalArgumentException("Cannot forget an internal group: " + group);
			}
		}
		return this.conceptGraph.remove(new GroupConceptNode(concept));
	}

	@Override
	public boolean groupForgetConcept(IConcept concept, IProfile group) {
		if (concept.equals(this.getSelfConcept())) {
			throw new IllegalArgumentException("Cannot forget self");
		}
		if (concept.equals(group)) {
			throw new IllegalArgumentException("Group cannot forget itself");
		}
		boolean rem = getOrDefault(concept).removeFromGroup(group);
		if (rem)
			this.changeGroupNodeCount(group, -1);
		return rem;
	}

	@Override
	public boolean groupForgetConceptAndDelete(IConcept concept, IProfile group) {
		if (concept.equals(this.getSelfConcept())) {
			throw new IllegalArgumentException("Cannot forget self");
		}
		if (concept.equals(group)) {
			throw new IllegalArgumentException("Group cannot forget itself");
		}
		IGroupConceptNode noda = getOrDefault(concept);
		boolean doa = noda.removeFromGroup(group);
		if (doa)
			this.changeGroupNodeCount(group, -1);
		if (noda.knownByGroups().isEmpty())
			this.conceptGraph.remove(noda);
		return doa;
	}

	@Override
	public int countConcepts() {
		return this.conceptGraph.size();
	}

	@Override
	public int groupCountConcepts(IProfile forGroup) {
		return this.getGroupNodeCount(forGroup);
	}

	@Override
	public int countRelations() {
		return this.conceptGraph.edgeCount();
	}

	@Override
	public int groupCountRelations(IProfile forGroup) {
		return this.getGroupEdgeCount(forGroup);
	}

	@Override
	public int countRelations(IConcept forConcept) {
		return this.conceptGraph.degree(new GroupConceptNode(forConcept));
	}

	@Override
	public int groupCountRelations(IConcept forConcept, IProfile forGroup) {
		int[] num = { 0 };
		this.conceptGraph.forEachEdgeProperty(new GroupConceptNode(forConcept), REL_GROUPS, (set) -> {
			if (set != null && set.contains(forGroup))
				num[0]++;
		});
		return num[0];
	}

	@Override
	public int countRelationsOfType(IConcept forConcept, IConceptRelationType relation) {
		return this.conceptGraph.degree(new GroupConceptNode(forConcept), relation);
	}

	@Override
	public int groupCountRelationsOfType(IConcept forConcept, IConceptRelationType relation, IProfile forGroup) {
		int[] num = { 0 };
		this.conceptGraph.forEachEdgeProperty(new GroupConceptNode(forConcept), relation, REL_GROUPS, (set) -> {
			if (set != null && set.contains(forGroup))
				num[0]++;
		});
		return num[0];
	}

	@Override
	public int countRelationsBetween(IConcept fromConcept, IConcept toConcept) {
		return this.conceptGraph.getEdgeTypesBetween(new GroupConceptNode(fromConcept), new GroupConceptNode(toConcept))
				.size();
	}

	@Override
	public int groupCountRelationsBetween(IConcept forConcept, IConcept relation, IProfile forGroup) {
		int[] num = { 0 };
		this.conceptGraph.forEachEdgeProperty(new GroupConceptNode(forConcept), new GroupConceptNode(relation),
				REL_GROUPS, (set) -> {
					if (set != null && set.contains(forGroup))
						num[0]++;
				});
		return num[0];
	}

	@Override
	public int countRelationTypesFrom(IConcept fromConcept) {
		return this.conceptGraph.getConnectingEdgeTypes(new GroupConceptNode(fromConcept)).size();
	}

	@Override
	public int groupCountRelationTypesFrom(IConcept fromConcept, IProfile forGroup) {

		GroupConceptNode noda = new GroupConceptNode(fromConcept);
		return (int) this.conceptGraph.getConnectingEdgeTypes(noda).stream().filter((type) -> {
			return conceptGraph.getNeighbors(noda, type).stream().anyMatch((nodeto) -> {
				Set<IProfile> gros = conceptGraph.getProperty(noda, type, nodeto, REL_GROUPS);
				return gros != null && gros.contains(forGroup);
			});
		}).count();
	}

	@Override
	public int countConnectedConcepts(IConcept fromConcept) {
		return this.conceptGraph.getNeighbors(new GroupConceptNode(fromConcept)).size();
	}

	@Override
	public int groupCountConnectedConcepts(IConcept fromConcept, IProfile group) {
		GroupConceptNode noda = new GroupConceptNode(fromConcept);

		return (int) this.conceptGraph.getNeighbors(noda).stream().filter((no) -> no.knownByGroup(group))
				.filter((no) -> conceptGraph.getEdgeTypesBetween(noda, no).stream().anyMatch((type) -> {
					Set<IProfile> gros = conceptGraph.getProperty(noda, type, no, REL_GROUPS, false);
					return (gros != null && gros.contains(group));
				})).count();
	}

	@Override
	public boolean addConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);

		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		return added;
	}

	@Override
	public boolean groupAddConfidentRelation(IConcept from, IConceptRelationType relation, IConcept to,
			IProfile group) {
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);

		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.getProperty(fromN, relation, toN, REL_GROUPS, true).add(group);
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
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE, StorageType.DUBIOUS);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.CONFIDENCE, confidence);
		return added;
	}

	@Override
	public boolean groupAddDubiousRelation(IConcept from, IConceptRelationType relation, IConcept to, float confidence,
			IProfile group) {
		if (confidence > 1f || confidence < 0f) {
			throw new IllegalArgumentException("Cannot make edge with " + confidence + " confidence between" + from
					+ " and " + to + " of type " + relation);
		}
		if (confidence == 1f) {
			return this.groupAddConfidentRelation(from, relation, to, group);
		}
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);

		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.getProperty(fromN, relation, toN, REL_GROUPS, true).add(group);
		conceptGraph.getProperty(fromN, relation, toN, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.STORAGE_TYPE, StorageType.DUBIOUS);
		conceptGraph.getProperty(fromN, relation, toN, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.CONFIDENCE, confidence);
		return added;
	}

	@Override
	public boolean addTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to) {
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.setProperty(fromN, relation, toN, RelationProperties.STORAGE_TYPE, StorageType.TEMPORARY);
		return added;
	}

	@Override
	public boolean groupAddTemporaryRelation(IConcept from, IConceptRelationType relation, IConcept to,
			IProfile group) {
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);

		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		boolean added = this.conceptGraph.addEdge(fromN, relation, toN);
		conceptGraph.getProperty(fromN, relation, toN, REL_GROUPS, true).add(group);
		conceptGraph.getProperty(fromN, relation, toN, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.STORAGE_TYPE, StorageType.TEMPORARY);
		return added;
	}

	@Override
	public boolean addUnknownRelation(IConcept from, IConcept to) {
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);

		boolean added = this.conceptGraph.addEdge(fromN, ConceptRelationType.UNKNOWN, toN);
		conceptGraph.setProperty(fromN, ConceptRelationType.UNKNOWN, toN, RelationProperties.STORAGE_TYPE,
				StorageType.UNKNOWN);
		return added;
	}

	@Override
	public boolean groupAddUnknownRelation(IConcept from, IConcept to, IProfile group) {
		GroupConceptNode fromN = new GroupConceptNode(from);
		GroupConceptNode toN = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		boolean added = this.conceptGraph.addEdge(fromN, ConceptRelationType.UNKNOWN, toN);
		conceptGraph.getProperty(fromN, ConceptRelationType.UNKNOWN, toN, REL_GROUPS, true).add(group);
		conceptGraph.getProperty(fromN, ConceptRelationType.UNKNOWN, toN, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.STORAGE_TYPE, StorageType.UNKNOWN);
		return added;
	}

	@Override
	public boolean removeRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.removeEdge(new ConceptNode(from), type, new ConceptNode(to));
	}

	@Override
	public boolean groupRemoveRelation(IConcept from, IConceptRelationType type, IConcept to, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		if (conceptGraph.containsEdge(froma, type, toa)) {
			Set<IProfile> groupa = conceptGraph.getProperty(froma, type, toa, REL_GROUPS);
			if (groupa == null) {
				return false;
			}
			return groupa.remove(group);
		}
		return false;
	}

	@Override
	public boolean groupRemoveRelationAndDelete(IConcept from, IConceptRelationType type, IConcept to, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		if (conceptGraph.containsEdge(froma, type, toa)) {
			Set<IProfile> groupa = conceptGraph.getProperty(froma, type, toa, REL_GROUPS);
			if (groupa == null) {
				return false;
			}
			boolean dela = groupa.remove(toa);
			if (groupa.isEmpty()) {
				conceptGraph.removeEdge(froma, type, toa);
			}
			return dela;
		}
		return false;
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConcept to) {
		return conceptGraph.removeAllConnections(new GroupConceptNode(from), new GroupConceptNode(to));
	}

	@Override
	public boolean groupRemoveAllRelations(IConcept from, IConcept to, IProfile group) {
		boolean mod = false;
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		for (IConceptRelationType relation : conceptGraph.getEdgeTypesBetween(froma, toa)) {
			mod = mod || this.groupRemoveRelation(from, relation, to, group);
		}
		return mod;
	}

	@Override
	public boolean groupRemoveAllRelationsAndDelete(IConcept from, IConcept to, IProfile group) {
		boolean mod = false;
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		for (IConceptRelationType relation : new HashSet<>(conceptGraph.getEdgeTypesBetween(froma, toa))) {
			mod = mod || this.groupRemoveRelationAndDelete(from, relation, to, group);
		}
		return mod;
	}

	@Override
	public boolean removeAllRelations(IConcept from, IConceptRelationType type) {
		return conceptGraph.removeAllConnections(new GroupConceptNode(from), type);
	}

	@Override
	public boolean groupRemoveAllRelations(IConcept from, IConceptRelationType type, IProfile group) {
		boolean mod = false;
		GroupConceptNode froma = new GroupConceptNode(from);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		for (GroupConceptNode toa : conceptGraph.getNeighbors(froma, type)) {
			mod = mod || this.groupRemoveRelation(from, type, toa.getConcept(), group);
		}
		return mod;
	}

	@Override
	public boolean groupRemoveAllRelationsAndDelete(IConcept from, IConceptRelationType type, IProfile group) {
		boolean mod = false;
		GroupConceptNode froma = new GroupConceptNode(from);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		for (GroupConceptNode toa : new HashSet<>(conceptGraph.getNeighbors(froma, type))) {
			mod = mod || this.groupRemoveRelationAndDelete(from, type, toa.getConcept(), group);
		}
		return mod;
	}

	@Override
	public boolean hasRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.containsEdge(new GroupConceptNode(from), type, new GroupConceptNode(to));
	}

	@Override
	public boolean groupHasRelation(IConcept from, IConceptRelationType type, IConcept to, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			return false;
		if (!groupKnowsConcept(to, group))
			return false;
		if (hasRelation(from, type, to)) {
			Set<IProfile> gros = conceptGraph.getProperty(froma, type, toa, REL_GROUPS);
			if (gros == null)
				return false;
			return gros.contains(group);
		}
		return false;
	}

	@Override
	public boolean hasRelation(IConcept from, IConcept to) {
		return conceptGraph.containsEdge(new ConceptNode(from), new ConceptNode(to));
	}

	@Override
	public boolean groupHasRelation(IConcept from, IConcept to, IProfile group) {

		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			return false;
		if (!groupKnowsConcept(to, group))
			return false;
		if (hasRelation(from, to)) {
			return conceptGraph.getEdgeTypesBetween(froma, toa).stream().anyMatch((type) -> {
				Set<IProfile> gros = conceptGraph.getProperty(froma, type, toa, REL_GROUPS);
				return gros != null && gros.contains(group);
			});

		}
		return false;
	}

	@Override
	public Collection<IConceptRelationType> getRelationTypesFrom(IConcept from) {
		return conceptGraph.getConnectingEdgeTypes(new GroupConceptNode(from));
	}

	@Override
	public Iterable<? extends IConceptRelationType> groupGetRelationTypesFrom(IConcept from, IProfile group) {

		GroupConceptNode froma = new GroupConceptNode(from);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		return () -> conceptGraph.getConnectingEdgeTypes(froma).stream().filter((type) -> {
			return conceptGraph.getNeighbors(froma, type).stream().anyMatch((toa) -> {
				Set<IProfile> gros = conceptGraph.getProperty(froma, type, toa, REL_GROUPS);
				return gros != null && gros.contains(group);
			});
		}).iterator();
	}

	private void getCCLogicRecHelper(IConceptRelationType t, LogicType lt, GroupConceptNode node,
			Collection<IConcept> already, IProfile cg) {

		Collection<GroupConceptNode> neibs = null;
		if (t != null)
			neibs = conceptGraph.getNeighbors(node, t);
		else
			neibs = conceptGraph.getNeighbors(node);
		neibs.stream().filter((b) -> !already.contains(b.getConcept())).sequential().forEach((neighbor) -> {
			boolean canuse = true;
			if (cg != null) { // if we have to check group knowledge
				canuse = neighbor.knownByGroup(cg);
				if (canuse) { // also check relations in group knowledge
					if (lt != null) {
						if (!conceptGraph.getProperty(node, t, neighbor, REL_GROUPS, false).contains(cg)) {
							canuse = false;
						}
					} else {
						canuse = false;
						for (IConceptRelationType inxt : conceptGraph.getEdgeTypesBetween(node, neighbor)) {
							if (conceptGraph.getProperty(node, inxt, neighbor, REL_GROUPS, false).contains(cg)) {
								canuse = true;
							}
						}
					}
				}
			}
			if (canuse) {
				if (neighbor.getConceptType() == ConceptType.LOGIC_CONNECTOR
						&& neighbor.getConcept().asLogic().getLogicType() == lt) {
					getCCLogicRecHelper(t, lt, neighbor, already, cg);
				} else {
					already.add(neighbor.getConcept());
				}
			}
		});

	}

	/** if the given relation is null, do for all relations. cg = checkGroup */
	private Collection<IConcept> getCCLogicRecursive(IConcept from, IConceptRelationType t, LogicType lt, IProfile cg) {
		Set<IConcept> alr = Sets.newHashSet(from);
		getCCLogicRecHelper(t, lt, new GroupConceptNode(from), alr, cg);
		alr.remove(from);
		alr.removeIf((x) -> x.getConceptType() == ConceptType.LOGIC_CONNECTOR && x.asLogic().getLogicType() == lt);
		return alr;
	}

	@Override
	public Iterable<IConcept> getConnectedConcepts(IConcept from) {

		return () -> conceptGraph.getNeighbors(new GroupConceptNode(from)).stream().map(IConceptNode::getConcept)
				.iterator();
	}

	@Override
	public Iterable<? extends IConcept> getConnectedConceptsWithLogicalConnector(IConcept from,
			IConceptRelationType type, LogicType logicType) {
		return getCCLogicRecursive(from, type, logicType, null);
	}

	@Override
	public Iterable<? extends IConcept> getConnectedConceptsWithLogicalConnector(IConcept from, LogicType logicType) {
		return getCCLogicRecursive(from, null, logicType, null);
	}

	@Override
	public Iterable<? extends IConcept> groupGetConnectedConceptsWithLogicalConnector(IConcept from, LogicType ltype,
			IConceptRelationType type, IProfile group) {
		return getCCLogicRecursive(from, type, ltype, group);
	}

	@Override
	public Iterable<? extends IConcept> groupGetConnectedConceptsWithLogicalConnector(IConcept from, LogicType ltype,
			IProfile group) {
		return getCCLogicRecursive(from, null, ltype, group);
	}

	@Override
	public Iterable<? extends IConcept> groupGetConnectedConcepts(IConcept from, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		return () -> conceptGraph.getNeighbors(froma).stream()
				.filter((toa) -> conceptGraph.getEdgeTypesBetween(froma, toa).stream().anyMatch((type) -> {
					Set<IProfile> gros = conceptGraph.getProperty(froma, type, toa, REL_GROUPS);
					return gros != null && gros.contains(group);
				})).map(IConceptNode::getConcept).iterator();
	}

	@Override
	public Iterable<IConcept> getConnectedConcepts(IConcept from, IConceptRelationType type) {
		return () -> conceptGraph.getNeighbors(new GroupConceptNode(from), type).stream().map(IConceptNode::getConcept)
				.iterator();
	}

	@Override
	public Iterable<? extends IConcept> groupGetConnectedConcepts(IConcept from, IConceptRelationType type,
			IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		return () -> conceptGraph.getNeighbors(froma).stream().filter((toa) -> {
			Set<IProfile> gros = conceptGraph.getProperty(froma, type, toa, REL_GROUPS);
			return gros != null && gros.contains(group);
		}).map(IConceptNode::getConcept).iterator();
	}

	@Override
	public StorageType getStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.STORAGE_TYPE, false);
	}

	@Override
	public StorageType groupGetStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to,
			IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		Table<IProfile, IProperty<?>, Object> props = conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES,
				false);
		if (props == null)
			return null;
		return (StorageType) props.get(group, RelationProperties.STORAGE_TYPE);
	}

	@Override
	public void setStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype) {
		conceptGraph.setProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.STORAGE_TYPE, stype);
	}

	@Override
	public void groupSetStorageTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, StorageType stype,
			IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.STORAGE_TYPE, stype);
	}

	@Override
	public boolean isNot(IConcept from, IConceptRelationType type, IConcept to) {
		if (!conceptGraph.containsEdge(new GroupConceptNode(from), type, new GroupConceptNode(to))) {
			return true;
		}
		return conceptGraph.getProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.NOT);
	}

	@Override
	public boolean isOpposite(IConcept from, IConceptRelationType type, IConcept to) {
		if (!conceptGraph.containsEdge(new GroupConceptNode(from), type, new GroupConceptNode(to))) {
			return false;
		}
		return conceptGraph.getProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.OPPOSITE);
	}

	@Override
	public boolean groupIsOpposite(IConcept from, IConceptRelationType type, IConcept to, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		if (!conceptGraph.containsEdge(froma, type, toa)) {
			return true;
		}
		Table<IProfile, IProperty<?>, Object> props = conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES,
				false);
		if (props == null)
			return false;
		Boolean b = (Boolean) props.get(group, RelationProperties.OPPOSITE);
		return b == null ? false : b;
	}

	@Override
	public boolean groupIsNot(IConcept from, IConceptRelationType type, IConcept to, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		if (!conceptGraph.containsEdge(froma, type, toa)) {
			return true;
		}
		Table<IProfile, IProperty<?>, Object> props = conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES,
				false);
		if (props == null)
			return true;
		Boolean b = (Boolean) props.get(group, RelationProperties.NOT);
		return b == null ? true : b;
	}

	@Override
	public TruthType getTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.TRUTH_TYPE, false);
	}

	@Override
	public TruthType groupGetTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to,
			IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		Table<IProfile, IProperty<?>, Object> props = conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES,
				false);
		if (props == null)
			return null;
		return (TruthType) props.get(group, RelationProperties.TRUTH_TYPE);
	}

	@Override
	public void setTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, TruthType ttype) {
		conceptGraph.setProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.TRUTH_TYPE, ttype);
	}

	@Override
	public void groupSetTruthTypeOfRelation(IConcept from, IConceptRelationType type, IConcept to, TruthType ttype,
			IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.TRUTH_TYPE, ttype);
	}

	@Override
	public UUID getInfoSource(IConcept from, IConceptRelationType type, IConcept to) {
		return conceptGraph.getProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.INFO_SOURCE, false);
	}

	@Override
	public UUID groupGetInfoSource(IConcept from, IConceptRelationType type, IConcept to, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		Table<IProfile, IProperty<?>, Object> props = conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES,
				false);
		if (props == null)
			return null;
		return (UUID) props.get(group, RelationProperties.INFO_SOURCE);
	}

	@Override
	public void setInfoSource(IConcept from, IConceptRelationType type, IConcept to, UUID source) {
		conceptGraph.setProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.INFO_SOURCE, source);
	}

	@Override
	public void groupSetInfoSource(IConcept from, IConceptRelationType type, IConcept to, UUID source, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.INFO_SOURCE, source);
	}

	@Override
	public float getConfidence(IConcept from, IConceptRelationType type, IConcept to) {
		Float f = conceptGraph.getProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.CONFIDENCE, false);
		if (f == null)
			return 1f;
		return f;
	}

	@Override
	public float groupGetConfidence(IConcept from, IConceptRelationType type, IConcept to, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		Table<IProfile, IProperty<?>, Object> props = conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES,
				false);
		if (props == null)
			return 1f;
		Float f = (Float) props.get(group, RelationProperties.CONFIDENCE);
		if (f == null)
			return 1f;
		return f;
	}

	@Override
	public void setConfidence(IConcept from, IConceptRelationType type, IConcept to, float val) {
		conceptGraph.setProperty(new GroupConceptNode(from), type, new GroupConceptNode(to),
				RelationProperties.CONFIDENCE, val);
	}

	@Override
	public void groupSetConfidence(IConcept from, IConceptRelationType type, IConcept to, float val, IProfile group) {
		GroupConceptNode froma = new GroupConceptNode(from);
		GroupConceptNode toa = new GroupConceptNode(to);
		if (!groupKnowsConcept(from, group))
			throw new NodeNotFoundException(from);
		if (!groupKnowsConcept(to, group))
			throw new NodeNotFoundException(to);
		conceptGraph.getProperty(froma, type, toa, GROUP_BASED_PROPERTIES, true).put(group,
				RelationProperties.CONFIDENCE, val);
	}

	@Override
	public float getSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to) {
		Float f = conceptGraph.getProperty(new GroupConceptNode(from), ConceptRelationType.HAS_SOCIAL_BOND_TO,
				new GroupConceptNode(to), RelationProperties.CONFIDENCE, false);
		if (f == null)
			return 1f;
		return f;
	}

	@Override
	public void setSocialBondValue(IConcept from, ISocialBondTrait trait, IConcept to, float value) {
		conceptGraph.setProperty(new GroupConceptNode(from), ConceptRelationType.HAS_SOCIAL_BOND_TO,
				new GroupConceptNode(to), trait, value);
	}

	@Override
	public IRelationGraph<GroupConceptNode, IConceptRelationType> getConceptGraphView() {
		return this.immutableView;
	}

	@Override
	public void addConceptNodeSubgraph(IRelationGraph<? extends IConceptNode, IConceptRelationType> graph) {
		conceptGraph.addAll((IRelationGraph<GroupConceptNode, IConceptRelationType>) graph);
	}

	@Override
	public void learnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph) {
		conceptGraph.addAll(graph.mapCopy(GroupConceptNode::new, Function.identity()));
	}

	@Override
	public void groupsLearnConceptSubgraph(IRelationGraph<IConcept, IConceptRelationType> graph,
			Collection<? extends IProfile> groups) {
		IModifiableRelationGraph<GroupConceptNode, IConceptRelationType> copyGraph = graph
				.mappedEditableCopy(GroupConceptNode::new, Function.identity());
		copyGraph.edgeIterator().forEachRemaining((edge) -> copyGraph
				.getProperty(edge.getFirst(), edge.getSecond(), edge.getThird(), REL_GROUPS, true).addAll(groups));
		groups.forEach((group) -> copyGraph.forEach((nod) -> nod.addToGroup(group)));
		this.conceptGraph.addAll(copyGraph);
	}

	@Override
	public NoosphereKnowledgeBase clone() {
		NoosphereKnowledgeBase base;
		try {
			base = (NoosphereKnowledgeBase) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		base.conceptGraph = this.conceptGraph.deepCopy(GroupConceptNode::copy);
		base.immutableView = ImmutableGraphView.of(base.conceptGraph);
		base.containedGroups = new HashMap<IProfile, Pair<Integer, Integer>>(
				Maps.<IProfile, Pair<Integer, Integer>>asMap(this.containedGroups.keySet(),
						(prof) -> containedGroups.get(prof).clone()));
		return base;
	}

}
