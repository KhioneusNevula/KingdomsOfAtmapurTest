package metaphysics.magic;

import thinker.concepts.profile.IProfile;

public class ImTether implements ITether {

	private TetherType ttype;
	private IProfile toProfile;
	private int caps = TetherAbility.getAllCaps();

	protected ImTether(IProfile toProfile, TetherType ttype) {
		this.ttype = ttype;
		this.toProfile = toProfile;
	}

	@Override
	public TetherType getTetherType() {
		return ttype;
	}

	@Override
	public IProfile getReferentProfile() {
		return toProfile;
	}

	@Override
	public void setCapabilities(int caps) {
		this.caps = caps;
	}

	@Override
	public int getCapabilities() {
		return caps;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof ITether iit) {
			return this.toProfile.equals(iit.getReferentProfile()) && this.ttype == iit.getTetherType()
					&& this.caps == iit.getCapabilities();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toProfile.hashCode() * (1 + ttype.ordinal()) + (caps);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "_" + this.ttype + "{p=" + this.toProfile + ",c=" + caps + "("
				+ TetherAbility.get(caps) + ")" + "}";
	}

}
