package things.physical_form.channelsystems.signal;

import things.physical_form.channelsystems.IChannelResource;

/**
 * A Signal resource is just a boolean value indicating presence or absence of
 * signal.
 * 
 * @author borah
 *
 */
public class SignalChannelResource implements IChannelResource<Boolean> {

	private String name;

	public SignalChannelResource(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Boolean getEmptyValue() {
		return false;
	}

	@Override
	public Class<Boolean> getMeasureClass() {
		return boolean.class;
	}

	@Override
	public Boolean add(Boolean instance, Boolean instance2) {
		return instance || instance2;
	}

	@Override
	public Boolean subtract(Boolean g, Boolean l) {
		if (!g)
			return null;
		return g;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SignalChannelResource rs) {
			return this.name.equals(rs.name);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
