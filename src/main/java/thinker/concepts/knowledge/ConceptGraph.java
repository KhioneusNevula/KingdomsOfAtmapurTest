package thinker.concepts.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.RelationProperties;
import thinker.mind.memory.node.IConceptNode;
import utilities.graph.EmptyGraph;
import utilities.graph.IModifiableRelationGraph;
import utilities.graph.IRelationGraph;
import utilities.graph.RelationGraph;
import utilities.property.IProperty;

/**
 * A concept-node graph has an extra property from a normal graph, in that it
 * prunes logic nodes out of itself when they no longer are connected
 * 
 * @author borah
 *
 */
public class ConceptGraph<E extends IConceptNode> extends RelationGraph<E, IConceptRelationType> {

	public ConceptGraph() {
		this(EmptyGraph.instance());
	}

	public ConceptGraph(IRelationGraph<? extends E, ? extends IConceptRelationType> other) {
		super(other);
		this.edgeProperties = Map.of(RelationProperties.CONFIDENCE, RelationProperties.CONFIDENCE::defaultValue,
				RelationProperties.INFO_SOURCE, RelationProperties.INFO_SOURCE::defaultValue,
				RelationProperties.KNOWN_BY_GROUPS, RelationProperties.KNOWN_BY_GROUPS::defaultValue,
				RelationProperties.LIKELIHOOD, RelationProperties.LIKELIHOOD::defaultValue,
				RelationProperties.STORAGE_TYPE, RelationProperties.STORAGE_TYPE::defaultValue,
				RelationProperties.TRUTH_TYPE, RelationProperties.TRUTH_TYPE::defaultValue, RelationProperties.NEGATED,
				RelationProperties.NEGATED::defaultValue);
	}

	public ConceptGraph(Set<? extends IProperty<?>> props) {
		this();
		this.edgeProperties = new HashMap<>(edgeProperties);
		edgeProperties.putAll(Maps.asMap(props, (x) -> x::defaultValue));
		edgeProperties = ImmutableMap.copyOf(edgeProperties);
	}

	/**
	 * Equivalent to
	 * {@link IModifiableRelationGraph#addEdge(Object, utilities.graph.IInvertibleRelationType, Object)};
	 * however, if the concept is a logical-connector, then it will be automatically
	 * added to the graph (whereas for other concepts, if it doesn't exist, we would
	 * throw an error)
	 */
	@Override
	public boolean addEdge(E first, IConceptRelationType type, E second) {
		if (first.getConceptType() == ConceptType.LOGIC_CONNECTOR) {
			this.add(first);
		}
		if (second.getConceptType() == ConceptType.LOGIC_CONNECTOR) {
			this.add(second);
		}
		return super.addEdge(first, type, second);
	}

	protected IInvertibleEdge<E, IConceptRelationType> pruneLogicNode(IInvertibleEdge<E, IConceptRelationType> x) {
		INode<E, IConceptRelationType> node = x.getStart();
		INode<E, IConceptRelationType> other = x.getEnd();
		if (node.getValue().getConceptType() == ConceptType.LOGIC_CONNECTOR) {
			this.remove(node.getValue());
		}
		if (other.getValue().getConceptType() == ConceptType.LOGIC_CONNECTOR) {
			this.remove(other.getValue());
		}
		return x;
	}

	@Override
	protected IInvertibleEdge<E, IConceptRelationType> removeConnection(INode<E, IConceptRelationType> node,
			IConceptRelationType type, INode<E, IConceptRelationType> other) {

		return pruneLogicNode(super.removeConnection(node, type, other));
	}

	protected Collection<? extends IInvertibleEdge<E, IConceptRelationType>> pruneLogicNodes(
			Collection<? extends IInvertibleEdge<E, IConceptRelationType>> edges) {
		for (IInvertibleEdge<E, IConceptRelationType> edge : edges) {
			this.pruneLogicNode(edge);
		}

		return edges;
	}

	@Override
	protected Collection<? extends IInvertibleEdge<E, IConceptRelationType>> removeConnections(
			INode<E, IConceptRelationType> node) {
		return this.pruneLogicNodes(super.removeConnections(node));
	}

	@Override
	protected Collection<? extends IInvertibleEdge<E, IConceptRelationType>> removeConnections(
			INode<E, IConceptRelationType> node, IConceptRelationType type) {
		return this.pruneLogicNodes(super.removeConnections(node, type));
	}

	@Override
	protected Collection<? extends IInvertibleEdge<E, IConceptRelationType>> removeConnections(
			INode<E, IConceptRelationType> node, INode<E, IConceptRelationType> other) {
		return this.pruneLogicNodes(super.removeConnections(node, other));
	}

	@Override
	public ConceptGraph<E> copy() {
		return (ConceptGraph<E>) super.copy();
	}

	@Override
	public ConceptGraph<E> deepCopy(Function<E, E> cloner) {
		return (ConceptGraph<E>) super.deepCopy(cloner);
	}

}
