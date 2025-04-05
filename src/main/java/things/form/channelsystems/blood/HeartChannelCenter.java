package things.form.channelsystems.blood;

import java.util.Collection;
import java.util.Collections;

import _utilities.couplets.Pair;
import things.biology.genes.IGenomeEncoding;
import things.form.channelsystems.IChannelCenter;
import things.form.channelsystems.IResource;
import things.form.material.IMaterial;
import things.form.material.property.MaterialProperty;
import things.form.soma.ISoma;
import things.form.soma.component.IComponentPart;
import things.form.soma.stats.FloatPartStats;
import things.form.soma.stats.IPartStat;

public class HeartChannelCenter implements IChannelCenter {

	private String name;
	private BloodChannelSystem system;

	public HeartChannelCenter(String name, BloodChannelSystem bloodChannelSystem) {
		this.name = name;
		this.system = bloodChannelSystem;
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
		return ChannelRole.DISTRIBUTION;
	}

	@Override
	public BloodChannelSystem getSystem() {
		return system;
	}

	@Override
	public boolean canTick(ISoma body, IComponentPart part, long ticks) {
		return true; // TODO when would a heart stop beating...
	}

	@Override
	public void automaticTick(ISoma body, IComponentPart heartpart, long ticks) {
		// TODO hearts beat, also do not generate blood
		IMaterial bloodmat = heartpart.getEmbeddedMaterialFor(this.system.getBloodMaterial());
		float[] heartblood = { heartpart.getResourceAmount(this.system.getBloodMaterial()) };
		body.getConnectedParts(heartpart).forEach((a) -> {
			float blood = a.getResourceAmount(this.system.getBloodMaterial());
			float newblood = blood + 0.05f;
			float newheartblood = heartblood[0] - 0.05f;
			if (newblood < heartblood[0]) {
				a.changeMaterialResourceAmount(this.system.getBloodMaterial(),
						heartpart.getEmbeddedMaterialFor(this.system.getBloodMaterial()), newblood, true);
				heartpart.changeResourceAmount(this.system.getBloodMaterial(), newheartblood, true);
				heartblood[0] = heartpart.getResourceAmount(this.system.getBloodMaterial());
			}
		});
		if (Math.random() < body.getAggregateStat(FloatPartStats.BLOOD_REGENERATION)
				&& heartblood[0] < 0.1f * system.getBloodMaterial().getMaxValue()) {
			body.getPartsByName(system.getBloodGenPart()).forEach((x) -> {
				if (heartblood[0] < system.getBloodMaterial().getMaxValue()) {
					heartblood[0] += 0.1f;

				}
			});
			if (bloodmat == null) {
				bloodmat = this.system.getBloodMaterial().generateMaterialFromSettings(body.getCreationSettings());
			}
			heartpart.changeMaterialResourceAmount(this.system.getBloodMaterial(), bloodmat,
					Float.valueOf(heartblood[0]), true);
		}

	}

	@Override
	public boolean isAutomatic() {
		return true;
	}

	@Override
	public String toString() {
		return "{|" + this.name + "|}";
	}

}
