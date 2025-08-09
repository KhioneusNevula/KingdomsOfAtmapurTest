package thinker.concepts.general_types;

import java.util.UUID;

import _utilities.graph.IRelationGraph;
import things.form.condition.IFormCondition;
import thinker.concepts.IConcept;
import thinker.concepts.relations.IConceptRelationType;
import thinker.knowledge.base.IKnowledgeBase;
import thinker.mind.util.IBeingAccess;

/**
 * Pattern concept impl for {@link IActionPatternConcept}
 * 
 * @author borah
 *
 */
public class ActionPatternConcept extends PatternConcept implements IActionPatternConcept {

	public ActionPatternConcept(UUID id) {
		super(id);
	}

	@Override
	public boolean isEventType() {
		return true;
	}

	@Override
	public String getUnderlyingName() {
		return "action_" + super.getUnderlyingName();
	}

	@Override
	public String toString() {
		return "Action" + super.toString();
	}

	@Override
	public boolean isAction() {
		return true;
	}

	@Override
	public boolean isType() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IActionPatternConcept) {
			return super.equals(obj);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() + IActionPatternConcept.class.hashCode();
	}

}
