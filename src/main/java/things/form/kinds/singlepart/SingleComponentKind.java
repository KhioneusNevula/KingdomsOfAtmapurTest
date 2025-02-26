package things.form.kinds.singlepart;

import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import things.form.channelsystems.IChannelSystem;
import things.form.kinds.BasicKindProperties;
import things.form.kinds.IKind;
import things.form.kinds.settings.IKindSettings;
import things.form.material.IMaterial;
import things.form.shape.IShape;
import things.form.soma.IPartDestructionCondition;
import things.form.soma.abilities.IPartAbility;
import things.form.soma.component.StandardComponentPart;
import things.form.soma.stats.IPartStat;

public class SingleComponentKind implements IKind {

	private String name;

	public SingleComponentKind(String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public SingleComponentSoma generate(IKindSettings settings) {
		String name = settings.getSetting(BasicKindProperties.SINGLE_PART_NAME);
		int planes = settings.getSetting(BasicKindProperties.PLANES);
		IShape shape = settings.getSetting(BasicKindProperties.SHAPE);
		IMaterial mat = settings.getSetting(BasicKindProperties.MATERIAL);
		float size = settings.getSetting(BasicKindProperties.SIZE);
		float mass = settings.getSetting(BasicKindProperties.MASS);
		Set<? extends IPartAbility> abs = settings.getSetting(BasicKindProperties.SINGLE_PART_ABILITIES);
		Map<? extends IPartStat<?>, ? extends Object> stats = settings
				.getSetting(BasicKindProperties.SINGLE_PART_STATS);
		Set<? extends IMaterial> emb = settings.getSetting(BasicKindProperties.SINGLE_PART_EMBEDDED_MATERIALS);
		Set<? extends IChannelSystem> systems = settings.getSetting(BasicKindProperties.CHANNEL_SYSTEMS);
		Color color = settings.getSetting(BasicKindProperties.COLOR);
		IPartDestructionCondition descon = settings.getSetting(BasicKindProperties.DESTRUCTION_CONDITION);
		UUID id = UUID.randomUUID();
		StandardComponentPart component = new StandardComponentPart(name, id, mat, shape, 1f, planes, abs, stats, emb);
		SingleComponentSoma soma = new SingleComponentSoma(component, size, mass, systems, color, descon);
		soma.setKind(this);
		return soma;
	}

	@Override
	public String toString() {
		return "|<" + this.name + ">|";
	}

}
