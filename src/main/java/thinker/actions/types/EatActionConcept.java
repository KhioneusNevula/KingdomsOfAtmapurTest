package thinker.actions.types;

import java.util.Collection;

import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.eat.FuelChannelSystem;
import things.form.channelsystems.eat.FuelIntakeChannelCenter;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.condition.ISomaCondition;
import things.form.soma.condition.ISomaCondition.ISomaRelationType;
import things.form.soma.condition.ISomaCondition.SomaRelationType;
import things.form.soma.condition.SomaCondition;
import things.spirit.ISpirit;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.relations.ActionRelationType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.mind.needs.INeedConcept;
import thinker.mind.will.IWill;
import utilities.graph.EmptyGraph;
import utilities.graph.IRelationGraph;
import utilities.graph.RelationGraph;

public class EatActionConcept implements IActionConcept {

	private RelationGraph<IConcept, IConceptRelationType> intention;
	private IConcept foodConcept;
	private String systemName;

	public EatActionConcept(String systemName, INeedConcept foodNeed, IConcept foodConcept) {
		intention = new RelationGraph<>();
		intention.add(foodNeed);
		intention.add(IActionConcept.THIS_ACTION);
		intention.addEdge(IActionConcept.THIS_ACTION, ActionRelationType.INCREASES, foodNeed);
		this.foodConcept = foodConcept;
		this.systemName = systemName;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.ACTION;
	}

	@Override
	public String getUnderlyingName() {
		return "eat_action_concept";
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getIntention() {
		return intention;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getKnowledgeIntention() {
		return EmptyGraph.instance();
	}

	@Override
	public ISomaCondition bodyConditions(ISpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks) {
		if (inPart.getOwner() instanceof ISoma so
				&& so.getSystemByName(systemName) instanceof FuelChannelSystem system) {
			FuelIntakeChannelCenter mouth = (FuelIntakeChannelCenter) system.getCenterTypes(ChannelRole.INTAKE).stream()
					.findAny().get();

			RelationGraph<Object, ISomaRelationType> somagraph = new RelationGraph<>();
			somagraph.add("mouth");
			somagraph.add(mouth);
			somagraph.addEdge("mouth", SomaRelationType.HAS_ABILITY, mouth);

			SomaCondition condition = new SomaCondition(somagraph);
			return condition;

		} else {
			return null;
		}
	}

	@Override
	public boolean canExecute(ISpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks) {

		return false;
	}

	@Override
	public void executeTick(ISpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks) {
		// TODO Auto-generated method stub

	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateKnowledgeCondition(
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention) {
		// TODO Auto-generated method stub
		return null;
	}

}
