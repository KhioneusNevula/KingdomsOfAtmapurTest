package things.form.kinds.singlepart;

import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import things.form.channelsystems.IChannelSystem;
import things.form.kinds.BasicKindProperty;
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
		String name = settings.getSetting(BasicKindProperty.SINGLE_PART_NAME);
		int planes = settings.getSetting(BasicKindProperty.PLANES);
		IShape shape = settings.getSetting(BasicKindProperty.SHAPE);
		IMaterial mat = settings.getSetting(BasicKindProperty.MATERIAL);
		float size = settings.getSetting(BasicKindProperty.SIZE);
		float mass = settings.getSetting(BasicKindProperty.MASS);
		Set<? extends IPartAbility> abs = settings.getSetting(BasicKindProperty.SINGLE_PART_ABILITIES);
		Map<? extends IPartStat<?>, ? extends Object> stats = settings.getSetting(BasicKindProperty.SINGLE_PART_STATS);
		Set<? extends IMaterial> emb = settings.getSetting(BasicKindProperty.SINGLE_PART_EMBEDDED_MATERIALS);
		Set<? extends IChannelSystem> systems = settings.getSetting(BasicKindProperty.CHANNEL_SYSTEMS);
		Color color = settings.getSetting(BasicKindProperty.COLOR);
		IPartDestructionCondition descon = settings.getSetting(BasicKindProperty.DESTRUCTION_CONDITION);
		UUID id = UUID.randomUUID();
		StandardComponentPart component = new StandardComponentPart(name, id, mat, shape, 1f, planes, abs, stats, emb);
		SingleComponentSoma soma = new SingleComponentSoma(component, size, mass, systems, color, descon);
		return soma;
	}

	@Override
	public String toString() {
		return "|<" + this.name + ">|";
	}

}
