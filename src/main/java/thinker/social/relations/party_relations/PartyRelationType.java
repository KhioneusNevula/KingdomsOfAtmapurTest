package thinker.social.relations.party_relations;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import thinker.concepts.relations.IConceptRelationType;

public enum PartyRelationType implements IPartyRelationType, IConceptRelationType {
	/** Giving resources to the other end */
	GIVES_TO(1, 0, false, false), RECEIVES_FROM(GIVES_TO),
	/** Gives information to the other end */
	GIVES_KNOWLEDGE_TO(1, 0, false, false), RECEIVES_KNOWLEDGE_FROM(GIVES_KNOWLEDGE_TO),
	/** Giving protection to the other end */
	PROTECTS(1, 0, false, false), PROTECTED_BY(PROTECTS),
	/** Controls the rules that the other end must follow */
	LEGISLATES(0, 1, false, false), LEGISLATED_BY(LEGISLATES),
	/** Sets the goals that the other end must work toward */
	COMMANDS(0, 1, false, false), COMMANDED_BY(COMMANDS),
	/** Controls the punishment for the other end breaking rules */
	JUDGES(0, 1, false, false), JUDGED_BY(JUDGES),
	/** This end is obligated to attack or damage the other end */
	ATTACKS(0, 0, true, false), ATTACKED_BY(ATTACKS),
	/** This end steals some resource from the other end */
	STEALS_FROM(-1, 0, true, true), STOLEN_FROM(STEALS_FROM),
	/** This end secretly obtains knowledge from the other end */
	SPIES_ON(-1, 0, true, true), SPIED_ON_BY(SPIES_ON),
	/** This end is a member of the entity on the other end */
	MEMBER_OF(0, 0, false, false), HAS_MEMBER(MEMBER_OF),
	/**
	 * This end derives knowledge from the entity on the other end, i.e. the other
	 * end's Knowledge is a Parent to this one's Knowledge
	 */
	KNOWLEDGE_MEMBER_OF(0, 0, false, false), HAS_KNOWLEDGE_MEMBER(KNOWLEDGE_MEMBER_OF);

	private static final Set<PartyRelationType> KNOWABLE;
	private static final Set<PartyRelationType> UNKNOWN;

	private int resource;
	private int control;
	private boolean harms;
	private PartyRelationType opposite = this;
	private boolean secret;

	static {
		ImmutableSet.Builder<PartyRelationType> nonsec = ImmutableSet.builder();
		ImmutableSet.Builder<PartyRelationType> sec = ImmutableSet.builder();
		for (PartyRelationType type : values()) {
			if (!type.isNotKnown()) {
				nonsec.add(type);
			} else {
				sec.add(type);
			}
		}
		KNOWABLE = nonsec.build();
		UNKNOWN = sec.build();
	}

	private PartyRelationType(int resource, int control, boolean harms, boolean secret) {
		this.resource = resource;
		this.control = control;
		this.harms = harms;
		this.secret = secret;
	}

	private PartyRelationType(PartyRelationType opposite) {
		this(-opposite.resource, -opposite.control, false, false);
		this.opposite = opposite;
		opposite.opposite = this;
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
	public boolean isSecret() {
		return secret;
	}

	@Override
	public boolean isNotKnown() {
		return opposite.secret;
	}

	@Override
	public boolean hasKnowledgeParent() {
		return this == KNOWLEDGE_MEMBER_OF;
	}

	@Override
	public boolean isKnowledgeParent() {
		return this == HAS_KNOWLEDGE_MEMBER;
	}

	/**
	 * Return a collection of all relations which are known to the Start end of the
	 * relation
	 * 
	 * @return
	 */
	public static Collection<PartyRelationType> knowableRelations() {
		return KNOWABLE;
	}

	/**
	 * Return a collection of all relations which are not known to the Start end of
	 * the relation
	 * 
	 * @return
	 */
	public static Collection<PartyRelationType> unknownRelations() {
		return UNKNOWN;
	}

	@Override
	public boolean characterizesOther() {
		return false;
	}

	@Override
	public boolean isCharacterizedByOther() {
		return false;
	}

}
