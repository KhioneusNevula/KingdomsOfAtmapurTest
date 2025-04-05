package party.relations.types;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import party.IParty;
import thinker.concepts.IConcept.ConceptType;
import thinker.concepts.general_types.IDescriptiveConcept;
import thinker.concepts.relations.IConceptRelationType;

public enum PartyRelationType implements IPartyRelationType, IConceptRelationType {
	/** Giving resource to the other end */
	GIVES_TO(1, 0, false), RECEIVES_FROM(GIVES_TO),
	/** Controls the actions that the other end may do */
	LEGISLATES(0, 1, false), LEGISLATED_BY(LEGISLATES),
	/** Sets the goals that the other end must complete */
	COMMANDS(0, 1, false), COMMANDED_BY(COMMANDS),
	/** Controls the punishment for the other end breaking rules */
	JUDGES(0, 1, false), JUDGED_BY(JUDGES),
	/** This end harmfully takes some resource from the other end */
	DEPLETES(-1, 0, true), DEPLETED_BY(DEPLETES),
	/** This end is a member of the entity on the other end */
	MEMBER_OF(0, 0, false), HAS_MEMBER(MEMBER_OF),
	/**
	 * This end derives knowledge from the entity on the other end, i.e. the other
	 * end's Knowledge is a Parent to this one's Knowledge
	 */
	KNOWLEDGE_MEMBER_OF(0, 0, false), HAS_KNOWLEDGE_MEMBER(KNOWLEDGE_MEMBER_OF);

	private int resource;
	private int control;
	private boolean harms;
	private PartyRelationType opposite = this;

	private PartyRelationType(int resource, int control, boolean harms) {
		this.resource = resource;
		this.control = control;
		this.harms = harms;
	}

	private PartyRelationType(PartyRelationType opposite) {
		this(-opposite.resource, -opposite.control, false);
		this.opposite = opposite;
		opposite.opposite = this;
	}

	@Override
	public Collection<Class<?>> getEndClasses() {
		return Set.of(IParty.class, IDescriptiveConcept.class);
	}

	@Override
	public Object checkEndType(Object node) {
		if (node instanceof IParty)
			return null;
		return IConceptRelationType.super.checkEndType(node);
	}

	@Override
	public Integer maxPermitted() {
		return null;
	}

	@Override
	public ConceptType getEndType() {
		return ConceptType.NONE;
	}

	@Override
	public PartyRelationType invert() {
		return opposite;
	}

	@Override
	public boolean bidirectional() {
		return opposite == this;
	}

	@Override
	public boolean sendsResource() {
		return resource > 0;
	}

	@Override
	public boolean receivesResource() {
		return resource < 0;
	}

	@Override
	public boolean controls() {
		return control > 0;
	}

	@Override
	public boolean submits() {
		return control < 0;
	}

	@Override
	public boolean harms() {
		return harms;
	}

	@Override
	public boolean isHarmedByOther() {
		return opposite.harms;
	}

	@Override
	public boolean hasKnowledgeParent() {
		return this == KNOWLEDGE_MEMBER_OF;
	}

	@Override
	public boolean isKnowledgeParent() {
		return this == HAS_KNOWLEDGE_MEMBER;
	}
}
