package things.form.channelsystems.energy;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelSystem;
import things.form.material.IMaterial;

/**
 * A bidirectional Channel representing a channel for magic energy to travel
 * through
 */
public class EnergyChannel implements IChannel {

	private String name;
	private Collection<EnergyChannelResource> conveys;
	private IChannelSystem system;

	public EnergyChannel(String name, EnergyChannelResource signal, IChannelSystem system) {
		this.name = name;
		this.conveys = Collections.singleton(signal);
		this.system = system;
	}

	@Override
	public boolean severable() {
		return false;
	}

	@Override
	public EnergyChannel invert() {
		return this;
	}

	@Override
	public boolean bidirectional() {
		return true;
	}

	@Override
	public Collection<IMaterial> getVectorMaterials() {
		return Collections.emptySet();
	}

	@Override
	public Collection<EnergyChannelResource> conveys() {
		return conveys;
	}

	@Override
	public boolean isActiveDirection() {
		return true;
	}

	@Override
	public String toString() {
		return ">>" + name + ">>";
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public IChannelSystem getSystem() {
		return system;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnergyChannel ic) {
			return this.name.equals(ic.name()) && this.conveys.equals(ic.conveys());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + conveys.hashCode() + this.getClass().hashCode();
	}

}
