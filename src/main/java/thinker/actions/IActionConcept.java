package thinker.actions;

import java.util.Map;

import _utilities.couplets.Pair;
import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.IRelationGraph;
import _utilities.property.IProperty;
import things.form.condition.IFormCondition;
import things.interfaces.UniqueType;
import thinker.actions.expectations.IActionInfo;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IPatternConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.IKnowledgeRepresentation;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IBeingAccess;

/** 
 * Action structure:
 * 
 * 
 * 
 */

/**
 * Concepts<br>:
 * 
	I want to increase my satiation. First: Ponder; Look for Satiation -satisfied_by-> ???
	
	Found nothing. Therefore, look at the need condition:<br>
	SATISFIER -increases-> satiation.
	
	Invert relation: Look for Satiation -increased_by-> (Actions)<br>
	Actions: Eat (intention = THIS_ACTION -increases-> satiation), MagicEat (intention = THIS_ACTION -increases-> satiation)<br>
	-> Eat, which has condition (TARGET -at_part-> PART_TARGET, PART_TARGET -is-> Mouth, TARGET -is-> Food)
	
	Invert relation: Since TARGET is Food, Look for action that uses Food -is-> TARGET -moved_by-> (Actions). Check these actions for the intention (TARGET -at-> mouth), then find the intersection, 
		as well as including actions which have a relation to anything that has an -is- relation to Food or is_type_of or whatever. 
	Actions: Put (intention = TARGET -at-> Part)
	-> Put, which has condition (TARGET -at-> Hand, (generated condition) TARGET -characterized_as-> Food)
	
	Invert relation: Since TARGET is Food, look for action that Food -moved_by-> (Actions) , then check these actions for the intention (TARGET -at-> Hand)
		then find the intersection with the similar caveats as above.
	Actions: Pick Up (intention = TARGET -at-> Hand), Summon (intention = (same)), 
		Cook (intention = THIS_ACTION -creates-> TARGET, TARGET -at-> Hand)
	-> Pick Up, which has only the condition ((generated) ACTION_DOER -at-> Food)
	
	Invert relation: Look for action that has SELF -moved_by-> (actions) with similar caveats as above
	Actions: Walk (intention = ACTION_DOER -at-> Any_Place), Teleport (intention (same))
	-> Teleport, which has a Knowledge condition that (SATISFIER -place_where-> Food).
	
	If this is known, do the actions!
	Otherwise...
	
	Invert relation: Look for Action that have Food -at_location-> WHERE-QUESTION -answered_by-> (actions) with similar caveats as above
	Actions: Search (intention = THIS_ACTION -answers-> WHERE-QUESTION -place_where-> Any), Ask (intention = THIS_ACTION -answers-> ANY_QUESTION)
	-> Ask, which has condition that (SATISFIER -self_to-> Any_Individual)
	
	Invert relation: ... 
	
	We end when we get to a relation with no 
	
 * 
 */

/**
 * An ActionType is a kind of event that can be performed by an individual. They
 * can be turned into Actions
 * 
 * @author borah
 *
 */
public interface IActionConcept extends IConcept, IPrincipleConcept {

	/**
	 * Similar idea to
	 * {@link IProfile#createAnonymousGenericProfile(String, UniqueType)}
	 */
	public static IActionConcept createGenericActionConcept(String name) {
		return new IActionConcept() {

			@Override
			public String getUnderlyingName() {
				return name;
			}

			@Override
			public String toString() {
				return "*>" + name;
			}

			@Override
			public ConceptType getConceptType() {
				return ConceptType.ACTION;
			}

		};
	}

	/**
	 * An action in and of itself, represented for {@link #getIntention()}
	 */
	public static final IActionConcept THIS_ACTION = createGenericActionConcept("this_action");

	/**
	 * Used by some methods to signify an action was not found or could not be foudn
	 */
	public static final IActionConcept NO_ACTION = createGenericActionConcept("no_action");

	/** Incorporates an action's intention into a knowledge base */
	public static void incorporateIntoKnowledgeBase(IActionConcept action, IKnowledgeBase base) {
		IModifiableRelationGraph<IConcept, IConceptRelationType> intent = action.initIntendedResult(base)
				.getMappedConceptGraphView().editableCopy();
		if (intent.contains(THIS_ACTION))
			intent.set(THIS_ACTION, action);
		base.learnConceptSubgraph(intent);
		if (intent.contains(action))
			intent.set(action, THIS_ACTION);
	}

	/**
	 * Return a graph of what conditions this action usually fulfills (and also what
	 * questions it answers). Action roles must be satisfied by
	 * 
	 * @return
	 */
	public default IKnowledgeRepresentation initIntendedResult(IKnowledgeBase bas) {
		throw new UnsupportedOperationException(this.toString() + ": unavailable");
	}

	/**
	 * Return an {@link IFormCondition} of what is expected of the given body to
	 * perform the action; the body will check if the condition is true when running
	 * the action. Return {@link IFormCondition#FALSE} if it is impossible for this
	 * body to ever meet the requisite conditions
	 * 
	 * @param forSpirit
	 * @param inWill
	 * @param inPart
	 * @param access
	 * @param ticks
	 * @return
	 */
	public default IFormCondition bodyConditions(IActionInfo info) {
		throw new UnsupportedOperationException(this.toString() + ": unavailable");
	}

	/**
	 * Called if a body condition suddenly returns false and the action is
	 * physically prevented from continuing
	 */
	public default void abortAction(IActionInfo info) {
		throw new UnsupportedOperationException(this.toString() + ": unavailable");
	}

	/**
	 * If this action can currently execute. If not, first check
	 * {@link #bodyConditions(IBeingAccess)}, then call
	 * {@link #generateCondition(IRelationGraph, IRelationGraph)} and/or
	 * {@link #generateKnowledgeCondition(IRelationGraph, IRelationGraph)}. Returns
	 * true as long as the action can execute;
	 * 
	 * @param access
	 * @param expectation the Expectation being satisfied
	 * @return
	 */
	public default boolean canExecute(IActionInfo info) {
		throw new UnsupportedOperationException(this.toString() + ": unavailable");
	}

	/**
	 * Execute this action. Includes whatever Expectation was used to create this
	 * action
	 * 
	 * @param forSpirit
	 * @param inWill
	 * @param inPart
	 * @param access
	 * @param ticks
	 * @return
	 */
	public default void executeTick(IActionInfo info) {
		throw new UnsupportedOperationException(this.toString() + ": unavailable");
	}

	/**
	 * Creates a condition for this action based on the given Expectation.
	 * Conditions create relations between profiles.
	 * 
	 * Two things are returned: the condition, and a map
	 * 
	 * A Map is given to indicate which of the profiles in the given Intention are
	 * equivalent to the profiles in the output condition. IS relations can be drawn
	 * between these.
	 * 
	 * @param expectation the Expectation this action is satisfying
	 * 
	 * @return
	 */
	public default Pair<IKnowledgeRepresentation, Map<IProfile, IProfile>> generateCondition(IActionInfo info) {
		throw new UnsupportedOperationException(this.toString() + ": unavailable");
	}

	@Override
	default boolean isEventType() {
		return true;
	}

	/**
	 * Whether this concept is merely a pattern for an action, not an action itself
	 */
	public default boolean isPattern() {
		return this instanceof IPatternConcept;
	}

	/** Used to change the {@linkplain Obligation}s of conditional relationships */
	public static final IProperty<Obligation> OBLIGATION_PROP = IProperty.make("obligation", Obligation.class,
			Obligation.REQUIRED);

	/**
	 * Indicates how obligatory a conditional relationship is. Typically is
	 * {@link #REQUIRED}. This governs whether parts of the condition can be ignored
	 * or not, and if they are ignored whether that will meaningfully change the
	 * action itself
	 */
	public static enum Obligation {
		/**
		 * Indicates an action simply cannot occur if this relationship is not
		 * respected, as it is fundamental to the action's execution
		 */
		REQUIRED,
		/**
		 * Indicates that the action can still occur if this condition is violated, but
		 * it will not fulfill one or more of its intentions.
		 */
		OBLIGATORY,
		/**
		 * Indicates the action can occur and will fulfill its intentions if this
		 * relation is ignored, but still changes the behavior of the action
		 */
		OPTIONAL
	}

}
