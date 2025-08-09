package thinker.concepts.relations.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.google.common.base.Predicates;
import com.google.common.collect.Streams;

import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import things.interfaces.IUnique;
import thinker.concepts.IConcept;
import thinker.concepts.application.IConceptAssociationInfo;
import thinker.concepts.general_types.IConnectorConcept;
import thinker.concepts.general_types.IConnectorConcept.ConnectorType;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.TypeProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.descriptive.PropertyRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;

/**
 * Helper methods to interpret and deal with various relations in a graph
 * (usually a concept graph)
 */
public class RelationsHelper {

	/** if you want no equivalence relations */
	public static final long NO_EQ = 0;
	/**
	 * whether to include subclasses, i.e.
	 * {@link KnowledgeRelationType#IS_SUPERTYPE_OF}
	 */
	public static final long SUBCLASSES_EQ = 1;
	/**
	 * whether to include superclasses {@link KnowledgeRelationType#IS_SUBTYPE_OF}
	 */
	public static final long SUPERCLASSES_EQ = 1 << 1;
	/** whether to include is-relations {@link ProfileInterrelationType#IS} */
	public static final long IS_EQ = 1 << 2;
	/**
	 * whether to include the {@link PropertyRelationType#MEMBER_OF}-relations
	 */
	public static final long CONTAINING_GROUP_EQ = 1 << 3;

	/**
	 * whether to include the {@link PropertyRelationType#HAS_MEMBER}-relations
	 */
	public static final long MEMBER_EQ = 1 << 4;
	/**
	 * whether to include the {@link PropertyRelationType#PART_OF}-relations
	 */
	public static final long CONTAINING_WHOLE_EQ = 1 << 5;

	/**
	 * whether to include the {@link PropertyRelationType#HAS_PART}-relations
	 */
	public static final long SUBPARTS_EQ = 1 << 6;

	/**
	 * whether to include has-trait-relations {@link PropertyRelationType#HAS_TRAIT}
	 */
	public static final long TRAIT_EQ = 1 << 7;

	/** Gets the equivalence relation types represented by the given long */
	public static Set<IConceptRelationType> eqTypes(long dir) {
		if (dir == 0) {
			return Collections.emptySet();
		}
		Set<IConceptRelationType> rs = new HashSet<>();
		if ((dir & SUBCLASSES_EQ) != 0)
			rs.add(KnowledgeRelationType.IS_SUPERTYPE_OF);
		if ((dir & IS_EQ) != 0)
			rs.add(ProfileInterrelationType.IS);
		if ((dir & IS_EQ) != 0)
			rs.add(ProfileInterrelationType.IS);
		if ((dir & TRAIT_EQ) != 0)
			rs.add(PropertyRelationType.HAS_TRAIT);
		if ((dir & SUPERCLASSES_EQ) != 0)
			rs.add(KnowledgeRelationType.IS_SUBTYPE_OF);
		if ((dir & CONTAINING_GROUP_EQ) != 0)
			rs.add(ProfileInterrelationType.MEMBER_OF);
		if ((dir & MEMBER_EQ) != 0)
			rs.add(ProfileInterrelationType.HAS_MEMBER);
		if ((dir & CONTAINING_WHOLE_EQ) != 0)
			rs.add(ProfileInterrelationType.PART_OF);
		if ((dir & SUBPARTS_EQ) != 0)
			rs.add(ProfileInterrelationType.HAS_PART);
		return rs;
	}

	/**
	 * Return all concepts equivalence-related to this one. "long dir" indicates
	 * what direction the relations are allowed to go, which may be any
	 * bitwise-OR-connected combination of {@link #SUBCLASSES_EQ},
	 * {@link #SUPERCLASSES_EQ} and {@link #IS_EQ}
	 * 
	 * @param graph
	 * @param item
	 * @param dir
	 * @return
	 */
	/*
	 * public static Collection<IConcept>
	 * getEquivalenceRelations(IRelationGraph<IConcept, IConceptRelationType> graph,
	 * IConcept item, long dir) {
	 * 
	 * return graph.traverseBFS(item, eqTypes(dir), (x) -> { }, (a, b) -> true); }
	 */

	/**
	 * Returns all neighbors of this concept, but also marks if the relation
	 * connecting it is negative
	 */
	/*
	 * public static Iterable<Pair<IConcept, Boolean>>
	 * getNeighborsWithNegativeMarking( IRelationGraph<IConcept,
	 * IConceptRelationType> gra, IConcept from, IConceptRelationType edgeType) {
	 * return () -> Streams.stream(gra.outgoingEdges(from)).filter((ed) ->
	 * ed.center().equals(edgeType)) .map((ed) -> Pair.of(ed.getThird(),
	 * gra.getProperty(from, ed.getSecond(), ed.getThird(),
	 * RelationProperties.NOT))) .iterator();
	 * 
	 * }
	 */

	/**
	 * Returns all neighbors of this concept with teh given set of possible
	 * connecting relations, but also marks if the relation connecting it is
	 * negative
	 */
	/*
	 * public static Iterable<Pair<IConcept, Boolean>>
	 * getNeighborsWithNegativeMarking( IRelationGraph<IConcept,
	 * IConceptRelationType> gra, IConcept from, Collection<IConceptRelationType>
	 * edgeTypes) { return () -> Streams.stream(gra.outgoingEdges(from)).filter((ed)
	 * -> edgeTypes.contains(ed.center())) .map((ed) -> Pair.of(ed.getThird(),
	 * gra.getProperty(from, ed.getSecond(), ed.getThird(),
	 * RelationProperties.NOT))) .iterator();
	 * 
	 * }
	 */

	/**
	 * Return all NEIGHBOR-CONCEPTS matching the given predicate by testing if the
	 * predicate applies to all NEIGHBOR-CONCEPTS connected by the given relations
	 * from the FOCAL CONCEPT of the given connector types. Note that, perhaps
	 * ironically, the FOCAL CONCEPT itself will not be tested. It makes sense if
	 * you think about it.
	 * 
	 * @param predicate      the predicate to test. The edge given is the one
	 *                       directly connecting to the tested concept; EXCEPT for
	 *                       equivalence relations, where the edge MAY NOT exist.
	 *                       This is because for equivalence relations, the edge
	 *                       given will just be the one that connects to the
	 *                       original center of the equivalence. The predicate MAY
	 *                       be run more than once on some concepts
	 * @param equivalenceDir which relations to use as equivalence relations (you
	 *                       might use {@link #eqTypes(long) to simplify this}).
	 * @param orDefault      whether to treat connections without obvious logical
	 *                       connectors as OR-relations (by default, they are
	 *                       AND-relations)
	 * @param onlyPermit     a predicate to ignore concepts connected by edges that
	 *                       fail this predicate
	 */
	public static Set<IConcept> allConceptsMatchingPredicate(IRelationGraph<IConcept, IConceptRelationType> gra,
			IConcept from, Collection<IConceptRelationType> connectorTypes,
			BiPredicate<IConceptRelationType, IConcept> predicate, Set<IConceptRelationType> equivalenceDir,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> onlyPermit) {
		return __conceptMatchesPredicate(gra, from, null, connectorTypes, predicate, equivalenceDir, false, false,
				new HashSet<>(), onlyPermit, false);
	}

	/**
	 * See
	 * {@link #conceptsMatchingPredicate(IRelationGraph, IConcept, Collection, BiPredicate, Set, boolean, Predicate)}.
	 * This is simply the version of that which returns TRUE if all neighbors match
	 * the predicate.
	 * <ul>
	 * <li>Multiple edges from the FOCAL CONCEPT is treated as an AND relation
	 * unless otherwise specified
	 * <li>Any logical connector is evaluated as it ought to be. They do not count
	 * as NEIGHBOR-CONCEPTS, but may connect to NEIGHBOR-CONCEPTS
	 * <li>For any {@link RelationProperties#OPPOSITE} relation, the concept failing
	 * that test will return true
	 * <li>For any {@link RelationProperties#NOT} relation, no checks will be run
	 * and it will be ignored
	 * <li>All concepts connected by an equivalence-relation (if equivalenceDir is
	 * nonzero) to one of the NEIGHBOR-CONCEPTS will also be evaluated by the
	 * predicate
	 * </ul>
	 * 
	 * @param gra
	 * @param from
	 * @param connectorTypes
	 * @param predicate
	 * @param equivalenceDir
	 * @param orDefault
	 * @param onlyPermit
	 * @return
	 */
	public static boolean conceptAllNeighborsMatchPredicate(IRelationGraph<IConcept, IConceptRelationType> gra,
			IConcept from, Collection<IConceptRelationType> connectorTypes,
			BiPredicate<IConceptRelationType, IConcept> predicate, Set<IConceptRelationType> equivalenceDir,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> onlyPermit) {
		return __conceptMatchesPredicate(gra, from, null, connectorTypes, predicate, equivalenceDir, false, false,
				new HashSet<>(), onlyPermit, true) != null;
	}

	/**
	 * See
	 * {@link #conceptAllNeighborsMatchPredicate(IRelationGraph, IConcept, Collection, BiPredicate, Set, Predicate)}.
	 * This is simply the version of that which returns TRUE if any of the neighbors
	 * match the predicate.
	 * 
	 * @param gra
	 * @param from
	 * @param connectorTypes
	 * @param predicate
	 * @param equivalenceDir
	 * @param orDefault
	 * @param onlyPermit
	 * @return
	 */
	public static boolean conceptAnyNeighborsMatchPredicate(IRelationGraph<IConcept, IConceptRelationType> gra,
			IConcept from, Collection<IConceptRelationType> connectorTypes,
			BiPredicate<IConceptRelationType, IConcept> predicate, Set<IConceptRelationType> equivalenceDir,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> onlyPermit) {
		return __conceptMatchesPredicate(gra, from, null, connectorTypes, predicate, equivalenceDir, true, true,
				new HashSet<>(), onlyPermit, true) != null;
	}

	/**
	 * Functionally alike to
	 * {@link #conceptAllNeighborsMatchPredicate(IRelationGraph, IConcept, Collection, BiPredicate, Set, Predicate)}.
	 * However, it also checks the neighbors of all concepts that are equivalent to
	 * the focal concept by the equivalent relations specified.
	 * 
	 * @param equivalenceDirForFocus     the types of equivalences to check for the
	 *                                   "focal" concept
	 * @param equivalenceDirForNeighbors the types of equivalences to check for the
	 *                                   "neighbor" concepts
	 */
	public static boolean conceptOrEquivalencesAllNeighborsMatchPredicate(
			IRelationGraph<IConcept, IConceptRelationType> gra, IConcept from,
			Collection<IConceptRelationType> connectorTypes, BiPredicate<IConceptRelationType, IConcept> predicate,
			Set<IConceptRelationType> equivalenceDirForFocus, Set<IConceptRelationType> equivalenceDirForNeighbors,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> onlyPermit) {
		Set<Triplet<IConcept, IConceptRelationType, IConcept>> vedges = new HashSet<>();
		return __conceptMatchesPredicate(gra, from, null, equivalenceDirForFocus,
				(edg, nod) -> __conceptMatchesPredicate(gra, nod, null, connectorTypes, predicate,
						equivalenceDirForNeighbors, false, false, vedges, onlyPermit, true) != null,
				equivalenceDirForFocus, true, true, vedges, (a) -> true, true) != null;
	}

	/**
	 * Functionally alike to
	 * {@link #conceptAnyNeighborsMatchPredicate(IRelationGraph, IConcept,
	 * Collection, BiPredicate, Set, Predicate). However, it also checks the
	 * neighbors of all concepts that are equivalent to the focal concept by the
	 * equivalent relations specified.
	 * 
	 * @param equivalenceDirForFocus     the types of equivalences to check for the
	 *                                   "focal" concept
	 * @param equivalenceDirForNeighbors the types of equivalences to check for the
	 *                                   "neighbor" concepts
	 */
	public static boolean conceptOrEquivalencesAnyNeighborsMatchPredicate(
			IRelationGraph<IConcept, IConceptRelationType> gra, IConcept from,
			Collection<IConceptRelationType> connectorTypes, BiPredicate<IConceptRelationType, IConcept> predicate,
			Set<IConceptRelationType> equivalenceDirForFocus, Set<IConceptRelationType> equivalenceDirForNeighbors,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> onlyPermit) {
		Set<Triplet<IConcept, IConceptRelationType, IConcept>> vedges = new HashSet<>();
		return __conceptMatchesPredicate(gra, from, null, equivalenceDirForFocus,
				(edg, nod) -> __conceptMatchesPredicate(gra, nod, null, connectorTypes, predicate,
						equivalenceDirForNeighbors, true, true, vedges, onlyPermit, true) != null,
				equivalenceDirForFocus, true, true, vedges, (a) -> true, true) != null;
	}

	/**
	 * 
	 * @param gra
	 * @param from
	 * @param connector         connector of previous concept to this sourc
	 * @param connectorTypes
	 * @param predicate
	 * @param equivalenceDir
	 * @param branchingIsOr     whether branches are interpreted as ors
	 * @param orDefault         whether or is "default" (used for equivalency)
	 * @param visitedEdges
	 * @param onlyPermit
	 * @param onlyPredicateMode whether to ignore all set-building and just return
	 *                          an Object or null for true or false
	 * @return
	 */
	private static Set<IConcept> __conceptMatchesPredicate(IRelationGraph<IConcept, IConceptRelationType> gra,
			IConcept from, IConceptRelationType connector, Collection<IConceptRelationType> connectorTypes,
			BiPredicate<IConceptRelationType, IConcept> predicate, Set<IConceptRelationType> equivalenceDir,
			boolean branchingIsOr, boolean orDefault,
			Set<Triplet<IConcept, IConceptRelationType, IConcept>> visitedEdges,
			Predicate<Triplet<IConcept, IConceptRelationType, IConcept>> onlyPermit, boolean onlyPredicateMode) {

		Set<IConcept> concepts = new HashSet<>();
		if (connector == null || from instanceof IConnectorConcept) { // i.e. we can only branch
			// exclude any NOT relations
			Iterable<Triplet<IConcept, IConceptRelationType, IConcept>> neis = () -> connectorTypes.stream()
					.flatMap((e) -> Streams.stream(gra.outgoingEdges(from, e)))
					.filter((e) -> !gra.getProperty(e.getFirst(), e.getSecond(), e.getThird(), RelationProperties.NOT))
					.iterator();
			for (Triplet<IConcept, IConceptRelationType, IConcept> neighborEdge : neis) {
				if (visitedEdges.contains(neighborEdge) || !onlyPermit.test(neighborEdge))
					continue;
				visitedEdges.add(neighborEdge);
				boolean desiredPositivity = false;// the desired output of the matching
				if (onlyPredicateMode) {
					desiredPositivity = !gra.getProperty(neighborEdge.getFirst(), neighborEdge.getSecond(),
							neighborEdge.getThird(), RelationProperties.OPPOSITE);
				}
				if (neighborEdge.right() instanceof IConnectorConcept ilc) { // logical connector be like
					boolean orMode = ilc.getConnectorType() == ConnectorType.OR;
					if (ilc.getConnectorType() == ConnectorType.AND || ilc.getConnectorType() == ConnectorType.OR) {
						Set<IConcept> cons = __conceptMatchesPredicate(gra, ilc, neighborEdge.center(), connectorTypes,
								predicate, equivalenceDir, orMode, orDefault, visitedEdges, onlyPermit,
								onlyPredicateMode);
						if (onlyPredicateMode) { // if we only need true/false
							if ((cons != null) == desiredPositivity) {
								if (branchingIsOr)
									return Collections.emptySet(); // if we are in OR mode, then one true = end
							} else { // if we are in AND mode, then one false = end
								if (!branchingIsOr)
									return null;
							}
						} else { // otherwise we add to our list
							concepts.addAll(cons);
						}
					} else {
						// do nothing if it is another kind of logic
					}
				} else { // anything other than logic
				}
			}
			if (onlyPredicateMode) { // if we are only returning a predicate
				return branchingIsOr ? null : Collections.emptySet();
				// if we are doing only OR, then all failing is false. otherwise, all passing is
				// true
			} else {
				return concepts;
			}
		} else { // if we do have a connector and from is not a logical concept, i.e. we are at a
					// neighbor and can only check equivalences
			if (predicate.test(connector, from)) { // if the predicate succeeds, we done if in onlyPredicateMode
				if (onlyPredicateMode) {
					return Collections.emptySet();
				} else {
					concepts.add(from);
				}
			} // if it doesnt, let's check equivalences
			/*
			 * we exclude negations as well
			 */
			Set<IConcept> equivalences = __conceptMatchesPredicate(gra, from, null, equivalenceDir, (edg, nod) -> {
				Set<IConcept> cons = __conceptMatchesPredicate(gra, nod, null, connectorTypes, predicate,
						equivalenceDir, orDefault, orDefault, visitedEdges, onlyPermit, onlyPredicateMode);
				if (onlyPredicateMode) {
					return cons != null;
				}
				concepts.addAll(cons);
				return !concepts.isEmpty();
			}, equivalenceDir, orDefault, orDefault, visitedEdges, (a) -> true, onlyPredicateMode);
			if (onlyPredicateMode) {
				return equivalences;
			} else {
				return concepts;
			}

		}
	}

	/** Check if an object matches a target {@link TypeProfile}. */
	public static boolean matchesTypeProfile(IUnique target, IConcept cona,
			IRelationGraph<IConcept, IConceptRelationType> know, boolean form2being) {
		if (!(cona instanceof IProfile))
			throw new IllegalArgumentException("what");
		IProfile profile = (IProfile) cona;

		if (profile.isAnyMatcher() && profile.getDescriptiveType() == target.getUniqueType())
			return true;

		for (IConcept con : know.getNeighbors(profile, KnowledgeRelationType.C_CHECKS_ASSOCIATOR)) {
			IConceptAssociationInfo inf = (IConceptAssociationInfo) con;
			if (inf.getApplier().applies(target, profile))
				return true;
		}
		if (form2being)
			for (IConcept con : know.getNeighbors(profile, KnowledgeRelationType.C_CHECKS_FORM2BEING_ASSOCIATOR)) {
				IConceptAssociationInfo inf = (IConceptAssociationInfo) con;
				if (inf.getApplier().applies(target, profile))
					return true;
			}
		for (IConcept con : know.getNeighbors(profile, ProfileInterrelationType.IS)) {
			boolean anymatch = false;
			for (IConcept cas : know.getNeighbors(con, KnowledgeRelationType.C_CHECKS_ASSOCIATOR)) {
				IConceptAssociationInfo inf = (IConceptAssociationInfo) cas;
				if (inf.getApplier().applies(target, con)) {
					anymatch = true;
					break;
				}
			}
			if (form2being)
				for (IConcept cas : know.getNeighbors(con, KnowledgeRelationType.C_CHECKS_FORM2BEING_ASSOCIATOR)) {
					IConceptAssociationInfo inf = (IConceptAssociationInfo) cas;
					if (inf.getApplier().applies(target, con)) {
						anymatch = true;
						break;
					}
				}
			if (!anymatch)
				return false;
		}
		return true;
	}

	/**
	 * Checks if the target object matches all the properties that the given concept
	 * {@link PropertyRelationType#IS} in the conditionGraph, using the given
	 * knowledgeGraph to look up the concept's associators. Treats relations that
	 * are specified in "equivalenceRelations" as equivalences in the condition
	 * graph
	 * 
	 */
	public static boolean matchesAllConditions(IUnique target, IConcept center,
			IRelationGraph<IConcept, IConceptRelationType> conditionGraph, boolean matchBeingToForm,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeGraph,
			Set<IConceptRelationType> equivalenceRelations) {
		return RelationsHelper.conceptOrEquivalencesAllNeighborsMatchPredicate(conditionGraph, center,
				matchBeingToForm
						? Set.of(ProfileInterrelationType.IS, KnowledgeRelationType.C_CHECKS_ASSOCIATOR,
								KnowledgeRelationType.C_CHECKS_FORM2BEING_ASSOCIATOR)
						: Set.of(ProfileInterrelationType.IS, KnowledgeRelationType.C_CHECKS_ASSOCIATOR),
				(edge, concept) -> (concept instanceof IConceptAssociationInfo info
						&& info.getApplier().applies(target, center))
						|| matchesConcept(target, concept, matchBeingToForm, knowledgeGraph),
				equivalenceRelations, equivalenceRelations, Predicates.alwaysTrue());
	}

	/**
	 * Checks if the target object matches any of the conditions that the given
	 * concept {@link PropertyRelationType#IS} in the conditionGraph, using the
	 * given knowledgeGraph to look up the concept's associators. Treats relations
	 * that are specified in "equivalenceRelations" as equivalences in the condition
	 * graph
	 * 
	 */
	public static boolean matchesAnyConditions(IUnique target, IConcept center,
			IRelationGraph<IConcept, IConceptRelationType> conditionGraph, boolean matchBeingToForm,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeGraph,
			Set<IConceptRelationType> equivalenceRelations) {
		return RelationsHelper.conceptOrEquivalencesAnyNeighborsMatchPredicate(conditionGraph, center,
				matchBeingToForm
						? Set.of(ProfileInterrelationType.IS, KnowledgeRelationType.C_CHECKS_ASSOCIATOR,
								KnowledgeRelationType.C_CHECKS_FORM2BEING_ASSOCIATOR)
						: Set.of(ProfileInterrelationType.IS, KnowledgeRelationType.C_CHECKS_ASSOCIATOR),
				(edge, concept) -> (concept instanceof IConceptAssociationInfo info
						&& info.getApplier().applies(target, center))
						|| matchesConcept(target, concept, matchBeingToForm, knowledgeGraph),
				equivalenceRelations, equivalenceRelations, Predicates.alwaysTrue());
	}

	/**
	 * 
	 */
	public static Collection<IConcept> matchingConcepts(IUnique target, Iterable<IConcept> concepts,
			boolean matchBeingToForm, IRelationGraph<IConcept, IConceptRelationType> graph) {
		// TODO How to match things to concepts? discuss.
		Collection<IConcept> match = new HashSet<>();
		for (IConcept c : concepts) { // each concept being checked
			if (matchesConcept(target, c, matchBeingToForm, graph)) {
				match.add(c);
			}
		}
		return match;
	}

	/**
	 * Returns true if the given target matches all the given concepts. See
	 * {@link #matchesConcept(Object, IConcept, boolean)} for more details
	 */
	public static boolean matchesAllConcepts(IUnique target, Iterable<IConcept> concepts, boolean matchBeingToForm,
			IRelationGraph<IConcept, IConceptRelationType> graph) {
		// TODO How to match things to concepts? discuss.
		for (IConcept c : concepts) { // each concept being checked
			if (!matchesConcept(target, c, matchBeingToForm, graph)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the given target matches any one of the given concepts. See
	 * {@link #matchingConcepts(Object, Collection, boolean)} for more details
	 */
	public static boolean matchesAnyConcepts(IUnique target, Iterable<IConcept> concepts, boolean matchBeingToForm,
			IRelationGraph<IConcept, IConceptRelationType> graph) {
		// TODO How to match things to concepts? discuss.
		for (IConcept c : concepts) { // each concept being checked
			if (matchesConcept(target, c, matchBeingToForm, graph)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the given item (a Unique, profile-able entity, but not always)
	 * matches given concepts. If matchBeingToForm is true, will attempt to match
	 * Form targets to beings as well as forms. <br>
	 * The rules of matching are as follows:
	 * <ul>
	 * <li>if the concept being tested has a direct applicator associated to it,
	 * check using any associated applicator
	 * <li>Other rules match that of
	 * {@link RelationsHelper#conceptOrEquivalencesAllNeighborsMatchPredicate(IRelationGraph, IConcept, Collection, java.util.function.BiPredicate, Set, Set, Predicate)},
	 * using the equivalence relations {@link RelationsHelper#SUBCLASSES_EQ} and
	 * {@link RelationsHelper#IS_EQ}
	 * </ul>
	 */
	public static boolean matchesConcept(IUnique target, IConcept concept, boolean matchBeingToForm,
			IRelationGraph<IConcept, IConceptRelationType> graph) {
		return RelationsHelper.conceptOrEquivalencesAllNeighborsMatchPredicate(graph, concept,
				matchBeingToForm
						? Set.of(KnowledgeRelationType.C_CHECKS_ASSOCIATOR,
								KnowledgeRelationType.C_CHECKS_FORM2BEING_ASSOCIATOR)
						: Set.of(KnowledgeRelationType.C_CHECKS_ASSOCIATOR),
				(e, app) -> {
					if (app instanceof IConceptAssociationInfo lap) {
						return lap.getApplier().applies(target, concept);
					}
					return false;
				}, RelationsHelper.eqTypes(RelationsHelper.SUBCLASSES_EQ | RelationsHelper.IS_EQ),
				RelationsHelper.eqTypes(RelationsHelper.NO_EQ), Predicates.alwaysTrue());
	}

}
