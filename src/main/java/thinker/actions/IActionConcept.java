package thinker.actions;

import java.util.Collection;

import things.form.soma.component.IComponentPart;
import things.form.soma.condition.ISomaCondition;
import things.spirit.ISpirit;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.mind.will.IWill;
import utilities.graph.IRelationGraph;

/**
 * Concepts<br>:
 * 
	I want to increase my satiation. First: Ponder; Look for Satiation -satisfied_by-> ???
	
	Found nothing. Therefore, look at the need condition:<br>
	SATISFIER -increases-> satiation.
	
	Invert relation: Look for Satiation -increased_by-> (Actions)<br>
	Actions: Eat (intention = THIS_ACTION -increases-> satiation), MagicEat (intention = THIS_ACTION -increases-> satiation)<br>
	-> Eat, which has condition (SATISFIER -target_to_part-> Mouth, SATISFIER -targets-> Food)
	
	Invert relation: Look for action that Mouth -target_part_of-> (Actions) and also Food -target_of-> (Actions), then find the intersection, 
		as well as including actions which have a -targets- relation to anything that has an -is- relation to Food, 
		or actions with a -targets- relation to anything that has a CATEGORIZES or CATEGORIZED_BY relation to Food. 
	Actions: Put (intention = THIS_ACTION -target_to_part-> Part, THIS_ACTION -targets-> Thing)
	-> Put, which has condition (SATISFIER -target_to_part-> Hand, (generated condition) SATISFIER -targets-> Food)
	
	Invert relation: Look for action that Hand -target_part_of (actions) and also Food -target_of-> (Actions), 
		then find the intersection with the similar caveats as above.
	Actions: Pick Up (intention = THIS_ACTION -target_to_part-> Hand, THIS_ACTION -acquires-> Thing), Summon (intention = (same)), 
		Cook (intention = THIS_ACTION -creates-> Food, THIS_ACTION -acquires-> Food, THIS_ACTION -target_to_part-> Hand)
	-> Pick Up, which has only the condition ((generated) SATISFIER -self_to-> Food)
	
	Invert relation: Look for action that has Food -self_target_location_of-> (actions) with similar caveats as above
	Actions: Walk (intention = THIS_ACTION -self_to-> Any_Place), Teleport (intention (same))
	-> Teleport, which has a Knowledge condition that (SATISFIER -place_where-> Food).
	
	If this is known, do the actions!
	Otherwise...
	
	Invert relation: Look for Action that have Food -at_location-> WHERE-QUESTION -answered_by-> (actions) with similar caveats as above
	Actions: Search (intention = THIS_ACTION -answers-> WHERE-QUESTION -place_where-> Thing), Ask (intention = THIS_ACTION -answers-> ANY_QUESTION)
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
	 * Return a graph of what conditions this action usually fulfills
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
	 * Return an {@link ISomaCondition} of what is expected of the given body to
	 * perform the action. Return an empty condition if the action is plausible with
	 * the given body. Return null if it is impossible for this body to ever meet
	 * the requisite conditions
	 * 
	 * @param forSpirit
	 * @param inWill
	 * @param inPart
	 * @param access
	 * @param ticks
	 * @return
	 */
	public ISomaCondition bodyConditions(ISpirit forSpirit, IWill inWill, IComponentPart inPart,
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
	 * @param ticks
	 * @return
	 */
	public boolean canExecute(ISpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks);

	/**
	 * Execute this action.
	 * 
	 * @param forSpirit
	 * @param inWill
	 * @param inPart
	 * @param access
	 * @param ticks
	 * @return
	 */
	public void executeTick(ISpirit forSpirit, IWill inWill, IComponentPart inPart,
			Collection<? extends IComponentPart> access, long ticks);

	/**
	 * Creates a condition for this action based on the given intention
	 * 
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention);

	/**
	 * Creates a condition for what *knowledge* must be known based on the given
	 * intention
	 * 
	 * @param intention
	 * @return
	 */
	public IRelationGraph<IConcept, IConceptRelationType> generateKnowledgeCondition(
			IRelationGraph<IConcept, IConceptRelationType> intention,
			IRelationGraph<IConcept, IConceptRelationType> knowledgeIntention);

}
