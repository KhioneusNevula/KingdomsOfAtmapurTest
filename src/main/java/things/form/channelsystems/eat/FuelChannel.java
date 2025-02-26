package things.form.channelsystems.eat;

import java.util.Collection;
import java.util.Collections;

import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelSystem;
import things.form.material.IMaterial;

/**
 * A bidirectional Channel representing a channel for fuel to travel through
 */
public class FuelChannel implements IChannel {

	private String name;
	private Collection<IMaterial> material;
	private Collection<FuelChannelResource> fuel;
	private IChannelSystem system;

	/**
	 * 
	 * @param name
	 * @param vessMaterial if this is null, no physical esophagus
	 * @param fuel
	 * @param system
	 */
	public FuelChannel(String name, IMaterial vessMaterial, FuelChannelResource food, IChannelSystem system) {
		this.name = name;
		this.material = vessMaterial == null ? Collections.emptySet() : Collections.singleton(vessMaterial);
		this.fuel = Collections.singleton(food);
		this.system = system;
	}

	@Override
	public boolean severable() {
		return true;
	}

	@Override
	public FuelChannel invert() {
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
	public Collection<FuelChannelResource> conveys() {
		return fuel;
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
		if (obj instanceof FuelChannel ic) {
			return this.name.equals(ic.name()) && this.material.equals(ic.getVectorMaterials())
					&& this.fuel.equals(ic.conveys());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + material.hashCode() + fuel.hashCode() + this.getClass().hashCode();
	}

}
