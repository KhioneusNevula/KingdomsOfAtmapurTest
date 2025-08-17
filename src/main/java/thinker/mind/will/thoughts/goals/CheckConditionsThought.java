package thinker.mind.will.thoughts.goals;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import thinker.actions.IActionConcept;
import thinker.actions.searching.IProfileFinder;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.GoalMemoryConcept;
import thinker.goals.IGoalConcept;
import thinker.mind.util.IBeingAccess;
import thinker.mind.will.IThinkerWill;
import thinker.mind.will.thoughts.IThought;

/**
 * Check a goal and create a set of expectations from it if its questions are
 * unanswered
 * 
 * @author borah
 *
 */
public class CheckConditionsThought implements IThought {

	private UUID pid;
	private IGoalConcept focusCondition;
	private boolean done;
	private boolean already;

	public CheckConditionsThought(UUID tid) {
		this.pid = tid;
	}

	@Override
	public UUID getProcessID() {
		return pid;
	}

	@Override
	public void tickThoughtActively(IThinkerWill owner, int ticksSinceCreation, IBeingAccess info) {
		if (focusCondition == null) {
			List<IConcept> conco;
			// check PID for conditions already under consideration
			if (info.knowledge().knowsConcept(this.dummyProcessInMemory())) {
				conco = Lists.newArrayList(info.knowledge().getConnectedConcepts(this.dummyProcessInMemory(),
						KnowledgeRelationType.QUICK_ACCESS));
				Collections.shuffle(conco);
				for (IConcept concept : conco) {
					if (concept instanceof IGoalConcept igc && !igc.isExpectation()) {
						this.focusCondition = igc;
						break;
					}
				}
			}
			if (focusCondition == null) {
				// check focus for conditions that are of note
				conco = Lists.newArrayList(
						info.knowledge().getConnectedConcepts(IConcept.FOCUS, KnowledgeRelationType.P_EXPECTED_BY));
				Collections.shuffle(conco);
				for (IConcept concept : conco) {
					if (concept instanceof IGoalConcept igc) {
						focusCondition = igc;
						break;
					}
				}
			}
			// if we find nothing in focus, search the general places
			if (focusCondition == null) {
				conco = Lists.newArrayList(info.knowledge().getConnectedConcepts(IConcept.UNFULFILLMENT,
						KnowledgeRelationType.P_EXPECTED_BY));
				Collections.shuffle(conco);
				for (IConcept concept : conco) {
					if (concept instanceof IGoalConcept igc) {
						focusCondition = igc;
						break;
					}
				}
			}

		} else { // if we have found a condition
			IProfileFinder pfinder = owner.getProfileFinder();

			// find possible questions (ones which are UNANSWERED)
			List<IWhQuestionConcept> focusQuestions = Streams
					.stream(info.knowledge().getConnectedConcepts(focusCondition, KnowledgeRelationType.P_ASKS))
					.filter(IWhQuestionConcept.class::isInstance).map(IWhQuestionConcept.class::cast)
					.filter((a) -> info.knowledge().countRelationsOfType(a, KnowledgeRelationType.WH_ANSWERED_BY) < 1)
					.collect(Collectors.toList());
			if (focusQuestions.isEmpty()) { // if it has no unanswered questions, then, if it is an action requirement,
											// mark it as ready to execute. Otherwise add a goal completed memory
				IActionConcept conAction = Streams
						.stream(info.knowledge().getConnectedConcepts(focusCondition,
								KnowledgeRelationType.P_REQUIRED_BY))
						.filter(IActionConcept.class::isInstance).map(IActionConcept.class::cast).findFirst()
						.orElse(null);
				if (conAction != null) { // mark as ready
					info.knowledge().addTemporaryRelation(conAction, KnowledgeRelationType.A_READY_inv, IConcept.FOCUS);
				} else { // otherwise just store a memory
					this.createMemory(new GoalMemoryConcept(focusCondition, info.ticks()), info.knowledge());
				}
				this.done = true;
			} else {

				Collections.shuffle(focusQuestions); // reorder them
				IWhQuestionConcept focusQuestion = focusQuestions.get(0);

				Map<IWhQuestionConcept, IProfile> matchedProfiles = pfinder.matchProfiles(info, focusQuestion,
						owner.focusedThoughtsCap(), owner.getMindStrainChance(), true);
				for (IWhQuestionConcept question : matchedProfiles.keySet()) { // record that these profiles are
																				// answered
					if (matchedProfiles.get(question) != null) {
						info.knowledge().addTemporaryRelation(question, KnowledgeRelationType.WH_ANSWERED_BY,
								matchedProfiles.get(question));
						// also mark all profiles linked by IS as having this answer
						for (IWhQuestionConcept iwqc : (Iterable<IWhQuestionConcept>) () -> Streams
								.stream(info.knowledge().getConnectedConcepts(question, ProfileInterrelationType.IS))
								.filter(IWhQuestionConcept.class::isInstance).map(IWhQuestionConcept.class::cast)
								.iterator()) {
							info.knowledge().addTemporaryRelation(question, KnowledgeRelationType.WH_ANSWERED_BY,
									matchedProfiles.get(question));
						}
					}
				}

				if (already) {
					done = true; // end action if we already went one round
				} else {
					already = true; // do another round
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
		this.forgetProcessFromMemory(info.knowledge());
	}

	@Override
	public boolean hasChildThoughts() {
		return false;
	}

	@Override
	public Collection<Map.Entry<IThought, Boolean>> popChildThoughts() {
		return Collections.emptySet();
	}

	@Override
	public ThoughtType getThoughtType() {
		return ThoughtType.CHECK;
	}

}
