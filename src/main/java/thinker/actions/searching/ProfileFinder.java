package thinker.actions.searching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import party.util.IAgentAccess;
import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.ITypePatternConcept;
import thinker.concepts.general_types.IWhQuestionConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.descriptive.IProfileInterrelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.IGoalConcept;
import thinker.helpers.ConceptRelationsMap;
import thinker.helpers.RelationValence;
import thinker.helpers.RelationsHelper;
import thinker.knowledge.KnowledgeRepresentation;

/**
 * Implementation of {@link IProfileFinder}
 * 
 * @author borah
 *
 */
public class ProfileFinder implements IProfileFinder {

	private List<RelationMutability> relmut;

	/**
	 * not sure what to do with this anywayy
	 */
	private static final float DIST_PH = 0f;

	public ProfileFinder(List<RelationMutability> relmutH) {
		this.relmut = new ArrayList<>(relmutH);
	}

	@Override
	public Map<IWhQuestionConcept, IProfile> matchProfiles(IAgentAccess info, IWhQuestionConcept startQuestion,
			int attempts, float endChance, boolean doAccesses) {
		ConceptRelationsMap qMap = new ConceptRelationsMap(startQuestion, info.knowledge(), RelationValence.IS);
		if (startQuestion.getQuestionType() instanceof UniqueType uniqueType) {
			Map<IWhQuestionConcept, IProfile> ret = new HashMap<>();
			IProfile qProfile = qMap.get(KnowledgeRelationType.WH_REFERENCES_TYPE).stream()
					.filter(IProfile.class::isInstance).map(IProfile.class::cast).findAny().orElse(null); // profile
																											// corresponding
																											// to
			// the question
			if (qProfile == null) {
				throw new IllegalStateException("? " + qMap);
			}

			Set<IProfile> profilePossibilities = new HashSet<>(); // possible profiles based on their traits
			Stream<ITypePatternConcept> patterns = RelationsHelper.getProfilePatterns(qProfile, info.knowledge());
			for (ITypePatternConcept pattern : (Iterable<ITypePatternConcept>) () -> patterns.iterator()) {
				RelationsHelper.findProfilesWithSubsetOf(pattern, info.knowledge(), DIST_PH, doAccesses)
						.filter((a) -> a.isUniqueProfile()).forEach((a) -> profilePossibilities.add(a));
			}
			Multimap<IProfile, KnowledgeRepresentation> contenders = MultimapBuilder.hashKeys().hashSetValues().build();
			for (IConcept conc : qMap.get(KnowledgeRelationType.P_ASKED_BY)) {

				if (conc instanceof IGoalConcept goal) { // for each goal concept, find fitters
					for (RelationMutability mut : relmut) {
						ConceptRelationsMap relmap = new ConceptRelationsMap(qProfile, goal.getConditionsGraph());
						ConceptRelationsMap negrelmap = new ConceptRelationsMap(qProfile, goal.getConditionsGraph(),
								RelationValence.OPPOSITE);
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
