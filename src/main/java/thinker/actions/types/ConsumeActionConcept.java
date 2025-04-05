package thinker.actions.types;

import java.util.Collection;

import _utilities.graph.EmptyGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.eat.FuelChannelSystem;
import things.form.channelsystems.eat.FuelIntakeChannelCenter;
import things.form.condition.FormCondition;
import things.form.condition.IFormCondition;
import things.form.condition.IFormCondition.FormRelationType;
import things.form.condition.IFormCondition.IFormRelationType;
import things.form.graph.connections.PartConnection;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import thinker.actions.ActionsHelper;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.concepts.relations.descriptive.UniqueInterrelationType;
import thinker.concepts.relations.util.RelationsHelper;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IMindAccess;

/** Action for intaking something to fulfill a need */
public class ConsumeActionConcept implements IActionConcept {

	private FuelChannelSystem systemName;
	// private INeedConcept consumptionNeed;
	// private IConcept mouthConcept;
	// private IConcept consumableConcept;

	/**
	 * 
	 * @param consumptionNeed   -- what need this is satisfying
	 * @param consumableConcept -- a concept to CHARACTERIZE what is considered food
	 * @param mouthConcept      -- a concept to CHARACTERIZE the mouth part
	 */
	public ConsumeActionConcept(FuelChannelSystem system) {
		this.systemName = system;
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.ACTION;
	}

	@Override
	public String getUnderlyingName() {
		return "eat_action_concept_(" + systemName + ")";
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateIntendedResult(IKnowledgeBase knowledge) {

		Collection<IConcept> needConcepts = ActionsHelper.getSatisfiedNeedsOfEvent(this,
				knowledge.getMappedConceptGraphView());
		RelationGraph<IConcept, IConceptRelationType> intention = new RelationGraph<>();
		intention.add(THIS_ACTION);
		intention.addAll(needConcepts);
		for (IConcept need : needConcepts) {
			intention.addEdge(THIS_ACTION, EventRelationType.SATISFIES, need);
		}
		return intention;
	}

	private FuelIntakeChannelCenter getMouthType(FuelChannelSystem system) {
		return (FuelIntakeChannelCenter) system.getCenterTypes(ChannelRole.INTAKE).stream().findAny()
				.orElseThrow(IllegalStateException::new);
	}

	@Override
	public IFormCondition bodyConditions(IMindAccess info) {
		if (!info.isCorporeal()) {
			return IFormCondition.MUST_BE_CORPOREAL;
		}
		FuelChannelSystem system = info.maybeSoma().stream()
				.filter((soma) -> soma.getChannelSystems().contains(this.systemName)).map((x) -> systemName).findFirst()
				.orElse(null);
		if (system != null) {
			FuelIntakeChannelCenter mouth = getMouthType(system);
			RelationGraph<Object, IFormRelationType> somagraph = new RelationGraph<>();
			somagraph.add("mouth");
			somagraph.add(mouth);
			somagraph.addEdge("mouth", FormRelationType.HAS_ABILITY, mouth);

			FormCondition condition = new FormCondition(somagraph);
			return condition;

		} else {
			return new FormCondition(EmptyGraph.instance(), system);
		}
	}

	@Override
	public boolean canExecute(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention) {
		if (info.maybeSoma().isEmpty()) {
			return false;
		}
		ISoma so = info.maybeSoma().get();
		for (IComponentPart part : info.partAccess()) {
			if (RelationsHelper.matchesAnyConcepts(part, ActionsHelper.getThematicRoleOfEvent(this,
					info.being().getKnowledgeGraph(), EventRelationType.USES_PART), false,
					info.being().getKnowledgeGraph())) {
				Collection<IComponentPart> neis = so.getPartGraph().getNeighbors(part, PartConnection.HOLDING);
				if (neis.stream().map(IComponentPart::getTrueOwner).anyMatch(
						(b) -> intention.getNeighbors(THIS_ACTION, EventRelationType.ACTS_ON).stream().anyMatch(
								(target) -> RelationsHelper.matchesTypeProfile(so, target, intention, false)))) {
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public void abortAction(IMindAccess info) {

	}

	@Override
	public void executeTick(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention) {
		// TODO Food eating stuffs
		if (info.maybeSoma().isEmpty()) {
			return;
		}
		ISoma so = info.maybeSoma().get();
		FuelIntakeChannelCenter mouth = getMouthType(systemName);
		for (IComponentPart part : info.partAccess()) {
			if (part.getAbilities().contains(mouth)) {
				so.getPartGraph().getNeighbors(part, PartConnection.HOLDING).stream()
						.filter((p) -> mouth.canIntake(so, part, p))
						.forEach((p) -> mouth.intake(so, part, p, info.ticks()));

			}
		}

	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(IMindAccess info,
			IRelationGraph<IConcept, IConceptRelationType> intention) {

		RelationGraph<IConcept, IConceptRelationType> cond = new RelationGraph<>();
		Collection<IProfile> mouthTypeProfiles = ActionsHelper.getPartsUsedInEvent(this,
				info.being().getKnowledgeGraph());
		Collection<IProfile> foodTypeProfiles = ActionsHelper.getObjectsOfEvent(this, info.being().getKnowledgeGraph());

		cond.add(THIS_ACTION);
		for (IProfile foodType : foodTypeProfiles) {
			cond.add(foodType);
			cond.addEdge(THIS_ACTION, EventRelationType.ACTS_ON, foodType);
		}
		for (IProfile mouthType : mouthTypeProfiles) {
			cond.add(mouthType);
			cond.addEdge(THIS_ACTION, EventRelationType.PUTS_AT, mouthType);
			for (IProfile foodType : foodTypeProfiles) {
				cond.addEdge(foodType, UniqueInterrelationType.HELD_BY, mouthType);
			}
		}

		return cond;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConsumeActionConcept eac) {
			return this.systemName.equals(eac.systemName);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.systemName.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.systemName + ")";
	}

}
