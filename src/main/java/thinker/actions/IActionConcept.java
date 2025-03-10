package thinker.actions;

import java.util.Collection;

import _utilities.graph.IRelationGraph;
import _utilities.property.IProperty;
import things.form.condition.IFormCondition;
import things.form.soma.component.IComponentPart;
import thinker.IKnowledgeBase;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.individual.IMindSpirit;
import thinker.mind.will.IWill;

/**
 * Concepts<br>:
 * 
	I want to increase my satiation. First: Ponder; Look for Satiation -satisfied_by-> ???
	
	Found nothing. Therefore, look at the need condition:<br>
	SATISFIER -increases-> satiation.
	
	Invert relation: Look for Satiation -increased_by-> (Actions)<br>
	Actions: Eat (intention = THIS_ACTION -increases-> satiation), MagicEat (intention = THIS_ACTION -increases-> satiation)<br>
	-> Eat, which has condition (TARGET -at_location-> Mouth, TARGET -categorized_as-> Food)
	
	Invert relation: Since TARGET is Food, Look for action that uses Food -moved_by-> (Actions). Check these actions for the intention (TARGET -at-> mouth), then find the intersection, 
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
public interface IActionConcept extends IConcept {
	/**
	 * An action in and of itself, represented for {@link #getIntention()}
	 */
	public static final IConcept THIS_ACTION = new IConcept() {
		public ConceptType getConceptType() {
			return ConceptType.THE_CONCEPT_ITSELF;
		}

		@Override
		public String getUnderlyingName() {
			return "action_itself";
		}

		public String toString() {
			return "concept_action_itself";
		}
	};

	/**
	 * A concept representing the target of an action
	 */
	public static final IConcept TARGET = new IConcept() {
		public ConceptType getConceptType() {
			return ConceptType.THE_CONCEPT_ITSELF;
		}

		@Override
		public String getUnderlyingName() {
			return "action_target";
		}

		public String toString() {
			return "concept_action_target";
		}
	};

	/**
	 * A concept representing a "location" target of an action
	 */
	public static final IConcept LOCATION_TARGET = new IConcept() {
		public ConceptType getConceptType() {
			return ConceptType.THE_CONCEPT_ITSELF;
		}

		@Override
		public String getUnderlyingName() {
			return "action_location_target";
		}

		public String toString() {
			return "concept_action_location_target";
		}
	};

	/**
	 * A concept representing the doer of an action.
	 */
	public static final IConcept ACTION_DOER = new IConcept() {
		public ConceptType getConceptType() {
			return ConceptType.THE_CONCEPT_ITSELF;
		}

		@Override
		public String getUnderlyingName() {
			return "action_doer";
		}

		public String toString() {
			return "concept_action_doer";
		}
	};

	/**
	 * A concept representing the means of an action
	 */
	public static final IConcept MEANS = new IConcept() {
		public ConceptType getConceptType() {
			return ConceptType.THE_CONCEPT_ITSELF;
		}

		@Override
		public String getUnderlyingName() {
			return "action_means";
		}

		public String toString() {
			return "concept_action_means";
		}
	};

	/**
	 * Return a graph of what conditions this action usually fulfills.
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getIntention();

	/**
	 * Returns a graph of what knowledge this action imparts
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> getKnowledgeIntention();

	/**
	 * Return an {@link IFormCondition} of what is expected of the given body to
	 * perform the action; the body will check if the condition is true when running
	 * the action. Return null if it is impossible for this body to ever meet the
	 * requisite conditions
	 * 
	 * @param forSpirit
	 * @param inWill
	 * @param inPart
	 * @param access
	 * @param ticks
	 * @return
	 */
	public IFormCondition bodyConditions(IMindSpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks);

	/**
	 * Called if a body condition suddenly returns false and the action is
	 * physically prevented from continuing
	 */
	public void abortAction(IMindSpirit spirit, IWill will, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks);

	/**
	 * If this action can currently execute. If not, call
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
	public boolean canExecute(IMindSpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention, long ticks);

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
	public void executeTick(IMindSpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention, long ticks);

	/**
	 * Creates a condition for this action based on the given intention. A condition
	 * uses AND-matching by default; OR-matching must be specified
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(IMindSpirit forSpirit, IWill inWill,
			IComponentPart inPart, Collection<? extends IComponentPart> access,
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention);

	/**
	 * Creates a condition for what *knowledge* must be known based on the given
	 * intention
	 * 
	 * @param intention
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> generateKnowledgeCondition(IMindSpirit forSpirit,
			IWill inWill, IComponentPart inPart, Collection<? extends IComponentPart> access,
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention);

	/**
	 * Integrates this action into a knowledge-base by embedding its relationships.
	 * "Doer" indicates who is considered the doer of the action--may be
	 * "selfConcept", might be a role, might be something else, etc Return null if
	 * this cannot be accomplished for some reason
	 */
	public boolean integrateIntoKnowledgeBase(IKnowledgeBase base, IConcept doer);

	/**
	 * Whether this action directly satisfies some need, e.g. eating directly
	 * satisfies a food need
	 */
	public boolean directlySatisfiesNeed();

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
