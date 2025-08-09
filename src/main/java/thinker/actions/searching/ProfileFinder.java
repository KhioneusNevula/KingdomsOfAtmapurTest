package thinker.actions.searching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import party.util.IAgentAccess;
import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.ITypePatternConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.Profile;
import thinker.concepts.relations.descriptive.IProfileInterrelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.IGoalConcept;
import thinker.helpers.RelationsHelper;

/**
 * Implementation of {@link IProfileFinder}
 * 
 * @author borah
 *
 */
public class ProfileFinder implements IProfileFinder {

	private List<RelationMutability> relmut;

	public ProfileFinder(List<RelationMutability> relmutH) {
		this.relmut = new ArrayList<>(relmutH);
	}

	@Override
	public Map<IWhQuestionConcept, IProfile> matchProfiles(IAgentAccess info, IWhQuestionConcept startQuestion,
			int attempts, float endChance) {
		if (startQuestion.getQuestionType() instanceof UniqueType uniqueType) {
			Map<IWhQuestionConcept, IProfile> ret = new HashMap<>();
			IProfile qProfile = new Profile(startQuestion.getQuestionID(), uniqueType); // profile corresponding to
																						// the question

			Set<IProfile> profilePossibilities = new HashSet<>(); // possible profiles based on their traits
			Stream<ITypePatternConcept> patterns = RelationsHelper.getProfilePatterns(qProfile, info.knowledge());
			for (ITypePatternConcept pattern : (Iterable<ITypePatternConcept>) () -> patterns.iterator()) {
				RelationsHelper.findProfilesWithSubsetOf(pattern, info.knowledge())
						.forEach((a) -> profilePossibilities.add(a));
			}

			for (IConcept conc : info.knowledge().getConnectedConcepts(startQuestion,
					KnowledgeRelationType.P_ASKED_BY)) {
				// TODO get all conditions

				if (conc instanceof IGoalConcept goal) { // for each goal concept, find fitters
					for (RelationMutability mut : relmut) {
						// TODO profile finding
						for (IProfileInterrelationType ptype : mut.getRelationTypes()) {

						}
					}
				}
			}

			return ret;
		} else {
			throw new IllegalArgumentException(
					"Cannot find profile for non-profile type of question: " + startQuestion.getQuestionType());
		}
	}

	@Override
	public List<RelationMutability> getRelationMutabilityHierarchy() {
		return relmut;
	}

}
