package things.physical_form.channelsystems.signal;

import java.util.Collection;
import java.util.Collections;

import things.physical_form.channelsystems.IChannel;
import things.physical_form.material.IMaterial;
import utilities.graph.IInvertibleRelationType;

/**
 * A bidirectional Channel representing a channel for signal to travel through
 */
public class SignalChannel implements IChannel {

	private String name;
	private Collection<IMaterial> material;
	private Collection<SignalChannelResource> conveys;

	public SignalChannel(String name, IMaterial nerveMaterial, SignalChannelResource signal) {
		this.name = name;
		this.material = Collections.singleton(nerveMaterial);
		this.conveys = Collections.singleton(signal);
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

}
