package things.form.channelsystems.blood;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelSystem;
import things.form.material.IMaterial;

/**
 * A bidirectional Channel representing a channel for signal to travel through
 */
public class BloodVesselChannel implements IChannel {

	private String name;
	private Collection<IMaterial> material;
	private Collection<IMaterial> blood;
	private IChannelSystem system;

	/**
	 * 
	 * @param name
	 * @param vessMaterial if this is null, no blood vessels
	 * @param blood
	 * @param system
	 */
	public BloodVesselChannel(String name, IMaterial vessMaterial, IMaterial blood, IChannelSystem system) {
		this.name = name;
		this.material = vessMaterial == null ? Collections.emptySet() : Collections.singleton(vessMaterial);
		this.blood = Collections.singleton(blood);
		this.system = system;
	}

	@Override
	public boolean severable() {
		return true;
	}

	@Override
	public BloodVesselChannel invert() {
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
	public Collection<IMaterial> conveys() {
		return blood;
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
		if (this == obj)
			return true;
		if (obj instanceof BloodVesselChannel ic) {
			return this.name.equals(ic.name()) && this.material.equals(ic.getVectorMaterials())
					&& this.blood.equals(ic.conveys());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + material.hashCode() + blood.hashCode();
	}

}
