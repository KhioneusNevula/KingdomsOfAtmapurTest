package thinker.goals;

import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import party.IParty;
import party.PartyRelationGraph;
import party.systems.IRole;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.IEventRelationType;
import thinker.knowledge.IKnowledgeRepresentation;
import thinker.knowledge.base.IKnowledgeBase;

/** A goal cocnept for a generic condition */
public class ConditionGoalConcept implements IGoalConcept {

	private IKnowledgeRepresentation condition;
	private boolean isExpectation;

	public ConditionGoalConcept(IKnowledgeRepresentation cons, boolean isExpectation) {
		this.condition = cons;
		if (!isExpectation) {
			for (Triplet<IConcept, IConceptRelationType, IConcept> edge : (Iterable<Triplet<IConcept, IConceptRelationType, IConcept>>) () -> cons
					.getMappedConceptGraphView().edgeIterator()) {
				if (edge.getEdgeType() instanceof IEventRelationType) {
					throw new IllegalArgumentException(
							"Problematic graph passed into non-expectation condition because of edge: " + edge);
				}
			}
		}
		this.isExpectation = isExpectation;
	}

	@Override
	public boolean isExpectation() {
		return isExpectation;
	}

	@Override
	public String getUnderlyingName() {
		return "cgoal_concept_" + this.condition + (this.isExpectation ? "_expectation" : "");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConditionGoalConcept cgc) {
			return cgc.isExpectation == this.isExpectation && cgc.condition.equals(this.condition);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode() * (this.isExpectation ? 3 : 1) + this.condition.hashCode();
	}

	@Override
	public String toString() {
		return this.getUnderlyingName();
	}

	@Override
	public IKnowledgeRepresentation getConditionsGraph() {
		return condition;
	}

	@Override
	public boolean modifyRole(IRole role, IParty requester, PartyRelationGraph partyRelations) {
		return false;
	}

}
