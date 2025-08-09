package thinker.mind.will.thoughts.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import _utilities.couplets.Pair;
import _utilities.graph.IRelationGraph;
import things.form.condition.IFormCondition;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.ConditionGoalConcept;
import thinker.goals.GoalMemoryConcept;
import thinker.goals.IGoalMemoryConcept;
import thinker.goals.IPhysicalRestrictionMemoryConcept;
import thinker.goals.PhysicalRestrictionMemoryConcept;
import thinker.mind.emotions.IFeeling;
import thinker.mind.memory.StorageType;
import thinker.mind.util.IMindAccess;
import thinker.mind.will.IWill;
import thinker.mind.will.thoughts.IThought;

public class ActionThought implements IActionThought {

	private IActionConcept action;
	private IRelationGraph<IConcept, IConceptRelationType> intention;
	private UUID processID;
	private boolean isExecuting = false;
	private boolean done = false;
	private IRelationGraph<IConcept, IConceptRelationType> condition = null;
	private IFormCondition formCondition = null;

	public ActionThought(UUID pID, IActionConcept action, IRelationGraph<IConcept, IConceptRelationType> intention) {
		this.action = action;
		this.intention = intention;
		this.processID = pID;
	}

	@Override
	public Collection<? extends IFeeling> getPossibleIncreasedFeelings() {
		return Collections.emptySet();
	}

	@Override
	public Collection<? extends IFeeling> getPossibleDecreasedFeelings() {
		return Collections.emptySet();
	}

	@Override
	public UUID getProcessID() {
		return processID;
	}

	@Override
	public void tickThoughtActively(IWill owner, int ticksSinceCreation, IMindAccess info) {
		if (!isExecuting) {
			if (action.canExecute(info, intention)) {
				this.isExecuting = true;
			} else { // if not able to execute
				this.formCondition = action.bodyConditions(info);
				if (!formCondition.isAlwaysFalse()
						&& (formCondition.isAlwaysTrue() || info.maybeSoma().filter(formCondition).isPresent())) { // if
																													// body
					// condition
					// passes
					if (action.canExecute(info, intention)) { // check again

					} else {
						IRelationGraph<IConcept, IConceptRelationType> condition = action.generateCondition(info,
								intention);
						if (!condition.isEmpty()) {
							IGoalMemoryConcept mem = new GoalMemoryConcept(new ConditionGoalConcept(condition),
									ticksSinceCreation, false);
							info.being().getKnowledge().learnConcept(mem, StorageType.TEMPORARY);
							info.being().getKnowledge().addTemporaryRelation(this.action,
									KnowledgeRelationType.P_REQUIRES, mem);
						}

					}
				} else { // if body condition is false

					IPhysicalRestrictionMemoryConcept mem = new PhysicalRestrictionMemoryConcept(formCondition,
							info.ticks());
					info.being().getKnowledge().learnConcept(mem, StorageType.TEMPORARY);
					// info.being().getKnowledge(). ...TODO add to timeline
					this.done = true;
				}
			}
			IFormCondition bodyCon = action.bodyConditions(info);
			if (!bodyCon.isAlwaysFalse() && (bodyCon.isAlwaysTrue() || info.maybeSoma().filter(bodyCon).isPresent())) {

			} else { // if body condition is false
				IPhysicalRestrictionMemoryConcept mem = new PhysicalRestrictionMemoryConcept(bodyCon, info.ticks());
				info.being().getKnowledge().learnConcept(mem, StorageType.TEMPORARY);
				// info.being().getKnowledge(). ...TODO add to timeline
				this.done = true;
			}
		} else {

		}
	}

	@Override
	public void tickThoughtPassively(IWill owner, int ticksSinceCreation, IMindAccess info) {

	}

	@Override
	public boolean shouldDelete(IWill owner, int ticksSinceCreation, IMindAccess info) {
		return done;
	}

	@Override
	public void aboutToDelete(IWill owner, int ticksSinceCreation, boolean interrupted, IMindAccess info) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasChildThoughts() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Pair<? extends IThought, Boolean>> popChildThoughts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ThoughtType getThoughtType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IActionConcept getActionType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFormCondition getBodyCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getKnowledgeCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> getEventStructure() {
		// TODO Auto-generated method stub
		return null;
	}

}
