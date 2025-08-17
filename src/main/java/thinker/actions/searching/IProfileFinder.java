package thinker.actions.searching;

import java.util.List;
import java.util.Map;

import party.util.IAgentAccess;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.goals.IGoalConcept;

/**
 * This matches existing {@link IProfile}s to {@link IWhQuestionConcept}s based
 * on the specifications of the condition
 * 
 * @author borah
 *
 */
public interface IProfileFinder {

	/**
	 * Returns the "order" in which connections should be sought out; relations
	 * which are less mutable should be further near the front of the list, and more
	 * mutable ones should be near the back
	 * 
	 * @return
	 */
	public List<RelationMutability> getRelationMutabilityHierarchy();

	/**
	 * Matches a series of {@link IWhQuestionConcept}s to profiles; starts at one
	 * profile, using traits and {@link IGoalConcept}s to make these connections en
	 * masse based on profiles known in the mind and profiles currently sensed.
	 * Return empty spots in the map if not all profiles could be matched under any
	 * attempt
	 * 
	 * @param info
	 * @param startQuestion
	 * @param attempts
	 * @param endChance     the chance that ths finder will stop trying to match
	 *                      profiles after `attempts` attempts
	 * @param doAccesses    whether to call "access" on the knowledge base when
	 *                      searcing
	 * @return
	 */
	public Map<IWhQuestionConcept, IProfile> matchProfiles(IAgentAccess info, IWhQuestionConcept startQuestion,
			int attempts, float endChance, boolean doAccesses);
}
