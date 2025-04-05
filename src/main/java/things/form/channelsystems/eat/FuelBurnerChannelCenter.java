package things.form.channelsystems.eat;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IResource;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.IPartStat;

public class FuelBurnerChannelCenter<B extends Comparable<?>> implements IChannelCenter {

	public static final IPartStat<Float> FUEL_CONVERSION_RATE = new IPartStat<Float>() {

		@Override
		public String name() {
			return "fuel_conversion_rate";
		}

		@Override
		public Class<? super Float> getType() {
			return float.class;
		}

		@Override
		public Float getDefaultValue(IComponentPart part) {
			return 0.1f;
		}

		@Override
		public Float extract(Float val1, Float subVal, int count) {
			float sum = val1 * count;
			sum -= subVal;
			return sum / (count - 1f);
		}

		@Override
		public Float aggregate(Iterable<Float> values) {
			float su = 0;
			int c = 0;
			for (float f : values) {
				su += f;
				c++;
			}
			return su / c;
		}
	};

	private String name;
	private FuelChannelSystem system;
	private Function<Float, B> converter;
	private IResource<B> resourceCout;
	private FuelChannelResource inResource;

	public FuelBurnerChannelCenter(String name, IResource<B> resource, Function<Float, B> conv,
			FuelChannelSystem foodChannelSystem) {
		this.name = name;
		this.resourceCout = resource;
		this.system = foodChannelSystem;
		this.converter = conv;
		this.inResource = system.getFoodResource();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Collection<IPartStat<?>> getStats() {

		return Collections.emptySet();
	}

	@Override
	public boolean controllable() {
		return false;
	}

	@Override
	public ChannelRole getRole() {
		return ChannelRole.TRANSFORM;
	}

	@Override
	public FuelChannelSystem getSystem() {
		return system;
	}

	/**
	 * Return resource this converts food into
	 * 
	 * @return
	 */
	public IResource<?> getProducedResource() {
		return resourceCout;
	}

	/**
	 * Returns the resource consuemd by this generator
	 * 
	 * @return
	 */
	public FuelChannelResource getConsumedResource() {
		return inResource;
	}

	@Override
	public boolean canTick(ISoma body, IComponentPart part, long ticks) {
		return true;
	}

	@Override
	public void automaticTick(ISoma body, IComponentPart part, long ticks) {
		IChannelCenter.super.automaticTick(body, part, ticks);
		// TODO stomachs make resources
		// TODO pump resources from mouth to stomach
		for (IComponentPart conPart : body.getPartGraph().getNeighbors(part,
				system.getChannelConnectionTypes().iterator().next())) {
			float ores = conPart.getResourceAmount(inResource);
			if (ores == 0)
				continue;
			float tres = part.getResourceAmount(inResource);
			float overflow = ores + tres - inResource.getMaxValue();
			if (overflow < 0) {
				part.changeResourceAmount(inResource, ores + tres, true);
				conPart.changeResourceAmount(inResource, 0, true);
			} else {
				conPart.changeResourceAmount(inResource, overflow, true);
				part.changeResourceAmount(inResource, inResource.getMaxValue(), true);
			}
			break;
		}

		float food = part.getResourceAmount(this.inResource);
		float rate = part.getStat(FUEL_CONVERSION_RATE);

		if (food < rate)
			return;

		part.changeResourceAmount(this.inResource, food - rate, true);
		B com = part.getResourceAmount(this.resourceCout);
		B add = this.converter.apply(rate);

		part.changeResourceAmount(this.resourceCout, this.resourceCout.add(com, add), true);
	}

	@Override
	public String toString() {
		return "{|" + this.name + "|}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FuelBurnerChannelCenter cc) {
			return this.name.equals(cc.name) && this.resourceCout.equals(cc.resourceCout)
					&& this.inResource.equals(cc.inResource);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.resourceCout.hashCode() * this.inResource.hashCode()
				+ this.getClass().hashCode();
	}

}
