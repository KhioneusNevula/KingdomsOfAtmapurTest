package thinker.mind.perception.sensation;

import things.form.soma.IPartHealth;
import things.form.soma.component.IComponentPart;
import thinker.mind.util.IBeingAccess;

/**
 * A receptor which uses a {@link IPartHealth} function to determine what level
 * of "damage" it is experiencing; "damage" is increased inversely to the
 * overall health of its parts. Part size plays a role in weighting the level of
 * damage, as does the sensation's {@link ISensation#getSensitivityStat()}
 */
public class DamageSensationReceptor implements ISensationReceptor {

	private IPartHealth damage;

	public DamageSensationReceptor(IPartHealth damage) {
		this.damage = damage;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" + this.damage + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof DamageSensationReceptor dsr)
			return this.damage.equals(dsr.damage);
		return false;
	}

	@Override
	public int hashCode() {
		return damage.hashCode() + this.getClass().hashCode();
	}

	@Override
	public float getSensationLevel(ISensation sensation, IBeingAccess info) {
		float totalWeight = 0;
		float totalPain = 0;

		for (IComponentPart cp : info.partAccess()) {
			float sensitivity = cp.getStat(sensation.getSensitivityStat());
			if (sensitivity <= 0)
				continue;
			float psize = cp.getRelativeSize();
			float pWeight = psize * sensitivity;
			float pain = 1 - damage.health(info.maybeSoma().orElseThrow(
					() -> new UnsupportedOperationException("Cannot detect damage on nonexistent soma..." + info)), cp);

			totalWeight += pWeight;
			totalPain += pain * pWeight;
			if (pain != 0) {
				continue;
			}
		}
		if (totalWeight == 0) {
			return 0f;
		}
		return totalPain / totalWeight;
	}

}
