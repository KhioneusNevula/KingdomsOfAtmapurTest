package things.form.channelsystems.eat;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Sets;

import _sim.world.GameMap;
import things.form.channelsystems.ChannelNeed;
import things.form.channelsystems.IChannel;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IChannelCenter.ChannelRole;
import things.form.channelsystems.IChannelNeed;
import things.form.channelsystems.IChannelSystem;
import things.form.channelsystems.IResource;
import things.form.graph.connections.IPartConnection;
import things.form.kinds.settings.IKindSettings;
import things.form.material.IMaterial;
import things.form.material.condition.IMaterialCondition;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import thinker.mind.util.IBeingAccess;

/**
 * Channel system to intake material and turn it into something else, e.g. food
 * into energy, fuel into heat, and so on
 * 
 * @author borah
 *
 */
public class FuelChannelSystem implements IChannelSystem {

	private String name;
	private FuelChannelResource allowedFood;
	private FuelChannel channel;
	private String stomachPart;
	private FuelBurnerChannelCenter<?> stomachType;
	private String mouthPart;
	private FuelIntakeChannelCenter fuelIntake;
	private IChannelNeed need;

	/**
	 * 
	 * @param name
	 * @param blood
	 * @param bloodVesselMaterial if this is null, then simply have no blood vessels
	 * @param stomachPart
	 * @param allowedFood         what food can be eaten
	 * @param resource            = resource to convert the food to
	 * @param conv                = Function for converting food resources into
	 *                            whatever quantity the other resource needs (as an
	 *                            additional amount)
	 */
	public <B extends Comparable<?>> FuelChannelSystem(String name, IMaterial bloodVesselMaterial, String mouthPart,
			String stomachPart, IMaterialCondition allowedFood, IResource<B> resource, Function<Float, B> conv,
			String needName) {
		this.name = name;
		this.mouthPart = mouthPart;
		this.allowedFood = new FuelChannelResource(name, 1f, allowedFood);
		this.channel = new FuelChannel(name + "_pipe", bloodVesselMaterial, this.allowedFood, this);
		this.stomachPart = stomachPart;
		this.fuelIntake = new FuelIntakeChannelCenter(name + "_intake", this, this.allowedFood);
		this.stomachType = new FuelBurnerChannelCenter<>(name + "_burner", resource, conv, this);
		this.need = new ChannelNeed(needName, this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<IResource<?>> getChannelResources() {
		return Collections.singleton(this.allowedFood);
	}

	@Override
	public ChannelType getType() {
		return ChannelType.MATERIAL;
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes() {
		return Set.of(stomachType, fuelIntake);
	}

	@Override
	public Collection<IChannelCenter> getCenterTypes(ChannelRole role) {
		switch (role) {
		case TRANSFORM:
			return Collections.singleton(this.stomachType);
		case INTAKE:
			return Collections.singleton(this.fuelIntake);

		default:
			return Collections.emptySet();
		}
	}

	@Override
	public Collection<IChannelNeed> getChannelSystemNeeds() {
		return Collections.singleton(this.need);
	}

	@Override
	public float getNeedLevel(IChannelNeed forNeed, IBeingAccess info) {
		if (forNeed.equals(this.need)) {
			Iterable<IComponentPart> parts = () -> info.partAccess().stream().map((x) -> (IComponentPart) x)
					.filter((a) -> a.getAbilities().contains(this.stomachType)).iterator();
			float aggreg = 0;
			int count = 0;
			for (IComponentPart viablepart : parts) {
				count++;
				aggreg += viablepart.getResourceAmount(this.allowedFood) / this.allowedFood.getMaxValue();
			}
			if (count == 0)
				return -1f;
			aggreg /= count;
			return aggreg;
		}
		return -1f;
	}

	/**
	 * Gets the food resource
	 * 
	 * @return
	 */
	public FuelChannelResource getFoodResource() {
		return this.allowedFood;
	}

	@Override
	public Collection<IChannel> getChannelConnectionTypes() {

		return Collections.singleton(channel);
	}

	@Override
	public String toString() {
		return "sys{~" + this.name + "~}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FuelChannelSystem ics) {
			return this.name.equals(ics.name()) && ics.getType() == this.getType()
					&& this.stomachType.equals(ics.stomachType) && this.fuelIntake.equals(ics.fuelIntake)
					&& this.channel.equals(ics.channel) && this.mouthPart.equals(ics.mouthPart)
					&& this.stomachPart.equals(ics.stomachPart);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return name.hashCode() + stomachType.hashCode() + this.fuelIntake.hashCode() + channel.hashCode()
				+ this.mouthPart.hashCode() + this.stomachPart.hashCode();
	}

	@Override
	public Collection<? extends IComponentPart> populateBody(ISoma body, IKindSettings set, GameMap world) {
		Collection<IComponentPart> stomachs = body.getPartsByName(stomachPart);
		for (IComponentPart stomach : stomachs) {
			stomach.addAbility(stomachType, true);

		}
		Collection<IComponentPart> mouths = body.getPartsByName(mouthPart);
		for (IComponentPart mouth : mouths) {
			mouth.addAbility(fuelIntake, true);
			for (IComponentPart stom : stomachs) {
				body.addChannel(mouth, this.channel, stom, true);
			}
		}
		Set<IComponentPart> union = Sets.newHashSet(stomachs);
		union.addAll(mouths);
		return union;
	}

	@Override
	public void onBodyUpdate(ISoma body, IComponentPart updated) {
		// TODO update channel system

	}

	@Override
	public boolean onBodyLoss(ISoma body, IComponentPart lost) {
		// TODO update when the body loses a part
		return true;
	}

	@Override
	public void onBodyNew(ISoma body, IComponentPart gained, IPartConnection connection, IComponentPart to,
			boolean isNew) {
	}

}
