package thinker.goals;

import _utilities.graph.EmptyGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import party.IParty;
import party.PartyRelationGraph;
import party.relations.types.PartyRelationType;
import party.systems.IRole;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.knowledge.IKnowledgeRepresentation;
import thinker.knowledge.KnowledgeRepresentation;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.needs.INeedConcept;

/** A goal based on a given need */
public class NeedGoalConcept implements IGoalConcept {

	private INeedConcept need;
	private KnowledgeRepresentation graph;

	public NeedGoalConcept(INeedConcept forNeed) {
		this.need = forNeed;
		this.graph = new KnowledgeRepresentation(EmptyGraph.instance());
		graph.learnConcept(forNeed);
		graph.learnConcept(SATISFIER);
		graph.addConfidentRelation(SATISFIER, EventRelationType.SATIATES_NEED, forNeed);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof NeedGoalConcept ngc) {
			return this.need.equals(ngc.need);
		}
		return false;
	}

	@Override
	public boolean isExpectation() {
		return true;
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode() + this.need.hashCode();
	}

	@Override
	public KnowledgeRepresentation getConditionsGraph() {
		return graph;
	}

	@Override
	public boolean modifyRole(IRole role, IParty requester, PartyRelationGraph partyRelations) {
		partyRelations.addEdge(role, PartyRelationType.GIVES_TO, requester);
		partyRelations.getSentResources(role, PartyRelationType.GIVES_TO, requester, true).add(this.need);
		role.getKnowledge().addConfidentRelation(role.getProfile(), EventRelationType.SATIATES_NEED, need);
		return true;
	}

	@Override
	public String toString() {
		return "{(" + need.getUnderlyingName() + "_goal" + ")}";
	}

	@Override
	public String getUnderlyingName() {
		return "goal_" + this.need.getUnderlyingName();
	}

}
