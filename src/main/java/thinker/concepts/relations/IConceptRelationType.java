package thinker.concepts.relations;

import java.util.Collection;

import _utilities.graph.IInvertibleRelationType;
import thinker.concepts.IConcept;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;

public interface IConceptRelationType extends IInvertibleRelationType {

	@Override
	public IConceptRelationType invert();

	/**
	 * Return what {@link ConceptType}s must apply to whatever the concept on the
	 * other end of this relation characterizes; return an empty set if not
	 * applicable.
	 */
	public ConceptType getEndType();

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
				if (concept instanceof IConnectorConcept cc
						&& ConnectorType.logicalConnectors().contains(cc.getConnectorType())) {
					return maxPermitted();
				}
				if (this.getEndType() == ConceptType.NONE || concept.getConceptType() == this.getEndType()) {
					return maxPermitted();
				} else {
					return "ConceptType of " + concept + " is " + concept.getConceptType() + ", but should be "
							+ this.getEndType();
				}
			} else {
				return node + " is not an instanceof " + IConcept.class.getSimpleName();
			}
		} else {
			return node + " is not an instanceof any of these classes: " + this.getEndClasses();
		}
	}

}
