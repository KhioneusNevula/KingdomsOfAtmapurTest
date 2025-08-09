package party.kind;

import java.util.Optional;
import java.util.UUID;

import party.kind.spawning.IKindSpawningContext;
import things.form.kinds.IKind;
import things.interfaces.UniqueType;
import thinker.concepts.profile.IProfile;
import thinker.knowledge.base.IKnowledgeBase;

/**
 * A kind collective to generate differnet types of beings which have souls
 */
public class SentientKindCollective implements IKindCollective {

	private IKind kind;
	private IKindSpawningContext context;
	private UUID uuid;
	private IProfile profile;
	private IProfile formprop;
	private IProfile beingprop;
	private IKnowledgeBase knowledge;

	public SentientKindCollective(IProfile groupID, IKind kind, IKindSpawningContext context,
			IKnowledgeBase knowledge) {
		this.context = context;
		this.kind = kind;
		this.uuid = groupID.getUUID();
		this.profile = groupID.withType(UniqueType.COLLECTIVE);
		this.formprop = IProfile.typeOf(UniqueType.FORM, this.uuid, kind.name());
		this.beingprop = IProfile.typeOf(UniqueType.FIGURE, this.uuid, kind.name());
		this.knowledge = knowledge;
	}

	@Override
	public Integer getCount() {
		return null;
	}

	@Override
	public IKindSpawningContext getSpawnContext() {
		return context;
	}

	@Override
	public IProfile getFormTypeProfile() {
		return formprop;
	}

	@Override
	public Optional<IProfile> maybeGetFigureTypeProfile() {
		return Optional.of(beingprop);
	}

	@Override
	public IKindSpawningContext getMemberSpawnContext(IKind forKind) {
		if (!forKind.equals(this.kind)) {
			throw new IllegalArgumentException();
		}
		return context;
	}

	@Override
	public String report() {
		return this.getClass().getSimpleName() + "{kind=" + this.kind + ",profile=" + this.profile + ",spawnContext="
				+ this.context + ",knowledge=" + this.knowledge.getUnmappedConceptGraphView() + "}";
	}

	@Override
	public IProfile getProfile() {
		return this.profile;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public IKnowledgeBase getKnowledge() {
		return knowledge;
	}

	@Override
	public IKind getKind() {
		return kind;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.kind + ")" + "{" + this.profile + "}";
	}

}
