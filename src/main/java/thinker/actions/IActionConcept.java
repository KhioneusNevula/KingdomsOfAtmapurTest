package thinker.actions;

import _utilities.graph.IModifiableRelationGraph;
import _utilities.graph.IRelationGraph;
import _utilities.property.IProperty;
import things.form.condition.IFormCondition;
import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.general_types.IPrincipleConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.profile.TypeProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.concepts.relations.descriptive.UniqueInterrelationType;
import thinker.concepts.relations.technical.KnowledgeRelationType;
import thinker.goals.IGoalConcept;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IMindAccess;

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

			@Override
			public IRelationGraph<IConcept, IConceptRelationType> generateIntendedResult(IKnowledgeBase knowledge) {
				throw new UnsupportedOperationException("Cannot run generic anonymous action type..." + name);
			}

			@Override
			public IRelationGraph<IConcept, IConceptRelationType> generateCondition(IMindAccess info,
					IRelationGraph<IConcept, IConceptRelationType> intention) {
				throw new UnsupportedOperationException("Cannot run generic anonymous action type..." + name);
			}

			@Override
			public void executeTick(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention) {
				throw new UnsupportedOperationException("Cannot run generic anonymous action type..." + name);
			}

			@Override
			public boolean canExecute(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention) {
				throw new UnsupportedOperationException("Cannot run generic anonymous action type..." + name);
			}

			@Override
			public IFormCondition bodyConditions(IMindAccess info) {
				throw new UnsupportedOperationException("Cannot run generic anonymous action type..." + name);
			}

			@Override
			public void abortAction(IMindAccess info) {
				throw new UnsupportedOperationException("Cannot run generic anonymous action type..." + name);
			}

		};
	}

	/**
	 * An action in and of itself, represented for {@link #getIntention()}
	 */
	public static final IActionConcept THIS_ACTION = createGenericActionConcept("this_action");

	/** Incorporates an action's intention into a knowledge base */
	public static void incorporateIntoKnowledgeBase(IActionConcept action, IKnowledgeBase base) {
		IModifiableRelationGraph<IConcept, IConceptRelationType> intent = action.generateIntendedResult(base).editableOrCopy();
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
	public IRelationGraph<IConcept, IConceptRelationType> generateIntendedResult(IKnowledgeBase knowledge);

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
	public IFormCondition bodyConditions(IMindAccess info);

	/**
	 * Called if a body condition suddenly returns false and the action is
	 * physically prevented from continuing
	 */
	public void abortAction(IMindAccess info);

	/**
	 * If this action can currently execute. If not, first check
	 * {@link #bodyConditions(IMindAccess)}, then call
	 * {@link #generateCondition(IRelationGraph, IRelationGraph)} and/or
	 * {@link #generateKnowledgeCondition(IRelationGraph, IRelationGraph)}. Returns
	 * true as long as the action can execute;
	 * 
	 * @param forSpirit
	 * @param inWill
	 * @param inPart
	 * @param access
	 * @param intention          the intention being satisfied
	 * @param knowledgeIntention the knowledge-intention being satisfied
	 * @param ticks
	 * @return
	 */
	public boolean canExecute(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention);

	/**
	 * Execute this action. Includes whatever intentions were used to create this
	 * action
	 * 
	 * @param forSpirit
	 * @param inWill
	 * @param inPart
	 * @param access
	 * @param ticks
	 * @return
	 */
	public void executeTick(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention);

	/**
	 * Creates a condition for this action based on the given intention. A condition
	 * uses OR-matching. Conditions may only have some selection of these
	 * components:
	 * <ol>
	 * <li>A {@link IGoalConcept#SATISFIER} role, which is linked by
	 * {@link EventRelationType}s to some concept
	 * <li>A profile extending from an {@link #TARGETS} role, which is linked by
	 * {@link KnowledgeRelationType}s to {@link TypeProfile}s, which may themselves
	 * be linked by {@link UniqueInterrelationType}s to still other
	 * {@link TypeProfile}s
	 * <li>A {@link #ACTION_DOER} role, which is linked by
	 * {@link KnowledgeRelationType}s to {@link TypeProfile}s
	 * <li>A {@link #MEANS} role, linked by {@link KnowledgeRelationType}s to
	 * {@link TypeProfile}s
	 * <li>A {@link #PARTS_USED} role, linked by {@link KnowledgeRelationType}s to
	 * other {@link TypeProfile}s
	 * </ol>
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(IMindAccess info,
			IRelationGraph<IConcept, IConceptRelationType> intention);

	@Override
	default boolean isEventType() {
		return true;
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
