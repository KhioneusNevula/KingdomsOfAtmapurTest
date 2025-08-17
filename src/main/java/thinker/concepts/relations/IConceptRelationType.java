package thinker.concepts.relations;

import java.util.Collection;

import _utilities.graph.IInvertibleRelationType;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.relations.technical.KnowledgeRelationType;

public interface IConceptRelationType extends IInvertibleRelationType {

	@Override
	public IConceptRelationType invert();

	/**
	 * Whether this relation is immune to memory decay; mostly only applies to
	 * certain {@link KnowledgeRelationType}s.
	 * 
	 * @return
	 */
	public default boolean immuneToDecay() {
		return false;
	}

	/**
	 * Return what {@link ConceptType}s must apply to whatever the concept on the
	 * other end of this relation characterizes; return an empty set if not
	 * applicable.
	 */
	public Collection<ConceptType> getEndTypes();

	/**
	 * Return what classes the endpoints of this concept relation can be; return
	 * empty set if it can be anything
	 * 
	 * @return
	 */
	public Collection<Class<?>> getEndClasses();

	/**
	 * The max amount of edges of this type that can extend from a node; or Null if
	 * any number is permitted
	 */
	public Integer maxPermitted();

	/**
	 * Return true if a given object is permitted to be the node at the other end of
	 * this relation type (assumed that it has already been checked to be of the
	 * type of {@link #getEndClass()}
	 */
	public default Object checkEndType(Object node) {
		if (this.getEndClasses().isEmpty()
				|| this.getEndClasses().stream().anyMatch((clazz) -> clazz.isInstance(node))) {
			if (node instanceof IConcept concept) {
				if (this.getEndTypes().isEmpty()
						|| this.getEndTypes().stream().anyMatch((clazz) -> concept.getConceptType() == clazz)) {
					return maxPermitted();
				} else {
					return "ConceptType of " + concept + " is " + concept.getConceptType() + ", but should be "
							+ this.getEndTypes();
				}
			} else {
				return node + " is not anc instanceof " + IConcept.class.getSimpleName();
			}
		} else {
			return node + " is not an instanceof any of these classes: " + this.getEndClasses();
		}
	}

}
