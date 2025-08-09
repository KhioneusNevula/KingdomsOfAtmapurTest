package thinker.actions.types;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import _utilities.couplets.Triplet;
import _utilities.graph.IRelationGraph;
import _utilities.graph.RelationGraph;
import things.form.condition.FormCondition;
import things.form.condition.IFormCondition;
import things.form.condition.IFormCondition.FormRelationType;
import things.form.condition.IFormCondition.IFormRelationType;
import things.form.graph.connections.PartConnection;
import things.form.soma.ISoma;
import things.form.soma.abilities.PartAbility;
import things.form.soma.component.IComponentPart;
import things.interfaces.UniqueType;
import thinker.actions.IActionConcept;
import thinker.concepts.IConcept;
import thinker.concepts.profile.IProfile;
import thinker.concepts.relations.IConceptRelationType;
import thinker.concepts.relations.actional.EventRelationType;
import thinker.concepts.relations.descriptive.ProfileInterrelationType;
import thinker.concepts.relations.util.RelationsHelper;
import thinker.goals.IGoalConcept;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IMindAccess;

/**
 * Action for transferring something held in one part to another
 */
public enum PutActionConcept implements IActionConcept {
	INSTANCE;

	private RelationGraph<IConcept, IConceptRelationType> intention;

	/**
	 */
	private PutActionConcept() {
		intention = new RelationGraph<>();
		intention.add(IProfile.anyOf(UniqueType.FORM));
		intention.add(IActionConcept.THIS_ACTION);
		intention.addEdge(THIS_ACTION, EventRelationType.GRABS, IProfile.anyOf(UniqueType.FORM));
		intention.addEdge(THIS_ACTION, EventRelationType.ACTS_ON, IProfile.anyOf(UniqueType.FORM));
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.ACTION;
	}

	@Override
	public String getUnderlyingName() {
		return "put_action_concept";
	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateIntendedResult(IKnowledgeBase knowledge) {
		return intention;
	}

	@Override
	public IFormCondition bodyConditions(IMindAccess info) {
		RelationGraph<Object, IFormRelationType> somagraph = new RelationGraph<>();
		somagraph.add("hand");
		somagraph.add(PartAbility.GRASP);
		somagraph.addEdge("hand", FormRelationType.HAS_ABILITY, PartAbility.GRASP);

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
		ISoma so = info.maybeSoma().get();
		for (IComponentPart part : info.partAccess()) {
			if (part.getAbilities().contains(PartAbility.GRASP)) {
				Collection<IComponentPart> neis = so.getPartGraph().getNeighbors(part, PartConnection.HOLDING);
				if (neis.stream().map(IComponentPart::getTrueOwner).anyMatch(
						(b) -> intention.getNeighbors(THIS_ACTION, EventRelationType.ACTS_ON).stream().anyMatch(
								(target) -> RelationsHelper.matchesTypeProfile(so, target, intention, false)))) { // need
					// to
					// implement
					// and/or/not
					// oof
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public void executeTick(IMindAccess info, IRelationGraph<IConcept, IConceptRelationType> intention) {
		// TODO moving stuffs
		if (info.maybeSoma().isEmpty())
			return;
		ISoma so = info.maybeSoma().get();
		for (IComponentPart part : info.partAccess()) {
			if (part.getAbilities().contains(PartAbility.GRASP)) {

			}
		}

	}

	@Override
	public IRelationGraph<IConcept, IConceptRelationType> generateCondition(IMindAccess info,
			IRelationGraph<IConcept, IConceptRelationType> intention) {

		Set<IProfile> parts = Streams.stream(info.knowledge().getConnectedConcepts(this, EventRelationType.USES_PART))
				.map((a) -> (IProfile) a).collect(Collectors.toSet());

		RelationGraph<IConcept, IConceptRelationType> cond = new RelationGraph<>();
		Iterable<Triplet<IConcept, IConceptRelationType, IConcept>> edgeIterable = () -> intention
				.edgeIterator(Collections.singleton(ProfileInterrelationType.HELD_BY));
		for (Triplet<IConcept, IConceptRelationType, IConcept> edge : edgeIterable) {
			IProfile item = (IProfile) edge.getEdgeStart();
			cond.add(item);

			for (IProfile part : parts) {
				cond.add(part);
				cond.addEdge(item, ProfileInterrelationType.HELD_BY, part);
			}
		}

		return cond;
	}

	@Override
	public String toString() {
		return this.getDeclaringClass().getSimpleName();
	}

}
