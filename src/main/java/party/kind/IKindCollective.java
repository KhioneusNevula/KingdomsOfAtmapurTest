package party.kind;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import party.collective.ICollective;
import party.kind.spawning.IKindSpawningContext;
import things.form.kinds.IKind;
import thinker.concepts.profile.IProfile;

/**
 * A party representing the entirety of one kind (to act as a receptacle for
 * kind-level knowledge and such).
 */
public interface IKindCollective extends ICollective {

	/** The {@link IKind} that this Collective represents */
	public IKind getKind();

	/** Return the context in which this kind can spawn */
	public IKindSpawningContext getSpawnContext();

	@Override
	default Collection<IKind> memberKinds() {
		return Collections.singleton(this.getKind());
	}

	/**
	 * Returns a descriptive concept representing the physical forms of the members
	 * of the collective
	 */
	public IProfile getFormTypeProfile();

	/**
	 * Returns a descriptive concept representing the being concept of members of
	 * the collective, or an empty ptional if that's not applicable
	 */
	public Optional<IProfile> maybeGetFigureTypeProfile();
}
