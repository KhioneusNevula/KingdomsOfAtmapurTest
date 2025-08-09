package thinker.actions.types;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import _utilities.graph.EmptyGraph;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import things.form.condition.FormCondition;
import things.form.condition.IFormCondition;
import things.form.condition.IFormCondition.FormRelationType;
import things.form.condition.IFormCondition.IFormRelationType;
import things.form.soma.ISoma;
import things.form.soma.abilities.PartAbility;
import things.interfaces.UniqueType;
import thinker.actions.ActionsHelper;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IMindAccess;

/**
 * Action for putting something that is held in one's hands into/on some
 * specific part of something
 */
public enum WalkActionConcept implements IActionConcept {
	INSTANCE;

	/**
	 */
	private WalkActionConcept() {
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.ACTION;
	}

	@Override
	public String getUnderlyingName() {
		return "walk_action_concept";
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateIntendedResult(IKnowledgeBase knowledge) {
		return new RelationGraph<IConcept, IConceptRelationType>()
				.plusEdges(this, EventRelationType.MOVES, ActionsHelper.getBodyConcepts(knowledge))
				.plusEdges(IProfile.anyOf(UniqueType.PLACE), EventRelationType.IS_TARGET_OF_MOVE,
						ActionsHelper.getBodyConcepts(knowledge));
	}

	@Override
	public IFormCondition bodyConditions(IMindAccess info) {
		RelationGraph<Object, IFormRelationType> somagraph = new RelationGraph<>();
		somagraph.add("foot");
		somagraph.add(PartAbility.WALK);
		somagraph.addEdge("foot", FormRelationType.HAS_ABILITY, PartAbility.WALK);

		FormCondition condition = new FormCondition(somagraph);
		return condition;

	}

	@Override
	public void abortAction(IMindAccess info) {

	}

	@Override
	public boolean canExecute(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention) {
		if (info.maybeSoma().isEmpty())
			return false;
		return true;

	}

	@Override
	public void executeTick(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention) {
		// TODO walking stuffs
		if (info.maybeSoma().isEmpty())
			return;
		ISoma so = info.maybeSoma().get();
		Collection<IProfile> doers = ActionsHelper.getDoerOfEvent(this, intention);
		Collection<IProfile> bodies = doers.stream().flatMap(
				(a) -> Streams.stream(info.knowledge().getConnectedConcepts(a, ProfileInterrelationType.EMBODIED_AS)))
				.map((a) -> (IProfile) a).collect(Collectors.toSet());
		;
		if (so.getOwner() != null) {
			for (IProfile des : (Iterable<IProfile>) () -> bodies.stream()
					.flatMap((body) -> intention.getNeighbors(body, ProfileInterrelationType.AT_LOCATION).stream())
					.map((a) -> (IProfile) a).iterator()) {

			}
		}

	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(IMindAccess info,
			IRelationGraph<IConcept, IConceptRelationType> intention) {

		return EmptyGraph.instance();
	}

	@Override
	public String toString() {
		return this.getDeclaringClass().getSimpleName();
	}

}
