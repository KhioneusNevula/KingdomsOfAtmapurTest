package thinker.goals;

import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import party.IParty;
import party.PartyRelationGraph;
import party.relations.types.PartyRelationType;
import party.systems.IRole;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.needs.INeedConcept;

/** A goal based on a given need */
public class NeedGoalConcept implements IGoalConcept {

	private INeedConcept need;
	private RelationGraph<IConcept, IConceptRelationType> graph;

	public NeedGoalConcept(INeedConcept forNeed) {
		this.need = forNeed;
		this.graph = new RelationGraph<>();
		graph.add(forNeed);
		graph.add(SATISFIER);
		graph.addEdge(SATISFIER, EventRelationType.SATIATES_NEED, forNeed);
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
	public int hashCode() {
		return this.getClass().hashCode() + this.need.hashCode();
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getConditionsGraph(IKnowledgeBase knowledge) {
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
