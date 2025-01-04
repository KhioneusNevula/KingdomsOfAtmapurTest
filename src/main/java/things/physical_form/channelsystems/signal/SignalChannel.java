package things.physical_form.channelsystems.signal;

import java.util.Collection;
import java.util.Collections;

import things.physical_form.channelsystems.IChannel;
import things.physical_form.channelsystems.IChannelSystem;
import things.physical_form.material.IMaterial;
import utilities.graph.IInvertibleRelationType;

/**
 * A bidirectional Channel representing a channel for signal to travel through
 */
public class SignalChannel implements IChannel {

	private String name;
	private Collection<IMaterial> material;
	private Collection<SignalChannelResource> conveys;
	private IChannelSystem system;

	public SignalChannel(String name, IMaterial nerveMaterial, SignalChannelResource signal, IChannelSystem system) {
		this.name = name;
		this.material = Collections.singleton(nerveMaterial);
		this.conveys = Collections.singleton(signal);
		this.system = system;
	}

	@Override
	public boolean severable() {
		return true;
	}

	@Override
	public IInvertibleRelationType invert() {
		return this;
	}

	@Override
	public boolean bidirectional() {
		return true;
	}

	@Override
	public Collection<IMaterial> getVectorMaterials() {
		return material;
	}

	@Override
	public Collection<SignalChannelResource> conveys() {
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
		if (obj instanceof IChannel ic) {
			return this.name.equals(ic.name()) && this.material.equals(ic.getVectorMaterials())
					&& this.conveys.equals(ic.conveys());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + material.hashCode() + conveys.hashCode();
	}

}
