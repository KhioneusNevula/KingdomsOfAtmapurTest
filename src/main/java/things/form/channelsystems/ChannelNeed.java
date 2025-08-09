package things.form.channelsystems;

import things.interfaces.UniqueType;
import thinker.concepts.IConcept;
import thinker.concepts.application.IConceptApplier;
import thinker.concepts.application.INeedApplier;

public class ChannelNeed implements IChannelNeed {
	private String name;
	private IChannelSystem channelSystem;
	private String csn;

	public ChannelNeed(String name, IChannelSystem channelSystem) {
		this.name = name;
		this.channelSystem = channelSystem;
		this.csn = channelSystem.name();
	}

	@Override
	public FeelingReasonType getReasonType() {
		return FeelingReasonType.NEED;
	}

	@Override
	public String getName() {
		return name;
	}

	public Float defaultValue() {
		return 1f;
	}

	@Override
	public Class<? super Float> getType() {
		return float.class;
	}

	@Override
	public IChannelSystem getChannelSystem() {
		return this.channelSystem;
	}

	@Override
	public String getChannelSystemName() {
		return this.csn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof IChannelNeed in) {
			return this.name.equals(in.getName()) && this.csn.equals(in.getChannelSystemName());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.csn.hashCode();
	}

	@Override
	public String toString() {
		return "<:" + this.getClass().getSimpleName() + "(associator):" + this.csn + ":" + this.name + ":>";
	}

	@Override
	public String getPropertyName() {
		return this.getName();
	}

	@Override
	public ConceptType getConceptType() {
		return ConceptType.C_ASSOCIATION_INFO;
	}

	@Override
	public String getUnderlyingName() {
		return "channelNeed_" + this.csn + ":" + this.name;
	}

	private ChannelNeedApplier applier = new ChannelNeedApplier();

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IConceptApplier> T getApplier() {
		return (T) applier;
	}

	private class ChannelNeedApplier implements INeedApplier {

		@Override
		public boolean applies(Object forThing, IConcept checker) {
			return forThing.equals(ChannelNeed.this);
		}

		@Override
		public UniqueType forType() {
			return UniqueType.N_A;
		}

		@Override
		public IChannelNeed getPhysicalNeed() {
			return ChannelNeed.this;
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "(" + ChannelNeed.this + ")";
		}
	}
}
