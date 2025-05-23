package things.form.channelsystems.signal;

import things.form.channelsystems.IResource;

/**
 * A Signal resource is just a boolean value indicating presence or absence of
 * signal.
 * 
 * @author borah
 *
 */
public class SignalChannelResource implements IResource<Boolean> {

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
	public Boolean getMaxValue() {
		return true;
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
	public Boolean divide(Boolean g, int by) {
		return g;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IResource rs) {
			return this.name.equals(rs.name()) && this.getMeasureClass().equals(rs.getMeasureClass());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + this.getMeasureClass().hashCode();
	}

}
