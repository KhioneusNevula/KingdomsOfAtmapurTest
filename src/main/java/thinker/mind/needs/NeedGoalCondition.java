package thinker.mind.needs;

import thinker.concepts.IConcept;
import thinker.concepts.relations.ActionRelationType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.goals.IGoalCondition;
import thinker.social.IParty;
import thinker.social.PartyRelationGraph;
import thinker.social.relations.party_relations.PartyRelationType;
import thinker.social.systems.IRole;
import utilities.graph.IRelationGraph;
import utilities.graph.RelationGraph;

public class NeedGoalCondition implements IGoalCondition {

	private INeedConcept need;
	private RelationGraph<IConcept, IConceptRelationType> graph;

	public NeedGoalCondition(INeedConcept forNeed) {
		this.need = forNeed;
		this.graph = new RelationGraph<>();
		graph.add(forNeed);
		graph.add(SATISFIER);
		graph.addEdge(SATISFIER, ActionRelationType.INCREASES, forNeed);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof NeedGoalCondition ngc) {
			return this.need.equals(ngc.need);
		}
		if (obj instanceof IGoalCondition igc) {
			return this.graph.equals(igc.getConditionsGraph());
		}
		return super.equals(obj);
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getConditionsGraph() {
		return graph;
	}

	@Override
	public boolean modifyRole(IRole role, IParty requester, PartyRelationGraph partyRelations) {
		partyRelations.addEdge(role, PartyRelationType.GIVES_TO, requester);
		partyRelations.getSentResources(role, PartyRelationType.GIVES_TO, requester, true).add(this.need);
		role.getKnowledge().addConfidentRelation(role.getProfile(), ActionRelationType.INCREASES, need);
		return true;
	}

	@Override
	public String toString() {
		return "{(" + need.getUnderlyingName() + "_condition" + ")}";
	}

}
