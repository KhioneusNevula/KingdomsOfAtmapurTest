package thinker.actions.types;

import java.util.Collection;
import java.util.UUID;

import _utilities.graph.EmptyGraph;
import _utilities.graph.IModifiableRelationGraph;
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
import thinker.IKnowledgeBase;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.relations.ActionRelationType;
import thinker.concepts.relations.ConceptRelationType;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.RelationsHelper;
import thinker.individual.IMindSpirit;
import thinker.mind.needs.INeedConcept;
import thinker.mind.will.IWill;

/** Action for intaking something to fulfill a need */
public class ConsumeActionConcept implements IActionConcept {

	private RelationGraph<IConcept, IConceptRelationType> intention;
	private String systemName;
	private INeedConcept consumptionNeed;
	private IConcept mouthConcept;
	private IConcept consumableConcept;

	/**
	 * 
	 * @param consumptionNeed   -- what need this is satisfying
	 * @param consumableConcept -- a concept to CHARACTERIZE what is considered food
	 * @param mouthConcept      -- a concept to CHARACTERIZE the mouth part
	 */
	public ConsumeActionConcept(INeedConcept foodNeed, IConcept consumableConcept, IConcept fuelIntake) {
		this.consumptionNeed = foodNeed;
		this.consumableConcept = consumableConcept;
		intention = new RelationGraph<>();
		intention.add(foodNeed);
		intention.add(consumableConcept);
		intention.add(IActionConcept.THIS_ACTION);
		intention.addEdge(IActionConcept.THIS_ACTION, ActionRelationType.SATISFIES, foodNeed);
		intention.addEdge(IActionConcept.THIS_ACTION, ActionRelationType.DESTROYS, consumableConcept);
		this.mouthConcept = fuelIntake;
		this.systemName = foodNeed.getSystemName();
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.ACTION;
	}

	public IConcept getConsumableConcept() {
		return consumableConcept;
	}

	@Override
	public String getUnderlyingName() {
		return "eat_action_concept_(" + this.consumableConcept + ",into," + mouthConcept.getUnderlyingName() + ")";
	}

	public IConcept getFuelIntake() {
		return mouthConcept;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getIntention() {
		return intention;
	}

	@Override
	public boolean integrateIntoKnowledgeBase(IKnowledgeBase base, IConcept doer) {
		if (!base.knowsConcept(consumptionNeed))
			return false;
		base.learnConcept(this);
		base.addConfidentRelation(this, ActionRelationType.SATISFIES, consumptionNeed);
		base.addConfidentRelation(this, ActionRelationType.DESTROYS, consumableConcept);
		// base.learnConcept(Feeling.SATISFACTION);
		// base.addConfidentRelation(this, ActionRelationType.INCREASES,
		// Feeling.SATISFACTION);
		return true;
	}

	@Override
	public boolean directlySatisfiesNeed() {
		return true;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getKnowledgeIntention() {
		return EmptyGraph.instance();
	}

	/** Gets a fuel-channel-system from the body using the name and whatnot */
	private FuelChannelSystem getFoodSystem(IComponentPart inPart) {
		if (inPart.getOwner() instanceof ISoma so
				&& so.getSystemByName(systemName) instanceof FuelChannelSystem system) {
			return system;
		}
		return null;
	}

	private FuelIntakeChannelCenter getMouthType(FuelChannelSystem system) {
		return (FuelIntakeChannelCenter) system.getCenterTypes(ChannelRole.INTAKE).stream().findAny()
				.orElseThrow(IllegalStateException::new);
	}

	@Override
	public IFormCondition bodyConditions(IMindSpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks) {
		FuelChannelSystem system = getFoodSystem(inPart);
		if (system != null) {
			FuelIntakeChannelCenter mouth = getMouthType(system);
			RelationGraph<Object, IFormRelationType> somagraph = new RelationGraph<>();
			somagraph.add("mouth");
			somagraph.add(mouth);
			somagraph.addEdge("mouth", FormRelationType.HAS_ABILITY, mouth);

			FormCondition condition = new FormCondition(somagraph);
			return condition;

		} else {
			return null;
		}
	}

	@Override
	public boolean canExecute(IMindSpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention, long ticks) {

		ISoma so = (ISoma) inPart.getOwner();
		for (IComponentPart part : access) {
			if (RelationsHelper.matchesConcept(part, mouthConcept, false, forSpirit.getKnowledgeGraph())) {
				Collection<IComponentPart> neis = so.getRepresentationGraph().getNeighbors(part,
						PartConnection.HOLDING);
				if (neis.stream().map(IComponentPart::getTrueOwner)
						.anyMatch((b) -> RelationsHelper.matchesAllConditions(b, TARGET, intention, false,
								forSpirit.getKnowledgeGraph(),
								RelationsHelper.eqTypes(RelationsHelper.CHARACTERIZERS_EQ)))) {
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public void abortAction(IMindSpirit spirit, IWill will, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks) {

	}

	@Override
	public void executeTick(IMindSpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention, long ticks) {
		// TODO Food eating stuffs
		FuelChannelSystem system = getFoodSystem(inPart);
		if (system != null) {
			ISoma so = (ISoma) inPart.getOwner();
			FuelIntakeChannelCenter mouth = getMouthType(system);
			for (IComponentPart part : access) {
				if (part.getAbilities().contains(mouth)) {
					so.getRepresentationGraph().getNeighbors(part, PartConnection.HOLDING).stream()
							.filter((p) -> mouth.canIntake(so, part, p))
							.forEach((p) -> mouth.intake(so, part, p, ticks));

				}
			}
		} else {
			throw new IllegalStateException();
		}

	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(IMindSpirit forSpirit, IWill inWill,
			IComponentPart inPart, Collection<? extends IComponentPart> access,
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention) {
		RelationGraph<IConcept, IConceptRelationType> cond = new RelationGraph<>();
		cond.add(TARGET);
		cond.add(mouthConcept);
		cond.addEdge(TARGET, ConceptRelationType.AT_LOCATION, mouthConcept);
		cond.addEdge(mouthConcept, ConceptRelationType.PART_OF, IActionConcept.ACTION_DOER);
		cond.addEdge(TARGET, ConceptRelationType.CHARACTERIZED_BY, consumableConcept);
		cond.setProperty(TARGET, ConceptRelationType.CHARACTERIZED_BY, consumableConcept, OBLIGATION_PROP,
				Obligation.OBLIGATORY);
		if (intention.degree(TARGET) != 0) {
			cond.addAll(intention.traverseBFS(TARGET, null, (x) -> {
			}, (a, b) -> true));

		} else {
			cond.addAll(knowledgeIntention.traverseBFS(TARGET, null, (x) -> {
			}, (a, b) -> true));
		}
		return cond;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateKnowledgeCondition(IMindSpirit forSpirit,
			IWill inWill, IComponentPart inPart, Collection<? extends IComponentPart> access,
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention) {

		if (intention.degree(TARGET, ConceptRelationType.IS) == 0) {
			RelationGraph<IConcept, IConceptRelationType> cond = new RelationGraph<>();
			cond.add(TARGET);
			IWhQuestionConcept q = IWhQuestionConcept.what(UUID.randomUUID());
			cond.addEdge(TARGET, ConceptRelationType.IS, q);
			IModifiableRelationGraph<IConcept, IConceptRelationType> targ = intention.traverseBFS(TARGET, null, (x) -> {
			}, (a, b) -> true).editableOrCopy();
			targ.set(TARGET, q);
			cond.addAll(targ);

			if (cond.addEdge(q, ConceptRelationType.CHARACTERIZED_BY, consumableConcept)) {
				cond.setProperty(q, ConceptRelationType.CHARACTERIZED_BY, consumableConcept, OBLIGATION_PROP,
						Obligation.OBLIGATORY);
			}

			return cond;
		}

		return EmptyGraph.instance();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConsumeActionConcept eac) {
			return this.consumableConcept.equals(eac.consumableConcept) && this.mouthConcept.equals(eac.mouthConcept)
					&& this.systemName.equals(eac.systemName) && this.intention.equals(eac.intention);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.consumableConcept.hashCode() + this.mouthConcept.hashCode() + this.systemName.hashCode()
				+ this.intention.hashCode();
	}

}
