package thinker.actions.expectations;

import _utilities.graph.IRelationGraph;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.goals.IGoalConcept;

/**
 * Converts a descriptive relation into a graph with two new relations. Since it
 * is assumed that there is one that corresponds to each kind of descriptive
 * relation, the function does not accept a relation type. The
 * {@link IGoalConcept#SATISFIER} term should be used to represnt the satisfying
 * action
 * 
 * @author borah
 *
 */
public interface IEventRelationConverter {

	public IRelationGraph<IConcept, IConceptRelationType> convertRelation(IConcept left, IConcept right);

}
