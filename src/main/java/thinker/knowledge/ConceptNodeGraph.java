package thinker.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import _utilities.couplets.Pair;
import _utilities.graph.EmptyGraph;
import _utilities.graph.IInvertibleRelationType;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import _utilities.property.IProperty;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.util.RelationProperties;
import thinker.knowledge.node.IConceptNode;

/**
 * A concept-node graph has an extra property from a normal graph, in that it
 * prunes logic nodes out of itself when they no longer are connected
 * 
 * @author borah
 *
 */
public class ConceptNodeGraph<E extends IConceptNode> extends RelationGraph<E, IConceptRelationType> {

	public ConceptNodeGraph() {
		this((IRelationGraph<E, IConceptRelationType>) EmptyGraph.<E, IConceptRelationType>instance());
	}

	public ConceptNodeGraph(IRelationGraph<? extends E, ? extends IConceptRelationType> other) {
		super(other);
		this.edgeProperties = Map.of(RelationProperties.CONFIDENCE, RelationProperties.CONFIDENCE::defaultValue,
				RelationProperties.INFO_SOURCE, RelationProperties.INFO_SOURCE::defaultValue,
				RelationProperties.KNOWN_BY_GROUPS, RelationProperties.KNOWN_BY_GROUPS::defaultValue,
				RelationProperties.LIKELIHOOD, RelationProperties.LIKELIHOOD::defaultValue,
				RelationProperties.STORAGE_TYPE, RelationProperties.STORAGE_TYPE::defaultValue,
				RelationProperties.TRUTH_TYPE, RelationProperties.TRUTH_TYPE::defaultValue, RelationProperties.NOT,
				RelationProperties.NOT::defaultValue, RelationProperties.OPPOSITE,
				RelationProperties.OPPOSITE::defaultValue);
	}

	public ConceptNodeGraph(Set<? extends IProperty<?>> props) {
		this();
		this.edgeProperties = new HashMap<>(edgeProperties);
		edgeProperties.putAll(Maps.asMap(props, (x) -> x::defaultValue));
		edgeProperties = ImmutableMap.copyOf(edgeProperties);
	}

	@Override
	public Pair<Object, Object> checkEdgeEndsPermissible(Object first1, IInvertibleRelationType type, Object second1) {
		if (first1 instanceof IConceptNode first && second1 instanceof IConceptNode second) {
			return super.checkEdgeEndsPermissible(first.getConcept(), type, second.getConcept());
		}
		throw new IllegalStateException(
				"Where are we using this method bruh we got " + first1 + " and " + second1 + " impossibly....");
	}

	/**
	 * Equivalent to
	 * {@link IModifiableRelationGraph#addEdge(Object, _utilities.graph.IInvertibleRelationType, Object)};
	 * however, if the concept is a logical-connector, then it will be automatically
	 * added to the graph (whereas for other concepts, if it doesn't exist, we would
	 * throw an error)
	 */
	@Override
	public boolean addEdge(E first, IConceptRelationType type, E second) {
		if (first.getConceptType() == ConceptType.CONNECTOR) {
			this.add(first);
		}
		if (second.getConceptType() == ConceptType.CONNECTOR) {
			this.add(second);
		}
		return super.addEdge(first, type, second);
	}

	protected IInvertibleEdge<E, IConceptRelationType> pruneLogicNode(IInvertibleEdge<E, IConceptRelationType> x) {
		INode<E, IConceptRelationType> node = x.getStart();
		INode<E, IConceptRelationType> other = x.getEnd();
		if (node.getValue().getConceptType() == ConceptType.CONNECTOR) {
			this.remove(node.getValue());
		}
		if (other.getValue().getConceptType() == ConceptType.CONNECTOR) {
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
	public ConceptNodeGraph<E> copy() {
		return (ConceptNodeGraph<E>) super.copy();
	}

	@Override
	public ConceptNodeGraph<E> deepCopy(Function<E, E> cloner) {
		return (ConceptNodeGraph<E>) super.deepCopy(cloner);
	}

}
