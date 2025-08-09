package thinker.mind.will.thoughts.goals;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;
import com.google.common.collect.Table;

import _utilities.couplets.Pair;
import thinker.actions.IActionConcept;
import thinker.actions.expectations.IActionInfo;
import thinker.actions.searching.IActionFinder.IActionCriterion;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IActionPatternConcept;
import thinker.concepts.general_types.IProcessConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.ConditionGoalConcept;
import thinker.goals.GoalMemoryConcept;
import thinker.goals.IGoalConcept;
import thinker.goals.IGoalMemoryConcept.GoalFailureReason;
import thinker.knowledge.IKnowledgeRepresentation;
import thinker.mind.memory.StorageType;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.IThinkerWill;
import thinker.mind.will.thoughts.IThought;

/**
 * Thought that turns one goal into a process of actions, or adds actions to a
 * process Creates a chain of form (need/etc) --expect--> GOAL --solved_by-->
 * ACTION --requires--> CONDITION --expect--> GOAL --solved_by--> ACTION
 * --requires--> CONDITION --expect--> GOAL --solved_by--> ACTION ...
 * --expect--> Focus
 */
public class GoalsToActionsThought implements IThought {

	private UUID pid;
	private IProcessConcept process;
	private IGoalConcept focusGoal;
	private boolean done;
	private IThought conditionChecker;

	public GoalsToActionsThought(UUID tID) {
		this.pid = tID;
	}

	@Override
	public UUID getProcessID() {
		return this.pid;
	}

	/**
	 * Store a memory of a failure and mark as done
	 * 
	 * @param reason
	 * @param info
	 */
	private void fail(GoalFailureReason reason, IBeingAccess info) {
		GoalMemoryConcept memco = new GoalMemoryConcept(focusGoal, info.ticks(), reason);
		this.createMemory(memco, info.knowledge());
		info.knowledge().addTemporaryRelation(memco, KnowledgeRelationType.M_ABOUT, focusGoal);
		this.done = true;
	}

	/**
	 * Stores the information of this action in an action queue associated with the
	 * given expectation; this includes storing the next reuqirment of this action.
	 * Return false if some catastrophic failure happens
	 * 
	 * @param owner
	 * @param tsc
	 * @param info
	 * @param action
	 */
	private boolean storeActionInformation(IActionConcept action, IBeingAccess info) {

		IActionConcept actualAction = action;
		IActionInfo actionInfo = IActionInfo.create(info, focusGoal.getConditionsGraph());
		if (action.isPattern()) {
			actualAction = (IActionConcept) Streams
					.stream(info.knowledge().getConnectedConcepts(action, KnowledgeRelationType.A_PATTERN_OF)).findAny()
					.orElse(null);
			if (actualAction == null) {
				return false;
			}
			actionInfo = IActionInfo.create(info, focusGoal.getConditionsGraph(), (IActionPatternConcept) action);
		}

		Pair<IKnowledgeRepresentation, Map<IProfile, IProfile>> conditionAndMap = actualAction
				.generateCondition(actionInfo);
		IKnowledgeRepresentation condition = conditionAndMap.getFirst();
		IGoalConcept conditionConcept = new ConditionGoalConcept(condition, false);
		Map<IProfile, IProfile> equivalences = conditionAndMap.getSecond();
		info.knowledge().learnConcept(conditionConcept, StorageType.TEMPORARY);
		info.knowledge().addTemporaryRelation(action, KnowledgeRelationType.P_REQUIRES, conditionConcept);
		boolean addedQ = false;
		for (IConcept conc : condition.getMappedConceptGraphView()) {
			if (conc instanceof IProfile prof && prof.isIndefinite()) {
				addedQ = true;
				IWhQuestionConcept question = IWhQuestionConcept.create(prof.getUUID(), prof.getDescriptiveType());
				info.knowledge().learnConcept(question, StorageType.TEMPORARY);
				info.knowledge().addTemporaryRelation(conditionConcept, KnowledgeRelationType.P_ASKS, question);
				info.knowledge().addTemporaryRelation(question, KnowledgeRelationType.WH_ANSWERED_BY,
						IConcept.QUESTIONING);
				info.knowledge().addTemporaryRelation(question, KnowledgeRelationType.WH_REFERENCES_TYPE, prof);
				if (equivalences.get(prof) != null) {
					IProfile eq = equivalences.get(prof);
					IWhQuestionConcept question2 = IWhQuestionConcept.create(eq.getUUID(), eq.getDescriptiveType());
					if (!info.knowledge().knowsConcept(question2)) {
						return false;
					}
					info.knowledge().addTemporaryRelation(question, ProfileInterrelationType.IS, question2);
					info.knowledge().addTemporaryRelation(question, KnowledgeRelationType.QUICKLY_ACCESSIBLE_TO,
							this.process);
				}
			}
		}
		info.knowledge().addTemporaryRelation(conditionConcept, KnowledgeRelationType.P_EXPECTS,
				IConcept.UNFULFILLMENT);
		info.knowledge().addTemporaryRelation(conditionConcept, KnowledgeRelationType.P_EXPECTS, IConcept.FOCUS);

		if (addedQ) { // if there are any questions
			this.conditionChecker = new CheckConditionsThought(pid);
		}

		return true;
	}

	@Override
	public void tickThoughtActively(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		if (focusGoal == null) {
			// search for focused goal to make action chain
			for (IConcept concept : info.knowledge().getConnectedConcepts(IConcept.INTENTIONALITY,
					KnowledgeRelationType.P_SOLVED_BY)) {
				if (concept instanceof IGoalConcept igc && igc.isExpectation()) {
					focusGoal = igc;
					break;
				}
				if (info.gameMap().random() < owner.getMindStrainChance()) {
					break;
				}
			}

			// if none found, finish
			if (focusGoal == null) {
				this.done = true;
			} else {
				// remove relation for now
				info.knowledge().removeRelation(focusGoal, PropertyRelationType.OF_PRINCIPLE, IConcept.INTENTIONALITY);
			}
			process = createProcessInMemory(info.knowledge());
			info.knowledge().addTemporaryRelation(focusGoal, KnowledgeRelationType.QUICK_ACCESS, process);
		} else {
			Table<IActionConcept, IActionCriterion, Float> actionTable = owner.getActionFinder().findAction(info,
					focusGoal, this.getProcessID());
			Set<IActionConcept> blacklisted = Streams
					.stream(info.knowledge().getConnectedConcepts(focusGoal, KnowledgeRelationType.P_FAILED_BY))
					.map((a) -> (IActionConcept) a).collect(Collectors.toSet());
			if (actionTable.isEmpty()) {
				this.fail(GoalFailureReason.NO_AVAILABLE_ACTIONS, info);
			} else {
				IActionConcept actionConcept = owner.getActionPicker().pickAction(actionTable, info, blacklisted);
				if (actionConcept == IActionConcept.NO_ACTION) {
					this.fail(GoalFailureReason.ACTIONS_DID_NOT_MEET_STANDARDS, info);
				} else if (actionConcept != null) {
					if (!storeActionInformation(actionConcept, info)) {
						this.fail(GoalFailureReason.MEMORY_FAILURE, info);
					}
				} else {
					this.fail(GoalFailureReason.ALL_ACTIONS_FAILED, info);
				}
			}
		}
	}

	@Override
	public void tickThoughtPassively(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {

	}

	@Override
	public boolean shouldDelete(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		return done;
	}

	@Override
	public void aboutToDelete(IThinkerWill owner, int ticksSinceCreation, boolean interrupted, IBeingAccess info) {
		if (focusGoal != null) {
			// reconnect goal to intentionality
			info.knowledge().addTemporaryRelation(focusGoal, PropertyRelationType.OF_PRINCIPLE,
					IConcept.INTENTIONALITY);
		}
		if (this.conditionChecker == null) {
			this.forgetProcessFromMemory(info.knowledge());
		}
	}

	@Override
	public boolean hasChildThoughts() {

		return conditionChecker != null;
	}

	@Override
	public Collection<Entry<IThought, Boolean>> popChildThoughts() {
		IThought cc = this.conditionChecker;
		this.conditionChecker = null;
		if (cc == null)
			return null;
		return Map.of(cc, true).entrySet();
	}

	@Override
	public ThoughtType getThoughtType() {
		return ThoughtType.PONDER;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof GoalsToActionsThought gtt && gtt.pid.equals(this.pid);
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode() + this.pid.hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.pid + ")";
	}

}
